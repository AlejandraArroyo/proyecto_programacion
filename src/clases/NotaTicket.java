/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Alejandra Arroyo
 */
public class NotaTicket {
    private int id;
    private int ticketId;
    private String contenido;
    private Persona creador;
    private LocalDateTime fechaCreacion;
    private byte[] adjunto;
    private String nombreAdjunto;

    public NotaTicket(int ticketId, String contenido, Persona creador, byte[] adjunto, String nombreAdjunto) {
        this.ticketId = ticketId;
        this.contenido = contenido;
        this.creador = creador;
        this.adjunto = adjunto;
        this.nombreAdjunto = nombreAdjunto;
    }
    
     public NotaTicket(int id, String contenido, LocalDateTime fecha) {
        this.id = id;
        this.contenido = contenido;
        this.fechaCreacion = fecha;
    }


    public int getTicketId() { return ticketId; }
    public String getContenido() { return contenido; }
    public Persona getCreador() { return creador; }
    public byte[] getAdjunto() { return adjunto; }
    public String getNombreAdjunto() { return nombreAdjunto; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setNombreAdjunto(String nombreAdjunto) {
    this.nombreAdjunto = nombreAdjunto;
}
    
}
