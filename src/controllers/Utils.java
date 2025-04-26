/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputControl;

/**
 *
 * @author Alejandra Arroyo
 */
public class Utils {
   public static void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
       
        public static boolean campoVacio(TextInputControl campo) {
        return campo.getText() == null || campo.getText().trim().isEmpty();
    }
        
        
        public static boolean password(String contraseña) {

        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).{8,}$";
        return contraseña != null && contraseña.matches(regex);
    }

}
