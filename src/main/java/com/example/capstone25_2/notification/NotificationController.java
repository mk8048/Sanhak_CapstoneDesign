package com.example.capstone25_2.notification;

import com.example.capstone25_2.user.User; // UserDetails 등을 사용한다면 변경
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 단일 읽음 처리
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // 모두 읽음 처리
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build(); // 로그인 안됨
        }

        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
