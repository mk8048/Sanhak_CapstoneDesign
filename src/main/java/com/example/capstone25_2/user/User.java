package com.example.capstone25_2.user;


import com.example.capstone25_2.user.dto.UserSignupRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk_id;

    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    //(선택) 나머지 필드들
    private String phone;
    private String location;
    private String job;
    private String purpose;
    private String nickname;
    private String githubUrl;
    private String profileImageUrl;

    // DB에서 자동으로 관리되는 필드들
    @Column(updatable = false) // 생성 시에만 값이 들어가도록 설정
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // DTO를 Entity로 변환하는 정적 팩토리 메서드 (Builder 패턴을 사용해도 좋습니다)
    public static User from(UserSignupRequestDto dto) {
        User user = new User();
        user.id = dto.getId();
        user.password = dto.getPassword(); // 🚨 (주의) 암호화 처리 필요!
        user.name = dto.getName();
        user.email = dto.getEmail();
        user.phone = dto.getPhone();
        user.location = dto.getLocation();
        user.job = dto.getJob();
        user.purpose = dto.getPurpose();
        user.nickname = dto.getNickname();
        user.githubUrl = dto.getGithubUrl();
        user.profileImageUrl = dto.getProfileImageUrl();

        return user;
    }
}
