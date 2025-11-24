package com.example.capstone25_2.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String writer;

    private String profileImageUrl;

    private LocalDateTime createdAt;

    public ChatMessage(String content, String writer, String profileImageUrl) {
        this.content = content;
        this.writer = writer;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = LocalDateTime.now();
    }
}