package com.example.capstone25_2.task;

import com.example.capstone25_2.task.ProjectSchedule;
import com.example.capstone25_2.task.Task;
import com.example.capstone25_2.task.dto.TaskProgressDTO;
import com.example.capstone25_2.task.ProjectScheduleRepository;
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
    private final ProjectScheduleRepository scheduleRepository;

    // 1. 진행률 및 목록 조회
    @Transactional(readOnly = true)
    public TaskProgressDTO getProgress(Long projectId) {
        // (1) 예상 진행률 (날짜 기준)
        ProjectSchedule schedule = scheduleRepository.findById(projectId)
                .orElse(new ProjectSchedule(projectId, LocalDate.now(), LocalDate.now())); // 없으면 오늘로 퉁침

        double timeProgress = calculateTimeProgress(schedule.getStartDate(), schedule.getEndDate());

        // (2) 실제 진행률 (Task 기준)
        long total = taskRepository.countByProjectId(projectId);
        long completed = taskRepository.countByProjectIdAndIsCompletedTrue(projectId);
        double actualProgress = (total == 0) ? 0.0 : ((double) completed / total) * 100.0;

        // (3) 할 일 목록 가져오기
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        return new TaskProgressDTO(
                projectId,
                Math.round(timeProgress * 10) / 10.0,   // 소수점 한자리 반올림
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

    // 3. 할 일 완료 토글
    @Transactional
    public void toggleTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("할 일이 없습니다."));
        task.setCompleted(!task.isCompleted());
    }

    // 날짜 계산 로직
    private double calculateTimeProgress(LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(start)) return 0.0;
        if (today.isAfter(end)) return 100.0;

        long totalDays = ChronoUnit.DAYS.between(start, end);
        long passedDays = ChronoUnit.DAYS.between(start, today);

        if (totalDays <= 0) return 100.0;
        return ((double) passedDays / totalDays) * 100.0;
    }

    // [테스트용 데이터 자동 생성] - 서버 켜질 때 1번 프로젝트 데이터 만듦
    @PostConstruct
    public void initData() {
        // 1번 프로젝트: 10일 전 시작 ~ 10일 후 종료 (딱 50% 진행 시점)
        if(!scheduleRepository.existsById(1L)) {
            scheduleRepository.save(new ProjectSchedule(1L, LocalDate.now().minusDays(10), LocalDate.now().plusDays(10)));

            taskRepository.save(Task.builder().projectId(1L).title("요구사항 분석").isCompleted(true).build());
            taskRepository.save(Task.builder().projectId(1L).title("DB 설계").isCompleted(true).build());
            taskRepository.save(Task.builder().projectId(1L).title("API 개발").isCompleted(false).build());
            taskRepository.save(Task.builder().projectId(1L).title("화면 구현").isCompleted(false).build());
        }
    }

    // 4. 할 일 삭제
    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}