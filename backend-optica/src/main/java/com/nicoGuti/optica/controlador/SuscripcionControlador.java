package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.*;
import com.nicoGuti.optica.modelo.dto.RegistroSuscripcionDTO;
import com.nicoGuti.optica.servicio.*;
import com.nicoGuti.optica.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/suscripciones")
public class SuscripcionControlador {

    @Autowired
    private SuscripcionServicio suscripcionServicio;
    @Autowired
    private TipoSuscripcionServicio tipoSuscripcionServicio;
    @Autowired
    private CuponDescuentoServicio cuponServicio;
    @Autowired
    private OpticaServicio opticaServicio;
    @Autowired
    private UsuarioAdministradorServicio usuarioAdminServicio;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody RegistroSuscripcionDTO dto) {

        Optional<Optica> opticaOp = opticaServicio.buscarPorId(dto.getIdOptica());
        if (opticaOp.isEmpty()) {
            return ResponseEntity.ofNullable(new ApiResponse(false, "Optica no encontrada"));
        }

        Optional<TipoSuscripcion> tipoOp = tipoSuscripcionServicio.buscarPorId(dto.getIdTipoSuscripcion());
        if (tipoOp.isEmpty()) {
            return ResponseEntity.ofNullable(new ApiResponse(false, "Tipo de suscripcion no encontrado"));
        }

        UsuarioAdministrador usuario = (UsuarioAdministrador) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (usuario == null) {
            return ResponseEntity.ofNullable(new ApiResponse(false, "Usuario no encontrado"));
        }

        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setUsuario(usuario);
        suscripcion.setTipoSuscripcion(tipoOp.get());
        suscripcion.setFechaInicio(LocalDate.now());
        suscripcion.setActiva(true);

        // ✅ Manejo seguro del cupón
        if (dto.getCodigoCupon() != null && !dto.getCodigoCupon().isEmpty()) {
            Optional<CuponDescuento> cuponOp = cuponServicio.buscarPorCodigo(dto.getCodigoCupon());

            if (cuponOp.isEmpty()) {
                return ResponseEntity.ofNullable(new ApiResponse(false, "Cupón no válido"));
            }

            CuponDescuento cupon = cuponOp.get();

            // aplicar descuento 20%
            BigDecimal descuento = tipoOp.get().getValor().multiply(BigDecimal.valueOf(0.20));
            suscripcion.setDescuentoAplicado(descuento);

            // setear el cupón en la óptica
            opticaOp.get().setCuponUtilizado(cupon);
            cupon.getOpticasReferidas().add(opticaOp.get());

            // si llega a 3 usos, dar 2 meses gratis a la emisora
            if (cupon.getOpticasReferidas().size() >= 3 && !cupon.isBeneficioOtorgado()) {
                Optional<Suscripcion> susEmisoraOp = suscripcionServicio
                        .obtenerSuscripcionActivaPorOptica(cupon.getOpticaEmisora());

                if (susEmisoraOp.isEmpty()) {
                    return ResponseEntity.ofNullable(new ApiResponse(false, "Suscripción emisora no encontrada"));
                }

                Suscripcion susEmisora = susEmisoraOp.get();
                susEmisora.setFechaVencimiento(susEmisora.getFechaVencimiento().plusMonths(2));
                suscripcionServicio.guardar(susEmisora);
                cupon.setBeneficioOtorgado(true);
            }

            cuponServicio.guardar(cupon);
        }

        return ResponseEntity.ok(new ApiResponse(true, "Suscripcion exitosa", suscripcionServicio.guardar(suscripcion)));
    }

}
