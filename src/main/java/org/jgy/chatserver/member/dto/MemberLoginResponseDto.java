package org.jgy.chatserver.member.dto;

public record MemberLoginResponseDto(
        Long id,
        String accessToken)
{
}
