package com.example.capstone25_2.chat;

import com.example.capstone25_2.chat.dto.ChatRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatMessage recordChat(ChatRequestDto requestDto) {
        ChatMessage message = new ChatMessage(requestDto.getContent(), requestDto.getWriter(),
                requestDto.getProfileImageUrl());
        return chatRepository.save(message);
    }

    public List<ChatMessage> getAllChats() {
        return chatRepository.findAll();
    }
}