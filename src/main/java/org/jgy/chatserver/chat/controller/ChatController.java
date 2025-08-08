package org.jgy.chatserver.chat.controller;

import lombok.RequiredArgsConstructor;
import org.jgy.chatserver.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    /**
     * 그룹 채팅방 개설
     */
    @PostMapping("/room/group/create")
    public ResponseEntity<?> createGroupRoom(
            @RequestParam String roomName,
            @AuthenticationPrincipal Authentication authentication
    ) {
        chatService.createGroupRoom(roomName, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
