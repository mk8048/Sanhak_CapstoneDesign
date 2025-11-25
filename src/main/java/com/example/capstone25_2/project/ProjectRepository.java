package com.example.capstone25_2.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOrderByCreatedAtDesc();

    List<Project> findDistinctByMembers_UserId(String userId);

    Optional<Project> findByProjectNameAndMembers_UserId(String projectName, String userId);

    Optional<Project> findByProjectName(String projectName);

    List<Project> findByDeadline(LocalDate deadline);

    // 검색용 메서드
    List<Project> findByProjectNameContainingOrDescriptionContaining(String name, String description);
}
