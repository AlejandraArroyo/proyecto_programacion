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
    private Ticket ticket;
    private Persona responsable;
    private String descripcion;
    private LocalDateTime fechaHora;
    private List<Adjuntos> adjuntos;

    public NotaTicket(Ticket ticket, Persona responsable, String descripcion, List<Adjuntos> adjuntos) {
        this.ticket = ticket;
        this.responsable = responsable;
        this.descripcion = descripcion;
        this.fechaHora = LocalDateTime.now();
        this.adjuntos = adjuntos;
    }

    public void setAdjuntos(List<Adjuntos> adjuntos) {
        this.adjuntos = adjuntos;
    }
    
    
    
    
    
}
