package com.example.capstone25_2.task.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AddTaskRequestDTO {
    private Long projectId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String assignee;
}