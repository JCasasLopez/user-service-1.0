package init.config.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.exception.NoSuchUserException;
import init.service.BlockAccountService;
import init.utilidades.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	
	BlockAccountService blockAccountService;
	UsuariosDao usuariosDao; 
	CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	public CustomAuthenticationFailureHandler(BlockAccountService blockAccountService, 
			UsuariosDao usuariosDao, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.blockAccountService = blockAccountService;
		this.usuariosDao = usuariosDao;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String username = (String) request.getParameter("username");
		Usuario usuario = usuariosDao.findByUsername(username);
		if(usuario == null) {
			// Aunque esta excepción debería manejarse en el CustomAuthenticationEntryPoint, 
			//Spring Security no la propaga correctamente, así que se maneja directamente 
			//en este punto para garantizar que el flujo funcione correctamente 
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write("{\"error\": \"No existe el usuario " + username + "\"}");
	        return;
		} else {
			blockAccountService.incrementarIntentosFallidos(usuario);
			int intentosRestantes = Constants.MAX_INTENTOS_FALLIDOS - usuario.getIntentosFallidos() ;
			request.setAttribute("intentosRestantes", intentosRestantes);
		}
		SecurityContextHolder.clearContext(); 
		customAuthenticationEntryPoint.commence(request, response, exception);
	}

}
