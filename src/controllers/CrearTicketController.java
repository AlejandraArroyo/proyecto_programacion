/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.Departamento;
import clases.NivelPrioridad;
import clases.Sesion;
import clases.Ticket;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class CrearTicketController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Button btnSeleccionarArchivo;
   
    @FXML
    private ComboBox<Departamento> departamentoAsignado;
    @FXML
    private ComboBox<NivelPrioridad> comboPrioridad;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private Label lblArchivoAdjunto;
    @FXML
    private Button btnCrearTicket;
    
    private File archivoAdjunto;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
          for (Departamento departamento : Dao.listarDepartamentos()) {
            departamentoAsignado.getItems().add(departamento);
        }
          
          comboPrioridad.getItems().setAll(Dao.listarNivelesPrioridad());
    }    
    
        

 @FXML
private void crearTicket(ActionEvent event) {
    String titulo = txtTitulo.getText().trim();
    String descripcion = txtDescripcion.getText().trim();
    Departamento departamento = departamentoAsignado.getValue();
    NivelPrioridad prioridad = comboPrioridad.getValue();

    StringBuilder errores = new StringBuilder();
    if (titulo.isEmpty()) errores.append("- El título es obligatorio.\n");
    if (descripcion.isEmpty()) errores.append("- La descripción es obligatoria.\n");
    if (departamento == null) errores.append("- Seleccione un departamento.\n");
    if (prioridad == null) errores.append("- Seleccione una prioridad.\n");

    if (errores.length() > 0) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR, "Errores de validación", errores.toString());
        return;
    }

    Ticket nuevo = new Ticket();
    nuevo.setTitulo(titulo);
    nuevo.setDescripcion(descripcion);
    nuevo.setDepartamento(departamento);
    nuevo.setPrioridad(prioridad);
    nuevo.setFechaCreacion(LocalDateTime.now());
    nuevo.setEstadoActual(Dao.obtenerEstadoInicial()); 
    nuevo.setCreador(Sesion.getUsuarioActual());
    if (archivoAdjunto != null) {
        nuevo.setNombreArchivoAdjunto(archivoAdjunto.getName());
        nuevo.setAdjunto(Dao.convertirArchivoABytes(archivoAdjunto));
    }

  int ticketId = Dao.insertarTicket(nuevo);
if (ticketId > 0) {
    Dao.agregarTicketACola(ticketId, nuevo.getDepartamento().getId(), nuevo.getFechaCreacion());
    Dao.insertarHistorialEstado(ticketId, nuevo.getEstado(), "Creación de ticket");
    Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Ticket creado exitosamente.");
   
} else {
    Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el ticket.");
}
}
    
    
@FXML
private void seleccionarArchivo(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Seleccionar archivo");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"),
        new FileChooser.ExtensionFilter("PDF", "*.pdf")
    );
     Stage stage = (Stage) btnSeleccionarArchivo.getScene().getWindow();
    File archivoSeleccionado = fileChooser.showOpenDialog(stage);
     if (archivoSeleccionado != null) {
        archivoAdjunto = archivoSeleccionado;
        lblArchivoAdjunto.setText("Archivo: " + archivoSeleccionado.getName());
    } else {
        lblArchivoAdjunto.setText("Ningún archivo seleccionado");
    }
    
}


    
    
    
    
}
