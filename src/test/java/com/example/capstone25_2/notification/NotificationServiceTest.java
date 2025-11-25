package com.example.capstone25_2.notification;

import com.example.capstone25_2.memo.Memo;
import com.example.capstone25_2.memo.MemoEvent;
import com.example.capstone25_2.project.ProjectService;
import com.example.capstone25_2.user.User;
import com.example.capstone25_2.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.given;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 환경 활성화
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    @DisplayName("메모 생성 알림: 작성자는 제외하고 팀원들에게만 알림과 웹소켓 메시지를 전송한다")
    void handleMemoEvent_shouldNotifyMembers_ExcludingAuthor() {
        // given
        Long projectId = 100L;
        String authorId = "user_author"; // 작성자 ID (String)
        String memberId = "user_member"; // 받는 사람 ID (String)
        Long memoId = 1L;
        String content = "회의록 정리";

        // 1. Mock Memo 객체 생성 (Reflection이나 Setter 사용 가정이 필요할 수 있음. 여기서는 Mocking으로 처리하거나 생성자 활용)
        // 실제 엔티티에 Setter가 없다면 테스트용 생성자나 Builder가 필요하지만,
        // 여기서는 spy나 mock을 사용하여 getter 값을 조정합니다.
        Memo memo = mock(Memo.class);
        given(memo.getId()).willReturn(memoId);
        given(memo.getProjectId()).willReturn(projectId);
        given(memo.getAuthorId()).willReturn(authorId); // String 타입 반환
        given(memo.getContent()).willReturn(content);

        // 2. MemoEvent 생성
        MemoEvent event = new MemoEvent(memo, MemoEvent.EventType.CREATED);

        // 3. User(작성자, 멤버) 생성
        User author = mock(User.class);
        given(author.getId()).willReturn(authorId); // getId() 호출 시 "user_author" 반환

        User member = mock(User.class);
        given(member.getId()).willReturn(memberId);

        // 4. ProjectService 동작 정의 (작성자와 멤버 둘 다 프로젝트에 있음)
        given(projectService.getProjectMembers(projectId)).willReturn(Arrays.asList(author, member));

        // 5. 알림 개수 조회 Mocking
        given(notificationRepository.countByRecipientAndIsReadFalse(member)).willReturn(5L);

        // when
        notificationService.handleMemoEvent(event);

        // then

        // 검증 1: 작성자(author)에게는 알림이 저장되지 않아야 함
        verify(notificationRepository, never()).save(argThat(notification ->
                notification.getRecipient().getId().equals(authorId)
        ));

        // 검증 2: 멤버(member)에게는 알림이 저장되어야 함
        verify(notificationRepository, times(1)).save(argThat(notification ->
                notification.getRecipient().getId().equals(memberId) &&
                        notification.getType() == Notification.NotificationType.MEMO_CREATED &&
                        notification.getMessage().contains(content) &&
                        notification.getRelatedUrl().equals("/project/" + projectId + "/memos/" + memoId)
        ));

        // 검증 3: 멤버(member)에게 웹소켓 메시지가 전송되어야 함
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/sub/notifications/" + memberId),
                eq(5L)
        );

        // 검증 4: 작성자에게는 웹소켓 메시지도 전송되지 않아야 함 (로직상 loop 내부에 있으므로)
        verify(messagingTemplate, never()).convertAndSend(
                eq("/sub/notifications/" + authorId),
                anyLong()
        );
    }
}