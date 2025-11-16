package com.example.capstone25_2.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepoDTO(
        String name,            // 프로젝트 이름
        String full_name,       // 전체 이름 (owner/repo)
        String html_url,        // 링크
        String description,     // 설명
        int stargazers_count,   // 스타 수 (인기도)
        String language         // 주 사용 언어
) {}