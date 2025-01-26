package init.config.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import init.entities.Rol;
import init.entities.Usuario;

public class UsuarioSecurity implements UserDetails {
	
	private final Usuario usuario;
	private boolean accountNonLocked;
	
	public UsuarioSecurity(Usuario usuario) {
		this.usuario = usuario;
		this.accountNonLocked = true;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Rol> roles = usuario.getRoles();
		Set<GrantedAuthority> authorities = new HashSet<>();
		for(Rol rol:roles) {
			authorities.add(new SimpleGrantedAuthority(rol.getNombreRol()));
		}
		return authorities;
	}

	@Override
	public String getPassword() {
		return usuario.getPassword();
	}

	@Override
	public String getUsername() {
		return usuario.getUsername();
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}
	
	public void setIsAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}
	
}
