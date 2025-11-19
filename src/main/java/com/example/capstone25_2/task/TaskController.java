package com.example.capstone25_2.task;

import com.example.capstone25_2.task.dto.TaskProgressDTO;
import com.example.capstone25_2.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // 화면 보여주기 (테스트용 projectId=1 고정)
    @GetMapping
    public String viewTasks(Model model) {
        Long projectId = 1L; // 나중엔 @RequestParam으로 받으면 됨
        TaskProgressDTO progress = taskService.getProgress(projectId);

        model.addAttribute("progress", progress);
        return "task/task_list"; // HTML 파일 위치
    }

    // 할 일 추가
    @PostMapping("/add")
    public String addTask(@RequestParam String title) {
        Long projectId = 1L; // 테스트용 고정
        taskService.addTask(projectId, title);
        return "redirect:/tasks";
    }

    // 완료 체크/해제
    @PostMapping("/{taskId}/toggle")
    public String toggleTask(@PathVariable Long taskId) {
        taskService.toggleTask(taskId);
        return "redirect:/tasks";
    }

    // 할 일 삭제
    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return "redirect:/tasks"; // 삭제 후 목록 페이지로 새로고침
    }
}