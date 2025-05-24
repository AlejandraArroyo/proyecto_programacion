/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author Alejandra Arroyo
 */
public class ServicioColas {
    public static final Map<Integer, Queue<Ticket>> colasPorDepartamento = new HashMap<>();

    public static void crearColaParaDepartamento(int departamentoId) {
        colasPorDepartamento.putIfAbsent(departamentoId, new LinkedList<>());
    }

    public static Queue<Ticket> obtenerCola(int departamentoId) {
        return colasPorDepartamento.getOrDefault(departamentoId, new LinkedList<>());
    }

    public static void agregarATicketACola(int departamentoId, Ticket ticket) {
        colasPorDepartamento.computeIfAbsent(departamentoId, k -> new LinkedList<>()).add(ticket);
    }
    
    public static void eliminarDeTodasLasColas(int ticketId) {
    for (Queue<Ticket> cola : ServicioColas.colasPorDepartamento.values()) {
        cola.removeIf(t -> t.getId() == ticketId);
    }
}
}