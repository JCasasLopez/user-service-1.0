package init.config.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import init.config.security.UsuarioSecurity;
import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.service.JwtService;
import init.utilidades.Mapeador;
import init.utilidades.StandardResponseHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	JwtService jwtService;
	UsuariosDao usuariosDao;
	Mapeador mapeador;
	StandardResponseHandler standardResponseHandler;
	
	public JwtAuthenticationFilter(JwtService jwtService, UsuariosDao usuariosDao, Mapeador mapeador,
			StandardResponseHandler standardResponseHandler) {
		this.jwtService = jwtService;
		this.usuariosDao = usuariosDao;
		this.mapeador = mapeador;
		this.standardResponseHandler = standardResponseHandler;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			
			//Si se llama a "/logout", logUserOut() se encarga de realizar el logout
			if ("POST".equalsIgnoreCase(request.getMethod()) && request.getServletPath().equals("/logout")) {
				String mensaje = jwtService.logUserOut(token);
				standardResponseHandler.handleResponse(response, 200, mensaje, null);
				return; 
			}

			//Si esta línea no lanza una excepción, significa que el token es válido
			//por lo tanto, podemos establecer el objeto authentication en el SecurityContextHolder
			String username = jwtService.extractPayload(token).getSubject();
			Usuario usuario = usuariosDao.findByUsername(username);
			
			//Spring Security espera un objeto UserDetails (UsuarioSecurity) como principal para que ciertas 
			//expresiones de seguridad funcionen correctamente
			UsuarioSecurity usuarioSecurity = mapeador.usuarioToUsuarioSecurity(usuario);
			Authentication authentication = new UsernamePasswordAuthenticationToken
					(usuarioSecurity, token, usuarioSecurity.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}

}
