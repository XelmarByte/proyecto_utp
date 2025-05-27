package com.lp.gestionusuariosroles.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Maneja las solicitudes de los usuarios no autenticados o deshabilitados
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setContentType("application/json");

        if (authException instanceof DisabledException) {
            // Usuario deshabilitado (isEnabled=false)
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("{\"error\": \"Usuario deshabilitado\", \"message\": \"Contacta al administrador\"}");

        } else {
            // No autenticado (token inválido/ausente)
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"error\": \"No autenticado\", \"message\": \"Credenciales inválidas\"}");
        }
    }
}
