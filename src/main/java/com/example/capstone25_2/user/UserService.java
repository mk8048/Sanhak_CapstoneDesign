package com.example.capstone25_2.user;

import com.example.capstone25_2.user.dto.UserSignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    // ⭐️ (향후 추가) private final PasswordEncoder passwordEncoder;

    @Transactional // ⭐️ 데이터베이스에 변경 사항이 생길 때(C, U, D) 붙여주는 것이 좋습니다.
    public User signup(UserSignupRequestDto dto) {

        // 1. 유효성 검증
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }
        if (userRepository.existsById(dto.getId())) {
            throw new IllegalStateException("이미 존재하는 사용자 ID입니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        // 2. DTO -> Entity 변환
        User newUser = User.from(dto);

        // 3. (향후 필수) 비밀번호 암호화
        // String encodedPassword = passwordEncoder.encode(dto.getPassword());
        // newUser.setPassword(encodedPassword); // 암호화된 비밀번호로 설정

        // 4. DB에 저장 (save 메서드는 저장된 Entity를 반환)
        return userRepository.save(newUser);
    }
}
