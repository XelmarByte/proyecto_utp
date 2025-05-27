package com.lp.gestionusuariosroles.auth.service;

import com.lp.gestionusuariosroles.user.repository.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
    * Extrae el subject (email) del token
    *
    * @param token JWT del que se tiene que extraer el subject
    * @return El texto plano del subject (email)
    */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
    * Genera un nuevo JWT
    *
    * @param user Usuario al que se le generará el token
    * @return El resultado del servicio buildToken
    * */
    public String generateToken(final User user) {return buildToken(user, jwtExpiration);}

    /**
    * Construye el JWT (rol, email, iat, exp)
    *
    * @param user Usuario al que se le construirá el token
    * @param expiration Tiempo de duración del token
    * @return JWT construido
    * */
    public String buildToken(final User user, final long expiration){
        return Jwts.builder()
                .claims(Map.of(
                        "rol", user.getRol()
                ))
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
    * Extrae el Payload del JWT
    *
    * @param token El JWT
    * @return El payload del JWT
    */
    public Claims extractPayload(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
    * Verifica si un token JWT es válido para un usuario específico.
    *
    * @param token Token JWT a validar.
    * @param userDetails Detalles del usuario autenticado.
    * @return `true` si el token es válido (coincide con el usuario y no está expirado),
    *         `false` en caso contrario.
    * @throws io.jsonwebtoken.JwtException Si el token está malformado o es inválido.
    */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Verifica si un token JWT ha expirado.
     *
     * @param token Token JWT a validar.
     * @return `true` si el token está expirado, `false` si aún es válido.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración (claim "exp") del token JWT.
     *
     * @param token Token JWT del cual extraer la fecha.
     * @return Fecha de expiración del token.
     **/
    private Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    /**
     * Genera la clave secreta (SecretKey) usada para firmar/verificar tokens JWT.
     *
     * @return Clave secreta en formato HMAC-SHA.
     */
    private SecretKey getSigningKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
