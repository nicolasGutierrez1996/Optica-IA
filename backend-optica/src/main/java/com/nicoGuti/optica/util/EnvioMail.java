package com.nicoGuti.optica.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EnvioMail {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCredenciales(String destinatario, String usuario, String clave) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Alta de Usuario Administrador");
        mensaje.setText("Hola " + usuario + ", tu contraseña es: " + clave);
        mailSender.send(mensaje);
    }


    @Async
    public void enviarTokenDeRecuperacion(String destino, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject("Tu nueva cuenta en Optica IA");
        mensaje.setText("Hola,\n\nRecibimos una solicitud para restablecer tu contraseña.\n\nTu código de recuperación es: " + token +
                "\n\nSi no realizaste esta solicitud, por favor ignorá este mensaje.");

        mailSender.send(mensaje);
    }

}
