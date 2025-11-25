package com.example.correo_web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private MailController mailController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mailController).build();
    }

    @Test
    void sendEmail_Success() throws Exception {
        doNothing().when(emailService).sendEmailWithTemplate(any(), any(), any(), any(), any());

        mockMvc.perform(get("/sendEmail")
                        .param("to", "test@example.com")
                        .param("subject", "Test Subject")
                        .param("nombre", "Test Name")
                        .param("mensaje", "Test Message")
                        .param("link", "http://example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Correo enviado correctamente a test@example.com"));
    }

    @Test
    void sendEmail_MessagingException() throws Exception {
        doThrow(new jakarta.mail.MessagingException("Test Exception")).when(emailService).sendEmailWithTemplate(any(), any(), any(), any(), any());

        mockMvc.perform(get("/sendEmail")
                        .param("to", "test@example.com")
                        .param("subject", "Test Subject")
                        .param("nombre", "Test Name")
                        .param("mensaje", "Test Message")
                        .param("link", "http://example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Error al enviar el correo: Test Exception"));
    }

    @Test
    void sendRawEmail_Success() throws Exception {
        doNothing().when(emailService).sendSimpleEmail(any(), any(), any(), any(), any(), any(Boolean.class));

        mockMvc.perform(get("/sendRawEmail")
                        .param("to", "test@example.com")
                        .param("subject", "Test Subject")
                        .param("body", "Test Body")
                        .param("isHtml", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("Correo enviado correctamente a test@example.com"));
    }

    @Test
    void sendRawEmail_MessagingException() throws Exception {
        doThrow(new jakarta.mail.MessagingException("Test Exception")).when(emailService).sendSimpleEmail(any(), any(), any(), any(), any(), any(Boolean.class));

        mockMvc.perform(get("/sendRawEmail")
                        .param("to", "test@example.com")
                        .param("subject", "Test Subject")
                        .param("body", "Test Body")
                        .param("isHtml", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("Error al enviar el correo: Test Exception"));
    }
}
