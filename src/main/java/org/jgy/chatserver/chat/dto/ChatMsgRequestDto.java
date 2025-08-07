package org.jgy.chatserver.chat.dto;

public record ChatMsgRequestDto(
        String message,
        String senderEmail) {

}
