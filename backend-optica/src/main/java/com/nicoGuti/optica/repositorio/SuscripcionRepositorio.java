package com.nicoGuti.optica.repositorio;

import com.nicoGuti.optica.modelo.Optica;
import com.nicoGuti.optica.modelo.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuscripcionRepositorio extends JpaRepository<Suscripcion,Long> {
    Optional<Suscripcion> findByUsuario_OpticaAndActivaTrue(Optica optica);
    Optional<Suscripcion> findTopByUsuario_IdAndActivaTrueOrderByIdDesc(Long idUsuario);

    List<Suscripcion> findByActivaTrue();

    List<Suscripcion> findAllByUsuarioId(Long usuarioId);

    List<Suscripcion> findByUsuario_IdAndActivaTrue(Long usuarioId);


}
