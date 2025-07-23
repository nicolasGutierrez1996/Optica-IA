package com.nicoGuti.optica.seguridad;


import com.nicoGuti.optica.modelo.UsuarioAdministrador;
import com.nicoGuti.optica.servicio.UsuarioAdministradorServicio;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioAdministradorServicio usuarioServicio;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        System.out.println("➡️ Filtro evaluando path: " + path);
        return path.startsWith("/api/archivos/")
                || path.startsWith("/uploads/") // por si accedés directo
                || path.equals("/webhook-mercado-pago");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // no hay token, continuar sin autenticación
            return;
        }

        final String token = authHeader.substring(7); // remover "Bearer "

        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response); // token inválido, continuar sin autenticación
            return;
        }

        String username = jwtUtil.extractUsername(token);
        System.out.println("👤 Username extraído del token: " + username);

        UsuarioAdministrador usuario = usuarioServicio.buscarPorUsername(username).orElse(null);

        if (usuario != null) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(usuario, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("✅ Contexto luego de seteo: " + SecurityContextHolder.getContext().getAuthentication());


        }

        filterChain.doFilter(request, response);
    }
}