package com.example.capstone25_2.task;

import com.example.capstone25_2.project.ProjectRole; // ⭐️ Enum import
import com.example.capstone25_2.project.ProjectRepository;
import com.example.capstone25_2.project.ProjectService;
import com.example.capstone25_2.task.dto.TaskProgressDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    // Helper: 세션에서 userId 꺼내기
    private String getUserId(HttpSession session) {
        return (String) session.getAttribute("userId");
    }

    @ModelAttribute
    public void addLayoutAttributes(HttpSession session, Model model) {
        String userName = (String) session.getAttribute("userName");
        String userNickname = (String) session.getAttribute("userNickname");

        if (userName != null) {
            String displayName = (userNickname != null && !userNickname.isEmpty()) ? userNickname : userName;
            model.addAttribute("displayName", displayName);
        }

        Long projectId = (Long) session.getAttribute("currentProjectId");
        if (projectId != null) {
            model.addAttribute("currentProjectId", projectId);
            try {
                String projectName = projectService.getProjectNameById(projectId);
                model.addAttribute("pageTitle", projectName);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @GetMapping
    public String viewTasks(@RequestParam(required = false) Long projectId, HttpSession session, Model model) {
        String userId = getUserId(session); // 현재 사용자 ID

        if (projectId == null) {
            projectId = (Long) session.getAttribute("currentProjectId");
        }

        if (projectId == null) {
            return "redirect:/projects";
        }

        session.setAttribute("currentProjectId", projectId);

        TaskProgressDTO progress = taskService.getProgress(projectId);
        model.addAttribute("progress", progress);
        model.addAttribute("currentProjectId", projectId);

        // ⭐️ [추가] 현재 사용자의 '역할' 정보를 뷰로 전달 ⭐️
        // (HTML에서 VIEWER일 경우 버튼을 숨기기 위함)
        ProjectRole userRole = projectService.getUserRoleInProject(projectId, userId);
        model.addAttribute("userRole", userRole);

        return "task/task_list";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam Long projectId, @RequestParam String title, HttpSession session) {
        // ⭐️ userId 함께 전달
        taskService.addTask(projectId, title, getUserId(session));
        return "redirect:/tasks?projectId=" + projectId;
    }

    @PostMapping("/{taskId}/toggle")
    public String toggleTask(@PathVariable Long taskId, HttpSession session) {
        Long projectId = taskService.findProjectIdByTaskId(taskId);
        // ⭐️ userId 함께 전달
        taskService.toggleTask(taskId, getUserId(session));
        return "redirect:/tasks?projectId=" + projectId;
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId, HttpSession session) {
        Long projectId = taskService.findProjectIdByTaskId(taskId);
        // ⭐️ userId 함께 전달
        taskService.deleteTask(taskId, getUserId(session));
        return "redirect:/tasks?projectId=" + projectId;
    }
}