package com.example.capstone25_2.project.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
public class AddProjectRequest {

    private String prj_name;
    private String description;
    private LocalDateTime createdAt;
    private Long users_id;


    //@getter 대신
    public String getPrjName() {
        return prj_name;
    }
    public String getDescription() {
        return description;
    }
    public Long getUsersId() {
        return users_id;
    }

    //@setter 대신
    public void setPrj_name(String prj_name) {
        this.prj_name = prj_name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setUsers_id(Long users_id) {
        this.users_id = users_id;
    }

}
