/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.Permisos;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class PermisosController implements Initializable {

@FXML private TableView<Permisos> tablaPermisos;
@FXML private TableColumn<Permisos, String> colNombre;
@FXML private TableColumn<Permisos, String> colDescripcion;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
    colDescripcion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescripcion()));

    tablaPermisos.getItems().setAll(Dao.listarPermisosTabla());
    }    
    
}
