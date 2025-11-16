package com.example.capstone25_2.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubSearchResponse(
        List<GithubRepoDTO> items // 검색된 프로젝트 리스트
) {}