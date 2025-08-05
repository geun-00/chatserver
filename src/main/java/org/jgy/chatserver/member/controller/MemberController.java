package org.jgy.chatserver.member.controller;

import lombok.RequiredArgsConstructor;
import org.jgy.chatserver.member.dto.MemberLoginRequestDto;
import org.jgy.chatserver.member.dto.MemberLoginResponseDto;
import org.jgy.chatserver.member.dto.MemberResponseDto;
import org.jgy.chatserver.member.dto.MemberSaveRequestDto;
import org.jgy.chatserver.member.dto.MemberSaveResponseDto;
import org.jgy.chatserver.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveRequestDto memberSaveRequestDto) {
        MemberSaveResponseDto memberSaveResponseDto = memberService.create(memberSaveRequestDto);
        return new ResponseEntity<>(memberSaveResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {
        MemberLoginResponseDto memberLoginResponseDto = memberService.login(memberLoginRequestDto);
        return new ResponseEntity<>(memberLoginResponseDto, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> memberList() {
        List<MemberResponseDto> memberList = memberService.findAll();
        return new ResponseEntity<>(memberList, HttpStatus.OK);
    }
}
