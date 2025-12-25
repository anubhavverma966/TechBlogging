package com.anubhav.techblog.Techblogging.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class EmailServiceImpl implements EmailService {

    private final SendGrid sendGrid;
    private final SpringTemplateEngine templateEngine;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    public EmailServiceImpl(SendGrid sendGrid,
                            SpringTemplateEngine templateEngine) {
        this.sendGrid = sendGrid;
        this.templateEngine = templateEngine;
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {

        Context context = new Context();
        context.setVariable("resetLink", resetLink);
        context.setVariable("username", to);

        String htmlContent =
                templateEngine.process("email/resetEmail", context);

        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, "Reset Your Password", toEmail, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGrid.api(request);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
