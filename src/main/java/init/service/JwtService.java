package init.service;

import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	//El token expira a los 1 minutos (1*60*1000 milisegundos)
	private final long expiration = 1 * 60 * 1000;
	
	//@Value("{jwt.secret.key}")
	private String secretKey="RXN0YWVzbWljbGF2ZXNlY3JldGFwZXJmZWN0YWNvbnVubW9udG9uZGVieXRlc1ZpbmRlbDM5ISE=";
	
	public String createTokenJwt() {
		
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
}
