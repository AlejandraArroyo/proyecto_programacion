/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.Departamento;
import clases.Tecnico;
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
public class TecnicosDepartamentoController implements Initializable {

   @FXML private Label lblTitulo;
    @FXML private TableView<Tecnico> tablaTecnicos;
    @FXML private TableColumn<Tecnico, String> colNombre;
    @FXML private TableColumn<Tecnico, String> colCorreo;
    @FXML private TableColumn<Tecnico, String> colUsuario;
        private Departamento departamento;

    /**
     * Initializes the controller class.
     */
        
        
         public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
        lblTitulo.setText("Técnicos de " + departamento.getNombre());
        cargarTecnicos();
    }

    private void cargarTecnicos() {
        List<Tecnico> tecnicos = Dao.obtenerTecnicosPorDepartamento(departamento.getId());
        tablaTecnicos.getItems().setAll(tecnicos);
    }
        
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
         colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colCorreo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCorreo()));
        colUsuario.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreUsuario()));
    }    
    
}
