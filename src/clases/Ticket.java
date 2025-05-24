/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alejandra Arroyo
 */
public class Ticket {
    private int id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private byte[] archivoAdjunto;
    private String nombreArchivo;
    private EstadoTicket estado;
    private NivelPrioridad prioridad;
    private Departamento departamento;
    private Persona creador;
private Persona asignadoA;


    public Ticket(String titulo, String descripcion, LocalDateTime fechaCreacion, byte[] archivoAdjunto, String nombreArchivo,
                  EstadoTicket estado, NivelPrioridad prioridad, Departamento departamento, Persona creador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.archivoAdjunto = archivoAdjunto;
        this.nombreArchivo = nombreArchivo;
        this.estado = estado;
        this.prioridad = prioridad;
        this.departamento = departamento;
        this.creador = creador;
    }
    
    public Ticket() {
    
}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public byte[] getArchivoAdjunto() {
        return archivoAdjunto;
    }

    public void setArchivoAdjunto(byte[] archivoAdjunto) {
        this.archivoAdjunto = archivoAdjunto;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public void setEstado(EstadoTicket estado) {
        this.estado = estado;
    }

    public NivelPrioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(NivelPrioridad prioridad) {
        this.prioridad = prioridad;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Persona getCreador() {
        return creador;
    }

    public void setCreador(Persona creador) {
        this.creador = creador;
    }

    
    public String getNombreArchivoAdjunto() {
    return nombreArchivo;
}

public void setNombreArchivoAdjunto(String nombreArchivoAdjunto) {
    this.nombreArchivo = nombreArchivoAdjunto;
}

public byte[] getAdjunto() {
    return archivoAdjunto;
}

public void setAdjunto(byte[] adjunto) {
    this.archivoAdjunto = adjunto;
}



public EstadoTicket getEstadoActual() {
    return estado;
}

public void setEstadoActual(EstadoTicket estado) {
    this.estado = estado;
}


public Persona getAsignadoA() {
    return asignadoA;
}

public void setAsignadoA(Persona asignadoA) {
    this.asignadoA = asignadoA;
}

    
}
