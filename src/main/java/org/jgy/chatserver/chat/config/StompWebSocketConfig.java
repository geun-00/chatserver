package org.jgy.chatserver.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
        // ws://가 아닌 http:// 엔드포인트를 사용할 수 있게 해주는 sockJS 라이브러리를 통한 요청을 허용하는 설정
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //엔드포인트 예시) /publish/1 형태로 메시지 발생해야 함을 설정
        // /publish로 시작하는 url 패턴으로 메시지가 발행되면 @Controller 객체의 @MessageMapping 메서드로 라우팅 된다
        registry.setApplicationDestinationPrefixes("/publish")
                .enableSimpleBroker("/topic");
        //엔드포인트 예시) /topic/1 형태로 메시지를 수신해야 함을 설정
    }

    /**
     * 웹소켓 요청(connect, subscribe, disconnect 등) 시에는 http 헤더 등 http 메시지를 넣어올 수 있고,
     * 이를 인터셉터를 통해 가로채 토큰 등을 검증할 수 있다.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
