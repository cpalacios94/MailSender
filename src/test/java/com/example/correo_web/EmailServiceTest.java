package com.example.correo_web;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendEmailWithTemplate_Success() throws MessagingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", "Test");
        variables.put("mensaje", "Test message");
        variables.put("link", "http://example.com");

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html><body>Test</body></html>");

        emailService.sendEmailWithTemplate("test@example.com", "Test Subject", "cc@example.com", "bcc@example.com", variables);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendSimpleEmail_Success() throws MessagingException {
        emailService.sendSimpleEmail("test@example.com", "Test Subject", "cc@example.com", "bcc@example.com", "Test Body", false);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendSimpleEmail_MultipleRecipients() throws MessagingException {
        // Given
        String to = "test1@example.com,test2@example.com";
        String cc = "cc1@example.com;cc2@example.com";
        String bcc = "bcc1@example.com, bcc2@example.com";

        // When
        emailService.sendSimpleEmail(to, "Test Subject", cc, bcc, "Test Body", false);

        // Then
        ArgumentCaptor<Address[]> toCaptor = ArgumentCaptor.forClass(Address[].class);
        verify(mimeMessage).setRecipients(eq(MimeMessage.RecipientType.TO), toCaptor.capture());
        String[] capturedTo = Arrays.stream(toCaptor.getValue()).map(Address::toString).toArray(String[]::new);
        assertArrayEquals(new String[]{"test1@example.com", "test2@example.com"}, capturedTo);

        ArgumentCaptor<Address[]> ccCaptor = ArgumentCaptor.forClass(Address[].class);
        verify(mimeMessage).setRecipients(eq(MimeMessage.RecipientType.CC), ccCaptor.capture());
        String[] capturedCc = Arrays.stream(ccCaptor.getValue()).map(Address::toString).toArray(String[]::new);
        assertArrayEquals(new String[]{"cc1@example.com", "cc2@example.com"}, capturedCc);

        ArgumentCaptor<Address[]> bccCaptor = ArgumentCaptor.forClass(Address[].class);
        verify(mimeMessage).setRecipients(eq(MimeMessage.RecipientType.BCC), bccCaptor.capture());
        String[] capturedBcc = Arrays.stream(bccCaptor.getValue()).map(Address::toString).toArray(String[]::new);
        assertArrayEquals(new String[]{"bcc1@example.com", "bcc2@example.com"}, capturedBcc);

        verify(mailSender).send(mimeMessage);
    }
}
