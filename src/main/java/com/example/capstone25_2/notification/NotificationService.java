package com.example.capstone25_2.notification;

import com.example.capstone25_2.project.ProjectRepository;
import com.example.capstone25_2.project.ProjectService;
import com.example.capstone25_2.user.User;
import com.example.capstone25_2.user.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.capstone25_2.project.Project;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private final ProjectService projectService;

    public NotificationService(NotificationRepository notificationRepository, ProjectRepository projectRepository, ProjectService projectService, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectService = projectService;
    }

//    @Async
//    @EventListener
//    public void handleMemoEvent(MemoEvent memoEvent) {
//        Memo memo = event.getMemo();
//        // 프로젝트 멤버 조회
//        List<User> recipients = projectRepository.findUserByprojectId;
//
//        String message;
//        Notification.NotificationType type;
//
//        if (event.getEventType() == MemoEvent.EventType.CREATED) {
//            message = "새로운 메모가 추가되었습니다. :" + memo.getContent();
//            type = Notification.NotificationType.MEMO_CREATED;
//        } else {
//            message = "메모가 수정되었습니다. :" + memo.getContent();
//            type = Notification.NotificationType.MEMO_UPDATED;
//        }
//
//        String url = "/project/" + memo.getProject_id() + "/memos/" + memo.getId();
//
//        for (User recipient : recipients) {
//            Notification notification = Notification.of(recipient, message, type, url);
//            notificationRepository.save(notification);
//
//        }
//    }

    @Async
    @EventListener
    @Transactional
    public void handleProjectDeadlineEvent(ProjectDeadlineEvent event) {

        Project project = event.getProject();
        int daysleft = event.getDaysUntilDeadline(); // 이벤트에서 남은 날짜 가져오기

        List<User> recipients = projectService.getProjectMembers(project.getProjectId());

        // 동적 변경
        String message = "프로젝트 '" + project.getProjectName() + "의 마감 기한이 " + daysleft + "일 남았습니다!";

        String url = "/projects/" + project.getProjectId();

        for (User recipient : recipients) {
            Notification notification = Notification.of(recipient, message, Notification.NotificationType.PROJECT_DEADLINE_IMMINENT, url);
            notificationRepository.save(notification);
            // pushService.sendNotification(recipient, notification); // 5단계
        }
    }

//    @Async
//    @EventListener
//    public void handleFocusModeEvent(FocusModeEvent event) {
//        User eventUser = event.getUser();
//        if (!event.isStarted()) return; // 집중 모드 시작 시에만 알림
//
//        // 사용자가 참여하고 있는 모든 프로젝트의 멤버들에게 알림
//        List<Project> projects = projectMemberRepository.findProjectsByUserId(eventUser.getId());
//        for (Project project : projects) {
//            List<User> recipients = projectMemberRepository.findUsersByProjectId(project.getId());
//            recipients.remove(eventUser); // 자기 자신은 제외
//
//            String message = "팀원 '" + eventUser.getName() + "'님이 집중 모드를 시작했습니다.";
//            String url = "/projects/" + project.getId() + "/members/" + eventUser.getId();
//
//            for (User recipient : recipients) {
//                Notification notification = Notification.of(recipient, message, NotificationType.FOCUS_MODE_STARTED, url);
//                notificationRepository.save(notification);
//                // pushService.sendNotification(recipient, notification); // 5단계
//            }
//        }
//    }


    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findAllByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
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

        List<Notification> unreadList = notificationRepository.findAllByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);

        // 벌크 연산으로 최적화할 수도 있지만, JPA Dirty Checking 사용 시 반복문
        for (Notification notification : unreadList) {
            notification.setRead(true);
        }
    }
}