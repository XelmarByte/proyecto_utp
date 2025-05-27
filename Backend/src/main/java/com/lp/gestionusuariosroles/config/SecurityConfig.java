package com.lp.gestionusuariosroles.config;

import com.lp.gestionusuariosroles.auth.repository.Token;
import com.lp.gestionusuariosroles.auth.repository.TokenRepository;
import com.lp.gestionusuariosroles.error.CustomAccessDeniedHandler;
import com.lp.gestionusuariosroles.error.CustomAuthenticationEntryPoint;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Configura la cadena principal de filtros de seguridad para la aplicación.
     * Define las políticas de seguridad, manejo de excepciones, control de acceso,
     * gestión de sesiones y configuración del JWT.
     *
     * @param http Objeto HttpSecurity para configurar las reglas de seguridad
     * @return SecurityFilterChain completamente configurado
     * @throws Exception Si es que ocurre algún error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers("/auth/**").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager)
                .logout(logout ->
                        logout
                                .logoutUrl("/auth/logout")
                                .addLogoutHandler(this::logout)
                                .deleteCookies("USER_SESSION")
                                .invalidateHttpSession(true)
                                .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()))
                );


        return http.build();
    }

    /**
     * Maneja el proceso de logout invalidando el token JWT almacenado en las cookies.
     * Realiza las siguientes acciones:
     * 1. Busca la cookie de sesión USER_SESSION
     * 2. Si existe, marca el token como expirado y revocado en la base de datos
     * 3. Limpia el contexto de seguridad de Spring
     *
     * @param request Objeto HttpServletRequest que contiene las cookies
     * @param response Objeto HttpServletResponse
     * @param authentication Objeto Authentication con los detalles del usuario autenticado
     */
    private void logout(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("USER_SESSION")) {
                    String jwt = cookie.getValue();

                    final Token storedToken = tokenRepository.findByToken(jwt).orElse(null);
                    if (storedToken != null) {
                        storedToken.setIsExpired(true);
                        storedToken.setIsRevoked(true);
                        tokenRepository.save(storedToken);
                    }

                    break;
                }
            }
        }
        SecurityContextHolder.clearContext();
    }

    /**
     * Configura la política CORS (Cross-Origin Resource Sharing) para la aplicación.
     * Permite solicitudes desde cualquier origen (*) con los métodos HTTP comunes
     * y los encabezados necesarios para autenticación y contenido JSON.
     *
     * @return CorsConfigurationSource configurado con los permisos CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); //URL DEL FRONTEND
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
