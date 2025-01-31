package init.utilidades;

import java.io.IOException;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import init.entities.StandardResponse;
import jakarta.servlet.http.HttpServletResponse;

/*StandardResponseHandler es una clase que centraliza la gestión de respuestas HTTP en formato JSON.
Su propósito es evitar la duplicación de código al manejar respuestas HTTP en diferentes partes
de la aplicación, asegurando que todas las respuestas sigan un formato consistente.
Características principales:
 - Utiliza Jackson (`ObjectMapper`) para serializar respuestas en JSON.
 - Configura `ObjectMapper` para manejar fechas correctamente (`JavaTimeModule`).
 - Permite establecer el código de estado HTTP, un mensaje y detalles adicionales en la respuesta.
 - Devuelve un objeto `HttpServletResponse` con la respuesta JSON escrita en el cuerpo.
 
Ejemplo de uso:
 	responseHandler.handleResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                               "Acceso denegado", "Usuario o contraseña incorrectos");
Esto enviará la siguiente respuesta JSON con estado HTTP 401:
 
{
    "timestamp": "2024-01-31T12:00:00",
    "message": "Acceso denegado",
    "details": "Usuario o contraseña incorrectos",
    "status": "UNAUTHORIZED"
}
 */

@Component
public class StandardResponseHandler {
	
	ObjectMapper objectMapper;

    public StandardResponseHandler() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    public HttpServletResponse handleResponse(HttpServletResponse response, int status, 
    																String message, String details) throws IOException {
    	response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
    	response.setStatus(status);
    	StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), message, details, 
    																HttpStatus.resolve(status));
    	String jsonResponse = objectMapper.writeValueAsString(respuesta);
        response.getWriter().write(jsonResponse);
        return response;
    }
    
}
