/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alejandra Arroyo
 */
public class Ticket {
    private int identificador;
    private String titulo, descripcion;
    private Departamento departamento;
    private LocalDateTime fechaCreo;
    private EstadoTicket estadoActual;
    private Usuario usuario;
    private Tecnico tecnicoAsignado;
    private List<Adjuntos> adjuntos;
    private String prioridad;

    public Ticket(String titulo, String descripcion, Departamento departamento, LocalDateTime fechaCreo, EstadoTicket estadoActual, Usuario usuario, Tecnico tecnicoAsignado, List<Adjuntos> adjuntos, String prioridad) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.departamento = departamento;
        this.fechaCreo = fechaCreo;
        this.estadoActual = estadoActual;
        this.usuario = usuario;
        this.tecnicoAsignado = tecnicoAsignado;
        this.adjuntos = new ArrayList<>();
        this.prioridad = prioridad;
       
    }
    
     public void cambiarEstado(EstadoTicket nuevoEstado, String nota, Persona responsable) {
        this.estadoActual = nuevoEstado;
        
    }
     
     public void agregarAdjunto(Adjuntos adjunto) {
        adjuntos.add(adjunto);
    }
     
     
 

    
}
