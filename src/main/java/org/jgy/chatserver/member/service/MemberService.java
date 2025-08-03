package org.jgy.chatserver.member.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jgy.chatserver.common.auth.JwtProvider;
import org.jgy.chatserver.member.domain.Member;
import org.jgy.chatserver.member.dto.MemberLoginRequestDto;
import org.jgy.chatserver.member.dto.MemberLoginResponseDto;
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
    private final JwtProvider jwtProvider;

    @Transactional
    public MemberSaveResponseDto create(MemberSaveRequestDto memberSaveRequestDto) {
        if (memberRepository.existsByEmail(memberSaveRequestDto.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(memberSaveRequestDto.password());

        Member savedMember = memberRepository.save(memberSaveRequestDto.toEntity(encodedPassword));

        return MemberSaveResponseDto.from(savedMember);
    }

    public MemberLoginResponseDto login(MemberLoginRequestDto memberLoginRequestDto) {
        Member member = memberRepository.findByEmail(memberLoginRequestDto.email()).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 이메일입니다: " + memberLoginRequestDto.email())
        );

        if (!passwordEncoder.matches(memberLoginRequestDto.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다: " + memberLoginRequestDto.password());
        }

        String accessToken = jwtProvider.createToken(member.getEmail(), member.getRole().toString());
        return new MemberLoginResponseDto(member.getId(), accessToken);
    }
}
