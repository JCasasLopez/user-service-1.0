package init.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="tokens")
public class TokenJwt {
	//No se ha establecido la relación entre las entidades "Token" y "Usuario" como podría parecer lógico.
	//El motivo es que la única razón de ser de esta entidad es realizar el proceso de logout:
	//cuando un usuario se identifica con un token comprobamos que isLoggedOut = false. 
	//Después, en el filtro se usa el método de JwtService extractPayload() para comprobar que el token 
	//aún no haya expirado (así como la firma, etc), en otras palabras, no es estrictamente necesario
	//a qué usuario pertenece el token.
	
	//Si, en un futuro, quisiéramos realizar más operaciones con el token (búsqueda por usuario, fechas, etc)
	//un token JWT ya contiene toda la información necesaria dentro del mismo.
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idToken;
	@Column(unique=true)
	private String token;
	private boolean isLoggedOut;
	
	public TokenJwt(String token) {
		this.token = token;
		this.isLoggedOut = false;
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

	public boolean isLoggedOut() {
		return isLoggedOut;
	}

	public void setLoggedOut(boolean isLoggedOut) {
		this.isLoggedOut = isLoggedOut;
	}

}
