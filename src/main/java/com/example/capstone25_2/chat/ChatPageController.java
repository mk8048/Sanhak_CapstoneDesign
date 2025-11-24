package com.example.capstone25_2.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatPageController {

    @GetMapping("/chat/room")
    public String chatPage() {
        return "chat/chat";
    }
}