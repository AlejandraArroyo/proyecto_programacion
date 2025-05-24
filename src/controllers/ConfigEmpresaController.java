/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.ConfigEmpresa;
import clases.Dao;
import clases.Sesion;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
    @FXML
    private ImageView imagenLogo;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    comboIdioma.getItems().addAll("Español", "Inglés");
    comboZonaHoraria.getItems().addAll("UTC−12:00", "UTC−06:00", "UTC±00:00", "UTC+01:00", "UTC+08:00"); 
        cargarNivelesPrioridad();
    
      ConfigEmpresa config = Dao.obtenerConfiguracion();
    if (config != null) {
        txtNombreEmpresa.setText(config.getNombre());
        comboIdioma.setValue(config.getIdioma());
        comboZonaHoraria.setValue(config.getZonaHoraria());
        txtVencimientoTickets.setText(String.valueOf(config.getVencimiento()));
      
    }
    
    byte[] logoBytes = config.getLogoEmpresa();
        if (logoBytes != null && logoBytes.length > 0) {
            javafx.scene.image.Image imagen = new javafx.scene.image.Image(
                new java.io.ByteArrayInputStream(logoBytes)
            );
            imagenLogo.setImage(imagen);
        }
    
    
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
    if (listPrioridades.getItems().size() < 3) {
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
        Utils.mostrarAlerta(Alert.AlertType.ERROR, "Errores", errores.toString());
        return;
    }

    try {
    byte[] logoBytes = java.nio.file.Files.readAllBytes(logoEmpresa.toPath());

    ConfigEmpresa config = new ConfigEmpresa();
    config.setNombre(nombreEmpresa);
    config.setLogoEmpresa(logoBytes);
    config.setNombreArchivoLogo(logoEmpresa.getName());
    config.setIdioma(idioma);
    config.setZonaHoraria(zonaHoraria);
    config.setVencimiento(vencimiento);

    // Guardar configuración general
    Dao.guardarOActualizarConfiguracion(config);

    
    for (String prioridad : listPrioridades.getItems()) {
        Dao.insertarPrioridadSiNoExiste(prioridad); 
    }

   
    Dao.registrarBitacora(Sesion.getUsuarioActual(), "Configuró parámetros del sistema", "Configuración Empresa", "U");

    Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La configuración se ha guardado correctamente.");

} catch (Exception e) {
    e.printStackTrace();
    Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la configuración.");
}

}
    
    
        
    @FXML
private void agregarPrioridad(ActionEvent event) {
    String nuevaPrioridad = txtPrioridadNueva.getText().trim();

    if (nuevaPrioridad.isEmpty() || nuevaPrioridad.length() > 30) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ingrese una prioridad válida.");
        return;
    }

    if (listPrioridades.getItems().contains(nuevaPrioridad)) {
        Utils.mostrarAlerta(Alert.AlertType.WARNING, "Duplicado", "Esa prioridad ya existe.");
        return;
    }

    Dao.insertarPrioridad(nuevaPrioridad); // Guarda en BD
    listPrioridades.getItems().add(nuevaPrioridad);
    txtPrioridadNueva.clear();
}
    
    private void cargarNivelesPrioridad() {
    List<String> prioridades = Dao.obtenerNivelesPrioridad();
    listPrioridades.getItems().setAll(prioridades);
}
    
    
    @FXML
private void verHistorial(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Bitacora.fxml"));
        Parent root = loader.load();

        BitacoraController controller = loader.getController();
        controller.setModuloFiltro("Configuración Empresa");

        Stage stage = new Stage();
        stage.setTitle("Historial de cambios");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        System.err.println("Error al abrir historial: " + e.getMessage());
    }
}
    

}
