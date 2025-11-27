package com.example.capstone25_2.task;

import com.example.capstone25_2.project.Project;
import com.example.capstone25_2.project.ProjectRepository;
import com.example.capstone25_2.project.ProjectService; // ⭐️ 추가
import com.example.capstone25_2.task.dto.TaskProgressDTO;
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
    private final ProjectService projectService; // ⭐️ ProjectService 주입

    // 1. 진행률 및 목록 조회 (수정 없음)
    @Transactional(readOnly = true)
    public TaskProgressDTO getProgress(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. ID=" + projectId));

        LocalDate startDate = (project.getCreatedAt() != null) ? project.getCreatedAt().toLocalDate() : LocalDate.now();
        LocalDate endDate = (project.getDeadline() != null) ? project.getDeadline() : LocalDate.now();

        double timeProgress = calculateTimeProgress(startDate, endDate);

        long total = taskRepository.countByProjectId(projectId);
        long completed = taskRepository.countByProjectIdAndIsCompletedTrue(projectId);
        double actualProgress = (total == 0) ? 0.0 : ((double) completed / total) * 100.0;

        List<Task> tasks = taskRepository.findByProjectId(projectId);

        return new TaskProgressDTO(
                projectId,
                Math.round(timeProgress * 10) / 10.0,
                Math.round(actualProgress * 10) / 10.0,
                tasks
        );
    }

    // 2. 할 일 추가 (권한 체크 추가)
    @Transactional
    public void addTask(Long projectId, String title, String userId) { // ⭐️ userId 추가
        // 🛑 쓰기 권한 검사 (VIEWER 차단)
        projectService.validateWriteAccess(projectId, userId);

        taskRepository.save(Task.builder()
                .projectId(projectId)
                .title(title)
                .isCompleted(false)
                .build());
    }

    // 3. 완료 토글 (권한 체크 추가)
    @Transactional
    public void toggleTask(Long taskId, String userId) { // ⭐️ userId 추가
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("할 일 없음"));

        // 🛑 쓰기 권한 검사 (VIEWER 차단)
        projectService.validateWriteAccess(task.getProjectId(), userId);

        task.setCompleted(!task.isCompleted());
    }

    // 4. 삭제 (권한 체크 추가)
    @Transactional
    public void deleteTask(Long taskId, String userId) { // ⭐️ userId 추가
        // taskId로 프로젝트 ID를 먼저 찾아야 함
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("할 일 없음"));

        // 🛑 쓰기 권한 검사 (VIEWER 차단)
        projectService.validateWriteAccess(task.getProjectId(), userId);

        taskRepository.deleteById(taskId);
    }

    // [유틸] 해당 Task가 속한 Project ID 찾기
    @Transactional(readOnly = true)
    public Long findProjectIdByTaskId(Long taskId) {
        return taskRepository.findById(taskId)
                .map(Task::getProjectId)
                .orElse(1L);
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