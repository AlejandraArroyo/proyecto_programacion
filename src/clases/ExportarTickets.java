/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Alejandra Arroyo
 */
public class ExportarTickets {
    
    public static void exportarTickets(List<Ticket> tickets) {
        Path path = Paths.get("tickets_exportados.bin");

        try (OutputStream os = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(os)) {

            for (Ticket t : tickets) {
                oos.writeObject(t);
            }

            System.out.println(" Tickets exportados correctamente a " + path.toAbsolutePath());

        } catch (Exception e) {
            System.err.println(" Error al exportar tickets: " + e.getMessage());
        }
    }
}
