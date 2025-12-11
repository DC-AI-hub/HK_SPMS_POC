package com.spms.backend.service.sys.notification.template;

import java.util.Locale;
import java.util.Map;

public interface NotificationTemplateManage {
    String renderTemplate(String templatePath,
                          Locale locale,
                          Map<String, Object> data);
}
