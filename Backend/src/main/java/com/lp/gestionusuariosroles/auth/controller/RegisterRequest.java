package com.lp.gestionusuariosroles.auth.controller;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Datos requeridos para el registro de un usuario
public record RegisterRequest(
        String nombres,
        String apellidos,
        String email,
        String password,
        String rol, // EGRESADO, ADMINISTRADOR, SUPERVISOR
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate fechaNacimiento,
        String telefono,
        String dni
) {
}
