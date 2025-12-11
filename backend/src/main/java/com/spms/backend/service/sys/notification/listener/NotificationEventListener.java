package com.spms.backend.service.sys.notification.listener;

import com.spms.backend.service.sys.notification.NotificationService;
import com.spms.backend.service.sys.notification.event.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {
    private final NotificationService notificationService;

    @Autowired
    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        notificationService.handleNotification(event);
    }
}
