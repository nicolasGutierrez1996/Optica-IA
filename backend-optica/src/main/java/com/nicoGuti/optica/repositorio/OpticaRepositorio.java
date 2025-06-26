package com.nicoGuti.optica.repositorio;

import com.nicoGuti.optica.modelo.Optica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpticaRepositorio extends JpaRepository<Optica, Long> {
    Optional<Optica> findByEmail(String email);

}
