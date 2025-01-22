package init.service;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	//El token expira a los 1 minutos (1*60*1000 milisegundos)
	private final long expiration = 1 * 60 * 1000;
	
	//@Value("{jwt.secret.key}")
	private String secretKey = "RXN0YWVzbWljbGF2ZXNlY3JldGFwZXJmZWN0YWNvbnVubW9udG9uZGVieXRlc1ZpbmRlbDM5ISE=";
	
	public String createToken() {
		
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        SecretKey clave = Keys.hmacShaKeyFor(keyBytes);
        
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();
		
		return  	Jwts
						.builder()
						.header() 
						.type("JWT")
							.and()
						.subject(authenticated.getName()) 
						.claim("roles", authenticated.getAuthorities().stream()
				                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
						.issuedAt(new Date(System.currentTimeMillis()))
						.expiration(new Date(System.currentTimeMillis() + expiration))
						.signWith(clave, Jwts.SIG.HS256)
						.compact();
	}
	
	public boolean validateToken(String token) {
		
		try {
			Jws<Claims> parsedToken = Jwts.builder()
					.setSigningKey(clave) 
					.parseClaimsJws(jwt);

			// Acceder a las claims
			String subject = parsedToken.getSubject();
			String issuer = parsedToken.getBody().getIssuer();
			Date expiration = parsedToken.getBody().getExpiration();

		} catch (JwtException e) {
			// Capturar cualquier error de validación
			System.out.println("Token inválido: " + e.getMessage());
		}
	}
}
