package com.example.capstone25_2.repository;

import com.example.capstone25_2.domain.GanttTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GanttTaskRepository extends JpaRepository<GanttTask, Long> {



}