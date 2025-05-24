/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Bitacora;
import clases.Dao;
import clases.Persona;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class BitacoraController implements Initializable {

   
    @FXML private TableView<Bitacora> tablaBitacora;
    @FXML private TableColumn<Bitacora, String> colAccion;
    @FXML private TableColumn<Bitacora, String> colModulo;
    @FXML private TableColumn<Bitacora, String> colFecha;
    @FXML private Label lblTitulo;
    
      private String moduloFiltro = "";
    @FXML
    private TableColumn<Bitacora, String> colPersona;

    public void setModuloFiltro(String modulo) {
        this.moduloFiltro = modulo;
        lblTitulo.setText("Historial: " + modulo);
        cargarBitacora();
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
         colAccion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAccion()));
        colModulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getModulo()));
        colFecha.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFecha().toString()));
        
         colPersona.setCellValueFactory(cell -> {
        Persona p = cell.getValue().getUsuario();
        return new SimpleStringProperty(p != null ? p.getNombre() : "Desconocido");
    });
    }    
    
        private void cargarBitacora() {
        List<Bitacora> registros = Dao.obtenerBitacoraPorModulo(moduloFiltro);
        tablaBitacora.getItems().setAll(registros);
    }
    
}
