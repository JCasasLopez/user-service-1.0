package init.config.security;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import init.dao.UsuariosDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	UsuariosDao usuariosdao;

	public CustomAuthenticationEntryPoint(UsuariosDao usuariosdao) {
		this.usuariosdao = usuariosdao;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Object intentosRestantes = request.getAttribute("intentosRestantes");
		int intentos = 0;
		//En caso de que el problema no tenga nada que ver con un fallo de autenticación, 
		//el valor de la variable intentosRestantes va a ser null, lo que provocará una 
		//NullPointerException
		if(intentosRestantes == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("{\"error\": \"Ocurrió un error desconocido.\"}");
			return;
		} else {
			intentos = (Integer) intentosRestantes;
		}

		if (authException instanceof LockedException) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().write("{\"error\": \"La cuenta está bloqueada. Contacte con soporte.\"}");

		} else if (authException instanceof BadCredentialsException) {
			if(intentos >= 1) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("{\"error\": \"Credenciales incorrectas. Le quedan " 
						+ intentosRestantes + " intentos\"}");
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("{\"error\": \"Credenciales incorrectas. Su cuenta ha sido bloqueada\"}");
			}

		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("{\"error\": \"Ocurrió un error desconocido.\"}");
		}
	}
}
