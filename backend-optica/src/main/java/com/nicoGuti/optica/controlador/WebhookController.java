package com.nicoGuti.optica.controlador;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.nicoGuti.optica.configuraciones.MercadoPagoConfig;
import com.nicoGuti.optica.modelo.CuponDescuento;
import com.nicoGuti.optica.modelo.Optica;
import com.nicoGuti.optica.modelo.Suscripcion;
import com.nicoGuti.optica.modelo.enumeradores.EstadoSuscripcion;
import com.nicoGuti.optica.servicio.*;
import com.nicoGuti.optica.util.EnvioMail;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Autowired private MercadoPagoConfig mercadoPagoConfig;
    @Autowired private SuscripcionServicio suscripcionServicio;
    @Autowired private CuponDescuentoServicio cuponDescuentoServicio;
    @Autowired private UsuarioAdministradorServicio usuarioServicio;
    @Autowired private EnvioMail envioMail;

    @Autowired
    private OpticaServicio opticaService;

    @PostMapping("/webhook-mercado-pago")
    @Transactional
    public ResponseEntity<?> recibirWebhookPago(@RequestBody Map<String, Object> body) {
        try {
            logger.info("📩 Webhook recibido: {}", body);

            String topic = (String) body.get("topic");
            if (!"payment".equalsIgnoreCase(topic)) {
                logger.info("➡️ Evento ignorado: {}", topic);
                return ResponseEntity.ok().build();
            }

            Object resourceObj = body.get("resource");
            if (resourceObj == null) {
                logger.warn("⚠️ No se recibió 'resource' en el body");
                return ResponseEntity.badRequest().body("Falta 'resource'");
            }

            String paymentId = resourceObj.toString();
            logger.info("🔎 Consultando pago: {}", paymentId);

            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.parseLong(paymentId), mercadoPagoConfig.getRequestOptions());

            String externalReference = payment.getExternalReference();
            Optional<Suscripcion> suscripcionOp = suscripcionServicio.buscarPorId(Long.parseLong(externalReference));

            if (suscripcionOp.isEmpty()) {
                logger.warn("❌ Suscripción no encontrada: {}", externalReference);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Suscripción no encontrada");
            }

            Suscripcion suscripcion = suscripcionOp.get();

            if (suscripcion.isActiva() && suscripcion.getEstado() == EstadoSuscripcion.APROBADA) {
                logger.info("🔁 Webhook duplicado: la suscripción {} ya estaba activa y aprobada.", suscripcion.getId());
                return ResponseEntity.ok().build();
            }

            if (!"approved".equalsIgnoreCase(payment.getStatus())) {
                logger.info("🚫 Pago no aprobado: {}", payment.getStatus());
                suscripcion.setEstado(EstadoSuscripcion.RECHAZADA);
                suscripcion.setActiva(false);
                suscripcionServicio.guardar(suscripcion);
                envioMail.enviarMailPagoFallido(suscripcion.getUsuario().getEmail(),suscripcion.getUsuario().getOptica().getNombre());
                return ResponseEntity.ok().build();
            }


            List<Suscripcion> otrasActivas = suscripcionServicio.buscarActivasPorUsuario(suscripcion.getUsuario().getId());
            for (Suscripcion otra : otrasActivas) {
                if (!otra.getId().equals(suscripcion.getId())) {
                    otra.setActiva(false);
                    otra.setEstado(EstadoSuscripcion.VENCIDA);
                    suscripcionServicio.guardar(otra);
                }
            }

            logger.info("✅ Pago aprobado para suscripción {}", suscripcion.getId());
            suscripcion.setEstado(EstadoSuscripcion.APROBADA);
            suscripcion.setActiva(true);
            suscripcionServicio.guardar(suscripcion);

            // 🎁 Cupón
            Optica optica = suscripcion.getUsuario().getOptica();
            if (optica.getCuponUtilizado() != null) {
                CuponDescuento cupon = cuponDescuentoServicio.buscarPorCodigo(
                        optica.getCuponUtilizado().getCodigo()).orElse(null);
                if (cupon != null) {
                    cupon.getOpticasReferidas().add(optica);
                    if (cupon.getOpticasReferidas().size() > 2 && !cupon.isBeneficioOtorgado()) {
                        cupon.setBeneficioOtorgado(true);
                        usuarioServicio.buscarUserPorOptica(cupon.getOpticaEmisora()).ifPresent(
                                emisor -> envioMail.enviarMailBeneficioPorCupon(
                                        emisor.getEmail(), cupon.getOpticaEmisora().getNombre()
                                )
                        );
                    }
                    cuponDescuentoServicio.guardar(cupon);
                }
            }
            optica.setActivo(true);
            opticaService.guardar(optica);
            // ✉️ Mail de confirmación
            envioMail.enviarMailSuscripcionExitosa(
                    suscripcion.getUsuario().getEmail(),
                    optica.getNombre(),
                    suscripcion.getTipoSuscripcion().getNombre()
            );

            logger.info("📬 Webhook procesado correctamente.");
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("🔥 Error procesando webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando webhook");
        }
    }

}
