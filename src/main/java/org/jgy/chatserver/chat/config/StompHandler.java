package org.jgy.chatserver.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jgy.chatserver.common.auth.JwtProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            log.debug("connect 요청 시 토큰 유효성 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring("Bearer ".length());

            jwtProvider.validateToken(token);

            log.debug("토큰 검증 완료");
        }

        return message;
    }
}
