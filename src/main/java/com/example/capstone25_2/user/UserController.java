package com.example.capstone25_2.user;

import com.example.capstone25_2.user.dto.UserSignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // -- 로그인 --
    @GetMapping({"/user/login", "/"})
    public String loginPage() {
        System.err.println("loginPage");
        return "user/login";
    }

    @GetMapping("/user/login-success")
    public String loginProcess(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String ps
    ) {
        System.err.println("loginProcess");
        return "redirect:/main";
    }

    // -- 회원가입 --
    @GetMapping("/user/signup")
    public String signupPage() {
        return "user/signup";
    }

    @PostMapping("/user/signup")
    public String signupProcess(@ModelAttribute UserSignupRequestDto requestDto) {

        try {
            userService.signup(requestDto);

            // ⭐️ 회원가입 성공 시 로그인 페이지로 리다이렉트
            return "redirect:/user/login";

        } catch (IllegalArgumentException | IllegalStateException e) {
            // ⭐️ 실패 시 다시 회원가입 페이지로 (추후 에러 메시지 전달 로직 필요)
            System.err.println("회원가입 실패: " + e.getMessage());
            // (나중에) model.addAttribute("error", e.getMessage());
            return "user/signup";
        }
    }

}
