/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class DepartamentosController implements Initializable {

    @FXML
    private TextField tituloDep;
    @FXML
    private TextArea descDep;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void saveDepartamentos(ActionEvent event) {
        String titulo = tituloDep.getText();
        String desc = descDep.getText();
        StringBuilder errores = new StringBuilder();
        
         if (titulo == null || titulo.trim().length() < 3 || titulo.length() > 100) {
            errores.append("- El nombre del departamento debe contener entre 3 y 50 caracteres.\n");
        }
         
         
         if (errores.length() > 0) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR,"Errores", errores.toString());
    } else {
      
        Utils.mostrarAlerta(Alert.AlertType.INFORMATION,"Éxito", "El departamento se ha guardado correctamente.");
    }
         
         
    }
    
}
