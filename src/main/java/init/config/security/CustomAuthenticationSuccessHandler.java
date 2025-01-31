package init.config.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.service.BlockAccountService;
import init.service.JwtService;
import init.utilidades.StandardResponseHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
	
	BlockAccountService blockAccountService;
	UsuariosDao usuariosDao;
	JwtService jwtService;
	StandardResponseHandler standardResponseHandler;
	
	public CustomAuthenticationSuccessHandler(BlockAccountService blockAccountService, UsuariosDao usuariosDao,
			JwtService jwtService, StandardResponseHandler standardResponseHandler) {
		this.blockAccountService = blockAccountService;
		this.usuariosDao = usuariosDao;
		this.jwtService = jwtService;
		this.standardResponseHandler = standardResponseHandler;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String username = authentication.getName(); 
        String token = jwtService.createToken(); 
        standardResponseHandler.handleResponse(response, 200, "Autenticación llevada a cabo con éxito"
        																	, "Token: " + token);
        Usuario usuario = usuariosDao.findByUsername(username);
		blockAccountService.resetearIntentosFallidos(usuario);
	}
	
}
