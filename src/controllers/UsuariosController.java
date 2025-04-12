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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class UsuariosController implements Initializable {

    @FXML
    private TextField nombreUsuario;
    @FXML
    private TextField correoUsuario;
    @FXML
    private TextField User;
    @FXML
    private TextField passUsuario;
    @FXML
    private ComboBox<?> rolUsuario;
    @FXML
    private ComboBox<?> departamentoUsuario;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void guardarUsuario(ActionEvent event) {
        StringBuilder errores = new StringBuilder();

        if (Utils.campoVacio(nombreUsuario) || nombreUsuario.getText().trim().length() < 3 ||  nombreUsuario.getText().length() > 100) {
            errores.append("- El nombre de usuario debe contener entre 3 y 100 caracteres.\n");
        }

        if (Utils.campoVacio(correoUsuario)) {
            errores.append("- El correo electrónico es obligatorio.\n");
        }

        if (Utils.campoVacio(User)) {
            errores.append("- El nombre de usuario es obligatorio.\n");
        }

        if (Utils.campoVacio(passUsuario)) {
            errores.append("- La contraseña es obligatoria.\n");
        } else if (!Utils.password(passUsuario.getText())) {
            errores.append("- La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial.\n");
        }
        if (rolUsuario.getValue() == null) {
            errores.append("- Debe seleccionar un rol.\n");
        } else {
            String rolSeleccionado = rolUsuario.getValue().toString();
            if (rolSeleccionado.equalsIgnoreCase("Técnico") && departamentoUsuario.getValue() == null) {
                errores.append("- Debe seleccionar un departamento para técnicos.\n");
            }
        }

        if (errores.length() > 0) {
            Utils.mostrarAlerta("Errores de validación", errores.toString());
            return;
        }

       
        Utils.mostrarAlerta("Éxito", "Usuario válido y guardado exitosamente");
    }

}
