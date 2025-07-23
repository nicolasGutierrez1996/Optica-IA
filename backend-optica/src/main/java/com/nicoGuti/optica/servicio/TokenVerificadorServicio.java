package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.TokenVerificacion;
import com.nicoGuti.optica.modelo.UsuarioAdministrador;
import com.nicoGuti.optica.repositorio.TokenVerificacionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenVerificadorServicio {

    @Autowired
    private TokenVerificacionRepository tokenRepo;



    public Optional<TokenVerificacion> findByToken(String token){
        return tokenRepo.findByToken(token);
    }

    public void deleteToken(TokenVerificacion tokenVerificacion){
        tokenRepo.delete(tokenVerificacion);
    }


    public String generarToken(UsuarioAdministrador usuario) {
        String token = UUID.randomUUID().toString();
        TokenVerificacion tokenVerificacion = TokenVerificacion.builder()
                .token(token)
                .usuario(usuario)
                .fechaExpiracion(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepo.save(tokenVerificacion);
        return token;
    }

    @Transactional
    public void deleteByUsuario(UsuarioAdministrador usuario){
        tokenRepo.deleteByUsuario(usuario);
    }
}
