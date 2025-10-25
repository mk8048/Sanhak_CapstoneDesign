package com.example.capstone25_2.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @GetMapping({"/user/login", "/"})
    public String loginPage() {
        System.err.println("loginPage");
        return "user/login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "user/signup";
    }

    @GetMapping("/user/login-success")
    public String loginProcess(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String ps
    ) {
        System.out.println("로그인 중...");
        System.err.println("loginProcess");
        return "redirect:/main";
    }
}
