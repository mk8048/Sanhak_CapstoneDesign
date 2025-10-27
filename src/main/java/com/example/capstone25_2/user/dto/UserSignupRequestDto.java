package com.example.capstone25_2.user.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignupRequestDto {
    
    //필수 항목
    private String id;
    private String password;
    private String passwordConfirm;
    private String name;
    private String email;

    //선택 항목
    private String phone;
    private String location;
    private String job;
    private String purpose;
    private String nickname;
    private String githubUrl;
    private String profileImageUrl;
}
