package com.example.capstone25_2.github;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class GithubApiException extends RuntimeException {

    public enum Reason {
        UNAUTHORIZED,             // 401
        RATE_LIMIT_EXCEEDED,      // 403 + rate limit
        FORBIDDEN,                // 403 일반 권한 문제
        REPO_OR_BRANCH_NOT_FOUND, // 404 or 422
        SERVER_ERROR,             // 5xx
        NETWORK_ERROR,            // 연결 타임아웃 / DNS 오류
        CLIENT_ERROR,             // 그 외 4xx
        UNKNOWN                   // 그 외 모든 경우
    }

    private final Reason reason;
    private final HttpStatusCode httpStatus;
    private final String responseBody;
    private final String rateLimitRemaining;
    private final String rateLimitResetEpoch;

    public GithubApiException(
            Reason reason,
            HttpStatusCode httpStatus,
            String message,
            String responseBody,
            String rateLimitRemaining,
            String rateLimitResetEpoch,
            Throwable cause
    ) {
        super(message, cause);
        this.reason = reason;
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
        this.rateLimitRemaining = rateLimitRemaining;
        this.rateLimitResetEpoch = rateLimitResetEpoch;
    }
}
