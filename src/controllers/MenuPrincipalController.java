/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.ConfigEmpresa;
import clases.Dao;
import clases.Persona;
import clases.ServicioColas;
import clases.Sesion;
import clases.Ticket;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class MenuPrincipalController implements Initializable {

    @FXML
    private StackPane contentPane;
    @FXML
    private TitledPane paneGestionUsuarios;
    @FXML
    private Label lblUsuarioLogueado;
    @FXML
    private Accordion miAccordion;
    @FXML
    private Button btnSalir;
    @FXML
    private Button btnEmpresa;
    @FXML
    private Button btnRoles;
    @FXML
    private Button btnDepartamentos;
    @FXML
    private TitledPane paneConfig;
    @FXML
    private TitledPane paneConfigTickets;
    @FXML
    private Button btnCola;
    @FXML
    private Button bntCrearTicket;
    @FXML
    private Button btnTickets;
    @FXML
    private Button btnTicketProceso;
    @FXML
    private Button btnGestionTicket;
    @FXML
    private ImageView imgLogoEmpresa;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        Persona usuario = Sesion.getUsuarioActual();
        if (usuario != null) {
            lblUsuarioLogueado.setText("Bienvenido, " + usuario.getNombre());
        }

        if (!UtilsPermiso.tienePermiso("Gestionar usuarios")) {
            miAccordion.getPanes().remove(paneGestionUsuarios);
        }
        
        
          if (!UtilsPermiso.tienePermiso("Gestionar departamentos") && !UtilsPermiso.tienePermiso("Configurar parámetros del sistema") && !UtilsPermiso.tienePermiso("Gestionar roles") ) {
                      miAccordion.getPanes().remove(paneConfig);
        }
          
          
          
          if (!UtilsPermiso.tienePermiso("Gestionar Estados Ticket") && !UtilsPermiso.tienePermiso("Gestionar flujos de trabajo")  ) {
                      miAccordion.getPanes().remove(paneConfigTickets);
        }
          
          

        if (!UtilsPermiso.tienePermiso("Configurar parámetros del sistema")) {
            btnEmpresa.setVisible(false);
            btnEmpresa.setManaged(false);
        }
        
        
          if (!UtilsPermiso.tienePermiso("Gestionar roles")) {
            btnRoles.setVisible(false);
            btnRoles.setManaged(false);
        }
          
           if (!UtilsPermiso.tienePermiso("Gestionar departamentos")) {
            btnDepartamentos.setVisible(false);
            btnDepartamentos.setManaged(false);
        }
           
           
            if (!UtilsPermiso.tienePermiso("Cola de Atencion")) {
            btnCola.setVisible(false);
            btnCola.setManaged(false);
        }
           
            
                if (!UtilsPermiso.tienePermiso("Tickets en Proceso")) {
            btnTicketProceso.setVisible(false);
            btnTicketProceso.setManaged(false);
        }
                
                
                       if (!UtilsPermiso.tienePermiso("Crear tickets")) {
            bntCrearTicket.setVisible(false);
            bntCrearTicket.setManaged(false);
        }
           
                       
                       
                       if (!UtilsPermiso.tienePermiso("Tickets Usuario")) {
            btnTickets.setVisible(false);
            btnTickets.setManaged(false);
        }
     
                       
                          if (!UtilsPermiso.tienePermiso("Gestión de Tickets")) {
            btnGestionTicket.setVisible(false);
            btnGestionTicket.setManaged(false);
        }
           
          
          Dao.cargarColasDesdeBaseDatoss();
          
                System.out.println("Total de colas creadas: " + ServicioColas.colasPorDepartamento.size());

for (Map.Entry<Integer, Queue<Ticket>> entry : ServicioColas.colasPorDepartamento.entrySet()) {
    System.out.println("Departamento ID: " + entry.getKey() + " | Tickets en cola: " + entry.getValue().size());
}


 ConfigEmpresa config = Dao.obtenerConfiguracion();
    if (config != null && config.getLogoEmpresa() != null) {
        Image logo = new Image(new ByteArrayInputStream(config.getLogoEmpresa()));
        imgLogoEmpresa.setImage(logo);
    } else {
        System.out.println("No hay logo configurado.");
    }
           
      

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
        
        cargarVista("/vistas/GestionTickets.fxml");
    }

    @FXML
    private void flujo(ActionEvent event) {
        cargarVista("/vistas/FlujoTickets.fxml");
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
      
        Sesion.setUsuarioActual(null);

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar Sesión");
            stage.show();

            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageActual.close();

        } catch (IOException e) {
            e.printStackTrace();
            Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cerrar la sesión correctamente.");
        }
    }
}
