package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.Anteojo;
import com.nicoGuti.optica.repositorio.AnteojoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnteojoServicio {
    private final AnteojoRepositorio anteojoRepo;

    public AnteojoServicio(AnteojoRepositorio anteojoRepo) {
        this.anteojoRepo = anteojoRepo;
    }

    public List<Anteojo> obtenerTodos() {
        return anteojoRepo.findAll();
    }

    public List<Anteojo> obtenerPorOptica(Long opticaId) {
        return anteojoRepo.findByOpticaId(opticaId);
    }

    public Anteojo guardar(Anteojo anteojo) {
        return anteojoRepo.save(anteojo);
    }

    public void eliminar(Long id) {
        anteojoRepo.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return anteojoRepo.existsById(id);
    }
    public Optional<Anteojo>buscarPorId(Long id){
        return anteojoRepo.findById(id);
    }
}