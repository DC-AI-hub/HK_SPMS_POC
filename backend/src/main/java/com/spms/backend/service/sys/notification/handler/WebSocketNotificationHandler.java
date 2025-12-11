package com.spms.backend.service.sys.notification.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.spms.backend.service.sys.notification.event.NotificationEvent;

@Component
public class WebSocketNotificationHandler implements NotificationHandler {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketNotificationHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void handle(NotificationEvent event) {
        String destination = "/topic/notifications/" + event.getEventType().toLowerCase();
        messagingTemplate.convertAndSend(destination, event.getData());
    }

    @Override
    public String getChannelType() {
        return "WEBSOCKET";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
