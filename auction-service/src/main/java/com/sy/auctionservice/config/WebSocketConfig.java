package com.sy.auctionservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// WebSocketConfig.java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트에게 메시지를 보낼 때 사용할 경로(prefix)
        // /topic/auctions/123 처럼 특정 경매방을 구독
        registry.enableSimpleBroker("/topic");
        // 클라이언트가 서버로 메시지를 보낼 때 사용할 경로(prefix)
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 WebSocket 연결을 시작할 엔드포인트
        registry.addEndpoint("/ws-auction").withSockJS();
    }
}
