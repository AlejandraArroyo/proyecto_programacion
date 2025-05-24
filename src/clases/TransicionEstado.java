/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Alejandra Arroyo
 */
public class TransicionEstado {
    private EstadoTicket origen;
    private EstadoTicket destino;
    private String restriccion;

    public TransicionEstado(EstadoTicket origen, EstadoTicket destino, String restriccion) {
        this.origen = origen;
        this.destino = destino;
        this.restriccion = restriccion;
    }

    // Getters y Setters
    public EstadoTicket getOrigen() { return origen; }
    public EstadoTicket getDestino() { return destino; }
    public String getRestriccion() { return restriccion; }

    public void setOrigen(EstadoTicket origen) { this.origen = origen; }
    public void setDestino(EstadoTicket destino) { this.destino = destino; }
    public void setRestriccion(String restriccion) { this.restriccion = restriccion; }

}
