package init.utilidades;

import org.springframework.stereotype.Component;

import init.config.security.UsuarioSecurity;
import init.entities.Usuario;
import init.model.UsuarioDto;

@Component
public class Mapeador {
	
	public Usuario usuarioDtoToUsuario(UsuarioDto usuario) {
		return new Usuario(usuario.getUsername(),
						usuario.getPassword(),
						usuario.getNombreCompleto(),
						usuario.getEmail(),
						usuario.getFechaNacimiento());
	}
	
	public UsuarioDto usuarioToUsuarioDto(Usuario usuario) {
		return new UsuarioDto(usuario.getIdUsuario(),
						usuario.getUsername(),
						usuario.getPassword(),
						usuario.getNombreCompleto(),
						usuario.getEmail(),
						usuario.getFechaNacimiento(),
						usuario.getRoles());
	}
	
	public UsuarioSecurity usuarioToUsuarioSecurity (Usuario usuario) {
		return new UsuarioSecurity(usuario);
	}
	
	public UsuarioSecurity usuarioDtoToUsuarioSecurity(UsuarioDto usuario) {
		Usuario usuarioEntity = usuarioDtoToUsuario(usuario);
		return usuarioToUsuarioSecurity(usuarioEntity);
	}
}
