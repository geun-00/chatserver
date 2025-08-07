package org.jgy.chatserver.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 스프링과 stomp는 기본적으로 세션 관리를 내부적으로 자동으로 처리
 * 연결 및 해제 이벤트를 기록하고 연결된 세션 수를 실시간으로 확인할 목적으로 이벤트 리스너를 생성
 * 로그, 디버깅 목적
 */
@Slf4j
@Component
public class StompEventListener {

    private final Set<String> sessions = ConcurrentHashMap.newKeySet();

    @EventListener
    public void connectHandle(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessions.add(accessor.getSessionId());
        log.debug("connected session ID = {}", accessor.getSessionId());
        debugTotalSessions();
    }

    @EventListener
    public void disConnectHandle(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessions.remove(accessor.getSessionId());
        log.debug("disConnected session ID = {}", accessor.getSessionId());
        debugTotalSessions();
    }

    private void debugTotalSessions() {
        log.debug("total sessions = {}", sessions.size());
    }
}
