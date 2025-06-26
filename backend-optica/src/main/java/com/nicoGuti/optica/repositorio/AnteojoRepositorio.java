package com.nicoGuti.optica.repositorio;

import com.nicoGuti.optica.modelo.Anteojo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnteojoRepositorio extends JpaRepository<Anteojo, Long> {

    public List<Anteojo> findByOpticaId(long id);
}
