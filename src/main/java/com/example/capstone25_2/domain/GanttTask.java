package com.example.capstone25_2.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.LocalDate;

@Entity
@Table(name = "gantt_tasks")
public class GanttTask {

    @Id
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private int progress;

    @Column(name = "assigned_to")
    private Long assignedTo;

    
}