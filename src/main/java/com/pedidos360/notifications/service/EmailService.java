package com.pedidos360.notifications.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Adaptado del ejemplo visto en clase (PASO_A_PASO_PARA_ENVIO_DE_CORREOS),
 * usando JavaMailSender - solo cambia el proveedor SMTP configurado en
 * application.yml (Gmail en vez de otro), el codigo es identico.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    /** Correo de texto plano - igual que el metodo "enviarCorreo" del ejemplo de clase. */
    public void enviarCorreo(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(remitente);
        mailSender.send(message);
    }

    /** Correo en HTML, usado para la confirmacion de pedido (mas presentable). */
    public void enviarCorreoHtml(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom(remitente);
        mailSender.send(message);
    }
}
