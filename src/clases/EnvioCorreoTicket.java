/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import jakarta.mail.internet.MimeMultipart;
/**
 *
 * @author Alejandra Arroyo
 */
public class EnvioCorreoTicket {
    
    public static void enviarTicket(String destinatario, String nombreUsuario, String contenidoHTML) {
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
            mensaje.setSubject("Tickets");
           
  MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(contenidoHTML, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(htmlPart);

        mensaje.setContent(multipart);
        Transport.send(mensaje);

            System.out.println("Correo enviado a: " + destinatario);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
        }
    }
}
