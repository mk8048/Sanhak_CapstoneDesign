package com.example.capstone25_2.notification;

import com.example.capstone25_2.memo.Memo;
import com.example.capstone25_2.memo.MemoEvent;
import com.example.capstone25_2.project.ProjectRepository;
import com.example.capstone25_2.project.ProjectService;
import com.example.capstone25_2.user.User;
import com.example.capstone25_2.user.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.capstone25_2.project.dto.ProjectMemberDto;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.capstone25_2.project.Project;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final SimpMessagingTemplate messagingTemplate; // 웹소켓 발송 도구 주입

    @Async
    @EventListener
    public void handleMemoEvent(MemoEvent memoEvent) {
        Memo memo = memoEvent.getMemo();
        // Memo에서 프로젝트 ID 가져오기
        Long projectId = memo.getProjectId();

        List<User> recipients = projectService.getProjectMembers(projectId).stream()
                .map(ProjectMemberDto::getUser) // DTO -> User 변환
                .collect(Collectors.toList());

        String message;

        Notification.NotificationType type;

        // 이벤트 타입에 따른 메시지 설정
        if (memoEvent.getEventType() == MemoEvent.EventType.CREATED) {
            message = "새로운 메모가 추가되었습니다: " + memo.getContent();
            type = Notification.NotificationType.MEMO_CREATED;
        } else {
            message = "메모가 수정되었습니다: " + memo.getContent();
            type = Notification.NotificationType.MEMO_UPDATED;
        }

        // [MODIFIED] URL을 /memo로 변경
        String url = "/memo";

        for (User recipient : recipients) {
            // 작성자 본인(AuthorId)에게는 알림을 보내지 않음
            if (recipient.getId().equals(memo.getAuthorId())) {
                continue;
            }

            // 1. DB 저장
            Notification notification = Notification.of(recipient, message, type, url);
            notificationRepository.save(notification);

            // 2. 실시간 웹소켓 전송 (배지 카운트 업데이트)
            long unreadCount = notificationRepository.countByRecipientAndIsReadFalse(recipient);
            messagingTemplate.convertAndSend("/sub/notifications/" + recipient.getId(), unreadCount);
        }
    }

    @Async
    @EventListener
    @Transactional
    public void handleProjectDeadlineEvent(ProjectDeadlineEvent event) {

        Project project = event.getProject();
        int daysleft = event.getDaysUntilDeadline(); // 이벤트에서 남은 날짜 가져오기

        List<User> recipients = projectService.getProjectMembers(project.getProjectId()).stream()
                .map(ProjectMemberDto::getUser) // DTO -> User 변환
                .collect(Collectors.toList());

        // 동적 변경
        String message = "프로젝트 '" + project.getProjectName() + "의 마감 기한이 " + daysleft + "일 남았습니다!";

        String url = "/projects/" + project.getProjectId();

        for (User recipient : recipients) {
            Notification notification = Notification.of(recipient, message,
                    Notification.NotificationType.PROJECT_DEADLINE_IMMINENT, url);
            notificationRepository.save(notification);
            // pushService.sendNotification(recipient, notification); // 5단계

            long unreadCount = notificationRepository.countByRecipientAndIsReadFalse(recipient);

            // 구독 경로: /sub/notifications/{userId}
            messagingTemplate.convertAndSend("/sub/notifications/" + recipient.getId(), unreadCount);
        }
    }

    // @Async
    // @EventListener
    // public void handleFocusModeEvent(FocusModeEvent event) {
    // User eventUser = event.getUser();
    // if (!event.isStarted()) return; // 집중 모드 시작 시에만 알림
    //
    // // 사용자가 참여하고 있는 모든 프로젝트의 멤버들에게 알림
    // List<Project> projects =
    // projectMemberRepository.findProjectsByUserId(eventUser.getId());
    // for (Project project : projects) {
    // List<User> recipients =
    // projectMemberRepository.findUsersByProjectId(project.getId());
    // recipients.remove(eventUser); // 자기 자신은 제외
    //
    // String message = "팀원 '" + eventUser.getName() + "'님이 집중 모드를 시작했습니다.";
    // String url = "/projects/" + project.getId() + "/members/" +
    // eventUser.getId();
    //
    // for (User recipient : recipients) {
    // Notification notification = Notification.of(recipient, message,
    // NotificationType.FOCUS_MODE_STARTED, url);
    // notificationRepository.save(notification);
    // // pushService.sendNotification(recipient, notification); // 5단계
    // }
    // }
    // }

    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findAllByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    // 모든 알림 조회
    public List<Notification> getAllNotifications(User user) {
        return notificationRepository.findAllByRecipientOrderByCreatedAtDesc(user);
    }

    // 1. 단일 알림 읽음 처리
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        // 이미 읽은 상태라면 로직 수행 X (최적화)
        if (!notification.isRead()) {
            notification.setRead(true); // Entity에 setIsRead 메서드 필요
        }
    }

    // 2. 특정 사용자의 모든 알림 읽음 처리
    public void markAllAsRead(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Notification> unreadList = notificationRepository
                .findAllByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);

        // 벌크 연산으로 최적화할 수도 있지만, JPA Dirty Checking 사용 시 반복문
        for (Notification notification : unreadList) {
            notification.setRead(true);
        }
    }
}