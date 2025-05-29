/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.Departamento;
import clases.EstadoTicket;
import clases.ExportarTickets;
import clases.Persona;
import clases.Sesion;
import clases.Ticket;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class GestionTicketsController implements Initializable {

   @FXML private TableView<Ticket> tablaTickets;
    @FXML private TableColumn<Ticket, String> colTitulo, colEstado, colPrioridad, colDepartamento, colFecha,colCreador,colAsignado;
    @FXML private ComboBox<EstadoTicket> comboEstado;
    @FXML private ComboBox<Departamento> comboDepartamento;
  private Ticket ticketSeleccionado;
   @FXML private TableColumn<Ticket, Void> colAcciones;
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         cargarColumnas();
        cargarTickets();
        comboEstado.getItems().addAll(Dao.listarEstados());
        comboDepartamento.getItems().addAll(Dao.listarDepartamentos());
        
         agregarBotonesDetalle();
    }    

 
    
    
      private void cargarColumnas() {
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitulo()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado().getNombre()));
        colPrioridad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrioridad().getNombre()));
        colDepartamento.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartamento().getNombre()));
        colFecha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaCreacion().toString()));
        colCreador.setCellValueFactory(data -> {
    Persona creador = data.getValue().getCreador(); 
    return new SimpleStringProperty(creador != null ? creador.getNombre() : "Desconocido");
});

colAsignado.setCellValueFactory(data -> {
    Persona asignado = data.getValue().getAsignadoA();
    return new SimpleStringProperty(asignado != null ? asignado.getNombre() : "No asignado");
});
    }
      
        private void cargarTickets() {
        tablaTickets.getItems().setAll(Dao.listarTodosLosTickets());
    }

    @FXML
    private void reasignarTicket(ActionEvent event) {
   
    Ticket seleccionado = tablaTickets.getSelectionModel().getSelectedItem();
    Departamento nuevo = comboDepartamento.getValue();

    if (seleccionado == null || nuevo == null) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "Seleccione un ticket y un nuevo departamento.");
        return;
    }

    Dao.reasignarTicketADepartamento(seleccionado.getId(), nuevo.getId());
    Dao.insertarHistorialEstado(seleccionado.getId(), seleccionado.getEstado(), "Reasignado a " + nuevo.getNombre());
    Dao.registrarBitacora(Sesion.getUsuarioActual(), "Reasignó ticket ID: " + seleccionado.getId() + " al departamento " + nuevo.getNombre(), "Tickets", "M");

    Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Ticket reasignado correctamente.");
    cargarTickets(); 
    }
    
    
       @FXML
    private void actualizarEstado(ActionEvent event) {
        
        Ticket ticketSeleccionado = tablaTickets.getSelectionModel().getSelectedItem();
    EstadoTicket nuevoEstado = comboEstado.getValue();

    if (ticketSeleccionado == null || nuevoEstado == null) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar un ticket y un nuevo estado.");
        return;
    }

    Dao.actualizarEstadoTicket(ticketSeleccionado.getId(), nuevoEstado);
    Dao.insertarHistorialEstado(ticketSeleccionado.getId(), nuevoEstado, "Cambio de estado por administrador");
    Dao.registrarBitacora(Sesion.getUsuarioActual(), "Cambio de estado del ticket " + ticketSeleccionado.getId() + " a " + nuevoEstado.getNombre(), "Gestión de Tickets", "Actualización");

    Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Estado actualizado.");
    cargarTickets(); 
    }

    @FXML
    private void verHistorial(ActionEvent event) {
                  try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Bitacora.fxml"));
        Parent root = loader.load();

        BitacoraController controller = loader.getController();
        controller.setModuloFiltro("Tickets");

        Stage stage = new Stage();
        stage.setTitle("Historial de cambios");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        System.err.println("Error al abrir historial: " + e.getMessage());
    }
        
    }
    
    
    
    private void agregarBotonesDetalle() {
    colAcciones.setCellFactory(col -> new TableCell<>() {
        private final Button btnDetalles = new Button("Detalles");
        private final Button btnNota = new Button("Agregar Nota");
        private final HBox contenedor = new HBox(5, btnDetalles, btnNota);

        {
            btnDetalles.setOnAction(e -> {
                Ticket ticket = getTableView().getItems().get(getIndex());
                abrirVentanaDetallesConDescarga(ticket);
            });

            btnNota.setOnAction(e -> {
                Ticket ticket = getTableView().getItems().get(getIndex());
                abrirVentanaAgregarNota(ticket);
            });

            contenedor.setStyle("-fx-alignment: center;");
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : contenedor);
        }
    });
}

    

    private void abrirVentanaDetallesConDescarga(Ticket ticket) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/VistaDetalleTicket.fxml"));
        Parent root = loader.load();

        VistaDetalleTicketController controller = loader.getController();
        controller.setTicket(ticket);  

        Stage stage = new Stage();
        stage.setTitle("Detalles del Ticket");
        stage.setScene(new Scene(root));
        stage.show();

    } catch (IOException e) {
        System.err.println("Error al abrir ventana de detalles: " + e.getMessage());
    }
}

    
private void verDetalleTicket(ActionEvent event) {
    Ticket seleccionado = tablaTickets.getSelectionModel().getSelectedItem();
    if (seleccionado != null) {
        abrirVentanaDetallesConDescarga(seleccionado);
    }
}



private void abrirVentanaAgregarNota(Ticket ticket) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/AgregarNota.fxml"));
        Parent root = loader.load();

        AgregarNotaController controller = loader.getController();
        controller.setTicket(ticket);

        Stage stage = new Stage();
        stage.setTitle("Agregar Nota");
        stage.setScene(new Scene(root));
        stage.show();

    } catch (IOException e) {
        System.err.println("Error al abrir ventana de nota: " + e.getMessage());
    }
}



    
}
