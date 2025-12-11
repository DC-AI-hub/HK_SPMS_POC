package com.spms.backend.service.sys.notification.event;

import org.springframework.context.ApplicationEvent;
import java.util.Map;
import java.util.Locale;

public abstract class NotificationEvent extends ApplicationEvent {
    private final String eventType;
    private final Map<String, Object> data;
    private final Locale locale;

    public NotificationEvent(Object source, String eventType, Map<String, Object> data, Locale locale) {
        super(source);
        this.eventType = eventType;
        this.data = data;
        this.locale = locale;
    }

    public String getEventType() {
        return eventType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Locale getLocale() {
        return locale;
    }
}
