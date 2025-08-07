package org.jgy.chatserver.chat.controller;

import lombok.RequiredArgsConstructor;
import org.jgy.chatserver.chat.service.ChatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
}
