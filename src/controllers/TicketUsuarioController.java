/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.EstadoTicket;
import clases.Sesion;
import clases.Ticket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class TicketUsuarioController implements Initializable {

    
  @FXML private TableView<Ticket> tablaTicketsUsuario;
@FXML private TableColumn<Ticket, String> colTitulo, colEstado, colPrioridad, colFecha;
@FXML private ComboBox<EstadoTicket> comboEstados;

private ObservableList<Ticket> todosMisTickets = FXCollections.observableArrayList();

    @FXML
    private Button btnVerDetalle;
   @FXML private VBox panelDetalles;
@FXML private Label lblDescripcion, lblDepartamento, lblAsignadoA, lblEstadoActual, lblPrioridad;
@FXML private ListView<String> listArchivos, listHistorial;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
         colTitulo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitulo()));
    colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado().getNombre()));
    colPrioridad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrioridad().getNombre()));
    colFecha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaCreacion().toString()));

    comboEstados.getItems().setAll(Dao.listarEstados());
    cargarTicketsUsuario();
    
       tablaTicketsUsuario.setOnMouseClicked(event -> {
        Ticket seleccionado = tablaTicketsUsuario.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarDetallesTicket(seleccionado);
        }
    });
    
    
    }    

    @FXML
    private void filtrarPorEstado(ActionEvent event) {
        
           EstadoTicket estadoSeleccionado = comboEstados.getValue();
    if (estadoSeleccionado == null) {
        tablaTicketsUsuario.setItems(todosMisTickets);
    } else {
        tablaTicketsUsuario.setItems(todosMisTickets.filtered(t -> t.getEstado().equals(estadoSeleccionado)));
    }
    }

    @FXML
    private void verDetalleTicket(ActionEvent event) {
        
         Ticket seleccionado = tablaTicketsUsuario.getSelectionModel().getSelectedItem();
    if (seleccionado != null) {
       
        Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Detalle del Ticket",
                "Título: " + seleccionado.getTitulo() + "\nDescripción: " + seleccionado.getDescripcion());
    }
    }
    
    private void cargarTicketsUsuario() {
    int idUsuario = Sesion.getUsuarioActual().getIdentificacion();
    todosMisTickets.setAll(Dao.obtenerTicketsPorUsuario(idUsuario));
    tablaTicketsUsuario.setItems(todosMisTickets);
}
    
    
    private void mostrarDetallesTicket(Ticket ticket) {
    if (ticket == null) return;

   


    listArchivos.getItems().clear();
    List<String> archivos = Dao.obtenerArchivosPorTicket(ticket.getId());
    listArchivos.getItems().addAll(archivos);

    
    listHistorial.getItems().clear();
    List<String> historial = Dao.obtenerHistorialTicket(ticket.getId());
    listHistorial.getItems().addAll(historial);

    panelDetalles.setVisible(true);
}
    
    

    
}
