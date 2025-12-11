package com.spms.backend.service.sys.notification.template;

import com.spms.backend.service.sys.notification.exception.NotificationProcessingException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

@Component
public class NotificationTemplateManagerFreeMakerImpl implements NotificationTemplateManage {
    private final Configuration freemarkerConfig;
    @Autowired
    public NotificationTemplateManagerFreeMakerImpl(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public String renderTemplate(String templatePath,
                                 Locale locale,
                                 Map<String, Object> data) {
        try {
            Template template = freemarkerConfig.getTemplate(
                locale.toLanguageTag() + "/" + templatePath
            );
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new NotificationProcessingException("Template rendering failed", e);
        }
    }
}
