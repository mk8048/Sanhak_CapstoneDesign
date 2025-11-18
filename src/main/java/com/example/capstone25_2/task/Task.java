package com.example.capstone25_2.task;

import jakarta.persistence.*;
import lombok.*;

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

    // [핵심] 객체 연동 대신 ID만 저장 (나중에 연동하기 편함)
    private Long projectId;

    // 나중에 담당자 연동을 위해 미리 만들어둔 필드 (지금은 null로 써도 됨)
    private Long assignedUserId;
}