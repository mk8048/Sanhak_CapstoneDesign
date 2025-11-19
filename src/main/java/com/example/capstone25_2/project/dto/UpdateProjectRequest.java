package com.example.capstone25_2.project.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDate;

@SuppressWarnings({"LombokGetterMayBeUsed"})
public class UpdateProjectRequest {
    private final String projectName;
    private final String description;
    private final LocalDate deadline;

    @JsonCreator
    public UpdateProjectRequest(String projectName, String description, LocalDate deadline) {
        this.projectName = projectName;
        this.description = description;
        this.deadline = deadline;
    }

    //@getter 대신
    public String getProjectName() {
        return projectName;
    }
    public String getDescription() {
        return description;
    }
    public LocalDate getDeadline() {
        return deadline;
    }
}
