package com.lp.gestionusuariosroles.auth.controller;

// Datos requeridos para la autenticación de un usuario
public record AuthRequest(
        String email,
        String password
) {
}
