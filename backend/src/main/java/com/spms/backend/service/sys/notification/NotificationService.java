package com.spms.backend.service.sys.notification;

import com.spms.backend.service.sys.notification.event.NotificationEvent;

public interface NotificationService {
    void publishEvent(NotificationEvent event);

    void handleNotification(NotificationEvent event);
}
