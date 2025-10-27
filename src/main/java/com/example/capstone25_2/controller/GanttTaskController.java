package com.example.capstone25_2.controller;

import com.example.capstone25_2.dto.GanttTaskCreateRequestDto;
import com.example.capstone25_2.dto.GanttTaskResponseDto;
import com.example.capstone25_2.service.GanttTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gantt")
@RequiredArgsConstructor
public class GanttTaskController {

    private final GanttTaskService ganttTaskService;
    @PostMapping
    public void createTask(@RequestBody GanttTaskCreateRequestDto requestDto) {
        ganttTaskService.createTask(requestDto);
    }

    @GetMapping("/project/{projectId}")
    public List<GanttTaskResponseDto> getTasks(@PathVariable Long projectId) {
        return ganttTaskService.getTasksByProjectId(projectId);
    }

}