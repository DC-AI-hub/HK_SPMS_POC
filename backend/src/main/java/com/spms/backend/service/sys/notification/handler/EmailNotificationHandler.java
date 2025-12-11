package com.spms.backend.service.sys.notification.handler;

import com.spms.backend.service.sys.notification.template.NotificationTemplateManage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import com.spms.backend.service.sys.notification.event.NotificationEvent;
import com.spms.backend.service.sys.notification.exception.NotificationProcessingException;


//@Component
public class EmailNotificationHandler implements NotificationHandler {
    private final JavaMailSender mailSender;
    private final NotificationTemplateManage templateManager;

    public EmailNotificationHandler(JavaMailSender mailSender, 
                                   NotificationTemplateManage templateManager) {
        this.mailSender = mailSender;
        this.templateManager = templateManager;
    }

    @Override
    public void handle(NotificationEvent event) {
        try {
            String templatePath = "email/" + event.getEventType().toLowerCase() + ".ftl";
            String content = templateManager.renderTemplate(templatePath, 
                                                           event.getLocale(), 
                                                           event.getData());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setTo((String) event.getData().get("email"));
            helper.setSubject((String) event.getData().get("subject"));
            helper.setText(content, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new NotificationProcessingException("Email sending failed", e);
        }
    }

    @Override
    public String getChannelType() {
        return "EMAIL";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
