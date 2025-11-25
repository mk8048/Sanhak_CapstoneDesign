package com.example.capstone25_2.meeting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByTitleContainingOrContentContainingOrSummaryContaining(String title, String content,
            String summary);
}