package init.model;

import java.time.LocalDate;
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
	private int intentosFallidos;
	private boolean cuentaBloqueada;
	
	//UsuarioDto -> Usuario (JPA)
	//Para la creación de un nuevo usuario se asigna el rol "ROLE_USER" automáticamente
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
	
	//Usuario (JPA) -> UsuarioDto 
	//Para pasar la entidad Usuario de la base de datos al front-end
	public UsuarioDto(String username, String nombreCompleto, String email, LocalDate fechaNacimiento, Set<Rol> roles,
			int intentosFallidos, boolean cuentaBloqueada) {
		super();
		this.username = username;
		this.nombreCompleto = nombreCompleto;
		this.email = email;
		this.fechaNacimiento = fechaNacimiento;
		this.roles = roles;
		this.intentosFallidos = intentosFallidos;
		this.cuentaBloqueada = cuentaBloqueada;
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

	public int getIntentosFallidos() {
		return intentosFallidos;
	}

	public void setIntentosFallidos(int intentosFallidos) {
		this.intentosFallidos = intentosFallidos;
	}

	public boolean isCuentaBloqueada() {
		return cuentaBloqueada;
	}

	public void setCuentaBloqueada(boolean cuentaBloqueada) {
		this.cuentaBloqueada = cuentaBloqueada;
	}

}
