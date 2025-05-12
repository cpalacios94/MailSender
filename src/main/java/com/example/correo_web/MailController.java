package com.example.correo_web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Controller
public class MailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/sendEmail")
    @ResponseBody
    public String sendEmail(@RequestParam("to") String to,
                            @RequestParam(value = "cc", required = false, defaultValue = "") String cc,
                            @RequestParam(value = "bcc", required = false, defaultValue = "") String bcc,
                            @RequestParam("subject") String subject,
                            @RequestParam("nombre") String nombre,
                            @RequestParam("mensaje") String mensaje,
                            @RequestParam("link") String link) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("nombre", nombre);
            variables.put("mensaje", mensaje);
            variables.put("link", link);

            emailService.sendEmailWithTemplate(to, subject, cc, bcc, variables);
            return "Correo enviado correctamente a " + to;
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error al enviar el correo: " + e.getMessage();
        }
    }

    @GetMapping("/sendRawEmail")
    @ResponseBody
    public String sendRawEmail(@RequestParam("to") String to,
                               @RequestParam("subject") String subject,
                               @RequestParam("body") String body,
                               @RequestParam(value = "cc", required = false, defaultValue = "") String cc,
                               @RequestParam(value = "bcc", required = false, defaultValue = "") String bcc,
                               @RequestParam("isHtml") boolean isHtml) {
        try {
            emailService.sendSimpleEmail(to, subject, cc, bcc, body, isHtml);
            return "Correo enviado correctamente a " + to;
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error al enviar el correo: " + e.getMessage();
        }
    }


}