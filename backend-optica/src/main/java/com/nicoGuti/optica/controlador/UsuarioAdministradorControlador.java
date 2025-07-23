package com.nicoGuti.optica.controlador;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.nicoGuti.optica.configuraciones.MercadoPagoConfig;
import com.nicoGuti.optica.modelo.*;
import com.nicoGuti.optica.modelo.dto.PerfilDTO;
import com.nicoGuti.optica.modelo.enumeradores.EstadoSuscripcion;
import com.nicoGuti.optica.seguridad.JwtUtil;
import com.nicoGuti.optica.servicio.*;
import com.nicoGuti.optica.util.ApiResponse;
import com.nicoGuti.optica.util.EnvioMail;
import com.nicoGuti.optica.util.FuncionesVarias;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Autowired
    private TokenVerificadorServicio tokenServicio;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OpticaServicio opticaService;

    @Autowired
    private CuponDescuentoServicio cuponDescuentoServicio;

    @Autowired
    private SuscripcionServicio suscripcionServicio;

    @Autowired
    private TipoSuscripcionServicio tipoSuscripcionServicio;

    @Autowired
    private MercadoPagoConfig mercadoPagoConfig;



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
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        usuario.setVerificado(false);
        UsuarioAdministrador guardado = usuarioServicio.guardar(usuario);

        // Generar token de verificación
        String token = tokenServicio.generarToken(guardado);

        // Enviar mail con enlace
        String link = "http://localhost:4200/verificar?token=" + token;

        envioMail.enviarCredenciales(usuario.getEmail(), usuario.getUsername(), usuario.getNombre(), usuario.getApellido(),link);


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

    @GetMapping("/verificar-token/{email}/{token}")
    public ResponseEntity<?> verificarToken(
            @PathVariable String email,
            @PathVariable String token) {

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

        return ResponseEntity.ok(new ApiResponse(true, "Token válido"));
    }


    @PutMapping("/actualizar-clave")
    public ResponseEntity<?> actualizarClave(@RequestBody Map<String, String> body) {

        String nuevaClave = body.get("nuevaClave");
        String email=body.get("email");

        Optional<UsuarioAdministrador> usuarioOpt = usuarioServicio.buscarPorMail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Usuario no encontrado"));
        }

        if (nuevaClave == null || nuevaClave.trim().isEmpty() || nuevaClave.length()<6 || nuevaClave.length()>100) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "La nueva clave es invalida."));
        }

        UsuarioAdministrador usuario = usuarioOpt.get();


        String claveEncriptada = passwordEncoder.encode(nuevaClave);
        usuario.setPassword(claveEncriptada);
        usuario.setToken(null);
        usuarioServicio.guardar(usuario);

        return ResponseEntity.ok(new ApiResponse(true, "Contraseña actualizada exitosamente"));
    }


    @PostMapping("/autogenerarUserName")
    public ResponseEntity<?> autogenerarUserName(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        String apellido = body.get("apellido");

        if (nombre == null || nombre.trim().isEmpty() || apellido == null || apellido.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Nombre y apellido son obligatorios"));
        }

        String base = (nombre.trim().toLowerCase().charAt(0) + apellido.trim().toLowerCase())
                .replaceAll("[^a-z0-9]", "");

        // 🔐 Asegurarse que base tenga al menos 6 caracteres
        if (base.length() < 6) {
            base += "123456";
            base = base.substring(0, 6);
        }

        String username = base;
        int contador = 1;

        while (usuarioServicio.existeUserName(username)) {
            username = base + contador;
            contador++;
        }

        return ResponseEntity.ok(new ApiResponse(true, "Se autogeneró correctamente", username));
    }

    @GetMapping("existeUserName/{username}")
    public ResponseEntity<?> existeUserName(@PathVariable String username){
        if(username==null || username.length()<6){
            return ResponseEntity.ofNullable(new ApiResponse(false,"El user name ingresado es invalido"));
        }
        else if(usuarioServicio.existeUserName(username)){
            return ResponseEntity.ok(new ApiResponse(true,"El user name ya existe",true));
        }else{
            return ResponseEntity.ok(new ApiResponse(true,"El user name no existe",false));

        }
    }

    @GetMapping("existeEmail/{email}")
    public ResponseEntity<?> existeEmail(@PathVariable String email){
        if(email==null || email.length()>100 || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            return ResponseEntity.ofNullable(new ApiResponse(false,"El email ingresado es invalido"));
        }
        else if(usuarioServicio.existeEmail(email)){
            return ResponseEntity.ok(new ApiResponse(true,"El email ya existe",true));
        }else{
            return ResponseEntity.ok(new ApiResponse(true,"El email no existe",false));

        }
    }


    @GetMapping("usuarioVerificado/{username}")
    public ResponseEntity<ApiResponse> esUsuarioVerificado(@PathVariable String username) {
        if (username == null || username.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "El usuario ingresado es inválido"));
        }

        Optional<Boolean> verificadoOpt = usuarioServicio.esUsuarioVerificado(username);

        if (verificadoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "El username ingresado no existe"));
        }

        if (!verificadoOpt.get()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "El usuario todavía no se encuentra verificado"));
        }

        return ResponseEntity.ok(new ApiResponse(true, "Usuario verificado"));
    }
    @PostMapping("/verificarContrasenia")
    public ResponseEntity<?> verificarContrasenia(@RequestBody Map<String, String> body){
        String username = body.get("username");
        String contrasenia = body.get("contrasenia");

        if (username == null || username.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "El usuario ingresado es inválido"));
        }
        if (contrasenia == null || contrasenia.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "La contraseña ingresada es inválida"));
        }
       Optional<UsuarioAdministrador> usuarioOp= usuarioServicio.buscarPorUsername(username);

        if(usuarioOp.isEmpty()){
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "El username ingresado no existe"));
        }
        UsuarioAdministrador usuario=usuarioOp.get();


        if (!passwordEncoder.matches(contrasenia, usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Contraseña invalida"));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Contraseña válida"));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");

        Optional<UsuarioAdministrador> usuarioOp = usuarioServicio.buscarPorUsername(username);

        if (usuarioOp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Usuario no encontrado"));
        }

        UsuarioAdministrador usuario = usuarioOp.get();


        String token = jwtUtil.generateToken(usuario);




        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("id", usuario.getId());
        usuarioMap.put("username", usuario.getUsername());
        usuarioMap.put("optica", usuario.getOptica());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("usuario", usuarioMap);

        return ResponseEntity.ok(new ApiResponse(true, "Login exitoso", data, null));
    }

    @PostMapping("/suscribirse")
    public ResponseEntity<?> iniciarSuscripcion(@RequestBody Map<String, String> datos) {
        System.out.println("🧪 Datos recibidos en suscripción: " + datos);

        Long usuarioId = Long.parseLong(datos.get("usuarioId"));
        String dniUsuario = datos.get("dniUsuario");
        Long opticaId = Long.parseLong(datos.get("opticaId"));
        Long tipoSuscripcionId = Long.parseLong(datos.get("tipoSuscripcionId"));
        String cuponCodigo = datos.get("cupon");

        Optional<UsuarioAdministrador> usuarioOp = usuarioServicio.buscarPorId(usuarioId);
        Optional<Optica> opticaOp = opticaService.buscarPorId(opticaId);
        Optional<TipoSuscripcion> tipoSuscripcionOp = tipoSuscripcionServicio.buscarPorId(tipoSuscripcionId);

        if (cuponCodigo != null && !cuponCodigo.trim().isEmpty()) {


            Optional<CuponDescuento> cuponDescuento = cuponDescuentoServicio.buscarPorCodigo(cuponCodigo);


            if (cuponDescuento.isEmpty()) {
                return ResponseEntity.ofNullable(new ApiResponse(false, "El cupón ingresado no existe"));
            }



            CuponDescuento cupon = cuponDescuento.get();

            if (opticaOp.get().getCuponUtilizado() != null) {
                return ResponseEntity.ofNullable(new ApiResponse(false, "Ya usaste un cupón previamente, no podés usar otro."));
            }

            if (cupon.getOpticaEmisora().getId().equals(opticaOp.get().getId())) {
                return ResponseEntity.ofNullable(new ApiResponse(false, "No podés usar tu propio cupón"));
            }

            Optional<Suscripcion> ultimaSuscripcion = suscripcionServicio.buscarPorUsuario(usuarioId);
            if (ultimaSuscripcion.isPresent()) {
                return ResponseEntity.ofNullable(new ApiResponse(false, "Solo ópticas nuevas pueden usar un cupón de referido."));
            }


            if (cupon.getOpticasReferidas().size() > 2 || cupon.isBeneficioOtorgado()) {
                return ResponseEntity.ofNullable(new ApiResponse(false, "El cupón ya no está vigente"));
            }
        }

        if (usuarioOp.isEmpty() || opticaOp.isEmpty() || tipoSuscripcionOp.isEmpty()) {
            return ResponseEntity.ofNullable(new ApiResponse(false, "Datos inválidos de usuario, óptica o suscripción."));
        }

        Optica optica = opticaOp.get();
        UsuarioAdministrador usuario = usuarioOp.get();
        TipoSuscripcion tipoSuscripcion = tipoSuscripcionOp.get();

        if (!usuario.getOptica().getId().equals(optica.getId())) {
            return ResponseEntity.ofNullable(new ApiResponse(false, "La óptica seleccionada no pertenece al usuario."));
        }

        usuario.setDni(dniUsuario);


        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setTipoSuscripcion(tipoSuscripcion);
        suscripcion.setUsuario(usuario);
        suscripcion.setFechaInicio(LocalDate.now());
        suscripcion.setFechaVencimiento(LocalDate.now().plusDays(30));
        suscripcion.setEstado(EstadoSuscripcion.PENDIENTE_PAGO);
        suscripcion.setActiva(false);

        if (cuponCodigo != null && !cuponCodigo.trim().isEmpty()) {
            CuponDescuento cupon = cuponDescuentoServicio.buscarPorCodigo(cuponCodigo).get();


            suscripcion.setDescuentoAplicado(BigDecimal.valueOf(20.00));
            BigDecimal descuento = tipoSuscripcion.getValor()
                    .multiply(BigDecimal.valueOf(0.20))
                    .setScale(2);
            suscripcion.setMontoFinal(tipoSuscripcion.getValor().subtract(descuento));
            optica.setCuponUtilizado(cupon);
        } else {
            suscripcion.setMontoFinal(tipoSuscripcion.getValor());
        }

        usuarioServicio.guardar(usuario);
        opticaService.guardar(optica);
        Suscripcion suscripcionGuardada = suscripcionServicio.guardar(suscripcion);

        // Generar link de pago
        String linkDePago;
        try {
             linkDePago = generarLinkPago(
                    tipoSuscripcion.getNombre(),
                    "Suscripción a " + tipoSuscripcion.getNombre(),
                    suscripcionGuardada.getMontoFinal(),
                    String.valueOf(suscripcionGuardada.getId()) // <- importante
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error al generar link de pago: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "mensaje", "Suscripción iniciada correctamente. Redirigiendo al pago...",
                "linkPago", linkDePago
        ));
    }

    public String generarLinkPago(String nombrePlan, String descripcion, BigDecimal precio, String externalReference) {
        PreferenceClient preferenceClient = new PreferenceClient();

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title(nombrePlan)
                .description(descripcion)
                .quantity(1)
                .unitPrice(precio)
                .currencyId("ARS")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(itemRequest))
                .externalReference(externalReference)
                .notificationUrl("https://18b434009c90.ngrok-free.app/webhook-mercado-pago") // 👈 esta es la línea que te falta
                .build();

        try {
            Preference preference = preferenceClient.create(preferenceRequest, mercadoPagoConfig.getRequestOptions());
            return preference.getInitPoint();
        } catch (MPException | MPApiException e) {
            throw new RuntimeException("Error al generar preferencia de pago", e);
        }
    }

    @GetMapping("/suscripcion/{idUsuario}")
    public ResponseEntity<?> obtenerSuscripcionPorUsuario(@PathVariable Long idUsuario) {
        Optional<Suscripcion> suscripcionOp = suscripcionServicio.buscarPorUsuario(idUsuario);

        return suscripcionOp
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/mi-perfil")
    public ResponseEntity<?> obtenerMiPerfil(Authentication authentication) {
        System.out.println("📥 Entró al endpoint /api/usuarios/mi-perfil");
        System.out.println("👤 Usuario autenticado: " + authentication);
        UsuarioAdministrador usuario = (UsuarioAdministrador) authentication.getPrincipal();


        Optional<UsuarioAdministrador> usuarioOpt = usuarioServicio.buscarPorUsername(usuario.getUsername());
        System.out.println("👤 Usuario ENCONTRADO POR USERNAME: " + authentication);


        if (usuarioOpt.isPresent()) {
            System.out.println("devuelvo ok");
            return ResponseEntity.ok(new ApiResponse(true,"Perfil obtenido correctamente",usuarioOpt.get()));
        } else {
            System.out.println("Error");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/dni")
    public ResponseEntity<?> obtenerDniPorId(@PathVariable Long id) {

        System.out.println("🟢 Llegó al controlador de DNI");

        Optional<UsuarioAdministrador> usuarioAdministradorOp=usuarioServicio.buscarPorId(id);

        if(usuarioAdministradorOp.isEmpty()){
            return ResponseEntity.ofNullable(new ApiResponse(false,"No se encontro usuario con ese ID"));

        }
        UsuarioAdministrador usuarioAdministrador=usuarioAdministradorOp.get();

       return ResponseEntity.ok(new ApiResponse(true,"DNI encontrado",usuarioAdministrador.getDni()));
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id, @RequestBody PerfilDTO perfilDTO) {
        Optional<UsuarioAdministrador> usuarioOp = usuarioServicio.buscarPorId(id);
        if (usuarioOp.isEmpty()) {
            return ResponseEntity.ofNullable(new ApiResponse(false,"Usuario no encontrado"));
        }

        UsuarioAdministrador usuario = usuarioOp.get();

        Optional<UsuarioAdministrador> usuariosConEseEmail = usuarioServicio.buscarPorMail(perfilDTO.getEmail());
        boolean otroUsuarioYaLoUsa = usuariosConEseEmail.stream()
                .anyMatch(u -> !u.getId().equals(id));

        if (otroUsuarioYaLoUsa) {
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Ya existe otro usuario con ese email"));
        }

        Optional<UsuarioAdministrador> usuariosConEseDni = usuarioServicio.buscarPorDni(perfilDTO.getDni());
        boolean otroDniYaUsado = usuariosConEseDni.stream()
                .anyMatch(u -> !u.getId().equals(id));

        if (otroDniYaUsado) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Ya existe otro usuario con ese DNI"));
        }

        usuario.setNombre(perfilDTO.getNombre());
        usuario.setApellido(perfilDTO.getApellido());
        usuario.setDni(perfilDTO.getDni());
        usuario.setEmail(perfilDTO.getEmail());

        usuarioServicio.guardar(usuario);

        return ResponseEntity.ok(new ApiResponse(true,"Edicion exitosa"));
    }

    @PostMapping("/confirmar-password")
    public ResponseEntity<?> confirmarPassword(@RequestBody Map<String, String> body) {
        try {
            Long usuarioId = Long.parseLong(body.get("usuarioId"));
            String passwordIngresado = body.get("password");

            Optional<String> passwordOptional = usuarioServicio.buscarContraseniaPorId(usuarioId);

            if (!passwordOptional.isPresent()) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "No se encontró contraseña del usuario"));
            }

            String passwordHasheado = passwordOptional.get();

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean esValido = encoder.matches(passwordIngresado, passwordHasheado);

            if (esValido) {
                return ResponseEntity.ok(new ApiResponse(true,"contraseña correcta"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false,"Contraseña incorrecta"));

            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false,"Error al realizar la consulta"));

        }
    }
}












