package init.entities;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="users")
public class Usuario {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idUsuario;
	
	@Column(unique=true)
	@NotBlank()
	private String username;
	
	@NotBlank()
	private String password;
	
	@NotBlank()
	private String nombreCompleto;
	
	@Column(unique=true)
	@NotBlank()
	@Email(message = "El email debe tener un formato válido")
	private String email;
	
	@NotNull()
	private LocalDate fechaNacimiento;
	
	@ManyToMany(fetch = FetchType.EAGER) 
    @JoinTable(name="user_roles",
        joinColumns=@JoinColumn(name="user_id", referencedColumnName="idUsuario"),
        inverseJoinColumns=@JoinColumn(name="rol_id", referencedColumnName="idRol"))
	private Set<Rol> roles;

	public Usuario(@NotBlank String username,
			@NotBlank String password,
			@NotBlank String nombreCompleto,
			@NotBlank @Email(message = "El email debe tener un formato válido") String email,
			@NotNull LocalDate fechaNacimiento) {
		this.username = username;
		this.password = password;
		this.nombreCompleto = nombreCompleto;
		this.email = email;
		this.fechaNacimiento = fechaNacimiento;
	}
	
	public Usuario(@NotBlank String username, 
			@NotBlank String password, @NotBlank String nombreCompleto,
			@NotBlank @Email(message = "El email debe tener un formato válido") String email,
			@NotNull LocalDate fechaNacimiento, 
			Set<Rol> roles) {
		this.username = username;
		this.password = password;
		this.nombreCompleto = nombreCompleto;
		this.email = email;
		this.fechaNacimiento = fechaNacimiento;
		this.roles = roles;
	}

	public Usuario() {
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
