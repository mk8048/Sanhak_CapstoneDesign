package com.example.capstone25_2.task;

import com.example.capstone25_2.project.Project;
import com.example.capstone25_2.project.ProjectRepository;
import com.example.capstone25_2.task.dto.TaskProgressDTO;
import com.example.capstone25_2.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ProjectRepository projectRepository;

    @GetMapping
    public String viewTasks(@RequestParam(required = false) Long projectId, Model model) {
        // 1. 사이드바용 프로젝트 목록
        List<Project> allProjects = projectRepository.findAll();
        model.addAttribute("projects", allProjects);

        // 2. 선택된 ID가 없으면 첫 번째 프로젝트 선택
        if (projectId == null) {
            if (!allProjects.isEmpty()) {
                projectId = allProjects.get(0).getProjectId();
            } else {
                return "task/task_list"; // 프로젝트가 아예 없을 때
            }
        }

        // 3. 해당 프로젝트 정보 및 할 일 조회
        TaskProgressDTO progress = taskService.getProgress(projectId);

        model.addAttribute("progress", progress);
        model.addAttribute("currentProjectId", projectId);

        return "task/task_list";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam Long projectId, @RequestParam String title) {
        taskService.addTask(projectId, title);
        return "redirect:/tasks?projectId=" + projectId;
    }

    @PostMapping("/{taskId}/toggle")
    public String toggleTask(@PathVariable Long taskId) {
        Long projectId = taskService.findProjectIdByTaskId(taskId); // 원래 페이지 유지를 위해 ID 조회
        taskService.toggleTask(taskId);
        return "redirect:/tasks?projectId=" + projectId;
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId) {
        Long projectId = taskService.findProjectIdByTaskId(taskId);
        taskService.deleteTask(taskId);
        return "redirect:/tasks?projectId=" + projectId;
    }
}