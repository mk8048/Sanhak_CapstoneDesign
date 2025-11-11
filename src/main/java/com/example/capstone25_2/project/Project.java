package com.example.capstone25_2.project;

import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.UpdateProjectRequest;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@SuppressWarnings({"LombokGetterMayBeUsed"})
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false, length = 100)
    private String projectName;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private Long usersId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    //DTO를 Entity로 변환하는 정적 팩토리 메서드
    public static Project from(AddProjectRequest request) {
        Project project = new Project();

        project.projectName = request.getProjectName();
        project.description = request.getDescription();
        project.usersId = request.getUsersId();

        return project;
    }

    public void update(UpdateProjectRequest request) {

        this.projectName = request.getProjectName();
        this.description = request.getDescription();
        this.usersId = request.getUsersId();
    }


    protected Project() {
    }

    // Getters
    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getUsersId() {
        return usersId;
    }
}