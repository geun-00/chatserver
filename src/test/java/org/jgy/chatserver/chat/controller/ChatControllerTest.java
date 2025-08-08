package org.jgy.chatserver.chat.controller;

import org.jgy.chatserver.RestDocsSupport;
import org.jgy.chatserver.chat.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatControllerTest extends RestDocsSupport {

    private final ChatService chatService = mock(ChatService.class);

    @Override
    protected Object initController() {
        return new ChatController(chatService);
    }

    @Test
    @DisplayName("그룹 채팅방 개설")
    void createGroupRoom() throws Exception {
        //given
        Authentication authentication = mock(Authentication.class);
        given(authentication.getName()).willReturn("john@naver.com");

        //when
        //then
        mockMvc.perform(post("/chat/room/group/create")
                       .header(HttpHeaders.AUTHORIZATION, "Bearer {Access Token}")
                       .queryParam("roomName", "my-room")
                       .principal(authentication)
               )
               .andDo(print())
               .andExpect(status().isOk())
               .andDo(document("chat-room-group-create",
                       queryParameters(
                               parameterWithName("roomName").description("채팅방 이름")
                       ),
                       requestHeaders(
                               headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                       )
               ));
    }
}