package org.jgy.chatserver.member.controller;

import org.jgy.chatserver.RestDocsSupport;
import org.jgy.chatserver.member.dto.MemberSaveRequestDto;
import org.jgy.chatserver.member.dto.MemberSaveResponseDto;
import org.jgy.chatserver.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends RestDocsSupport {

    private final MemberService memberService = mock(MemberService.class);

    @Override
    protected Object initController() {
        return new MemberController(memberService);
    }

    @Test
    @DisplayName("회원 등록 API")
    void createMember() throws Exception {
        //given
        MemberSaveRequestDto saveRequestDto = new MemberSaveRequestDto("geun", "email@email.com", "password12@");
        MemberSaveResponseDto saveResponseDto = new MemberSaveResponseDto(1L, saveRequestDto.name(), saveRequestDto.email());

        given(memberService.create(any(MemberSaveRequestDto.class))).willReturn(saveResponseDto);

        //when
        //then
        mockMvc.perform(
                       post("/member/create")
                               .contentType(MediaType.APPLICATION_JSON_VALUE)
                               .content(objectMapper.writeValueAsBytes(saveRequestDto))
               )
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(saveResponseDto.id()))
               .andExpect(jsonPath("$.name").value(saveResponseDto.name()))
               .andExpect(jsonPath("$.email").value(saveResponseDto.email()))
               .andDo(print())
               .andDo(document("member-create",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),

                       requestFields(
                               fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                               fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                               fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                       ),
                       responseFields(
                               fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                               fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                               fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일")
                       )
               ));

        verify(memberService, times(1)).create(any(MemberSaveRequestDto.class));
    }
}