package com.example.capstone25_2.notification;

import com.example.capstone25_2.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // save();

    // 읽지 않은 알림을 최신순으로 조회
    List<Notification> findAllByRecipientAndIsReadFalseOrderByCreatedAtDesc(User user);

    // 읽지 않은 알림 개수만 조회 (배지 표시용 가벼운 쿼리)
    long countByRecipientAndIsReadFalse(User user);
}