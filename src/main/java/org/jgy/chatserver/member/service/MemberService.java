package org.jgy.chatserver.member.service;

import lombok.RequiredArgsConstructor;
import org.jgy.chatserver.member.domain.Member;
import org.jgy.chatserver.member.dto.MemberSaveRequestDto;
import org.jgy.chatserver.member.dto.MemberSaveResponseDto;
import org.jgy.chatserver.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberSaveResponseDto create(MemberSaveRequestDto memberSaveRequestDto) {
        if (memberRepository.existsByEmail(memberSaveRequestDto.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(memberSaveRequestDto.email());

        Member savedMember = memberRepository.save(memberSaveRequestDto.toEntity(encodedPassword));

        return MemberSaveResponseDto.from(savedMember);
    }
}
