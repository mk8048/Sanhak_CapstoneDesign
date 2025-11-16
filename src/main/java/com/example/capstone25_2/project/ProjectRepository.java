package com.example.capstone25_2.project;

import com.example.capstone25_2.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long > {
    List<Project> findAllByMemberIdsContaining(String memberId);
    Optional<Project> findByProjectNameAndMemberIdsContaining(String projectName, String memberId);
}
