package init.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensaje, true); 

            mailSender.send(mimeMessage);
            System.out.println("Correo enviado a: " + destinatario);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
        }
    }
}