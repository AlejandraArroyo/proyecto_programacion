/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class MenuPrincipalController implements Initializable {

    @FXML
    private StackPane contentPane;
    @FXML
    private Button bRoles;

    /**
     * Initializes the controller class.
     */
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void vistaEmpresa() {
        cargarVista("/vistas/ConfigEmpresa.fxml");
    }
    
    
    
    
       private void cargarVista(String vista) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
            Pane nuevaVista = loader.load();
            contentPane.getChildren().setAll(nuevaVista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarRol(ActionEvent event) {
        cargarVista("/vistas/Roles.fxml");
    }

    @FXML
    private void viewDepartamentos(ActionEvent event) {
        cargarVista("/vistas/Departamentos.fxml");
    }

    @FXML
    private void CrearUsuario(ActionEvent event) {
         cargarVista("/vistas/Usuarios.fxml");
    }

    @FXML
    private void Estadosticket(ActionEvent event) {
         cargarVista("/vistas/EstadosTicket.fxml");
    }

    @FXML
    private void crearTicket(ActionEvent event) {
        
        cargarVista("/vistas/CrearTicket.fxml");
    }

    @FXML
    private void colaAtencion(ActionEvent event) {
         cargarVista("/vistas/ColaAtencion.fxml");
    }

    @FXML
    private void ticketUsuario(ActionEvent event) {
         cargarVista("/vistas/TicketUsuario.fxml");
    }

    @FXML
    private void ticketProceso(ActionEvent event) {
         cargarVista("/vistas/ProcesoTickets.fxml");
    }

    @FXML
    private void gestionarTickets(ActionEvent event) {
    }

    @FXML
    private void historialTickets(ActionEvent event) {
    }
    
}
