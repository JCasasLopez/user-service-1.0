package init.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import init.config.security.UsuarioSecurity;
import init.entities.StandardResponse;
import init.model.UsuarioDto;
import init.service.BlockAccountService;
import init.service.CustomUserDetailsManager;
import init.service.JwtService;
import init.utilidades.Mapeador;
import jakarta.validation.Valid;

@CrossOrigin("*")
@RestController
public class UsuariosController {

	CustomUserDetailsManager customUserDetailsManager;
	Mapeador mapeador;
	JwtService jwtService;
	BlockAccountService blockAccountService;

	public UsuariosController(CustomUserDetailsManager customUserDetailsManager, Mapeador mapeador,
			JwtService jwtService, BlockAccountService blockAccountService) {
		this.customUserDetailsManager = customUserDetailsManager;
		this.mapeador = mapeador;
		this.jwtService = jwtService;
		this.blockAccountService = blockAccountService;
	}

	//loadUserByUsername() es un método interno usado por Spring Security durante
	//el proceso de autenticación y no debe exponerse directamente a los usuarios

	//***** RECUERDA INCLUIR UNA LISTA "ROLES" VACÍA EN EL JSON ******
	@PostMapping(value="/altaUsuario", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StandardResponse> altaUsuario(@Valid @RequestBody UsuarioDto usuario){
		customUserDetailsManager.passwordIsValid(usuario.getPassword());
		if(usuario.getRoles() == null) {
			//Como es una razón muy específica que nos interesa manejar de forma individual,
			//lanzamos la excepción aquí en vez de delegarla al GlobalExceptionManager
			throw new IllegalArgumentException("Falta la lista vacía de roles en el JSON: 'roles': []");
		}
		UsuarioSecurity usuarioSecurity = new UsuarioSecurity(mapeador.usuarioDtoToUsuario(usuario));
		customUserDetailsManager.createUser(usuarioSecurity);
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), "Usuario creado correctamente", null,
				HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	@DeleteMapping(value="/borrarUsuario", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StandardResponse> borrarUsuario(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		customUserDetailsManager.deleteUser(username);
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
										"Usuario borrado correctamente", null, HttpStatus.OK);
	    return ResponseEntity.status(HttpStatus.OK).body(respuesta);
	}

	@PutMapping(value="/cambiarPassword", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StandardResponse> cambiarPassword(@Valid @RequestParam  String oldPassword, 
			@RequestParam String newPassword){
		customUserDetailsManager.changePassword(oldPassword, newPassword);
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
									"Contraseña cambiada correctamente", null, HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(respuesta);
	}

	/*@GetMapping(value="/usuarioExiste", produces=MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> usuarioExiste(@Valid @RequestParam  String username){
		boolean response = customUserDetailsManager.userExists(username);
		return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(response));
	}*/

	@PostMapping(value="/crearAdmin")
	public ResponseEntity<StandardResponse> crearAdmin(@RequestParam String username){
		customUserDetailsManager.upgradeUser(customUserDetailsManager.findUser(username));
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
							"Usuario promocionado a ADMIN correctamente", null, HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(respuesta);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERADMIN')")
	@PostMapping(value="/desbloquearCuenta")
	public ResponseEntity<StandardResponse> desbloquearCuenta(@RequestParam String username){
		blockAccountService.desbloquearCuenta(username);
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
					"La cuenta del usuario " + username + " ha sido desbloqueada correctamente", 
					null, HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(respuesta);
	}

	/*@GetMapping(value="/usuarioEsAdmin")
	public ResponseEntity<StandardResponse> usuarioEsAdmin(@RequestParam String username){
		boolean usuarioEsAdmin = customUserDetailsManager.isUserAdmin(username);
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
												String.valueOf(usuarioEsAdmin), null, HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(respuesta);
	}*/
}
