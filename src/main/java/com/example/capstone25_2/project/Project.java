package com.example.capstone25_2.project;

import com.example.capstone25_2.project.dto.AddProjectRequest;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@SuppressWarnings({"LombokGetterMayBeUsed"})
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prj_id;

    @Column(nullable = false, length = 100)
    private String prj_name;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private Long users_id;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    //DTO를 Entity로 변환하는 정적 팩토리 메서드
    public static Project from(AddProjectRequest request) {
        Project project = new Project();

        project.prj_name = request.getPrjName();
        project.description = request.getDescription();
        project.users_id = request.getUsersId();

        return project;
    }


    protected Project() {
    }

    // Getters
    public Long getPrjId() {
        return prj_id;
    }

    public String getPrjName() {
        return prj_name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getUsersId() {
        return users_id;
    }
}