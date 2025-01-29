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

## Características técnicas y decisiones de diseño
La característica técnica más importante del servicio es el uso extensivo de Spring Security para securizarlo. 
************** Mencionar las tablas: users, roles y roles_users y su relación. Tokens es independiente. Los campos para la funcionalidad de bloqueo de cuenta
se han integrado en usuario, priorizando simplicidad sobre escalabilidad (en un futuro me gustaría ampliar esto...)

## Desafíos y soluciones

## Instalación

## Tecnologías utilizadas

