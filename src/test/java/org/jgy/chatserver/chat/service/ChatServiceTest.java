package org.jgy.chatserver.chat.service;

import org.jgy.chatserver.chat.domain.ChatMessage;
import org.jgy.chatserver.chat.domain.ChatParticipant;
import org.jgy.chatserver.chat.domain.ChatRoom;
import org.jgy.chatserver.chat.domain.ReadStatus;
import org.jgy.chatserver.chat.dto.ChatMsgRequestDto;
import org.jgy.chatserver.chat.dto.ChatRoomListResponseDto;
import org.jgy.chatserver.chat.repository.ChatMessageRepository;
import org.jgy.chatserver.chat.repository.ChatRoomRepository;
import org.jgy.chatserver.chat.repository.ReadStatusRepository;
import org.jgy.chatserver.member.domain.Member;
import org.jgy.chatserver.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Transactional
class ChatServiceTest {

    @Autowired
    ChatService chatService;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    ReadStatusRepository readStatusRepository;

    @Test
    @DisplayName("채팅 메시지 저장 시 필요한 연관 관계를 정상적으로 매핑한다.")
    void saveMessage() {
        //given
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                                                            .name("my-chat")
                                                            .build()
        );
        Member sender = memberRepository.save(Member.builder()
                                                    .name("Francisca Souza")
                                                    .email("sender@gmail.com")
                                                    .password("my-password")
                                                    .build()
        );

        ChatMsgRequestDto chatMsgRequestDto = new ChatMsgRequestDto("hello", sender.getEmail());

        //when
        chatService.saveMessage(chatRoom.getId(), chatMsgRequestDto);

        //then
        List<ChatMessage> chatMessages = chatMessageRepository.findAll();
        assertThat(chatMessages).hasSize(1);

        ChatMessage chatMessage = chatMessages.getFirst();
        ChatMessage savedChatMessage = chatMessageRepository.findById(chatMessage.getId()).orElseThrow();

        assertThat(savedChatMessage).isNotNull();
        assertThat(savedChatMessage.getContent()).isEqualTo(chatMsgRequestDto.message());
        assertThat(savedChatMessage.getChatRoom()).isEqualTo(chatRoom);
        assertThat(savedChatMessage.getMember()).isEqualTo(sender);

        assertThat(chatRoom.getMessages()).hasSize(1);
        assertThat(chatRoom.getMessages().getFirst()).isEqualTo(savedChatMessage);

        List<ReadStatus> readStatuses = readStatusRepository.findAll();

        assertThat(readStatuses).hasSize(chatRoom.getParticipants().size());
        for (ReadStatus status : readStatuses) {
            Boolean isRead = status.getIsRead();

            if (status.getMember() == sender) {
                assertThat(isRead).isTrue();
            } else {
                assertThat(isRead).isFalse();
            }
        }
    }

    @Test
    @DisplayName("그룹 채팅방 개설 시 필요한 연관 관계를 정상적으로 매핑한다.")
    void createGroupRoom() {
        //given
        Member creator = memberRepository.save(Member.builder()
                                                     .name("Francisca Souza")
                                                     .email("sender@gmail.com")
                                                     .password("my-password")
                                                     .build()
        );
        String roomName = "my-room";
        String email = creator.getEmail();

        //when
        chatService.createGroupRoom(roomName, email);

        //then
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        assertThat(chatRooms).hasSize(1);

        ChatRoom chatRoom = chatRooms.getFirst();
        ChatRoom savedChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElseThrow();

        assertThat(savedChatRoom.getName()).isEqualTo(roomName);
        assertThat(savedChatRoom.getIsGroupChat()).isEqualTo("Y");

        List<ChatParticipant> participants = savedChatRoom.getParticipants();
        assertThat(participants).hasSize(1);

        ChatParticipant participant = participants.getFirst();
        assertThat(participants).contains(participant);
        assertThat(participant.getChatRoom()).isEqualTo(savedChatRoom);
        assertThat(participant.getMember()).isEqualTo(creator);
    }

    @Test
    @DisplayName("그룹 채팅 목록 조회")
    void getGroupChatRooms() {
        //given
        String roomName = "my-room";

        ChatRoom groupChat1 = chatRoomRepository.save(ChatRoom.builder()
                                                              .isGroupChat("Y")
                                                              .name(roomName)
                                                              .build()
        );
        ChatRoom noGroupChat = chatRoomRepository.save(ChatRoom.builder()
                                                               .isGroupChat("N")
                                                               .name(roomName)
                                                               .build()
        );
        ChatRoom groupChat2 = chatRoomRepository.save(ChatRoom.builder()
                                                              .isGroupChat("Y")
                                                              .name(roomName)
                                                              .build()
        );

        //when
        List<ChatRoomListResponseDto> chatRooms = chatService.getGroupChatRooms();

        //then
        assertThat(chatRooms)
                .hasSize(2)
                .extracting("roomId", "roomName")
                .containsExactly(
                        tuple(groupChat1.getId(), roomName),
                        tuple(groupChat2.getId(), roomName)
                ).doesNotContain(tuple(noGroupChat.getId(), roomName));
    }
}