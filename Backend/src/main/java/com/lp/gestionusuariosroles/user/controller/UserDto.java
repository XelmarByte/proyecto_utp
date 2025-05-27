package com.lp.gestionusuariosroles.user.controller;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Datos que se pueden actualizar de un usuario (la contraseña y estado se actualizan aparte)
public record UserDto(
        String nombres,
        String apellidos,
        String email,
        String rol, // EGRESADO, ADMINISTRADOR, SUPERVISOR
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate fechaNacimiento,
        String telefono,
        String dni
) {
}
