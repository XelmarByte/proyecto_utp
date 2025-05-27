package com.lp.gestionusuariosroles.user.controller;

// Datos requeridos para actualizar la contraseña
public record NewPasswordRequest(
        String currentPassword,
        String newPassword
) {
}
