package com.example.capstone25_2.project.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class UpdateProjectRequest {
    private final String projectName;
    private final String description;
    private final Long usersId;

    @JsonCreator
    public UpdateProjectRequest(String projectName, String description, Long usersId) {
        this.projectName = projectName;
        this.description = description;
        this.usersId = usersId;
    }

    //@getter 대신
    public String getProjectName() {
        return projectName;
    }
    public String getDescription() {
        return description;
    }
    public Long getUsersId() {
        return usersId;
    }
}
