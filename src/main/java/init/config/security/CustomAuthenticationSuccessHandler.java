package init.config.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.service.BlockAccountService;
import init.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
	
	BlockAccountService blockAccountService;
	UsuariosDao usuariosDao;
	JwtService jwtService;
	
	public CustomAuthenticationSuccessHandler(BlockAccountService blockAccountService, UsuariosDao usuariosDao,
			JwtService jwtService) {
		this.blockAccountService = blockAccountService;
		this.usuariosDao = usuariosDao;
		this.jwtService = jwtService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String username = authentication.getName(); 
        String token = jwtService.createToken();
        response.setContentType("application/json");
        Map<String, String> tokenResponse = Map.of("token", token);
        new ObjectMapper().writeValue(response.getWriter(), tokenResponse);
        Usuario usuario = usuariosDao.findByUsername(username);
		blockAccountService.resetearIntentosFallidos(usuario);
	}
	
}
