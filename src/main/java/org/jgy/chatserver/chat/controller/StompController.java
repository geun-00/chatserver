package org.jgy.chatserver.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jgy.chatserver.chat.dto.ChatMsgRequestDto;
import org.jgy.chatserver.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;

    /**
     * 방법1 - MessageMapping(수신)과 SendTo(topic에 메시지 전달) 한꺼번에 처리
     * <ul>
     *     <li>{@link DestinationVariable @DestinationVariable}
     *     MessageMapping 어노테이션으로 정의된 Websocket Controller 내에서만 사용한다.
     *     클라이언트에서 특정 publish/roomId 형태로 메시지를 발행 시 MessageMapping 수신
     *     </li>
     *     <li>{@link SendTo @SendTo}
     *         해당 roomId에 메시지를 발행하여 구독 중인 클라이언트에게 메시지 전송
     *     </li>
     * </ul>
     */
//    @MessageMapping("/{roomId}")
//    @SendTo("/topic/{roomId}")
    public String sendMessageOld(@DestinationVariable Long roomId, String message) {
        log.debug(message);

        return message;
    }

    /**
     * 방법2 - MessageMapping만 활용
     */
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMsgRequestDto chatMsgRequestDto) {
        log.debug(chatMsgRequestDto.message());
        chatService.saveMessage(roomId, chatMsgRequestDto);
        messageTemplate.convertAndSend("/topic/" + roomId, chatMsgRequestDto);
    }
}
