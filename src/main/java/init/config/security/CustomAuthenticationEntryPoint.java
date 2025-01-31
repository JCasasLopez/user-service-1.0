package init.config.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import init.dao.UsuariosDao;
import init.entities.StandardResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	UsuariosDao usuariosdao;
	ObjectMapper objectMapper;

	public CustomAuthenticationEntryPoint(UsuariosDao usuariosdao) {
		this.usuariosdao = usuariosdao;
		this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Object intentosRestantes = request.getAttribute("intentosRestantes");
		int intentos = 0;
		StandardResponse respuesta = null;
		//En caso de que el problema no tenga nada que ver con un fallo de autenticación, 
		//el valor de la variable intentosRestantes va a ser null, lo que provocará una 
		//NullPointerException
		if(intentosRestantes == null) {
			respuesta = new StandardResponse (LocalDateTime.now(), 
					"Error no relacionado con la autenticación", null, HttpStatus.INTERNAL_SERVER_ERROR);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		} else {
			intentos = (Integer) intentosRestantes;
		}

		if (authException instanceof LockedException) {
			respuesta = new StandardResponse (LocalDateTime.now(), 
					"La cuenta está bloqueada. Contacte con soporte", null, HttpStatus.UNAUTHORIZED);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		} else if (authException instanceof BadCredentialsException) {
			if(intentos >= 1) {
				respuesta = new StandardResponse (LocalDateTime.now(), 
						"Credenciales incorrectas. Le quedan " + intentosRestantes + " intentos", 
						null, HttpStatus.UNAUTHORIZED);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			} else {
				respuesta = new StandardResponse (LocalDateTime.now(), 
						"Credenciales incorrectas. Su cuenta ha sido bloqueada por seguridad", 
						null, HttpStatus.UNAUTHORIZED);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}

		} else {
			respuesta = new StandardResponse (LocalDateTime.now(), 
										"Error desconocido", null, HttpStatus.INTERNAL_SERVER_ERROR);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		String jsonResponse = objectMapper.writeValueAsString(respuesta);
        response.getWriter().write(jsonResponse);
	}
}
