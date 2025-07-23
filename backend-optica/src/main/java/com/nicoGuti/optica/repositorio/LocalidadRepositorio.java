package com.nicoGuti.optica.repositorio;

import com.nicoGuti.optica.modelo.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalidadRepositorio extends JpaRepository<Localidad, Long> {
    List<Localidad> findByProvinciaId(Long provinciaId);
}
