package init.events;

public class UsuarioBorradoEvent {
	private final String username;
	private final String email;

	public UsuarioBorradoEvent(String username, String email) {
		this.username = username;
		this.email = email;
	}

	public String getUsername() { return username; }
	public String getEmail() { return email; }
}
