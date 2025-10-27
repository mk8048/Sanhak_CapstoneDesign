package com.example.capstone25_2.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserFindIdRequestDto {
    private String name;
    private String email;
}
