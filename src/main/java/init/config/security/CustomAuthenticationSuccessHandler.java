package init.config.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import init.dao.UsuariosDao;
import init.entities.StandardResponse;
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
	ObjectMapper objectMapper;
	
	public CustomAuthenticationSuccessHandler(BlockAccountService blockAccountService, UsuariosDao usuariosDao,
			JwtService jwtService) {
		this.blockAccountService = blockAccountService;
		this.usuariosDao = usuariosDao;
		this.jwtService = jwtService;
		this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String username = authentication.getName(); 
        String token = jwtService.createToken(); 
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
						"Autenticación llevada a cabo con éxito", "Token: " + token, HttpStatus.OK);
		response.setStatus(HttpServletResponse.SC_OK);
		String jsonResponse = objectMapper.writeValueAsString(respuesta);
        response.getWriter().write(jsonResponse);
        
        Usuario usuario = usuariosDao.findByUsername(username);
		blockAccountService.resetearIntentosFallidos(usuario);
	}
	
}
