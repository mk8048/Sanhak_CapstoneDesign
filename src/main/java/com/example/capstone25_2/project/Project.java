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
    private Long usersId; // 프로젝트 생성자/소유자 ID

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // DTO를 Entity로 변환하는 정적 팩토리 메서드
    public static Project from(AddProjectRequest request) {
        Project project = new Project();

        project.projectName = request.getProjectName();
        project.description = request.getDescription();
        // usersId는 로그인 후 Service에서 따로 설정되므로 DTO에서 직접 받지 않습니다.
        // project.usersId = request.getUsersId(); // 이 줄은 Service에서 처리하므로 삭제 또는 주석 처리 권장

        return project;
    }

    public void update(UpdateProjectRequest request) {

        this.projectName = request.getProjectName();
        this.description = request.getDescription();
        // this.usersId = request.getUsersId(); // usersId는 일반적으로 update 시 DTO에서 받지 않습니다.
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

    public void setUsersId(Long usersId) {
        this.usersId = usersId;
    }
}