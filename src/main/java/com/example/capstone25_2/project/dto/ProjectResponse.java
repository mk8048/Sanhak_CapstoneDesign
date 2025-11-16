package com.example.capstone25_2.project.dto;

import com.example.capstone25_2.project.Project;

import java.time.LocalDateTime;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class ProjectResponse {
    private final Long projectId;
    private final String projectName;
    private final String description;
    private final LocalDateTime createdAt;
    private final Long usersId;
    private final String ownerLoginId;

    public ProjectResponse(Project project, String ownerLoginId) {
        this.projectId = project.getProjectId();
        this.projectName = project.getProjectName();
        this.description = project.getDescription();
        this.createdAt = project.getCreatedAt();
        this.usersId = project.getUsersId();
        this.ownerLoginId = ownerLoginId;
    }

    //@getter 대신
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
    public String getOwnerLoginId() {return ownerLoginId;}
}
