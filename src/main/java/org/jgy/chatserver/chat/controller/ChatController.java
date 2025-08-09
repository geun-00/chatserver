package org.jgy.chatserver.chat.controller;

import lombok.RequiredArgsConstructor;
import org.jgy.chatserver.chat.dto.ChatRoomListResponseDto;
import org.jgy.chatserver.chat.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        chatService.createGroupRoom(roomName, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * 그룹 채팅 목록 조회
     */
    @GetMapping("/room/group/list")
    public ResponseEntity<?> getGroupChatRooms() {
        List<ChatRoomListResponseDto> chatRooms = chatService.getGroupChatRooms();

        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    /**
     * 그룹 채팅방 참여
     */
    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChatRoom(
            @PathVariable("roomId") Long roomId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        chatService.addParticipantToGroupChat(roomId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
