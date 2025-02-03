package init.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import init.events.UsuarioBloqueadoEvent;
import init.events.UsuarioCreadoEvent;

@Service
public class NotificationService {
	
	EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void handleUsuarioCreado(UsuarioCreadoEvent event) {
        String subject = "¡Bienvenido a la plataforma!";
        String message = "Hola, " + event.getUsername() + ". Tu cuenta ha sido creada correctamente.";

        emailService.enviarCorreo(event.getEmail(), subject, message);
    }

    @EventListener
    public void handleUsuarioBloqueado(UsuarioBloqueadoEvent event) {
        String subject = "Tu cuenta ha sido bloqueada";
        String message = "Hola, " + event.getUsername() 
                + ". Tu cuenta ha sido bloqueada al sobrepasar los 3 intentos de que dispones para autenticarte. " 
                + "Contacta con soporte para desbloquearla.";
        emailService.enviarCorreo(event.getEmail(), subject, message);
    }
}
