package com.example.capstone25_2.user;

import com.example.capstone25_2.user.dto.UserFindIdRequestDto;
import com.example.capstone25_2.user.dto.UserFindPsRequestDto;
import com.example.capstone25_2.user.dto.UserLoginRequestDto;
import com.example.capstone25_2.user.dto.UserSignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    // ⭐️ (향후 추가) private final PasswordEncoder passwordEncoder;

    // -- 회원가입 --
    @Transactional
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

    // -- 로그인 --
    public User login(UserLoginRequestDto dto) {
        Optional<User> userOptional = userRepository.findById(dto.getId());

        if(userOptional.isEmpty()) {
            System.err.println("loginFailed : not exist " + dto.getId());
            throw new IllegalArgumentException("아이디가 존재하지 않습니다.");
        }

        User user = userOptional.get();

        if(!user.getPassword().equals(dto.getPs())){
            System.err.println("loginFailed : password not match.");
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    // -- 아이디 찾기 --
    public User findId(UserFindIdRequestDto dto) {
        Optional<User> userOptional = userRepository.findByNameAndEmail(dto.getName(),dto.getEmail());

        if(userOptional.isEmpty()) {
            System.err.println("findIdFailed : not exist");
            throw new IllegalArgumentException("입력하신 정보와 일치하는 계정이 없습니다.");
        }

        User user = userOptional.get();

        return user;
    }

    // -- 비밀번호 찾기 --
    public User findPs(UserFindPsRequestDto dto) {
        Optional<User> userOptional = userRepository.findByIdAndNameAndEmail(
                dto.getId(), dto.getName(), dto.getEmail());

        if(userOptional.isEmpty()) {
            System.err.println("findPsFailed : not exist");
            throw new IllegalArgumentException("입력하신 정보와 일치하는 계정 없습니다.");
        }

        User user = userOptional.get();

        return user;
    }

}
