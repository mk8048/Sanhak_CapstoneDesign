package com.example.capstone25_2.task;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 할 일 내용 (예: 로그인 UI 만들기)

    private boolean isCompleted; // 완료 여부

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private String assignee;

    private Long projectId;

    private Long assignedUserId;
}