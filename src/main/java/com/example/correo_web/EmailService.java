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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmailWithTemplate(String to, String subject, String cc, String bcc, Map<String, Object> variables) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        // Procesar contenido HTML con Thymeleaf
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process("email-template", context);

        helper.setFrom("envios.siu@cu.ucsg.edu.ec");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Validar y agregar CC si corresponde
        if (cc != null && !cc.trim().isEmpty()) {
            helper.setCc(cc);
        }

        // Validar y agregar BCC si corresponde
        if (bcc != null && !bcc.trim().isEmpty()) {
            String[] bccArray = Arrays.stream(bcc.split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            if (bccArray.length > 0) {
                helper.setBcc(bccArray);
            }
        }

        mailSender.send(message);
    }


    // Servicio: aquí hacemos split de comas o punto y coma
    public void sendSimpleEmail(
            String to,
            String subject,
            String cc,
            String bcc,
            String body,
            boolean isHtml
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setFrom("envios.siu@cu.ucsg.edu.ec");

        // 1) Split para To
        String[] toArray = Arrays.stream(to.split("[,;]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        helper.setTo(toArray);

        // 2) Split para CC si viene algo
        if (cc != null && !cc.isBlank()) {
            String[] ccArray = Arrays.stream(cc.split("[,;]+"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            helper.setCc(ccArray);
        }

        // 3) Split para BCC si viene algo
        if (bcc != null && !bcc.isBlank()) {
            String[] bccArray = Arrays.stream(bcc.split("[,;]+"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            helper.setBcc(bccArray);
        }

        // Subject y body
        helper.setSubject(subject);
        helper.setText(body, isHtml);

        // Envío final
        mailSender.send(message);
    }


}
