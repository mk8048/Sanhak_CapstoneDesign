package com.example.capstone25_2.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long > {
    List<Project> findAllByOrderByCreatedAtDesc();
    List<Project> findAllByMemberIdsContaining(String memberId);
    Optional<Project> findByProjectNameAndMemberIdsContaining(String projectName, String memberId);

    List<Project> findByDeadline(LocalDate deadline);
}
