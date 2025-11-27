package com.example.capstone25_2.task;

import com.example.capstone25_2.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId); // 특정 프로젝트의 할 일 목록

    long countByProjectId(Long projectId); // 전체 개수

    long countByProjectIdAndIsCompletedTrue(Long projectId); // 완료된 개수

    // 검색용 메서드
    List<Task> findByTitleContaining(String title);

    void deleteByProjectId(Long projectId);
}