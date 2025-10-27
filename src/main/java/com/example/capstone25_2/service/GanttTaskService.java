package com.example.capstone25_2.service;

import com.example.capstone25_2.domain.GanttTask;
import com.example.capstone25_2.dto.GanttTaskCreateRequestDto;
import com.example.capstone25_2.repository.GanttTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GanttTaskService {

    private final GanttTaskRepository ganttTaskRepository;
    public void createTask(GanttTaskCreateRequestDto requestDto) {
        GanttTask ganttTask = new GanttTask();
        ganttTask.setProjectId(requestDto.getProjectId());
        ganttTask.setName(requestDto.getName());
        ganttTask.setStartDate(requestDto.getStartDate());
        ganttTask.setEndDate(requestDto.getEndDate());
        ganttTask.setAssignedTo(requestDto.getAssignedTo());
        ganttTask.setProgress(0);

        ganttTaskRepository.save(ganttTask);

    }


}