package com.example.capstone25_2.service;

import com.example.capstone25_2.domain.GanttTask;
import com.example.capstone25_2.dto.GanttTaskCreateRequestDto;
import com.example.capstone25_2.dto.GanttTaskResponseDto;
import com.example.capstone25_2.repository.GanttTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<GanttTaskResponseDto> getTasksByProjectId(Long projectId) {

        List<GanttTask> tasks = ganttTaskRepository.findByProjectId(projectId);
        List<GanttTaskResponseDto> dtoList = new ArrayList<>();
        for (GanttTask task : tasks) {
            GanttTaskResponseDto dto = new GanttTaskResponseDto();
            dto.setId(task.getId());
            dto.setProjectId(task.getProjectId());
            dto.setName(task.getName());
            dto.setStartDate(task.getStartDate());
            dto.setEndDate(task.getEndDate());
            dto.setAssignedTo(task.getAssignedTo());
            dto.setProgress(task.getProgress());

            dtoList.add(dto);
        }

        return dtoList;

    }

}