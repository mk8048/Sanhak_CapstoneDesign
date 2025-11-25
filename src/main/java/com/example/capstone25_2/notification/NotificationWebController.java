package com.example.capstone25_2.notification;

import com.example.capstone25_2.user.User;
import com.example.capstone25_2.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationWebController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/notifications")
    public String notificationListPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }

        User user = userService.findById(userId);
        List<Notification> notifications = notificationService.getAllNotifications(user);

        model.addAttribute("notifications", notifications);
        model.addAttribute("pageTitle", "알림 내역");

        return "notification/list";
    }
}
