package init.config.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import init.utilidades.StandardResponseHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	
	StandardResponseHandler standardResponseHandler;
	
	public CustomAccessDeniedHandler(StandardResponseHandler standardResponseHandler) {
		this.standardResponseHandler = standardResponseHandler;
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		 standardResponseHandler.handleResponse(response, 403, 
	        		"Acceso denegado. El usuario no tiene permiso para acceder a este recurso"
				 	, null);
	}

}
