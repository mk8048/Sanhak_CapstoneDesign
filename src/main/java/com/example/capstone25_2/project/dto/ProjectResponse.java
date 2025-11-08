package com.example.capstone25_2.project.dto;

import com.example.capstone25_2.project.Project;

import java.time.LocalDateTime;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class ProjectResponse {
    private final Long prj_id;
    private final String prj_name;
    private final String description;
    private final LocalDateTime createAt;
    private final Long users_id;

    public ProjectResponse(Project project) {
        this.prj_id = project.getPrjId();
        this.prj_name = project.getPrjName();
        this.description = project.getDescription();
        this.createAt = project.getCreatedAt();
        this.users_id = project.getUsersId();
    }

    //@getter 대신
    public Long getPrjId() {
        return prj_id;
    }
    public String getPrjName() {
        return prj_name;
    }
    public String getDescription() {
        return description;
    }
    public LocalDateTime getCreateAt() {
        return createAt;
    }
    public Long getUsersId() {
        return users_id;
    }
}
