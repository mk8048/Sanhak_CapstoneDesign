package com.example.capstone25_2.task;

import com.example.capstone25_2.project.ProjectRole; // ⭐️ Enum import
import com.example.capstone25_2.project.ProjectRepository;
import com.example.capstone25_2.project.ProjectService;
import com.example.capstone25_2.task.dto.AddTaskRequestDTO;
import com.example.capstone25_2.task.dto.TaskProgressDTO;
import com.example.capstone25_2.task.dto.UpdateTaskRequestDTO;
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

        // (HTML에서 VIEWER일 경우 버튼을 숨기기 위함)
        ProjectRole userRole = projectService.getUserRoleInProject(projectId, userId);
        model.addAttribute("userRole", userRole);

        return "task/task_list";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute AddTaskRequestDTO request, HttpSession session) { // DTO로 받음
        taskService.addTask(request, getUserId(session)); // DTO 전달
        return "redirect:/tasks?projectId=" + request.getProjectId();
    }

    @PostMapping("/{taskId}/toggle")
    public String toggleTask(@PathVariable Long taskId, HttpSession session) {
        Long projectId = taskService.findProjectIdByTaskId(taskId);
        taskService.toggleTask(taskId, getUserId(session));
        return "redirect:/tasks?projectId=" + projectId;
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId, HttpSession session) {
        Long projectId = taskService.findProjectIdByTaskId(taskId);
        taskService.deleteTask(taskId, getUserId(session));
        return "redirect:/tasks?projectId=" + projectId;
    }

    @PostMapping("/{taskId}/update")
    public String updateTask(@PathVariable Long taskId,
                             @ModelAttribute UpdateTaskRequestDTO request,
                             HttpSession session) {

        // 서비스 호출 (수정)
        taskService.updateTask(taskId, request, getUserId(session));

        // 수정을 마친 후 다시 해당 프로젝트의 업무 목록으로 이동하기 위해 Project ID 조회
        Long projectId = taskService.findProjectIdByTaskId(taskId);

        return "redirect:/tasks?projectId=" + projectId;
    }
}