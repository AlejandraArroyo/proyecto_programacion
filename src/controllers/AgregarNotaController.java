/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.NotaTicket;
import clases.Sesion;
import clases.Ticket;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class AgregarNotaController implements Initializable {

  @FXML private TextArea txtContenido;
    @FXML private Label lblNombreArchivo;

    private File archivoAdjunto;
    private Ticket ticket;

    /**
     * Initializes the controller class.
     */
    
    
    
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

  @FXML
    private void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        archivoAdjunto = fileChooser.showOpenDialog(null);
        if (archivoAdjunto != null) {
            lblNombreArchivo.setText(archivoAdjunto.getName());
        }
    }

    @FXML
    private void guardarNota() {
        String contenido = txtContenido.getText().trim();
        if (contenido.isEmpty()) {
            Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "La nota no puede estar vacía.");
            return;
        }

        byte[] datosAdjunto = null;
        if (archivoAdjunto != null) {
            datosAdjunto = Dao.convertirArchivoABytes(archivoAdjunto);
        }

        NotaTicket nota = new NotaTicket(ticket.getId(), contenido, Sesion.getUsuarioActual(), datosAdjunto, archivoAdjunto != null ? archivoAdjunto.getName() : null);
        boolean guardado = Dao.insertarNota(nota);

        if (guardado) {
            Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Nota guardada", "La nota ha sido registrada correctamente.");
            ((Stage) txtContenido.getScene().getWindow()).close();
        } else {
            Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la nota.");
        }
    }
    
    
    
    
    
}
