package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.Localidad;
import com.nicoGuti.optica.modelo.Provincia;
import com.nicoGuti.optica.repositorio.LocalidadRepositorio;
import com.nicoGuti.optica.repositorio.ProvinciaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinciaService {
    private final ProvinciaRepositorio provinciaRepo;
    private final LocalidadRepositorio localidadRepo;


    public ProvinciaService(ProvinciaRepositorio provinciaRepo, LocalidadRepositorio localidadRepo) {
        this.provinciaRepo = provinciaRepo;
        this.localidadRepo=localidadRepo;
    }

    public List<Provincia> listarProvincias(){
        return provinciaRepo.findAll();
    }


  public List<Localidad> listarLocalidadesPorProvincia(long provincia_id){
        return localidadRepo.findByProvinciaId(provincia_id);
  }


}
