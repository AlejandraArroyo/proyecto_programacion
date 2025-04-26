/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.Persona;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;
/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class LoginController implements Initializable {

    @FXML
    private TextField textUsuario;
    @FXML
    private TextField textContra;

    /**pero 
     * Initializes the controller class.
     */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
    }    
   

    @FXML
    private void miBoton(javafx.event.ActionEvent event) throws IOException {

          String usuario = textUsuario.getText().trim();
        String contrasena = textContra.getText().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
             Utils.mostrarAlerta(Alert.AlertType.ERROR,"Campos vacíos", "Por favor llena ambos campos.");
            return;
        }

        Persona persona = Dao.buscarPorCredenciales(usuario, contrasena);

        if (persona != null) {
           
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/MenuPrincipal.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Bienvenido " );
            stage.show();

           
            Stage actualStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            actualStage.close();
        } else {
            Utils.mostrarAlerta(Alert.AlertType.ERROR,"Login Fallido", "Usuario o contraseña incorrectos.");
        }
    }

  
}
