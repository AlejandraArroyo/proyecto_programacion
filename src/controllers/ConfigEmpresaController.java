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
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class ConfigEmpresaController implements Initializable {

    @FXML
    private Button btnSeleccionarArchivo;
    @FXML
    private TextField txtNombreEmpresa;

    @FXML
    private ComboBox<String> comboIdioma;

    @FXML
    private ComboBox<String> comboZonaHoraria;

    @FXML
    private TextField txtVencimientoTickets;

        @FXML
    private TextField txtPrioridadNueva;

    @FXML
    private ListView<String> listPrioridades;
    private ObservableList<String> prioridades = FXCollections.observableArrayList();
    @FXML
    private Button btnGuardar;
    
    private File logoEmpresa;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    comboIdioma.getItems().addAll("Español", "Inglés");
    comboZonaHoraria.getItems().addAll("UTC−12:00", "UTC−06:00", "UTC±00:00", "UTC+01:00", "UTC+08:00"); 
    listPrioridades.setItems(prioridades);
        // TODO
    }    

    @FXML
    private void seleccionarArchivo(ActionEvent event) {
        
           FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar logo");

        
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        
        Stage stage = (Stage) btnSeleccionarArchivo.getScene().getWindow();
        File archivoSeleccionado = fileChooser.showOpenDialog(stage);

         if (archivoSeleccionado != null) {
        if (archivoSeleccionado.length() <= 2 * 1024 * 1024) {
            logoEmpresa = archivoSeleccionado;
            System.out.println("Archivo seleccionado: " + archivoSeleccionado.getAbsolutePath());
        } else {
            Utils.mostrarAlerta(Alert.AlertType.ERROR,"Error", "El archivo seleccionado excede los 2MB.");
        }
    } else {
        System.out.println("Selección cancelada");
    }
    }
    
    @FXML
    private void agregarPrioridad(ActionEvent event) {
        
         String nueva = txtPrioridadNueva.getText().trim();

            if (nueva.isEmpty()) {
                Utils.mostrarAlerta(Alert.AlertType.ERROR,"Error", "Debe escribir un nivel de prioridad.");
                return;
            }

            if (prioridades.contains(nueva)) {
                Utils.mostrarAlerta(Alert.AlertType.ERROR,"Error", "Esta prioridad ya fue agregada.");
                return;
            }

            prioridades.add(nueva);
            txtPrioridadNueva.clear();
    }

    @FXML
    private void guardarConfig(ActionEvent event) {

        String nombreEmpresa = txtNombreEmpresa.getText();
        String idioma = comboIdioma.getValue();
        String zonaHoraria = comboZonaHoraria.getValue();
        String vencimientoStr = txtVencimientoTickets.getText();
        StringBuilder errores = new StringBuilder();

        if (nombreEmpresa == null || nombreEmpresa.trim().length() < 3 || nombreEmpresa.length() > 100) {
            errores.append("- El nombre de la empresa debe tener entre 3 y 100 caracteres.\n");
        }
        if (idioma == null) {
            errores.append("- Debe seleccionar un idioma.\n");
        }
        if (zonaHoraria == null) {
            errores.append("- Debe seleccionar una zona horaria.\n");
        }
        if (prioridades.size() < 3) {
            errores.append("- Debe definir al menos tres niveles de prioridad.\n");
        }
        
         int vencimiento = -1;
    try {
        vencimiento = Integer.parseInt(vencimientoStr);
        if (vencimiento < 1 || vencimiento > 365) {
            errores.append("- El tiempo de vencimiento debe estar entre 1 y 365 días.\n");
        }
    } catch (NumberFormatException e) {
        errores.append("- El tiempo de vencimiento debe ser un número válido.\n");
    }
      if (logoEmpresa == null) {
        errores.append("- Debe seleccionar un logo de la empresa.\n");
    } else if (logoEmpresa.length() > 2 * 1024 * 1024) {
        errores.append("- El logo no debe superar los 2MB.\n");
    }
        
        if (errores.length() > 0) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR,"Errores", errores.toString());
    } else {
      

        Utils.mostrarAlerta(Alert.AlertType.INFORMATION,"Éxito", "La configuración se ha guardado correctamente.");
    }

    }
    
}
