package org.jgy.chatserver.chat.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jgy.chatserver.chat.domain.ChatMessage;
import org.jgy.chatserver.chat.domain.ChatParticipant;
import org.jgy.chatserver.chat.domain.ChatRoom;
import org.jgy.chatserver.chat.domain.ReadStatus;
import org.jgy.chatserver.chat.dto.ChatMsgRequestDto;
import org.jgy.chatserver.chat.repository.ChatMessageRepository;
import org.jgy.chatserver.chat.repository.ChatParticipantRepository;
import org.jgy.chatserver.chat.repository.ChatRoomRepository;
import org.jgy.chatserver.chat.repository.ReadStatusRepository;
import org.jgy.chatserver.member.domain.Member;
import org.jgy.chatserver.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    /**
     * 채팅 메시지 저장
     *
     * @param roomId            채팅방 아이디
     * @param chatMsgRequestDto 메시지, 전송자 DTO
     */
    @Transactional
    public void saveMessage(Long roomId, ChatMsgRequestDto chatMsgRequestDto) {
        //1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                                              .orElseThrow(() -> new EntityNotFoundException("Cannot find ChatRoom for : " + roomId));
        //2. 보낸사람 조회
        Member sender = memberRepository.findByEmail(chatMsgRequestDto.senderEmail())
                                        .orElseThrow(() -> new EntityNotFoundException("Cannot find Member for : " + chatMsgRequestDto.senderEmail()));
        //3. 메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                                             .chatRoom(chatRoom)
                                             .member(sender)
                                             .content(chatMsgRequestDto.message())
                                             .build();

        chatMessageRepository.save(chatMessage);

        //4. 사용자별로 읽음 여부 저장
        List<ChatParticipant> chatParticipants = chatRoom.getParticipants();

        for (ChatParticipant participant : chatParticipants) {
            ReadStatus readStatus = ReadStatus.builder()
                                              .chatRoom(chatRoom)
                                              .member(participant.getMember())
                                              .chatMessage(chatMessage)
                                              .isRead(participant.getMember().equals(sender))
                                              .build();

            readStatusRepository.save(readStatus);
        }
    }
}
