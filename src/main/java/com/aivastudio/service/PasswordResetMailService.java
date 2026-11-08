package com.aivastudio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetMailService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetMailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String from;

    public PasswordResetMailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                                    @Value("${app.mail.from:no-reply@aivastudio.com}") String from) {
        this.mailSenderProvider = mailSenderProvider;
        this.from = from;
    }

    // @Async: la respuesta HTTP no espera al servidor SMTP, así el tiempo de respuesta
    // es el mismo exista o no el correo (no se puede averiguar qué cuentas existen).
    @Async
    public void sendResetLink(String to, String name, String link) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            // Modo desarrollo: no hay SMTP configurado, el enlace se imprime en la consola del backend.
            log.warn("[DESARROLLO] SMTP no configurado. Enlace de recuperación para {}: {}", to, link);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Recupera tu contraseña de AIVA Studio");
            message.setText("Hola " + name + ",\n\n"
                    + "Recibimos una solicitud para restablecer la contraseña de tu cuenta en AIVA Studio.\n\n"
                    + "Abre este enlace para elegir una nueva contraseña (vence en 30 minutos):\n"
                    + link + "\n\n"
                    + "Si no fuiste tú, ignora este correo: tu contraseña actual seguirá funcionando.\n\n"
                    + "— Equipo AIVA Studio");
            sender.send(message);
        } catch (MailException ex) {
            log.error("No se pudo enviar el correo de recuperación a {}", to, ex);
        }
    }
}