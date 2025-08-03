package org.jgy.chatserver.member.dto;

import org.jgy.chatserver.member.domain.Member;

public record MemberSaveRequestDto(
        String name,
        String email,
        String password)
{
    public Member toEntity() {
        return Member.builder()
                     .name(name)
                     .email(email)
                     .password(password)
                     .build();
    }
}
