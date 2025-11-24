package com.example.capstone25_2.chat;

import com.example.capstone25_2.chat.dto.ChatRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/api/chat/send")
    public void sendByPost(@RequestBody ChatRequestDto requestDto) {
        ChatMessage savedMessage = chatService.recordChat(requestDto);

        messagingTemplate.convertAndSend("/topic/public", savedMessage);
    }

    @GetMapping("/api/chat")
    public List<ChatMessage> getAllChats() {
        return chatService.getAllChats();
    }
}