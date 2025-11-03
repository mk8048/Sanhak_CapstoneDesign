package com.example.capstone25_2.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    // 1. 수정할 정보 필드 (signup.html 폼 기준)
    private String name;
    private String email;
    private String nickname;
    private String phone;
    private String location;
    private String job;
    private String purpose;
    private String githubUrl;
    private String profileImageUrl;

    // 2. 선택적 비밀번호 변경 필드
    private String newPassword;
    private String newPasswordConfirm;

}
