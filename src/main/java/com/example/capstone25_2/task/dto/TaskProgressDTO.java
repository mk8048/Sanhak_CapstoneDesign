package com.example.capstone25_2.task.dto;

import com.example.capstone25_2.task.Task;
import java.util.List;

public record TaskProgressDTO(
        Long projectId,
        double timeProgress,    // 시간상 진행률 (예상치)
        double actualProgress,  // 실제 업무 진행률 (완료치)
        List<Task> tasks        // 할 일 목록
) {}