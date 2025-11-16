package com.example.capstone25_2.task;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
// 날짜 관리용 임시 엔티티 -> 추후 Project 생성 시 연동

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectSchedule {

    @Id
    private Long projectId; // 실제 프로젝트 ID와 똑같이 맞춤

    private LocalDate startDate;
    private LocalDate endDate;
}