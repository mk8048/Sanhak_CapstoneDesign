package com.example.capstone25_2.ganttTask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GanttTaskRepository extends JpaRepository<GanttTask, Long> {

    List<GanttTask> findByProjectId(Long projectId);

}