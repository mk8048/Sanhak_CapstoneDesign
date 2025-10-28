package com.example.capstone25_2.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class GithubConfig {

    @Value("${github.token:}")
    private String githubToken;

    @Bean
    public RestTemplate githubRestTemplate() {
        RestTemplate rt = new RestTemplate();
        ClientHttpRequestInterceptor auth = (req, body, ex) -> {
            req.getHeaders().add("Accept", "application/vnd.github+json");
            req.getHeaders().add("User-Agent", "capstone-commit-viewer");
            if (githubToken != null && !githubToken.isBlank()) {
                req.getHeaders().add("Authorization", "Bearer " + githubToken);
            }
            return ex.execute(req, body);
        };
        rt.setInterceptors(List.of(auth));
        return rt;
    }
}
