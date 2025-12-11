package com.spms.backend.service.sys.notification;

import com.spms.backend.service.sys.notification.event.NotificationEvent;
import com.spms.backend.service.sys.notification.handler.NotificationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final ApplicationEventPublisher eventPublisher;
    private final List<NotificationHandler> notificationHandlers;

    @Autowired
    public NotificationServiceImpl(ApplicationEventPublisher eventPublisher,
                                   List<NotificationHandler> notificationHandlers) {
        this.eventPublisher = eventPublisher;
        this.notificationHandlers = notificationHandlers;
    }

    @Override
    public void publishEvent(NotificationEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void handleNotification(NotificationEvent event) {
        
        for (NotificationHandler handler : notificationHandlers) {
            if(handler.isEnabled()) {
                handler.handle(event);
            }
        }
    }
}
