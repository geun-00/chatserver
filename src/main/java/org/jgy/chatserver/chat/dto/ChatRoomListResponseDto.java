package org.jgy.chatserver.chat.dto;

import org.jgy.chatserver.chat.domain.ChatRoom;

public record ChatRoomListResponseDto(
        Long roomId,
        String roomName)
{
    public static ChatRoomListResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomListResponseDto(
                chatRoom.getId(),
                chatRoom.getName()
        );
    }
}
