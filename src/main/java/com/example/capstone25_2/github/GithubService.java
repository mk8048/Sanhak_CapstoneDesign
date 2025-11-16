package com.example.capstone25_2.github;

import com.example.capstone25_2.github.dto.GithubCommitDTO;
import com.example.capstone25_2.github.dto.GithubRepoDTO;
import com.example.capstone25_2.github.dto.GithubSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@Service
public class GithubService {

    private final RestTemplate rt;
    private final String baseUrl;
    private final String defaultOwner;
    private final String defaultRepo;
    private final String defaultBranch;

    public GithubService(
            RestTemplate githubRestTemplate,
            @Value("${github.base-url}") String baseUrl,
            @Value("${github.default-owner}") String defaultOwner,
            @Value("${github.default-repo}") String defaultRepo,
            @Value("${github.default-branch}") String defaultBranch
    ) {
        this.rt = githubRestTemplate;
        this.baseUrl = baseUrl;
        this.defaultOwner = defaultOwner;
        this.defaultRepo = defaultRepo;
        this.defaultBranch = defaultBranch;
    }

    public List<GithubCommitDTO> getCommits(
            String owner, String repo, String branch,
            OffsetDateTime since, OffsetDateTime until,
            Integer perPage, Integer page
    ) {
        String o = Optional.ofNullable(owner).filter(s -> !s.isBlank()).orElse(defaultOwner);
        String r = Optional.ofNullable(repo).filter(s -> !s.isBlank()).orElse(defaultRepo);
        String b = Optional.ofNullable(branch).filter(s -> !s.isBlank()).orElse(defaultBranch);

        UriComponentsBuilder ub = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/repos/{owner}/{repo}/commits")
                .queryParam("sha", b)
                .queryParam("per_page", Optional.ofNullable(perPage).orElse(20))
                .queryParam("page", Optional.ofNullable(page).orElse(1));

        if (since != null) ub.queryParam("since", since);
        if (until != null) ub.queryParam("until", until);

        URI uri = ub.build(o, r);

        try {
            ResponseEntity<GithubCommitDTO[]> resp = rt.exchange(
                    uri, HttpMethod.GET,
                    new HttpEntity<>(new LinkedMultiValueMap<>(), new HttpHeaders()),
                    GithubCommitDTO[].class
            );

            GithubCommitDTO[] body = resp.getBody();
            return body == null ? List.of() : Arrays.asList(body);

        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("GitHub 401 Unauthorized. uri={}, msg={}, body={}",
                    uri, e.getMessage(), safeBody(e.getResponseBodyAsString()));
            throw new GithubApiException(
                    GithubApiException.Reason.UNAUTHORIZED,
                    e.getStatusCode(),
                    "GitHub 인증 실패(401): 토큰이 없거나 잘못되었을 수 있습니다.",
                    safeBody(e.getResponseBodyAsString()),
                    header(e.getResponseHeaders(), "X-RateLimit-Remaining"),
                    header(e.getResponseHeaders(), "X-RateLimit-Reset"),
                    e
            );

        } catch (HttpClientErrorException.Forbidden e) {
            String remaining = header(e.getResponseHeaders(), "X-RateLimit-Remaining");
            String reset = header(e.getResponseHeaders(), "X-RateLimit-Reset");
            boolean rateLimited = "0".equals(remaining);

            log.warn("GitHub 403 Forbidden. rateLimited={}, remaining={}, reset={}, uri={}, body={}",
                    rateLimited, remaining, reset, uri, safeBody(e.getResponseBodyAsString()));
            throw new GithubApiException(
                    rateLimited ? GithubApiException.Reason.RATE_LIMIT_EXCEEDED
                            : GithubApiException.Reason.FORBIDDEN,
                    e.getStatusCode(),
                    rateLimited
                            ? "GitHub 레이트 리밋 초과(403). resetEpochSec=" + reset
                            : "GitHub 접근 거부(403): 권한이 부족할 수 있습니다.",
                    safeBody(e.getResponseBodyAsString()),
                    remaining,
                    reset,
                    e
            );

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("GitHub 404 Not Found. uri={}, body={}", uri, safeBody(e.getResponseBodyAsString()));
            throw new GithubApiException(
                    GithubApiException.Reason.REPO_OR_BRANCH_NOT_FOUND,
                    e.getStatusCode(),
                    "대상 리포지토리/브랜치를 찾을 수 없습니다(404).",
                    safeBody(e.getResponseBodyAsString()),
                    header(e.getResponseHeaders(), "X-RateLimit-Remaining"),
                    header(e.getResponseHeaders(), "X-RateLimit-Reset"),
                    e
            );

        } catch (HttpClientErrorException.UnprocessableEntity e) {
            log.warn("GitHub 422 Unprocessable Entity. uri={}, body={}", uri, safeBody(e.getResponseBodyAsString()));
            throw new GithubApiException(
                    GithubApiException.Reason.REPO_OR_BRANCH_NOT_FOUND,
                    e.getStatusCode(),
                    "요청 파라미터가 유효하지 않습니다(422). 잘못된 sha/브랜치일 수 있습니다.",
                    safeBody(e.getResponseBodyAsString()),
                    header(e.getResponseHeaders(), "X-RateLimit-Remaining"),
                    header(e.getResponseHeaders(), "X-RateLimit-Reset"),
                    e
            );

        } catch (HttpServerErrorException e) {
            log.error("GitHub 5xx Server Error. status={}, uri={}, body={}",
                    e.getStatusCode(), uri, safeBody(e.getResponseBodyAsString()));
            throw new GithubApiException(
                    GithubApiException.Reason.SERVER_ERROR,
                    e.getStatusCode(),
                    "GitHub 서버 오류(5xx)로 데이터를 가져오지 못했습니다.",
                    safeBody(e.getResponseBodyAsString()),
                    header(e.getResponseHeaders(), "X-RateLimit-Remaining"),
                    header(e.getResponseHeaders(), "X-RateLimit-Reset"),
                    e
            );

        } catch (ResourceAccessException e) {
            Throwable root = rootCause(e);
            boolean unknownHost = root instanceof UnknownHostException;
            log.warn("GitHub 네트워크 오류. unknownHost={}, uri={}, root={}, msg={}",
                    unknownHost, uri, root.getClass().getSimpleName(), root.getMessage());
            throw new GithubApiException(
                    GithubApiException.Reason.NETWORK_ERROR,
                    null,
                    unknownHost
                            ? "네트워크 오류: 호스트를 찾을 수 없습니다. 인터넷/DNS를 확인하세요."
                            : "네트워크 오류: 연결 타임아웃 또는 전송 실패가 발생했습니다.",
                    null,
                    null,
                    null,
                    e
            );

        } catch (HttpStatusCodeException e) {
            log.warn("GitHub 4xx Client Error. status={}, uri={}, body={}",
                    e.getStatusCode(), uri, safeBody(e.getResponseBodyAsString()));
            throw new GithubApiException(
                    GithubApiException.Reason.CLIENT_ERROR,
                    e.getStatusCode(),
                    "요청이 거부되었습니다: " + e.getStatusCode(),
                    safeBody(e.getResponseBodyAsString()),
                    header(e.getResponseHeaders(), "X-RateLimit-Remaining"),
                    header(e.getResponseHeaders(), "X-RateLimit-Reset"),
                    e
            );

        } catch (RestClientException e) {
            log.error("GitHub 호출 중 알 수 없는 오류. uri={}, msg={}", uri, e.getMessage(), e);
            throw new GithubApiException(
                    GithubApiException.Reason.UNKNOWN,
                    null,
                    "GitHub 호출 중 알 수 없는 오류가 발생했습니다.",
                    null,
                    null,
                    null,
                    e
            );
        }
    }

    private static String header(HttpHeaders headers, String name) {
        if (headers == null) return null;
        return Optional.ofNullable(headers.getFirst(name)).orElse(null);
    }

    private static String safeBody(String body) {
        if (body == null) return null;
        int max = 2000;
        return body.length() > max ? body.substring(0, max) + "...(truncated)" : body;
    }

    private static Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) cur = cur.getCause();
        return cur;
    }

    // 오픈소스 프로젝트 검색 및 추천 기능
    public List<GithubRepoDTO> searchOpenSourceProjects(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        // 검색 API URL 빌드
        // q=키워드 & sort=stars(별순) & order=desc(내림차순)
        UriComponentsBuilder ub = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/search/repositories")
                .queryParam("q", keyword)
                .queryParam("sort", "stars")
                .queryParam("order", "desc")
                .queryParam("per_page", 10); // 상위 10개만 추천

        try {
            GithubSearchResponse body = rt.getForObject(ub.toUriString(), GithubSearchResponse.class);

            return body != null && body.items() != null ? body.items() : List.of();

        } catch (Exception e) {
            log.error("오픈소스 검색 실패: {}", e.getMessage());
            return List.of(); // 에러나면 빈 리스트 반환 (시스템 멈춤 방지)
        }
    }
}
