package com.nicoGuti.optica.seguridad;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // Permitimos CORS

                .authorizeHttpRequests(auth -> auth
                        // ENDPOINTS PÚBLICOS
                        .requestMatchers(
                                "/webhook-mercado-pago",
                                "/api/email/contacto",
                                "/api/usuarios/autogenerarUserName",
                                "/api/usuarios/existeUserName/**",
                                "/api/usuarios/existeEmail/**",
                                "/api/verificador/verificar/**",
                                "/api/usuarios/usuarioVerificado/**",
                                "/api/usuarios/verificarContrasenia",
                                "/api/usuarios/recuperar/**",
                                "/api/usuarios/verificar-token/**",
                                "/api/usuarios/actualizar-clave",
                                "/api/usuarios/login",
                                "/api/verificador/reenviar-verificacion/**",
                                "/api/provincia",
                                "/api/provincia/*/localidades",
                                "/api/public/**",
                                "/uploads/**",
                                "/registro/**",
                                "/api/login",
                                "/api/archivos/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .anyRequest().authenticated()
                )

                // Si hay un error de autenticación, devolver 401
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        (request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado")
                ))

                // Registramos el filtro JWT antes del filtro por defecto
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
