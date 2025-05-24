/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.EstadoTicket;
import clases.Persona;
import clases.Sesion;
import clases.Ticket;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        configurarColumnas();
        cargarTicketsAsignados();
         comboEstados.getItems().setAll(Dao.listarEstados());
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


    
}
