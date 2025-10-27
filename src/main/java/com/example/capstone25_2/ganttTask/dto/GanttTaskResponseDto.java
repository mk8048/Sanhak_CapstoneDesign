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
