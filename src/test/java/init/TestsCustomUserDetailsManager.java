package init;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import init.dao.UsuariosDao;
import init.service.CustomUserDetailsManager;
import init.utilidades.Mapeador;

@ExtendWith(MockitoExtension.class)
public class TestsCustomUserDetailsManager {

	@Mock
	UsuariosDao usuariosDao;
	
	@Mock
	Mapeador mapeador;
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@InjectMocks
	CustomUserDetailsManager customUserDetailsManager;
	
	@Test
	@DisplayName("El usuario carga correctamente desde la base de datos")
	void loadUserByUsername_usuarioCargaCorrectamente() {
		
	}
	
}
