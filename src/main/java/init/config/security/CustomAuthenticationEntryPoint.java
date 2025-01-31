package init.config.security;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import init.dao.UsuariosDao;
import init.utilidades.StandardResponseHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	UsuariosDao usuariosdao;
	StandardResponseHandler standardResponseHandler;

	public CustomAuthenticationEntryPoint(UsuariosDao usuariosdao,
			StandardResponseHandler standardResponseHandler) {
		this.usuariosdao = usuariosdao;
		this.standardResponseHandler = standardResponseHandler;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		Object intentosRestantes = request.getAttribute("intentosRestantes");
		int intentos = 0;
		
		//En caso de que el problema no tenga nada que ver con un fallo de autenticación, 
		//el valor de la variable intentosRestantes va a ser null, lo que provocará una 
		//NullPointerException que manejamos aquí
		if(intentosRestantes == null) {
			standardResponseHandler.handleResponse(response, 500, 
											"Error no relacionado con la autenticación", null);
			return;
		} else {
			intentos = (Integer) intentosRestantes;
		}

		if (authException instanceof LockedException) {
			standardResponseHandler.handleResponse(response, 401, 
					"La cuenta está bloqueada. Contacte con soporte", null);

		} else if (authException instanceof BadCredentialsException) {
			if(intentos >= 1) {
				standardResponseHandler.handleResponse(response, 401, 
						"Credenciales incorrectas. Le quedan " + intentosRestantes + " intentos", null);
			} else { 
				standardResponseHandler.handleResponse(response, 401, 
						"Credenciales incorrectas. Su cuenta ha sido bloqueada por seguridad", null);
			}

		} else {
			standardResponseHandler.handleResponse(response, 500, "Error desconocido", null);
		}
	}
}
