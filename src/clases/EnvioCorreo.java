/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
/**
 *
 * @author Alejandra Arroyo
 */
public class EnvioCorreo {
    
    public static void enviarCredenciales(String destinatario, String nombreUsuario, String contrasena) {
        final String remitente = "harroyov@miumg.edu.gt"; 
        final String clave = "utdbfkqieikfcujc";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session sesion = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("Credenciales de Acceso");
           String contenidoHTML = """
    <h2 style='color:#17aeae;'>Bienvenido al Sistema de Tickets</h2>
    <p>Hola <b>%s</b>,</p>
    <p>Tu usuario es: <strong>%s</strong></p>
    <p>Tu contraseña es: <strong>%s</strong></p>
    <hr>
    <p style='font-size:12px;color:#555;'>Este es un mensaje automático, no respondas a este correo.</p>
    """.formatted(nombreUsuario, nombreUsuario, contrasena);

mensaje.setContent(contenidoHTML, "text/html; charset=utf-8");

            Transport.send(mensaje);
            System.out.println("Correo enviado a: " + destinatario);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
        }
    }
}
