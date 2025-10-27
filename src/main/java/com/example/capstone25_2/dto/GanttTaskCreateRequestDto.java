package com.example.capstone25_2.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GanttTaskCreateRequestDto {

    private Long projectId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long assignedTo;

}
