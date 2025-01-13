package init.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Table(name="roles")
@Entity
public class Rol {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idRol;
	
	@Column(unique=true)
	private String nombreRol;
	
	@ManyToMany(mappedBy="roles")
	private Set<Usuario> usuarios = new HashSet<>();

	public Rol(int idRol, String nombreRol) {
		this.idRol = idRol;
		this.nombreRol = nombreRol;
	}

	public Rol() {
		super();
	}

	public int getIdRol() {
		return idRol;
	}

	public void setIdRol(int idRol) {
		this.idRol = idRol;
	}

	public String getNombreRol() {
		return nombreRol;
	}

	public void setNombreRol(String nombreRol) {
		this.nombreRol = nombreRol;
	}

	public Set<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(Set<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
	
}
