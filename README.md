# Gestor de usuarios

## Descripción
El servicio USUARIOS es un microservicio que permite la gestión de usuarios con las funcionalidades típicas. 
Está diseñado para ser utilizado junto con otros microservicios en aplicaciones empresariales. En mi proyecto particular se usa para la gestión y reserva de aulas.

Los usuarios pueden tener 3 tipos de roles diferentes:
- ROLE_USER: o usuario normal. Este rol se asigna automáticamente al crear un nuevo usuario.
- ROLE_ADMIN: administrador. Tiene privilegios especiales, como desbloquear la cuenta de un usuario cuando se han superado los 3 intentos permitidos de autenticación.
- ROLE_SUPERADMIN: Es el encargado de promocionar a usuarios normales a administradores.
  
Funcionalidades:
- CREAR USUARIO: no se permiten usuarios con el mismo username o email; la contraseña tiene que tener al menos 8 caracteres, una letra mayúscula, una letra minúscula,
  un número y un símbolo.
- BORRAR USUARIO: solo el mismo usuario, una vez se ha autenticado, puede borrar su cuenta (ni los administradores ni el superadministrador pueden).
- CAMBIAR CONTRASEÑA: igual que BORRAR USUARIO. La nueva contraseña también se verifica para que cumpla los requisitos de seguridad.
- CREAR ADMINSTRADOR: promoción de un usuario normal a administrador. Como ya se ha comentado más arriba, solo el superadministrador tiene este privilegio.
- DESBLOQUEAR CUENTA: después de los 3 intentos fallidos de autenticación que se permiten, un administrador o el superadministrador tienen que desbloquear la
  cuenta del usuario. La cuenta NO se desbloquea automáticamente pasado un período de tiempo determinado.
- ES USUARIO ADMINISTRADOR: esta funcionalidad sirve para que otros microservicios sepan qué usuarios tienen privilegios
  especiales de administrador para acceder a ciertas funcionalidad protegidas (como la creación y elminación de aulas, etc).

  Para mantener la simplicidad, no se permite la actualización de los datos del usuario (excepto la contraseña, como se ha visto más arriba).

## Estructura del proyecto

## Características técnicas 
Las características técnicas más reseñables del servicio son las siguientes:
1) Uso extensivo de SPRING SECURITY: : el servicio utiliza Spring Security para la autenticación y autorización de usuarios.
   
i) La primera autenticación hace uso del flujo estándar, mediante una implementación personalizada de la interfaz UserDetailsManager, así como implementaciones estándar
de PasswordEncoder (BCrypt), AuthenticationProvider (DaoAuthenticationProvider), etc. También usa un filtro UsernamePasswordAuthenticationFilter personalizado para el login, junto con un AuthenticationFailureHandler y un AuthenticationSuccessHandler para la gestión de los 3 intentos del login y el bloqueo de la cuenta, y un AuthenticationSuccessHandler para la gestión de excepciones relacionadas con la autenticación.

ii) Las autenticaciones posteriores usan tokens JWT que se generan y validan con la libreria JJWT (su versión 0.12.6, cuidado porque la sintaxis ha cambiado en esta versión). La gestión de los mismos se realiza a través de un filtro integrado en SecurityFilterchain (JwtAuthenticationFilter) que intercepta las peticiones y valida los tokens. Los tokens tienen un periodo de expiración de 30 minutos y usan para la firma un algoritmo HS256.

iii)  El logout no hace uso del flujo estándar y el filtro predeterminado LogoutFilter (se gestiona también en el JwtAuthenticationFilter), ya que no se puede integrar este flujo con los tokens JWT. Para solucionarlo, he recurrido al "blacklisting" de los tokens: hay una tabla aparte en la base de datos donde se almacenan todos los tokens y un campo que señala si se ha realizado el logout o no; una vez se ha producido, cambia el valor de este campo, y los token ya no vuelven a ser válidos.

iv) Para la autorización se recurre a la configuración típica del SecurityFilterChain, así como a la seguridad a nivel de métodos (anotación @PreAuthorize).
     
2) Amplia cobertura de TESTS, incluyendo tests unitarios, integrados y end-to-end.
3) Uso de NOTIFICACIONES.
4) OTROS: cabe reseñar el uso de MySQL y JPA Data para la persistencia. Las contraseñas se persisten codificadas mediante la función hash criptográfica BCrypt. Para la gestión de excepciones se usa un GlobalExceptionHandler.

## Decisiones de diseño
Las tablas de la base de datos para los usuarios ("users") y los roles ("roles") tienen la tabla de unión estándar en estos casos ("user_roles"). 
Para la gestión de los intentos fallidos y el bloqueo de cuenta, he optado por campos dentro de la propia tabla "users", en lugar de una tabla aparte relacionada con esta. La razón de esta decisión es intentar mantener la simplicidad al máximo, dentro de un proyecto que aspira a tener unos estándares lo más cercanos a una aplicación profesional. En un futuro me gustaría cambiar este diseño y tener una tabla aparte relacionada con usuarios, que incluya campos como el IP de origen y otros, para desarrollar una funcionalidad que avise al usuario de intentos sospechosos de autenticación.

Respecto a la tabla "tokens" se puede decir exactamente lo mismo que en el caso anterior. Lo lógico en este caso si se hubiera buscado la máxima escalabilidad y flexibilidad, habría sido que estuviese relacionada con la tabla "users". En mi caso, de nuevo priorizando la simplicidad, he optado por mantenerla independiente simplemente con un campo isLoggedOut para saber si el token sigue siendo válido o el usuario ha cerrado ya la sesión, es decir, lo mínimo para poder implementar la funcionalidad de bloqueo de cuenta tras 3 intentos fallidos. De nuevo tengo planes para en un futuro incrementar la sofisticación de este sistema, y que se puedan hacer búsquedas por usuario, auditorías de seguridad, tener estadísticas de duración de sesión, etc.

## Instalación

## Tecnologías utilizadas

