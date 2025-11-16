package com.example.capstone25_2.project.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class AddProjectRequest {

    private String projectName;
    private String description;
    private LocalDateTime createdAt;


    //@getter 대신
    public String getProjectName() {
        return projectName;
    }
    public String getDescription() {
        return description;
    }

    //@setter 대신
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
