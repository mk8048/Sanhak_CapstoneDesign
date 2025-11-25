package com.example.capstone25_2;

// 프로젝트 내의 모든 @Controller가 view를 랜더링하기 전에 이 코드를 먼저 실행

import com.example.capstone25_2.notification.Notification;
import com.example.capstone25_2.notification.NotificationService;
import com.example.capstone25_2.user.User;
import com.example.capstone25_2.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@Slf4j
@ControllerAdvice // 모든 컨트롤러에 적용됨
@RequiredArgsConstructor
public class GlobalDataController {

    private final UserService userService;
    private final NotificationService notificationService;

    // 모든 요청에 대해 'notifications'와 'unreadCount'를 모델에 담음
    @ModelAttribute
    public void addGlobalAttributes(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");

        // 비로그인 상태면 아무것도 하지 않고 리턴 (빠른 종료)
        if (userId == null) {
            return;
        }

        try {
            // 1. 사용자 정보 조회
            User user = userService.findById(userId);

            // 2. 알림 조회
            List<Notification> notifications = notificationService.getUnreadNotifications(user);

            // 3. 모델 담기
            model.addAttribute("globalNotifications", notifications);
            model.addAttribute("unreadCount", notifications.size());

            // 4. displayName 주입 (닉네임/이름 우선순위)
            String userName = (String) session.getAttribute("userName");
            String userNickname = (String) session.getAttribute("userNickname");

            if (userName != null) {
                String displayName;
                if (userNickname != null && !userNickname.isEmpty()) {
                    displayName = userNickname;
                } else {
                    displayName = userName;
                }
                model.addAttribute("displayName", displayName);
            }

            // 5. currentProjectId 주입
            Long projectId = (Long) session.getAttribute("currentProjectId");
            if (projectId != null) {
                model.addAttribute("currentProjectId", projectId);
            }

        } catch (IllegalArgumentException e) {
            // Case: 세션에는 ID가 있는데 DB에 유저가 없는 경우 (탈퇴 등)
            log.warn("GlobalDataController - 유효하지 않은 사용자 세션 ID: {}", userId);

            // 보안상 세션을 정리하는 것이 좋음
            session.invalidate();

            // 빈 리스트 전달하여 에러 페이지 방지
            model.addAttribute("globalNotifications", Collections.emptyList());
            model.addAttribute("unreadCount", 0);

        } catch (Exception e) {
            // Case: DB 연결 오류, 기타 런타임 예외 등
            log.error("GlobalDataController - 알림 로드 중 알 수 없는 오류 발생", e);

            // 에러가 났더라도 페이지 내용은 보여줘야 하므로 빈 값 처리
            model.addAttribute("globalNotifications", Collections.emptyList());
            model.addAttribute("unreadCount", 0);
        }
    }
}
