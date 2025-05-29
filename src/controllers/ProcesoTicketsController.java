/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.EnvioCorreoTicket;
import clases.EstadoTicket;
import clases.Persona;
import clases.Sesion;
import clases.Ticket;
import java.io.File;
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
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class ProcesoTicketsController implements Initializable {

   @FXML private TableView<Ticket> tablaMisTickets;
@FXML private TableColumn<Ticket, String> colTitulo;
@FXML private TableColumn<Ticket, String> colEstado;
@FXML private TableColumn<Ticket, String> colPrioridad;
@FXML private TableColumn<Ticket, String> colFecha;

    @FXML
    private TextArea txtComentario;
    @FXML private ComboBox<EstadoTicket> comboEstados;
    @FXML
    private Button btnAdjuntar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnCerrar;
    
    private Ticket ticketSeleccionado;
    private File archivoAdjunto;
    @FXML private TableColumn<Ticket, Void> colAcciones;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        configurarColumnas();
        cargarTicketsAsignados();
         comboEstados.getItems().setAll(Dao.listarEstados());
         
         agregarBotonesDetalle();
    }    
    
    
    private void configurarColumnas() {
    colTitulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitulo()));
    colEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado().getNombre()));
    colPrioridad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPrioridad().getNombre()));
    colFecha.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFechaCreacion().toString()));
}
    
    private void cargarTicketsAsignados() {
    Persona tecnico = Sesion.getUsuarioActual();
    if (tecnico == null) return;

    List<Ticket> asignados = Dao.obtenerTicketsPorTecnico(tecnico.getIdentificacion());
    tablaMisTickets.getItems().setAll(asignados);
}
    
    
private void seleccionarTicket() {
    ticketSeleccionado = tablaMisTickets.getSelectionModel().getSelectedItem();
    if (ticketSeleccionado != null) {
        comboEstados.setValue(ticketSeleccionado.getEstado());
        txtComentario.clear();
        archivoAdjunto = null;
    }
}

private void adjuntarArchivos(ActionEvent event) {
    FileChooser fc = new FileChooser();
    fc.setTitle("Seleccionar archivo");
    File f = fc.showOpenDialog(null);
    if (f != null) archivoAdjunto = f;
}



   @FXML
   private void actualizarTicket(ActionEvent event) {
    Ticket ticket = tablaMisTickets.getSelectionModel().getSelectedItem();
    if (ticket == null) {
        Utils.mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un ticket", "Debes seleccionar un ticket.");
        return;
    }

    EstadoTicket nuevoEstado = comboEstados.getValue();
    String comentario = txtComentario.getText().trim();

    if (nuevoEstado == null) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR, "Estado requerido", "Selecciona un nuevo estado.");
        return;
    }   

    ticket.setEstado(nuevoEstado);
    if (archivoAdjunto != null) {
        ticket.setArchivoAdjunto(Dao.convertirArchivoABytes(archivoAdjunto));
        ticket.setNombreArchivo(archivoAdjunto.getName());
    }
    
     if (archivoAdjunto != null) {
        Dao.insertarArchivoTicket(ticketSeleccionado.getId(),
                                  Dao.convertirArchivoABytes(archivoAdjunto),
                                  archivoAdjunto.getName());
    }

    Dao.actualizarTicket(ticket);
    Dao.insertarHistorialEstado(ticket.getId(), nuevoEstado, comentario.isEmpty() ? "Actualización" : comentario);
     Dao.registrarBitacora(Sesion.getUsuarioActual(), "Se cambio el estado del ticket: "   + nuevoEstado.getNombre() , "Tickets", "M");
    Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Ticket actualizado", "El ticket se actualizó correctamente.");
    
    Persona creador = Dao.obtenerPersonaPorId(ticket.getCreador().getIdentificacion());
String asunto = "Flujo  de Ticket";
String mensaje = "<h2>Ticket actualizado exitosamente</h2>" +
                 "<p><strong>Título:</strong> " + ticket.getTitulo() + "</p>" +
                 "<p><strong>Descripción:</strong> " + ticket.getDescripcion() + "</p>" +
                 "<p>Su ticket ha pasado a estado : "+ nuevoEstado.getNombre() +" </p>";

EnvioCorreoTicket.enviarTicket(creador.getCorreo(), creador.getNombre(), mensaje);
    
    
    
    cargarTicketsAsignados();
}

    @FXML
    private void seleccionarArchivo(ActionEvent event) {
        
        FileChooser fc = new FileChooser();
    fc.setTitle("Seleccionar archivo");
    File f = fc.showOpenDialog(null);
    if (f != null) archivoAdjunto = f;
    }

    @FXML
    private void cerrarTicket(ActionEvent event) {
        Ticket ticket = tablaMisTickets.getSelectionModel().getSelectedItem();
        if (ticket == null) {
        Utils.mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un ticket para cerrar.");
        return;
    }

    Dao.cerrarTicket(ticket.getId());
    Dao.registrarBitacora(Sesion.getUsuarioActual(), "El usuario cerro el ticket: " + ticket.getId(), "Tickets", "M");
    Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Ticket cerrado correctamente.");
    cargarTicketsAsignados(); 
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
    Ticket seleccionado = tablaMisTickets.getSelectionModel().getSelectedItem();
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
