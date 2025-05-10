/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.EstadoTicket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class EstadosTicketController implements Initializable {

    @FXML
    private ComboBox<String> comboSiNO;
    @FXML
    private TextField txtNombreEstado;
    @FXML
    private TextArea txtDescripcionEstado;
    @FXML
    private ListView<?> listEstadosSeleccionados;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        comboSiNO.getItems().addAll("Si", "NO");
    }    
    
private void guardarEstado(ActionEvent event) {
    String nombre = txtNombreEstado.getText().trim();
    String descripcion = txtDescripcionEstado.getText().trim();
    boolean esFinal = comboSiNO.getValue().equalsIgnoreCase("Si");
    List<EstadoTicket> siguientes = (List<EstadoTicket>) listEstadosSeleccionados.getItems(); 
    StringBuilder errores = new StringBuilder();

    if (nombre.isEmpty() || nombre.length() < 3 || nombre.length() > 50) {
        errores.append("- El nombre del estado debe tener entre 3 y 50 caracteres.\n");
    }

    if (!esFinal && siguientes.isEmpty()) {
        errores.append("- Debe seleccionar al menos un estado siguiente si el estado no es final.\n");
    }

    if (errores.length() > 0) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR, "Errores de validación", errores.toString());
        return;
    }

   
    EstadoTicket estado = new EstadoTicket();
    estado.setNombre(nombre);
    estado.setDescripcion(descripcion);
    estado.setEsFinal(esFinal);


    Dao.insertarEstadoTicket(estado); 
    
        Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Estado guardado correctamente.");
   // limpiarCampos();
    //cargarEstados();

    }

   

    
}
