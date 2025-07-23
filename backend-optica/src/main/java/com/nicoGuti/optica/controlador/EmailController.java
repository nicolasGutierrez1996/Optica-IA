package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.Contacto;
import com.nicoGuti.optica.util.ApiResponse;
import com.nicoGuti.optica.util.EnvioMail;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EnvioMail envioMail;

    @PostMapping("/contacto")
    public ResponseEntity<?> enviarContacto(@RequestBody @Valid Contacto contacto, BindingResult result) {
        try {
            String errores = result.getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Datos inválidos:"+errores,contacto));
            }
            envioMail.enviarMailContacto(contacto);
            return ResponseEntity.ok(new ApiResponse(true,"Contacto enviado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false,"Ocurrio un error al enviar el mail de contacto"));
        }
    }
}