package init.config.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.exception.NoSuchUserException;
import init.service.BlockAccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	
	BlockAccountService blockAccountService;
	UsuariosDao usuariosDao;

	public CustomAuthenticationFailureHandler(BlockAccountService blockAccountService, UsuariosDao usuariosDao) {
		this.blockAccountService = blockAccountService;
		this.usuariosDao = usuariosDao;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String username = (String) request.getAttribute("username");
		Usuario usuario = usuariosDao.findByUsername(username);
		if(usuario==null) {
			throw new NoSuchUserException("El usuario " + username + " no existe");
		}
		blockAccountService.incrementarIntentosFallidos(usuario);
	}

}
