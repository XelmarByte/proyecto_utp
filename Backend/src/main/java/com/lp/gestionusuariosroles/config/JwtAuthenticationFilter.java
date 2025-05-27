package com.lp.gestionusuariosroles.config;

import com.lp.gestionusuariosroles.auth.repository.TokenRepository;
import com.lp.gestionusuariosroles.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Intercepta las solicitudes entrantes, extrae el JWT de la cookie y lo válida
 * -
 * Si el JWT es válido, crea un objeto de autenticación que representa al usuario y lo almacena
 * en el SecurityContextHolder para ser utilizado por Spring en el proceso de autorización
 * -
 * Si el JWT no es válido, rechaza la solicitud
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login", "/auth/register"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if(isPublicPath(request)){
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("USER_SESSION")) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if(jwt == null){
            filterChain.doFilter(request, response);
            return;
        }

        final String userEmail = jwtService.extractUsername(jwt);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(userEmail == null || authentication != null){
            filterChain.doFilter(request, response);
            return;
        }

        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

        final boolean isTokenExpiredOrRevoked = tokenRepository.findByToken(jwt)
                .map(token -> token.getIsExpired() || token.getIsRevoked())
                .orElse(true);

        if(!isTokenExpiredOrRevoked && jwtService.isTokenValid(jwt, userDetails)){
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(HttpServletRequest request) {
        String uri = request.getServletPath();
        return PUBLIC_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

}
