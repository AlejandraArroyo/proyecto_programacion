/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.EstadoTicket;
import clases.Persona;
import clases.ServicioColas;
import clases.Sesion;
import clases.Tecnico;
import clases.Ticket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class ColaAtencionController implements Initializable {

   @FXML
    private TableView<Ticket> tablaTickets;
    @FXML
    private TableColumn<Ticket, String> colTitulo;
    @FXML
    private TableColumn<Ticket, String> colEstado;
    @FXML
    private TableColumn<Ticket, String> colPrioridad;
    @FXML
    private TableColumn<Ticket, String> colFecha;
    @FXML
    private Button btnTomarTicket;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    configurarColumnas();
     cargarColaDeTecnico();
      cargarTickets();
    }    
    
     private void configurarColumnas() {
        colTitulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitulo()));
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado().getNombre()));
        colPrioridad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPrioridad().getNombre()));
        colFecha.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFechaCreacion().toString()));
        
        System.out.println("Total de colas activas: " + ServicioColas.colasPorDepartamento.size());
ServicioColas.colasPorDepartamento.forEach((id, cola) -> {
    System.out.println("Departamento ID: " + id + " -> Tickets en cola: " + cola.size());
});
    }


 private void cargarColaDeTecnico() {
        if (Sesion.getUsuarioActual() instanceof Tecnico tecnico) {
            int idDepartamento = tecnico.getDepartamento().getId();
            Queue<Ticket> cola = ServicioColas.obtenerCola(idDepartamento);

            if (cola != null && !cola.isEmpty()) {
                tablaTickets.getItems().setAll(cola);
            } else {
                tablaTickets.getItems().clear();
                System.out.println("No hay tickets en la cola del departamento: " + idDepartamento);
            }
        } else {
            System.out.println("El usuario actual no es técnico.");
        }
    }

   @FXML
private void tomarTicket(ActionEvent event) {
    Ticket seleccionado = tablaTickets.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        Utils.mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Selecciona un ticket para tomar.");
        return;
    }

    Persona usuario = Sesion.getUsuarioActual();
    if (!(usuario instanceof Tecnico tecnico)) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR, "Acceso denegado", "Solo técnicos pueden tomar tickets.");
        return;
    }

    EstadoTicket estadoEnProceso = Dao.obtenerEstadoPorNombre("En proceso");
    if (estadoEnProceso == null) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el estado 'En proceso'.");
        return;
    }

    
    seleccionado.setEstado(estadoEnProceso);
    seleccionado.setAsignadoA(tecnico);
    Dao.asignarTicket(seleccionado); // lo veremos abajo

    
    ServicioColas.obtenerCola(tecnico.getDepartamento().getId()).remove(seleccionado);

    
    Dao.insertarHistorialEstado(seleccionado.getId(), estadoEnProceso, "Ticket tomado por el técnico.");

    Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Ticket tomado correctamente.");
    cargarTickets(); 
}

private void cargarTickets() {
    Persona usuario = Sesion.getUsuarioActual();

    if (usuario instanceof Tecnico tecnico) {
        int departamentoId = tecnico.getDepartamento().getId();

        Queue<Ticket> cola = ServicioColas.obtenerCola(departamentoId);
        if (cola != null) {
            List<Ticket> tickets = new ArrayList<>(cola);
            tablaTickets.getItems().setAll(tickets);
        } else {
            System.out.println(" No se encontró una cola para el departamento ID: " + departamentoId);
        }
    }
}

    
}
