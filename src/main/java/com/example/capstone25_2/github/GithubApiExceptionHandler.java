package com.example.capstone25_2.github;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GithubApiExceptionHandler {

    @ExceptionHandler(GithubApiException.class)
    public ResponseEntity<?> handleGithub(GithubApiException e) {

        // 1) 상태코드 결정 (예외에 httpStatus가 없으면 reason으로 합리적 매핑)
        int status = (e.getHttpStatus() != null) ? e.getHttpStatus().value() : mapReasonToStatus(e.getReason());

        // 2) 응답 바디
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("reason", e.getReason().name());
        body.put("status", status);
        body.put("message", e.getMessage());
        body.put("rateLimitRemaining", e.getRateLimitRemaining());
        body.put("rateLimitResetEpoch", e.getRateLimitResetEpoch());

        return ResponseEntity.status(status).body(body);
    }

    private int mapReasonToStatus(GithubApiException.Reason r) {
        switch (r) {
            case UNAUTHORIZED:             return 401;
            case FORBIDDEN:                return 403;
            case RATE_LIMIT_EXCEEDED:      return 429;
            case REPO_OR_BRANCH_NOT_FOUND: return 404;  // 404/422 모두 여기로
            case CLIENT_ERROR:             return 400;
            case SERVER_ERROR:             return 502;  // GitHub 5xx → Bad Gateway 권장
            case NETWORK_ERROR:            return 503;  // 네트워크/타임아웃 → Service Unavailable
            case UNKNOWN:
            default:                       return 500;
        }
    }
}
