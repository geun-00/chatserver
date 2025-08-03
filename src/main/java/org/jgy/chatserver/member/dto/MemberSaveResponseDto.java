package org.jgy.chatserver.member.dto;

import org.jgy.chatserver.member.domain.Member;

public record MemberSaveResponseDto(
        Long id,
        String name,
        String email)
{
    public static MemberSaveResponseDto from(Member member) {
        return new MemberSaveResponseDto(
                member.getId(),
                member.getName(),
                member.getEmail()
        );
    }
}
