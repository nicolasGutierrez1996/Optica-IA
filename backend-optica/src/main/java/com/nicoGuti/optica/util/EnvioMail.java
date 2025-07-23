package com.nicoGuti.optica.util;

import com.nicoGuti.optica.modelo.Contacto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EnvioMail {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void enviarCredenciales(String email, String username, String nombre, String apellido, String link) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("🎉 ¡Bienvenido a Óptica IA!");

            String contenidoHtml = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<h2 style='color: #0066cc;'>¡Hola " + nombre + " " + apellido + "!</h2>"
                    + "<p>Tu cuenta ha sido <strong>creada exitosamente</strong> en la plataforma <strong>Óptica IA</strong>.</p>"
                    + "<p><strong>Usuario:</strong> " + username + "</p>"
                    + "<p>Ahora podés ingresar a la plataforma para comenzar a gestionar tu óptica y explorar todas las funcionalidades disponibles.</p>"
                    + "<p style='margin-top: 20px;'><strong>⚠ Para finalizar el procedimiento, verificá tu casilla de correo presionando en el siguiente enlace:</strong></p>"
                    + "<p><a href='" + link + "' style='color: #0066cc; text-decoration: underline;'>Verificar mi cuenta</a></p>"
                    + "<br>"
                    + "<br><br>"
                    + "<p style='font-size: 12px; color: #777;'>Si no reconocés este correo, por favor ignoralo.</p>"
                    + "<p>Saludos,<br>Equipo de Óptica IA</p>"
                    + "</div>";

            helper.setText(contenidoHtml, true);

            mailSender.send(mensaje);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @Async
    public void enviarTokenDeRecuperacion(String destino, String token) {
        MimeMessage mensaje = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setTo(destino);
            helper.setSubject("Recuperación de contraseña – Óptica IA");

            String contenidoHtml = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "  <style>" +
                    "    body { font-family: Arial, sans-serif; background-color: #f4f6f8; color: #333; padding: 20px; }" +
                    "    .card { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
                    "    .title { font-size: 18px; margin-bottom: 16px; color: #0066cc; }" +
                    "    .token { font-size: 24px; font-weight: bold; color: #004bcc; margin: 20px 0; }" +
                    "    .footer { font-size: 13px; color: #777; margin-top: 30px; }" +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='card'>" +
                    "    <div class='title'>Recuperación de contraseña</div>" +
                    "    <p>Hola,</p>" +
                    "    <p>Recibimos una solicitud para restablecer tu contraseña en <strong>Óptica IA</strong>.</p>" +
                    "    <p>Tu código de recuperación es:</p>" +
                    "    <div class='token'>" + token + "</div>" +
                    "    <p>Este código expirará en 30 minutos.</p>" +
                    "    <p>Si no realizaste esta solicitud, podés ignorar este mensaje.</p>" +
                    "    <div class='footer'>© Óptica IA - Todos los derechos reservados</div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            helper.setText(contenidoHtml, true); // true = HTML

            mailSender.send(mensaje);

        } catch (MessagingException e) {
            // Log o manejo de error
            e.printStackTrace();
        }
    }

    @Async
    public void enviarMailContacto(Contacto contacto){
        String cuerpo = "📩 Nuevo mensaje desde el formulario de contacto:\n\n" +
                "👤 Nombre: " + contacto.getNombre() + " " + contacto.getApellido() + "\n" +
                "✉️ Email: " + contacto.getEmail() + "\n" +
                "📞 Teléfono: " + contacto.getTelefono() + "\n" +
                "📌 Motivo: " + contacto.getMotivo() + "\n\n" +
                "📝 Mensaje:\n" + contacto.getDescripcion() + "\n\n" +
                "-----------------------------\n" +
                "Este mensaje fue generado automáticamente por la web de Óptica IA.";

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo("julionicolasgutierrez96@gmail.com");
        mensaje.setSubject("Solicitud de contacto Optica IA");
        mensaje.setText(cuerpo);

        mailSender.send(mensaje);
    }


    @Async
    public void enviarMailSuscripcionExitosa(String email, String nombreOptica, String nombrePlan) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("¡Tu suscripción a Óptica IA fue exitosa!");

            String contenidoHtml = """
                <p>Hola <strong>%s</strong>,</p>
                <p>Te damos la bienvenida a <strong>Óptica IA</strong>. Tu suscripción ha sido registrada exitosamente con el plan <strong>%s</strong>.</p>
                <p>Podés comenzar a disfrutar de todos los beneficios accediendo a tu panel:</p>
                <p><a href="http://localhost:4200/login" style="padding:10px 20px;background-color:#007bff;color:white;text-decoration:none;border-radius:5px;">Ir al panel</a></p>
                <br>
                <p>¡Gracias por confiar en nosotros!</p>
                <p><em>— El equipo de Óptica IA</em></p>
                """.formatted(nombreOptica, nombrePlan);

            helper.setText(contenidoHtml, true);
            mailSender.send(mensaje);

        } catch (Exception e) {
            System.out.println("Error al enviar mail de suscripción a {}:"+ email+"/"+ e.getMessage()+"/"+ e);
        }
    }


    @Async
    public void enviarMailBeneficioPorCupon(String email, String nombreOpticaEmisora) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("🎉 ¡Felicitaciones! Ganaste 2 meses gratis en Óptica IA");

            String contenidoHtml = """
                <p>Hola <strong>%s</strong>,</p>
                <p>Queremos felicitarte porque <strong>3 ópticas han usado tu cupón de descuento</strong>.</p>
                <p>Como recompensa, te otorgamos <strong>2 meses de suscripción gratis</strong> por ayudar a expandir nuestra comunidad.</p>
                <br>
                <p>¡Gracias por ser parte de Óptica IA!</p>
                <p><em>— El equipo de Óptica IA</em></p>
                """.formatted(nombreOpticaEmisora);

            helper.setText(contenidoHtml, true);
            mailSender.send(mensaje);

        } catch (Exception e) {
            System.out.println("Error al enviar mail de cupón a {}:"+ email+"/"+ e.getMessage()+"/"+ e);


        }
    }

    @Async
    public void enviarMailPagoFallido(String email, String nombreOptica) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("❌ Error en el intento de pago – Óptica IA");

            String contenidoHtml = """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f8f9fa; color: #333; padding: 20px; }
                    .card {
                        background-color: #fff;
                        border: 1px solid #ddd;
                        border-left: 5px solid #dc3545;
                        border-radius: 8px;
                        padding: 20px;
                        max-width: 600px;
                        margin: auto;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.05);
                    }
                    .header {
                        color: #dc3545;
                        font-size: 24px;
                        font-weight: bold;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    .mensaje {
                        margin-top: 15px;
                        font-size: 16px;
                        line-height: 1.6;
                    }
                    .footer {
                        margin-top: 30px;
                        font-size: 13px;
                        color: #777;
                        text-align: center;
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="header">❌ ¡Pago no procesado!</div>
                    <div class="mensaje">
                        Hola <strong>%s</strong>,<br><br>
                        Lamentablemente, no pudimos procesar tu intento de pago. Esto puede deberse a:
                        <ul>
                            <li>Fondos insuficientes</li>
                            <li>Datos de tarjeta incorrectos</li>
                            <li>Problemas temporales con el proveedor de pagos</li>
                        </ul>
                        Podés ingresar nuevamente a la plataforma e intentarlo más tarde.<br><br>
                        Si el problema persiste, escribinos para ayudarte.
                    </div>
                    <div class="footer">© Óptica IA – Todos los derechos reservados</div>
                </div>
            </body>
            </html>
        """.formatted(nombreOptica);

            helper.setText(contenidoHtml, true);
            mailSender.send(mensaje);

        } catch (Exception e) {
            System.out.println("Error al enviar mail de fallo de pago a {}:"+ email +"/"+ e.getMessage() + "/" + e);
        }
    }

    @Async
    public void enviarAvisoVencimiento(String email, String nombreOptica) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("⏳ Tu suscripción a Óptica IA ha vencido");

            String contenidoHtml = """
            <div style="font-family: Arial, sans-serif; color: #333;">
              <h2 style="color: #cc0000;">📛 ¡Atención, %s!</h2>
              <p>La suscripción de tu óptica <strong>%s</strong> ha vencido.</p>
              <p>Para continuar utilizando las funcionalidades de Óptica IA, deberás renovar tu plan.</p>
              <br>
              <p><a href="http://localhost:4200/login" style="padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;">Renovar ahora</a></p>
              <br><br>
              <p>Si ya realizaste el pago, por favor ignorá este mensaje.</p>
              <p>— El equipo de Óptica IA</p>
            </div>
        """.formatted(nombreOptica, nombreOptica);

            helper.setText(contenidoHtml, true);
            mailSender.send(mensaje);

        } catch (Exception e) {
            System.out.println("❌ Error al enviar mail de vencimiento a {}: {}"+"/"+ email+"/"+ e.getMessage());
        }
    }


    @Async
    public void enviarAvisoProximoVencimiento(String email, String nombreOptica, LocalDate fechaVencimiento) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("⏰ ¡Tu suscripción a Óptica IA vence pronto!");

            String contenidoHtml = """
            <div style="font-family: Arial, sans-serif; color: #333;">
              <h2 style="color: #cc6600;">¡Atención!</h2>
              <p>Hola <strong>%s</strong>,</p>
              <p>Tu suscripción a <strong>Óptica IA</strong> está por vencer el <strong>%s</strong>.</p>
              <p>Para evitar interrupciones, te recomendamos renovarla a tiempo.</p>
              <p style="margin-top: 20px;">
                <a href="http://localhost:4200/login" style="background-color: #28a745; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">
                  Renovar suscripción
                </a>
              </p>
              <br>
              <p><em>Gracias por confiar en nosotros.</em></p>
              <p>— El equipo de Óptica IA</p>
            </div>
        """.formatted(nombreOptica, fechaVencimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            helper.setText(contenidoHtml, true);
            mailSender.send(mensaje);

        } catch (MessagingException e) {
            System.out.println("❌ Error al enviar aviso de vencimiento a: " + email);
            e.printStackTrace();
        }
    }

}
