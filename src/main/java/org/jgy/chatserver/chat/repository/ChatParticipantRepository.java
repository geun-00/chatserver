package org.jgy.chatserver.chat.repository;

import org.jgy.chatserver.chat.domain.ChatParticipant;
import org.jgy.chatserver.chat.domain.ChatRoom;
import org.jgy.chatserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
}