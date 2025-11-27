package com.example.capstone25_2.project.dto;

import com.example.capstone25_2.project.ProjectRole;
import com.example.capstone25_2.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjectMemberDto {
    private User user;          // 사용자 정보 (이름, ID 등)
    private ProjectRole role;   // 프로젝트 내 역할 (MEMBER, VIEWER)
    private boolean isOwner;    // 소유자 여부

    public ProjectMemberDto(User user, ProjectRole role, boolean isOwner) {
        this.user = user;
        this.role = role;
        this.isOwner = isOwner;
    }
}