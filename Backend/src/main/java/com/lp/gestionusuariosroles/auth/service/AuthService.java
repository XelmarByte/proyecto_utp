package com.lp.gestionusuariosroles.auth.service;

import com.lp.gestionusuariosroles.auth.controller.AuthRequest;
import com.lp.gestionusuariosroles.auth.controller.RegisterRequest;
import com.lp.gestionusuariosroles.auth.repository.Token;
import com.lp.gestionusuariosroles.auth.repository.TokenRepository;
import com.lp.gestionusuariosroles.user.repository.User;
import com.lp.gestionusuariosroles.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
    * Autentica al usuario, genera un token JWT y lo agrega en una Cookie
    *
    * @param request Datos necesarios para la autenticación (email y contraseña)
    * @param response Objeto HttpServletResponse para agregar cookies
    * @throws UsernameNotFoundException Si el usuario no existe
    */
    @Transactional
    public void authenticate(AuthRequest request, HttpServletResponse response) {
        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        final String token = jwtService.generateToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, token);

        var cookie = new jakarta.servlet.http.Cookie("USER_SESSION", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpiration)/1000);

        response.addCookie(cookie);
    }

    /**
    * Registra al usuario y lo guarda en la Base de Datos
    *
    * @param request - Datos necesarios para crear un nuevo usuario
    */
    @Transactional
    public void register(RegisterRequest request){

        // Validaciones
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de registro no puede ser nula");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (!request.email().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        if(repository.findByEmail(request.email()).isPresent()){
            throw new IllegalArgumentException("El email ya existe");
        }
        if (!request.password().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial");
        }
        if (request.telefono().length() != 9){
            throw new IllegalArgumentException("El telefono debe tener 9 dígitos");
        }
        if (request.dni().length() != 8){
            throw new IllegalArgumentException("El dni debe tener 8 dígitos");
        }
        if (request.fechaNacimiento() != null && request.fechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }

        repository.save(
            User.builder()
                    .nombres(request.nombres())
                    .apellidos(request.apellidos())
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .rol(request.rol())
                    .fechaNacimiento(request.fechaNacimiento())
                    .telefono(request.telefono())
                    .dni(request.dni())
                    .estado(true)
                    .build()
        );
    }

    /**
    * Guarda el token del usuario en la Base de Datos
    *
    * @param user Objeto {@link com.lp.gestionusuariosroles.user.repository.User} (solo almacena el ID del usuario)
    * @param jwtToken El JWT generado del usuario
    */
    private void saveUserToken(User user, String jwtToken){
        final Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
    * Invalida todos los token de un usuario
    *
    * @param user Objeto {@link com.lp.gestionusuariosroles.user.repository.User} del usuario a invalidar sus tokens
    * */
    private void revokeAllUserTokens(final User user){
        final List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if(!validUserTokens.isEmpty()){
            validUserTokens.forEach(token -> {
                token.setIsRevoked(true);
                token.setIsExpired(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }

    /**
     * Invalida los tokens de un usuario, hace uso de un servicio privado
     * De uso puntual cuando el estado de un usuario se vuelve "false" o cuando se elimina un usuario
     *
     * @param userId Id del usuario a invalidar sus tokens
     */
    @Transactional
    public void revokeAllTokensForUser(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        revokeAllUserTokens(user);
    }


}
