package com.lp.gestionusuariosroles.config;

import com.lp.gestionusuariosroles.user.repository.User;
import com.lp.gestionusuariosroles.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository repository;

    /**
     * Crea y configura un UserDetailsService personalizado para Spring Security.
     * Carga los detalles del usuario desde la base de datos usando el email como nombre de usuario.
     *
     * @return Una implementación de UserDetailsService que busca usuarios por email
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = repository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(username)
                    .password(user.getPassword())
                    .authorities(new SimpleGrantedAuthority(user.getRol()))
                    .disabled(!user.isEnabled())
                    .build();
        };
    }

    /**
     * Configura el AuthenticationManager principal para la aplicación.
     * Utiliza DaoAuthenticationProvider con el UserDetailsService personalizado y el codificador de contraseñas.
     *
     * @param userDetailsService El servicio para cargar detalles de usuario
     * @param passwordEncoder El codificador de contraseñas a utilizar
     * @return AuthenticationManager configurado para la autenticación
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    /**
     * Proporciona el codificador de contraseñas que se usará en la aplicación.
     * Utiliza el algoritmo BCrypt para el hashing seguro de contraseñas.
     *
     * @return Instancia de BCryptPasswordEncoder como implementación de PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}


}
