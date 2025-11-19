package com.example.capstone25_2.chat;

import com.example.capstone25_2.chat.dto.ChatRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public void createChat(@RequestBody ChatRequestDto requestDto) {
        chatService.recordChat(requestDto);
    }

    @GetMapping
    public List<ChatMessage> getAllChats() {
        return chatService.getAllChats();
    }
}
