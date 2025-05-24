/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.EstadoTicket;
import clases.Sesion;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    
    
   @FXML private TableColumn<EstadoTicket, String> colNombre;
@FXML private TableColumn<EstadoTicket, String> colDescripcion;
@FXML private TableView<EstadoTicket> tablaEstados;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       // TODO
        comboSiNO.getItems().addAll("Si", "NO");
        
         colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
    colDescripcion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescripcion()));
    cargarEstados();
    }    
    
     @FXML
private void guardarEstado(ActionEvent event) {
    String nombre = txtNombreEstado.getText().trim();
    String descripcion = txtDescripcionEstado.getText().trim();
    boolean esFinal = comboSiNO.getValue().equalsIgnoreCase("Si");
   
    StringBuilder errores = new StringBuilder();

    if (nombre.isEmpty() || nombre.length() < 3 || nombre.length() > 50) {
        errores.append("- El nombre del estado debe tener entre 3 y 50 caracteres.\n");
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
    Dao.registrarBitacora(Sesion.getUsuarioActual(), "Guardo Estado de ticket " + nombre, "Estados de Ticket", "T");
    
        Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Estado guardado correctamente.");
   // limpiarCampos();
    cargarEstados();

    }

    @FXML
    private void verHistorial(ActionEvent event) {
        
             try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Bitacora.fxml"));
        Parent root = loader.load();

        BitacoraController controller = loader.getController();
        controller.setModuloFiltro("Estados de Ticket");

        Stage stage = new Stage();
        stage.setTitle("Historial de cambios");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        System.err.println("Error al abrir historial: " + e.getMessage());
    }
         
        
    }
    
    private void cargarEstados() {
    List<EstadoTicket> estados = Dao.listarEstadosTabla();
    tablaEstados.getItems().setAll(estados);
}

   

    
}
