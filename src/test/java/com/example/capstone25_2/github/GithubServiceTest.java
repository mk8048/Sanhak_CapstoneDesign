package com.example.capstone25_2.github;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.*;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private GithubService githubService;

    @BeforeEach
    void setUp() {
        // [수정 1] 현재 사용 중인 Service 생성자에 맞춰 파라미터 5개만 넣음 (token 제거)
        githubService = new GithubService(
                restTemplate,
                "https://api.github.com",
                "test-owner",
                "test-repo",
                "main"
        );
    }

    @Test
    @DisplayName("401 Unauthorized 예외가 발생하면 GithubApiException으로 변환되어야 한다")
    void testUnauthorized() {
        // [수정 2] any() 대신 any(Class)를 써서 모호함 해결
        when(restTemplate.exchange(
                any(URI.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        )).thenThrow(HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED, "Unauthorized", HttpHeaders.EMPTY, null, null));

        GithubApiException exception = assertThrows(GithubApiException.class, () -> {
            githubService.getCommits(null, null, null, null, null, null, null);
        });

        assertEquals(GithubApiException.Reason.UNAUTHORIZED, exception.getReason());
    }

    @Test
    @DisplayName("403 Forbidden (Rate Limit 초과) 발생 시 RATE_LIMIT_EXCEEDED로 변환되어야 한다")
    void testRateLimitExceeded() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RateLimit-Remaining", "0");
        headers.add("X-RateLimit-Reset", "1600000000");

        when(restTemplate.exchange(
                any(URI.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        )).thenThrow(HttpClientErrorException.create(
                HttpStatus.FORBIDDEN, "Forbidden", headers, null, null));

        GithubApiException exception = assertThrows(GithubApiException.class, () -> {
            githubService.getCommits(null, null, null, null, null, null, null);
        });

        assertEquals(GithubApiException.Reason.RATE_LIMIT_EXCEEDED, exception.getReason());
    }

    @Test
    @DisplayName("404 Not Found 발생 시 REPO_OR_BRANCH_NOT_FOUND로 변환되어야 한다")
    void testNotFound() {
        when(restTemplate.exchange(
                any(URI.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        )).thenThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null));

        GithubApiException exception = assertThrows(GithubApiException.class, () -> {
            githubService.getCommits(null, null, null, null, null, null, null);
        });

        assertEquals(GithubApiException.Reason.REPO_OR_BRANCH_NOT_FOUND, exception.getReason());
    }

    @Test
    @DisplayName("500 Server Error 발생 시 SERVER_ERROR로 변환되어야 한다")
    void testServerError() {
        when(restTemplate.exchange(
                any(URI.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        )).thenThrow(HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", HttpHeaders.EMPTY, null, null));

        GithubApiException exception = assertThrows(GithubApiException.class, () -> {
            githubService.getCommits(null, null, null, null, null, null, null);
        });

        assertEquals(GithubApiException.Reason.SERVER_ERROR, exception.getReason());
    }

    @Test
    @DisplayName("네트워크 오류(ResourceAccessException) 발생 시 NETWORK_ERROR로 변환되어야 한다")
    void testNetworkError() {
        when(restTemplate.exchange(
                any(URI.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        )).thenThrow(new ResourceAccessException("Connection timed out"));

        GithubApiException exception = assertThrows(GithubApiException.class, () -> {
            githubService.getCommits(null, null, null, null, null, null, null);
        });

        assertEquals(GithubApiException.Reason.NETWORK_ERROR, exception.getReason());
    }
}