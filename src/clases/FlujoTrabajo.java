/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alejandra Arroyo
 */
public class FlujoTrabajo {
    private int id;
    private String nombre;
    private String reglas;
    private String accionesAutomaticas;
    private Persona creador;
    private List<EstadoTicket> estadosInvolucrados = new ArrayList<>();
    private List<TransicionEstado> transiciones = new ArrayList<>();

     public FlujoTrabajo(String nombre, List<EstadoTicket> estadosInvolucrados, List<TransicionEstado> transiciones) {
        this.nombre = nombre;
        this.estadosInvolucrados = estadosInvolucrados;
        this.transiciones = transiciones;
    }


    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getReglas() { return reglas; }
    public void setReglas(String reglas) { this.reglas = reglas; }

    public String getAccionesAutomaticas() { return accionesAutomaticas; }
    public void setAccionesAutomaticas(String accionesAutomaticas) { this.accionesAutomaticas = accionesAutomaticas; }

    public Persona getCreador() { return creador; }
    public void setCreador(Persona creador) { this.creador = creador; }

    public List<EstadoTicket> getEstadosInvolucrados() { return estadosInvolucrados; }
    public List<TransicionEstado> getTransiciones() { return transiciones; }
}

