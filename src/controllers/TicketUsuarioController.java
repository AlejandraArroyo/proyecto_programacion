/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.EstadoTicket;
import clases.Sesion;
import clases.Ticket;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell; 
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class TicketUsuarioController implements Initializable {

    
  @FXML private TableView<Ticket> tablaTicketsUsuario;
@FXML private TableColumn<Ticket, String> colTitulo, colEstado, colPrioridad, colFecha;


private ObservableList<Ticket> todosMisTickets = FXCollections.observableArrayList();

   private VBox panelDetalles;
    private ListView<String> listArchivos;
    private ListView<String> listHistorial;
   @FXML private TableColumn<Ticket, Void> colAcciones;

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

   
    cargarTicketsUsuario();
    
       tablaTicketsUsuario.setOnMouseClicked(event -> {
        Ticket seleccionado = tablaTicketsUsuario.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mostrarDetallesTicket(seleccionado);
        }
    });
    
    agregarBotonesDetalle();

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

    
    
    
    private void mostrarDetallesEnDialog(Ticket ticket) {
    List<String> archivos = Dao.obtenerArchivosPorTicket(ticket.getId());
    List<String> historial = Dao.obtenerHistorialTicket(ticket.getId());

    StringBuilder mensaje = new StringBuilder("Título: " + ticket.getTitulo() + "\n");
    mensaje.append("Archivos adjuntos:\n");
    archivos.forEach(a -> mensaje.append("- ").append(a).append("\n"));

    mensaje.append("\nHistorial:\n");
    historial.forEach(h -> mensaje.append("- ").append(h).append("\n"));

    Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Detalles del Ticket", mensaje.toString());
}
    
    
    private void verHistorial(ActionEvent event) {
        
              try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Bitacora.fxml"));
        Parent root = loader.load();

        BitacoraController controller = loader.getController();
        controller.setModuloFiltro("Gestion de Usuarios");

        Stage stage = new Stage();
        stage.setTitle("Historial de cambios");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        System.err.println("Error al abrir historial: " + e.getMessage());
    }
        
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
    Ticket seleccionado = tablaTicketsUsuario.getSelectionModel().getSelectedItem();
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
