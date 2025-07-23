package com.nicoGuti.optica.repositorio;

import com.nicoGuti.optica.modelo.TokenVerificacion;
import com.nicoGuti.optica.modelo.UsuarioAdministrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenVerificacionRepository extends JpaRepository<TokenVerificacion,Integer> {
    Optional<TokenVerificacion> findByToken(String token);
    void deleteByUsuario(UsuarioAdministrador usuarioAdministrador);
}
