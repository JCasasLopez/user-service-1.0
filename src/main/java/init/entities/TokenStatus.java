package init.entities;

public enum TokenStatus {
    ACTIVO, 
    LOGGED_OUT, // Token invalidado por logout
    GASTADO, // Token ya usado para resetear la contraseña 
    EXPIRADO  
}
