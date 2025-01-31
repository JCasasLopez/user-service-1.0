package init.utilidades;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import init.entities.StandardResponse;
import jakarta.servlet.http.HttpServletResponse;

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
    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    	String jsonResponse = objectMapper.writeValueAsString(respuesta);
        response.getWriter().write(jsonResponse);
        return response;
    }
    
}
