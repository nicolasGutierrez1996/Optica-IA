package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.CuponDescuento;
import com.nicoGuti.optica.repositorio.CuponDescuentoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuponDescuentoServicio {
    private final CuponDescuentoRepositorio cuponDescuentoRepository;

    public Optional<CuponDescuento> buscarPorCodigo(String codigo) {
        return cuponDescuentoRepository.findByCodigo(codigo);
    }
    public void eliminar(Long cupon_id){
        cuponDescuentoRepository.deleteById(cupon_id);
    }
    public CuponDescuento guardar(CuponDescuento cupon) {
        return cuponDescuentoRepository.save(cupon);
    }

    public Optional<CuponDescuento> buscarPorOpticaId(long optica_id){
        return cuponDescuentoRepository.findByOpticaEmisora_Id(optica_id);
    }
}
