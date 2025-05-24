/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Alejandra Arroyo
 */
public class ColaAtencionDep {
    private final Queue<Ticket> cola = new LinkedList<>();

    public void agregarTicket(Ticket ticket) {
        cola.offer(ticket);
    }

    public Ticket siguienteTicket() {
        return cola.poll();
    }

    public List<Ticket> verCola() {
        return new ArrayList<>(cola);
    }
}
