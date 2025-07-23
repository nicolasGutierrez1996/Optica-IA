package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.TokenVerificacion;
import com.nicoGuti.optica.modelo.UsuarioAdministrador;
import com.nicoGuti.optica.servicio.TokenVerificadorServicio;
import com.nicoGuti.optica.servicio.UsuarioAdministradorServicio;
import com.nicoGuti.optica.util.ApiResponse;
import com.nicoGuti.optica.util.EnvioMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/verificador")
public class VerificarControlador {

   @Autowired
   private TokenVerificadorServicio tokenVerificadorServicio;
   @Autowired
   private UsuarioAdministradorServicio usuarioAdministradorServicio;

   @Autowired
   private EnvioMail envioMail;

    @GetMapping("/verificar")
    public ResponseEntity<?> verificarCuenta(@RequestParam String token) {
        Optional<TokenVerificacion> tokenOpt = tokenVerificadorServicio.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Token inválido"));
        }

        TokenVerificacion tokenVerificacion = tokenOpt.get();

        if (tokenVerificacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Token expirado"));
        }

        UsuarioAdministrador usuario = tokenVerificacion.getUsuario();
        usuario.setVerificado(true);
        usuarioAdministradorServicio.guardar(usuario);


        tokenVerificadorServicio.deleteToken(tokenVerificacion);

        return ResponseEntity.ok(new ApiResponse(true,"Cuenta verificada con exito"));
    }

    @PostMapping("/reenviar-verificacion")
    public ResponseEntity<?> reenviarVerificacion(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false,"El email es obligatorio"));
        }

        Optional<UsuarioAdministrador> usuarioOpt = usuarioAdministradorServicio.buscarPorEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false,"Usuario no encontrado"));
        }

        UsuarioAdministrador usuario = usuarioOpt.get();
        if (usuario.isVerificado()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false,"La cuenta ya fue verificada"));
        }

        tokenVerificadorServicio.deleteByUsuario(usuario);


        String token=tokenVerificadorServicio.generarToken(usuario);

        String link = "http://localhost:4200/verificar?token=" + token;
        envioMail.enviarCredenciales(usuario.getEmail(),usuario.getUsername() ,usuario.getNombre(),usuario.getApellido(), link);

        return ResponseEntity.ok(new ApiResponse(true,"Correo reenviado correctamente"));
    }
}
