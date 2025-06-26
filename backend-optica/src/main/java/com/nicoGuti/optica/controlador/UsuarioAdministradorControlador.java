package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.UsuarioAdministrador;
import com.nicoGuti.optica.servicio.UsuarioAdministradorServicio;
import com.nicoGuti.optica.util.ApiResponse;
import com.nicoGuti.optica.util.EnvioMail;
import com.nicoGuti.optica.util.FuncionesVarias;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioAdministradorControlador {

    private final UsuarioAdministradorServicio usuarioServicio;

    @Autowired
    private  EnvioMail envioMail;

    @Autowired
    private FuncionesVarias funcionesVarias;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioAdministradorControlador(UsuarioAdministradorServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        List<UsuarioAdministrador> usuarios = usuarioServicio.obtenerTodos();

        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse(true, "No se encontraron usuarios."));
        }

        return ResponseEntity.ok(new ApiResponse(true, usuarios));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<UsuarioAdministrador> usuario = usuarioServicio.buscarPorId(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No se encontró un usuario con ID " + id));
        }

        return ResponseEntity.ok(new ApiResponse(true, usuario.get()));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        Optional<UsuarioAdministrador> usuario = usuarioServicio.buscarPorEmail(email);

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No se encontró un usuario con email " + email));
        }

        return ResponseEntity.ok(new ApiResponse(true, usuario.get()));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> buscarPorUsername(@PathVariable String username) {
        Optional<UsuarioAdministrador> usuario = usuarioServicio.buscarPorUsername(username);

        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No se encontró un usuario con username " + username));
        }

        return ResponseEntity.ok(new ApiResponse(true, usuario.get()));
    }

    @GetMapping("/optica/{opticaId}")
    public ResponseEntity<?> buscarPorOptica(@PathVariable Long opticaId) {
        List<UsuarioAdministrador> usuarios = usuarioServicio.buscarPorOpticaId(opticaId);

        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse(true, "No hay administradores para esa óptica."));
        }

        return ResponseEntity.ok(new ApiResponse(true, usuarios));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody @Valid UsuarioAdministrador usuario, BindingResult result) {

        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Errores de validación", null, errores));
        }
        String passwordGenerada = funcionesVarias.generarPasswordAleatoria();
        String passwordEncriptada = passwordEncoder.encode(passwordGenerada);
        usuario.setPassword(passwordEncriptada);

        UsuarioAdministrador guardado = usuarioServicio.guardar(usuario);
        envioMail.enviarCredenciales(usuario.getEmail(), usuario.getUsername(), passwordGenerada);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Usuario administrador creado correctamente", guardado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (usuarioServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No se encontró un usuario con ID " + id));
        }

        usuarioServicio.eliminar(id);
        return ResponseEntity.ok(new ApiResponse(true, "Usuario eliminado correctamente."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody @Valid UsuarioAdministrador usuario, BindingResult result) {

        // Validaciones automáticas
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Errores de validación", null, errores));
        }

        // Validación de existencia del usuario
        if (!usuarioServicio.buscarPorId(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No se encontró un usuario con ID " + id));
        }

        usuario.setId(id);
        UsuarioAdministrador actualizado = usuarioServicio.guardar(usuario);

        return ResponseEntity.ok(new ApiResponse(true, "Usuario administrador actualizado correctamente", actualizado));
    }

    @PutMapping("/recuperar/{email}")
    public ResponseEntity<?> recuperarClave(@PathVariable String email) {
        Optional<UsuarioAdministrador> usuarioOpt = usuarioServicio.buscarPorMail(email);
        if (usuarioOpt.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false,"No existe un usuario con ese email"));
        }

        UsuarioAdministrador usuario = usuarioOpt.get();
        String token = FuncionesVarias.generarTokenDeClave();

        String hashedToken = passwordEncoder.encode(token);
        usuario.setToken(hashedToken);
        usuario.setTokenExpiracion(LocalDateTime.now().plusMinutes(30));

        usuarioServicio.guardar(usuario);

        envioMail.enviarTokenDeRecuperacion(usuario.getEmail(), token);


        return ResponseEntity.ok(new ApiResponse(true,"Se envió un token de recuperación al correo registrado."));
    }

    @PutMapping("/actualizar-clave/{email}/{token}")
    public ResponseEntity<?> actualizarClave(
            @PathVariable String email,
            @PathVariable String token,
            @RequestBody Map<String, String> body) {

        String nuevaClave = body.get("nuevaClave");

        Optional<UsuarioAdministrador> usuarioOpt = usuarioServicio.buscarPorMail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Usuario no encontrado"));
        }

        UsuarioAdministrador usuario = usuarioOpt.get();

        if (usuario.getTokenExpiracion() == null || usuario.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "El token ha expirado. Por favor solicitá uno nuevo."));
        }


        if (!passwordEncoder.matches(token, usuario.getToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Token inválido"));
        }

        String claveEncriptada = passwordEncoder.encode(nuevaClave);
        usuario.setPassword(claveEncriptada);
        usuario.setToken(null); // Limpia el token luego de usarlo
        usuarioServicio.guardar(usuario);

        return ResponseEntity.ok(new ApiResponse(true, "Contraseña actualizada exitosamente"));
    }


}

