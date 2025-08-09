package org.jgy.chatserver.chat.controller;

import org.jgy.chatserver.RestDocsSupport;
import org.jgy.chatserver.chat.dto.ChatRoomListResponseDto;
import org.jgy.chatserver.chat.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatControllerTest extends RestDocsSupport {

    private final ChatService chatService = mock(ChatService.class);

    @Override
    protected Object initController() {
        return new ChatController(chatService);
    }

    @BeforeEach
    void setupSecurityContext() {
        UserDetails userDetails = User.withUsername("hello").password("password").roles("USER").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("그룹 채팅방 개설")
    void createGroupRoom() throws Exception {
        //given

        //when
        //then
        mockMvc.perform(post("/chat/room/group/create")
                       .header(HttpHeaders.AUTHORIZATION, "Bearer {Access Token}")
                       .queryParam("roomName", "my-room")
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

    @Test
    @DisplayName("그룹 채팅 목록 조회")
    void getGroupChatRooms() throws Exception {
        //given
        List<ChatRoomListResponseDto> response = List.of(
                new ChatRoomListResponseDto(1L, "my-room-1"),
                new ChatRoomListResponseDto(2L, "my-room-2"),
                new ChatRoomListResponseDto(3L, "my-room-3")
        );

        given(chatService.getGroupChatRooms()).willReturn(response);

        //when
        //then
        mockMvc.perform(get("/chat/room/group/list")
                       .header(HttpHeaders.AUTHORIZATION, "Bearer {Access Token}")
               )
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(response.size()))
               .andDo(print())
               .andDo(document("chat-room-list",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),

                       responseFields(
                               fieldWithPath("[].roomId").type(JsonFieldType.NUMBER).description("채팅방 아이디"),
                               fieldWithPath("[].roomName").type(JsonFieldType.STRING).description("채팅방 이름")
                       ),
                       requestHeaders(
                               headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                       ),
                       responseBody()
               ));
    }

    @Test
    @DisplayName("그룹 채팅방 참여")
    void joinGroupChatRoom() throws Exception {
        //given
        Long roomId = 1L;

        //when
        //then
        mockMvc.perform(post("/chat/room/group/{roomId}/join", roomId)
                       .header(HttpHeaders.AUTHORIZATION, "Bearer {Access Token}")
               )
               .andExpect(status().isOk())
               .andDo(document("chat-room-join",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),

                       pathParameters(
                               parameterWithName("roomId").description("채팅방 아이디")
                       ),

                       requestHeaders(
                               headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                       ),
                       responseBody()
               ));
    }
}