package com.example.capstone25_2.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long > {
    List<Project> findAllByOrderByCreatedAtDesc();
}
