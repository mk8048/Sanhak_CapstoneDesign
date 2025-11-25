package com.example.capstone25_2.project;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project_members")
@Getter @Setter
@NoArgsConstructor
public class ProjectMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String userId;

    @Enumerated(EnumType.STRING)
    private ProjectRole role; // MEMBER or VIEWER

    public ProjectMember(Project project, String userId, ProjectRole role) {
        this.project = project;
        this.userId = userId;
        this.role = role;
    }
}