package com.example.capstone25_2.task;

import com.example.capstone25_2.project.Project;
import com.example.capstone25_2.project.ProjectRepository;
import com.example.capstone25_2.project.ProjectService; // ⭐️ 프로젝트 이름 조회를 위해 추가
import com.example.capstone25_2.task.dto.TaskProgressDTO;
import jakarta.servlet.http.HttpSession; // ⭐️ 세션 사용을 위해 추가
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
    private final ProjectService projectService; // ⭐️ 추가

    // ⭐️⭐️⭐️ [추가] 모든 요청 시 레이아웃 공통 데이터 주입 ⭐️⭐️⭐️
    @ModelAttribute
    public void addLayoutAttributes(HttpSession session, Model model) {
        // 1. 사용자 이름/닉네임 처리 (displayName)
        String userName = (String) session.getAttribute("userName");
        String userNickname = (String) session.getAttribute("userNickname");

        if (userName != null) {
            String displayName = (userNickname != null && !userNickname.isEmpty()) ? userNickname : userName;
            model.addAttribute("displayName", displayName);
        }

        // 2. 현재 선택된 프로젝트 정보 처리 (currentProjectId)
        Long projectId = (Long) session.getAttribute("currentProjectId");
        if (projectId != null) {
            model.addAttribute("currentProjectId", projectId);

            // 3. 헤더 제목 설정 (pageTitle)
            try {
                String projectName = projectService.getProjectNameById(projectId);
                model.addAttribute("pageTitle", projectName);
            } catch (Exception e) {
                // 프로젝트를 찾을 수 없는 경우 무시
            }
        }
    }

    @GetMapping
    public String viewTasks(@RequestParam(required = false) Long projectId, HttpSession session, Model model) {

        // 1. 파라미터로 projectId가 오면 우선 사용, 없으면 세션 값 사용
        if (projectId == null) {
            projectId = (Long) session.getAttribute("currentProjectId");
        }

        // 2. 그래도 없으면 에러 처리 또는 목록으로 리다이렉트
        if (projectId == null) {
            return "redirect:/projects";
        }

        // 3. 세션 업데이트 (현재 보고 있는 프로젝트로 갱신)
        session.setAttribute("currentProjectId", projectId);

        // 4. 해당 프로젝트 정보 및 할 일 조회
        TaskProgressDTO progress = taskService.getProgress(projectId);
        model.addAttribute("progress", progress);

        // 5. (선택) 사이드바 활성화를 위해 다시 한번 명시적으로 담아줌 (안 해도 @ModelAttribute가 처리함)
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
        Long projectId = taskService.findProjectIdByTaskId(taskId);
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