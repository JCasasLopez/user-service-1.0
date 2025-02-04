package init.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="tokens")
public class TokenJwt {
	/*No se ha establecido ninguna relación entre las entidades "Token" y "Usuario". El motivo es que 
	no hace falta realmente al contener el mismo token JWT toda la información necesaria.
	
	El token puede estar en 4 estados diferentes de validez (ver enumeración TokenStatus)*/
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idToken;
	
	@Column(unique=true)
	private String token;
	
	@Enumerated(EnumType.STRING)
    private TokenStatus validez;
	
	public TokenJwt(String token) {
		this.token = token;
		this.validez = TokenStatus.ACTIVO;
	}

	public TokenJwt() {
		super();
	}

	public int getIdToken() {
		return idToken;
	}

	public void setIdToken(int idToken) {
		this.idToken = idToken;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public TokenStatus getValidez() {
		return validez;
	}

	public void setValidez(TokenStatus validez) {
		this.validez = validez;
	}
	
}
