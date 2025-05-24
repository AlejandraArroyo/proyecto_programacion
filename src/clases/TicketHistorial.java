/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 *
 * @author Alejandra Arroyo
 */
public class TicketHistorial {
 private final Deque<EstadoTicket> historial = new ArrayDeque<>();

    public void agregarEstado(EstadoTicket estado) {
        historial.push(estado);
    }

    public EstadoTicket deshacerUltimoCambio() {
        return historial.isEmpty() ? null : historial.pop();
    }

    public List<EstadoTicket> obtenerHistorial() {
        return new ArrayList<>(historial);
    }
   
}
