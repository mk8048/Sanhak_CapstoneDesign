package com.example.capstone25_2.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto {

    private String type; // DELIVERABLE, PROJECT, MEMO, MEETING, TASK
    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;
    private String metadata; // 추가 정보 (작성자, 업로더 등)
    private String url; // 상세 페이지 URL
}
