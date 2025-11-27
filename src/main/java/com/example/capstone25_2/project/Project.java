package com.example.capstone25_2.project;

import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.UpdateProjectRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false, length = 100)
    private String projectName;

    @Lob @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDate deadline;

    // 👑 프로젝트 소유자 (유일한 관리자)
    @Column(nullable = true)
    private Long usersId;

    // 👥 나머지 멤버 목록
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static Project from(AddProjectRequest request) {
        Project project = new Project();
        project.setProjectName(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setDeadline(request.getDeadline());
        return project;
    }

    public void update(UpdateProjectRequest request) {
        this.projectName = request.getProjectName();
        this.description = request.getDescription();
        this.deadline = request.getDeadline();
    }

    // 멤버 추가 (기본 역할: MEMBER)
    public void addMember(String userId, ProjectRole role) {
        boolean exists = this.members.stream().anyMatch(m -> m.getUserId().equals(userId));
        if (!exists) {
            this.members.add(new ProjectMember(this, userId, role));
        }
    }

    // 멤버 제거
    public void removeMember(String userId) {
        this.members.removeIf(member -> member.getUserId().equals(userId));
    }

    // 멤버 역할 조회 헬퍼
    public Optional<ProjectRole> getMemberRole(String userId) {
        return this.members.stream()
                .filter(m -> m.getUserId().equals(userId))
                .map(ProjectMember::getRole)
                .findFirst();
    }

    // 소유자 설정
    public void setUsersId(Long usersId) {
        this.usersId = usersId;
    }
}