package com.example.capstone25_2.chat;

import com.example.capstone25_2.user.User;
import com.example.capstone25_2.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatPageController {

    private final UserService userService;

    @GetMapping("/chat/room")
    public String chatPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        String userNickname = (String) session.getAttribute("userNickname");
        String userName = (String) session.getAttribute("userName");

        // 닉네임 설정
        if (userNickname != null) {
            model.addAttribute("userNickname", userNickname);
        } else if (userName != null) {
            model.addAttribute("userNickname", userName);
        } else {
            model.addAttribute("userNickname", "익명");
        }

        // 프로필 이미지 설정
        if (userId != null) {
            try {
                User user = userService.findById(userId);
                model.addAttribute("profileImageUrl", user.getProfileImageUrl());
            } catch (Exception e) {
                // 사용자 조회 실패 시 예외 처리 (로그 남기기 등)
                System.err.println("ChatPageController: User not found for id " + userId);
            }
        }

        return "chat/chat";
    }
}