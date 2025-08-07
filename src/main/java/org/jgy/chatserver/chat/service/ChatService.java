package org.jgy.chatserver.chat.service;

import lombok.RequiredArgsConstructor;
import org.jgy.chatserver.chat.repository.ChatMessageRepository;
import org.jgy.chatserver.chat.repository.ChatParticipantRepository;
import org.jgy.chatserver.chat.repository.ChatRoomRepository;
import org.jgy.chatserver.chat.repository.ReadStatusRepository;
import org.jgy.chatserver.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
}
