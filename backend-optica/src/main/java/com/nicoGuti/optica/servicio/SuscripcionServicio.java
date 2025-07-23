package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.Optica;
import com.nicoGuti.optica.modelo.Suscripcion;
import com.nicoGuti.optica.modelo.enumeradores.EstadoSuscripcion;
import com.nicoGuti.optica.repositorio.OpticaRepositorio;
import com.nicoGuti.optica.repositorio.SuscripcionRepositorio;
import com.nicoGuti.optica.util.EnvioMail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuscripcionServicio {

    private final SuscripcionRepositorio suscripcionRepository;

    @Autowired
    private EnvioMail envioMail;

    @Autowired
    private OpticaRepositorio opticaRepositorio;



    public Optional<Suscripcion> buscarPorId(long id){
        return suscripcionRepository.findById(id);
    }

    public Optional<Suscripcion> obtenerSuscripcionActivaPorOptica(Optica optica) {
        return suscripcionRepository.findByUsuario_OpticaAndActivaTrue(optica);
    }
    public void eliminar(Long suscripcion_id){
         suscripcionRepository.deleteById(suscripcion_id);
    }

    public Suscripcion guardar(Suscripcion suscripcion) {
        return suscripcionRepository.save(suscripcion);
    }

    public Optional<Suscripcion> buscarPorUsuario(Long idUsuario) {
        return suscripcionRepository.findTopByUsuario_IdAndActivaTrueOrderByIdDesc(idUsuario);
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void verificarSuscripcionesVencidas() {
        List<Suscripcion> activas = suscripcionRepository.findByActivaTrue();

        for (Suscripcion suscripcion : activas) {
            if (suscripcion.getFechaVencimiento().isBefore(LocalDate.now())) {
                suscripcion.setActiva(false);
                suscripcion.setEstado(EstadoSuscripcion.VENCIDA);
                suscripcionRepository.save(suscripcion);
                Optica opticaBaja=suscripcion.getUsuario().getOptica();

                opticaBaja.setActivo(false);
                opticaRepositorio.save(opticaBaja);

                System.out.println("🔕 Suscripción vencida desactivada para óptica: {}"+ suscripcion.getUsuario().getOptica());

                envioMail.enviarAvisoVencimiento(
                        suscripcion.getUsuario().getEmail(),
                        suscripcion.getUsuario().getOptica().getNombre()
                );
            }
        }
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void avisarSuscripcionesPorVencer() {
        List<Suscripcion> activas = suscripcionRepository.findByActivaTrue();

        LocalDate hoy = LocalDate.now();
        LocalDate fechaAviso = hoy.plusDays(2);

        for (Suscripcion suscripcion : activas) {
            if (suscripcion.getFechaVencimiento().isEqual(fechaAviso)) {
                envioMail.enviarAvisoProximoVencimiento(
                        suscripcion.getUsuario().getEmail(),
                        suscripcion.getUsuario().getOptica().getNombre(),
                        suscripcion.getFechaVencimiento()
                );
                System.out.println("📢 Aviso de vencimiento enviado a: " + suscripcion.getUsuario().getEmail());
            }
        }
    }

    public List<Suscripcion> buscarTodasPorUsuario(Long usuarioId) {
        return suscripcionRepository.findAllByUsuarioId(usuarioId);
    }

    public List<Suscripcion> buscarActivasPorUsuario(Long usuarioId) {
        return suscripcionRepository.findByUsuario_IdAndActivaTrue(usuarioId);
    }
}
