package com.example.capstone25_2.project;

import com.example.capstone25_2.project.dto.AddProjectRequest;
import com.example.capstone25_2.project.dto.UpdateProjectRequest;
import jakarta.persistence.*;
import lombok.Getter; // ⭐️ Lombok 적용
import lombok.Setter; // ⭐️ Lombok 적용
import lombok.NoArgsConstructor; // ⭐️ Lombok 적용
import lombok.AccessLevel; // ⭐️ Lombok 적용

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    // 프로젝트 소유자 ID (Owner PK)
    @Column(nullable = true)
    private Long usersId;

    // 프로젝트 참여자 목록 (ElementCollection)
    @ElementCollection
    @CollectionTable(name = "project_members", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "member_id")
    private Set<String> memberIds = new HashSet<>();


    @PrePersist // 엔티티 저장 전 호출
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // DTO를 Entity로 변환하는 정적 팩토리 메서드
    public static Project from(AddProjectRequest request) {
        Project project = new Project();

        project.setProjectName(request.getProjectName());
        project.setDescription(request.getDescription());

        return project;
    }

    // 정보 수정 메서드
    public void update(UpdateProjectRequest request) {
        this.setProjectName(request.getProjectName());
        this.setDescription(request.getDescription());
        // usersId는 소유자이므로 이 메서드에서 변경하지 않는 것이 일반적입니다.
    }

    public void removeMember(String userId) {
        this.memberIds.remove(userId);
    }

    public void addMember(String userId) {
        this.memberIds.add(userId);
    }

}