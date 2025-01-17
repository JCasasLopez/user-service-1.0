package init.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import init.entities.Rol;

public class UsuarioDto {
	private int idUsuario;
	private String username;
	private String password;
	private String nombreCompleto;
	private String email;
	private LocalDate fechaNacimiento;
	private Set<Rol> roles;
	
	//UsuarioDto -> Usuario (JPA)
	//Para la creación de un nuevo usuario se asigna el rol "ROLE_USER" automáticamente
	public UsuarioDto(String username, String password, String nombreCompleto, String email,
			LocalDate fechaNacimiento) {
		this.username = username;
		this.password = password;
		this.nombreCompleto = nombreCompleto;
		this.email = email;
		this.fechaNacimiento = fechaNacimiento;
		this.roles = new HashSet<>();
		this.roles.add(new Rol("ROLE_USER"));
	}
	
	//Usuario (JPA) -> UsuarioDto
	public UsuarioDto(int idUsuario, String username, String password, String nombreCompleto, String email,
			LocalDate fechaNacimiento, Set<Rol> roles) {
		this.idUsuario = idUsuario;
		this.username = username;
		this.password = password;
		this.nombreCompleto = nombreCompleto;
		this.email = email;
		this.fechaNacimiento = fechaNacimiento;
		this.roles = roles;
	}

	public UsuarioDto() {
		super();
	}
	
	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Set<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}

	
}
