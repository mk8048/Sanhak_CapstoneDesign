package com.example.capstone25_2.notification;

import com.example.capstone25_2.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "Notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //알림 id

    @ManyToOne
    @JoinColumn(name = "pk_id", nullable = false)
    private User recipient; //알림 받는 사람

    @Column(nullable = false)
    private String message; //알림 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type; //알림 종류 (memo_update, project_end)

    @Column(nullable = false)
    private boolean isRead = false; // 읽음 여부

    private String relatedUrl; //클릭 시 이동 url (ex: "/api/memo/{id}")

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreadte() {
        this.createdAt = LocalDateTime.now();
    }

    protected Notification() {}

    public static Notification of(User recipient, String message, NotificationType type, String relatedUrl) {

        Notification notification = new Notification();

        notification.recipient = recipient;
        notification.message = message;
        notification.type = type;
        notification.relatedUrl = relatedUrl;

        return notification;
    }

    public enum NotificationType {
        MEMO_CREATED,
        MEMO_UPDATED,
        PROJECT_DEADLINE_IMMINENT, // 프로젝트 마감 임박
        FOCUS_MODE_STARTED // 집중 모드 시작
    }
}