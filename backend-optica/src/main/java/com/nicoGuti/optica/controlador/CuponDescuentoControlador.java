package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.CuponDescuento;
import com.nicoGuti.optica.modelo.Optica;
import com.nicoGuti.optica.modelo.dto.CuponDTO;
import com.nicoGuti.optica.servicio.CuponDescuentoServicio;
import com.nicoGuti.optica.servicio.OpticaServicio;
import com.nicoGuti.optica.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cupones")
public class CuponDescuentoControlador {
    @Autowired
    private CuponDescuentoServicio cuponServicio;
    @Autowired
    private OpticaServicio opticaServicio;

    @PostMapping("/generar")
    public ResponseEntity<?> generar(@RequestParam Long idOptica) {
        Optional<Optica> optica = opticaServicio.buscarPorId(idOptica);

        if(optica.isEmpty()){
            return ResponseEntity.ofNullable(new ApiResponse(false,"Optica no encontrada"));
        }


        CuponDescuento cupon = CuponDescuento.builder()
                .codigo("CUPON-" + System.currentTimeMillis())
                .opticaEmisora(optica.get())
                .build();

        CuponDescuento cuponDescuento=cuponServicio.guardar(cupon);

        return ResponseEntity.ok(new ApiResponse(true,"Cupon generado exitosamente",cuponDescuento));
    }

    @GetMapping("/por-optica/{id}")
    public ResponseEntity<?> buscarPorOptica(@PathVariable Long id) {
         Optional<CuponDescuento> cuponDescuentoOp=cuponServicio.buscarPorOpticaId(id);

        System.out.println("Cupon usado por: " + cuponDescuentoOp.get().getOpticasReferidas().size() + " ópticas");


        if(cuponDescuentoOp.isEmpty()){
             return ResponseEntity.ofNullable(new ApiResponse(false,"No se encontro cupon asociado a la optica"));
         }
         CuponDescuento cupon=cuponDescuentoOp.get();
        CuponDTO cuponDto = new CuponDTO(
                cupon.getCodigo(),
                cupon.isBeneficioOtorgado(),
                cupon.getOpticasReferidas().size()
        );


        return ResponseEntity.ok(new ApiResponse(true,"Se encontro exitosamente el cupon",cuponDto));
    }
}
