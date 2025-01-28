package init.config.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.service.BlockAccountService;
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
		if (username != null) {
			Usuario usuario = usuariosDao.findByUsername(username);
			blockAccountService.incrementarIntentosFallidos(usuario);
		}
		SecurityContextHolder.clearContext(); 
	    customAuthenticationEntryPoint.commence(request, response, exception);
	}

}
