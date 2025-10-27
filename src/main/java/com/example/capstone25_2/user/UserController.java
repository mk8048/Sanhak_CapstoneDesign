package com.example.capstone25_2.user;

import com.example.capstone25_2.user.dto.UserFindIdRequestDto;
import com.example.capstone25_2.user.dto.UserFindPsRequestDto;
import com.example.capstone25_2.user.dto.UserLoginRequestDto;
import com.example.capstone25_2.user.dto.UserSignupRequestDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // -- 로그인 --
    @GetMapping({"/user/login", "/"})
    public String loginPage(Model model) {
        System.err.println("loginPage");
        return "user/login";
    }

    @PostMapping("/user/login")
    public String loginProcess(
            @ModelAttribute UserLoginRequestDto requestDto,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            User loggedInUser = userService.login(requestDto);

            session.setAttribute("userName", loggedInUser.getName());
            session.setAttribute("userNickname", loggedInUser.getNickname());
            session.setAttribute("userId", loggedInUser.getId());

            redirectAttributes.addFlashAttribute(
                    "successMessage", loggedInUser.getName() + "님 환영합니다!");
            System.err.println("login Success" + loggedInUser.getId());
            return "redirect:/main";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("loginError", e.getMessage());
            redirectAttributes.addFlashAttribute("id", requestDto.getId());
            System.err.println("login Error");

            return "redirect:/user/login";
        }
    }

    // -- 회원가입 --
    @GetMapping("/user/signup")
    public String signupPage(
            Model model,
            @ModelAttribute UserSignupRequestDto userDto
    ) {
        System.err.println("signupPage");
        return "user/signup";
    }

    @PostMapping("/user/signup")
    public String signupProcess(@ModelAttribute UserSignupRequestDto requestDto, RedirectAttributes redirectAttributes) {

        try {
            userService.signup(requestDto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "회원가입이 완료되었습니다! 로그인해 주세요.");
            System.err.println("Signup Success");
            return "redirect:/user/login";

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Signup Error");
            redirectAttributes.addFlashAttribute("signupError", e.getMessage());
            redirectAttributes.addFlashAttribute("userDto", requestDto);

            return "redirect:/user/signup";
        }
    }

    // -- 로그아웃 --
    @PostMapping("/user/logout")
    public String logoutProcess(HttpSession session, RedirectAttributes redirectAttributes) {

        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "logout Success");
        return "redirect:/user/login";
    }

    // -- 아이디/비밀번호 찾기 --
    @GetMapping("/user/find-credentials")
    public String findCredentials(Model model) {
        System.err.println("findCredentialsPage");
        return "/user/find_credentials";
    }

    // -- 아이디 찾기 처리 --
    @PostMapping("/user/find-id")
    public String findIdProcess(@ModelAttribute UserFindIdRequestDto requestDto, Model model) {

        try {
            User foundUser = userService.findId(requestDto);

            String successMsg = "찾으신 아이디는 [" + foundUser.getId() + "] 입니다.";
            model.addAttribute("findErrorResult", successMsg);
            model.addAttribute("isSuccess", true);

            System.err.println("FindId Success: " + foundUser.getId());

            return "user/find_credentials";

        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("findErrorResult", e.getMessage());
            model.addAttribute("findIdDto", requestDto);
            model.addAttribute("isSuccess", false);

            System.err.println("FindId Error: " + e.getMessage());
            return "user/find_credentials";
        }
    }

    // -- 비밀번호 찾기 처리 --
    @PostMapping("/user/find-ps")
    public String findPsProcess(@ModelAttribute UserFindPsRequestDto requestDto, Model model) {

        try {
            User foundUser = userService.findPs(requestDto);

            String successMsg = "찾으신 비밀번호는 [" + foundUser.getPassword() + "] 입니다.";
            model.addAttribute("findErrorResult", successMsg);
            model.addAttribute("isSuccess", true);

            System.err.println("FindPs Success");
            return "user/find_credentials";

        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("findErrorResult", e.getMessage());
            model.addAttribute("findPsDto", requestDto);
            model.addAttribute("isSuccess", false);

            System.err.println("FindPs Error: " + e.getMessage());
            return "user/find_credentials";
        }
    }


}
