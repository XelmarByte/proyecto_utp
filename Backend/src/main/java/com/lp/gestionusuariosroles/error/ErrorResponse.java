package com.lp.gestionusuariosroles.error;

// Formato en el que se devolverán los errores que sucedan
public record ErrorResponse(
        int status,
        String error,
        String message
) {
}
