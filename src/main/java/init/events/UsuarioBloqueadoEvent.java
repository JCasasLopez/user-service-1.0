package init.events;

public class UsuarioBloqueadoEvent {
	private final String username;
	private final String email;

	public UsuarioBloqueadoEvent(String username, String email) {
		this.username = username;
		this.email = email;
	}

	public String getUsername() { return username; }
	public String getEmail() { return email; }
}
