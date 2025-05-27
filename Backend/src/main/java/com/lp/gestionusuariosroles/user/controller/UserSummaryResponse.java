package com.lp.gestionusuariosroles.user.controller;

// Formato en el que se devolverán los datos de todos los usuarios (para listas)
public record UserSummaryResponse(
        Long id,
        String nombres,
        String apellidos,
        String email,
        String rol, // EGRESADO, ADMINISTRADOR, SUPERVISOR
        Boolean estado // True o False
) {
}
