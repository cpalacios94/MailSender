package com.example.correo_web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;
//import java.util.HashMap;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmailWithTemplate(String to, String subject, Map<String, Object> variables) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process("email-template", context);

        helper.setFrom("soporte.siu@ucsg.edu.ec");
        helper.setCc("ismael.sosa@cu.ucsg.edu.ec");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // `true` indica que es HTML

        mailSender.send(message);
    }
}