package com.example.capstone25_2.ganttTask;

import com.example.capstone25_2.ganttTask.dto.GanttTaskCreateRequestDto;
import com.example.capstone25_2.ganttTask.dto.GanttTaskResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/gantt")
@RequiredArgsConstructor
public class GanttTaskController {

    private final GanttTaskService ganttTaskService;

    @PostMapping
    @ResponseBody
    public void createTask(@RequestBody GanttTaskCreateRequestDto requestDto) {
        ganttTaskService.createTask(requestDto);
    }

    @GetMapping("/project/{projectId}")
    @ResponseBody
    public List<GanttTaskResponseDto> getTasks(@PathVariable Long projectId) {
        return ganttTaskService.getTasksByProjectId(projectId);
    }

    @GetMapping("/test-page")
    public String ganttTestPage() {
        return "ganttTask/gantt-test";
    }
}

// Separate controller for page routing (not API)
@Controller
@RequestMapping("/gantt")
@RequiredArgsConstructor
class GanttPageController {

    @GetMapping("/test")
    public String ganttTestPage() {
        return "ganttTask/gantt-test";
    }
}