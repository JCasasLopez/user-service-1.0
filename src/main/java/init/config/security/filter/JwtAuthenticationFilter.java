package init.config.security.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import init.config.security.UsuarioSecurity;
import init.dao.TokensDao;
import init.dao.UsuariosDao;
import init.entities.TokenJwt;
import init.entities.TokenStatus;
import init.entities.Usuario;
import init.exception.NoSuchUserException;
import init.exception.TokenNotFoundException;
import init.service.JwtService;
import init.utilidades.Mapeador;
import init.utilidades.StandardResponseHandler;
import io.jsonwebtoken.JwtException;
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
	TokensDao tokensDao;
	
	public JwtAuthenticationFilter(JwtService jwtService, UsuariosDao usuariosDao, Mapeador mapeador,
			StandardResponseHandler standardResponseHandler, TokensDao tokensDao) {
		this.jwtService = jwtService;
		this.usuariosDao = usuariosDao;
		this.mapeador = mapeador;
		this.standardResponseHandler = standardResponseHandler;
		this.tokensDao = tokensDao;
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
			
			//llama al método isTokenValid, y si el token es válido, obtenemos el username al que pertenece
			String username = isTokenValid(token);
			Usuario usuario = usuariosDao.findByUsername(username).orElseThrow(
					() -> new NoSuchUserException(username));

			//Spring Security espera un objeto UserDetails (UsuarioSecurity) como principal para que ciertas 
			//expresiones de seguridad funcionen correctamente
			UsuarioSecurity usuarioSecurity = mapeador.usuarioToUsuarioSecurity(usuario);
			Authentication authentication = new UsernamePasswordAuthenticationToken
					(usuarioSecurity, token, usuarioSecurity.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}

	//Si el token es válido, el método devuelve el username contenido en el token para que el filtro
	//continúe con el proceso de autenticación.
	//Si no es válido, lanzará una excepción que es gestionada por GlobalExceptionHandler.
	private String isTokenValid(String token) {
		//Buscamos el token en la base de datos
		Optional<TokenJwt> tokenJwt = tokensDao.findByToken(token);
		
		//Si el filtro captura la petición es porque se ha incluido un token en el encabezado, así que,
		//en teoría al menos, el token deberia existir en la base de datos. Aún así, nos protegemos 
		//contra la posibilidad de que no sea así.
		if(tokenJwt.isEmpty()) {
			throw new TokenNotFoundException("El token no existe en la base de datos");
		} 
		
		/*Hay dos formas de que el token ya no sea válido:
		La primera es que se haya cambiado su atributo "validez", es decir, el usuario haya 
		abandonado la sesión (validez = LOGGEOUT) o ya haya gastado el token de un solo uso para
		resetear su contraseña (validez = GASTADO).
		Si se da alguno de estos dos casos, el token ya no es válido, y se lanza una excepción*/
		if(tokenJwt.get().getValidez() ==TokenStatus.LOGGED_OUT || 
													tokenJwt.get().getValidez()==TokenStatus.GASTADO) {
			throw new JwtException("El token ya no es válido");
		} else {
			
		/*La segunda ruta para verificar la validez, es comprobar que el token no haya expirado,
		de lo cual se encarga el método extractPayload().
		Si el token no ha expirado y ha pasado por todos los filtros anteriores, podemos concluir que
		es válido, y se devuelve el username al que pertenece*/
			return jwtService.extractPayload(token).getSubject();
		}
	}
}