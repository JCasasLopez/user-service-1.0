package init.config.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import init.dao.UsuariosDao;
import init.entities.StandardResponse;
import init.entities.Usuario;
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
	ObjectMapper objectMapper;

	public CustomAuthenticationFailureHandler(BlockAccountService blockAccountService, 
			UsuariosDao usuariosDao, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.blockAccountService = blockAccountService;
		this.usuariosDao = usuariosDao;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
		this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
			response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
			StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
							"No existe el usuario " + username, null, HttpStatus.NOT_FOUND);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			String jsonResponse = objectMapper.writeValueAsString(respuesta);
	        response.getWriter().write(jsonResponse);
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
