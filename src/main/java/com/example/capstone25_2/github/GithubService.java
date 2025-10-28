package com.example.capstone25_2.github;

import com.example.capstone25_2.github.dto.GithubCommitDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;

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
        ResponseEntity<GithubCommitDTO[]> resp = rt.exchange(
                uri, HttpMethod.GET,
                new HttpEntity<>(new LinkedMultiValueMap<>(), new HttpHeaders()),
                GithubCommitDTO[].class
        );

        GithubCommitDTO[] body = resp.getBody();
        return body == null ? List.of() : Arrays.asList(body);
    }
}
