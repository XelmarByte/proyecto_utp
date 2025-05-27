package com.lp.gestionusuariosroles.user.controller;

import java.time.LocalDate;

// Formato en el que se devolverán los datos de un usuario
public record UserDataResponse(
        Long id,
        String nombres,
        String apellidos,
        String email,
        String rol, // EGRESADO, ADMINISTRADOR, SUPERVISOR
        LocalDate fechaNacimiento,
        Boolean estado, // True o False
        String telefono,
        String dni

) {
}
