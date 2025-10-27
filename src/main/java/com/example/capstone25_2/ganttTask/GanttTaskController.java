package com.example.capstone25_2.ganttTask;

import com.example.capstone25_2.ganttTask.dto.GanttTaskCreateRequestDto;
import com.example.capstone25_2.ganttTask.dto.GanttTaskResponseDto;
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