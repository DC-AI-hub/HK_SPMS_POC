package com.spms.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint clients connect to
        registry.addEndpoint("/ws").withSockJS(); // Use .withSockJS() for fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for client-to-server messages
        registry.setApplicationDestinationPrefixes("/app");

        // Prefix for server-to-client messages (topic for pub-sub, queue for point-to-point)
        registry.enableSimpleBroker("/topic", "/queue");
    }
}