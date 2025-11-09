package com.example.capstone25_2.ganttTask.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GanttTaskResponseDto {

    private Long id;
    private Long projectId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long assignedTo;
    private int progress;
    private Long daysRemaining;

}

/*
Postman 테스트용 JSON 예시:
{
  "projectId": 1,
  "name": "초기 요구사항 분석",
  "startDate": "2025-11-01",
  "endDate": "2025-12-25",
  "assignedTo": 3
}
*/