package com.example.capstone25_2.task;

import com.example.capstone25_2.project.Project;
import com.example.capstone25_2.project.ProjectRepository;
import com.example.capstone25_2.task.Task;
import com.example.capstone25_2.task.dto.TaskProgressDTO;
import com.example.capstone25_2.task.TaskRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    // 1. 진행률 및 목록 조회
    @Transactional(readOnly = true)
    public TaskProgressDTO getProgress(Long projectId) {
        // 실제 프로젝트 정보 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. ID=" + projectId));

        // 날짜 계산 (created_at -> Start, deadline -> End)
        LocalDate startDate = (project.getCreatedAt() != null) ? project.getCreatedAt().toLocalDate() : LocalDate.now();
        LocalDate endDate = (project.getDeadline() != null) ? project.getDeadline() : LocalDate.now();

        double timeProgress = calculateTimeProgress(startDate, endDate);

        // 업무 진행률 계산
        long total = taskRepository.countByProjectId(projectId);
        long completed = taskRepository.countByProjectIdAndIsCompletedTrue(projectId);
        double actualProgress = (total == 0) ? 0.0 : ((double) completed / total) * 100.0;

        // 할 일 목록
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        return new TaskProgressDTO(
                projectId,
                Math.round(timeProgress * 10) / 10.0,
                Math.round(actualProgress * 10) / 10.0,
                tasks
        );
    }

    // 2. 할 일 추가
    @Transactional
    public void addTask(Long projectId, String title) {
        taskRepository.save(Task.builder()
                .projectId(projectId)
                .title(title)
                .isCompleted(false)
                .build());
    }

    // 3. 완료 토글
    @Transactional
    public void toggleTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("할 일 없음"));
        task.setCompleted(!task.isCompleted());
    }

    // 4. 삭제
    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    // [유틸] 해당 Task가 속한 Project ID 찾기 (리다이렉트용)
    @Transactional(readOnly = true)
    public Long findProjectIdByTaskId(Long taskId) {
        return taskRepository.findById(taskId)
                .map(Task::getProjectId)
                .orElse(1L); // 없으면 기본값
    }

    // 날짜 퍼센트 계산
    private double calculateTimeProgress(LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(start)) return 0.0;
        if (today.isAfter(end)) return 100.0;

        long totalDays = ChronoUnit.DAYS.between(start, end);
        long passedDays = ChronoUnit.DAYS.between(start, today);

        if (totalDays <= 0) return 100.0;
        return ((double) passedDays / totalDays) * 100.0;
    }
}