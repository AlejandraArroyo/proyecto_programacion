/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Alejandra Arroyo
 */
public class ColaAtencion {
     private int id;
    private Departamento departamento;
    private Queue<Ticket> tickets;
    
  
    public ColaAtencion(Departamento departamento) {
        this.departamento = departamento;
    }

    
     public void setDepartamentoId(int id) {
        this.id = id;
    }

    public void agregarTicket(Ticket ticket) {
        tickets.offer(ticket);
    }

    public Ticket obtenerSiguiente() {
        return tickets.poll();
    }

    public Queue<Ticket> getTickets() {
        return tickets;
    }
    
      public boolean estaVacia() {
        return tickets.isEmpty();
    }

    public Queue<Ticket> getTodosLosTickets() {
        return new LinkedList<>(tickets); 
    }

    public int getDepartamentoId() {
        return id;
    }
}
