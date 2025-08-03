package org.jgy.chatserver.member.dto;

public record MemberLoginRequestDto(
        String email,
        String password)
{
}
