package com.nicoGuti.optica.repositorio;

import com.nicoGuti.optica.modelo.UsuarioAdministrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioAdministradorRepositorio extends JpaRepository<UsuarioAdministrador, Long> {
    Optional<UsuarioAdministrador> findByUsername(String username);
    Optional<UsuarioAdministrador> findByEmail(String email);
    List<UsuarioAdministrador> findByOpticaId(Long opticaId);

    @Query("SELECT u.token FROM UsuarioAdministrador u WHERE u.email = :email")
    Optional<String> findTokenByEmail(@Param("email") String email);
}
