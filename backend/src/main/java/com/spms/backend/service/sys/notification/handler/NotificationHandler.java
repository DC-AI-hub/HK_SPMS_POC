package com.spms.backend.service.sys.notification.handler;

import com.spms.backend.service.sys.notification.event.NotificationEvent;

public interface NotificationHandler {
    void handle(NotificationEvent event);
    String getChannelType();
    boolean isEnabled();
}
