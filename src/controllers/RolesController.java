/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.Permisos;
import clases.Rol;
import clases.Sesion;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class RolesController implements Initializable {

    @FXML
    private ListView<Permisos> listaPermisos;
    @FXML
    private TextField txtNombreRol;
    @FXML
    private TextArea txtDescripcionRol;

    @FXML
    private TableView<Rol> tablaRoles;
    @FXML
    private TableColumn<Rol, String> colNombreRol;
    @FXML
    private TableColumn<Rol, String> colDescripcionRol;
    private Rol rolEditando = null;
    @FXML
    private TableColumn<Rol, String> colPermisos;
    @FXML
    private TableColumn<Rol, Void> colAcciones;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNombreRol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colDescripcionRol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescripcion()));

        colPermisos.setCellValueFactory(cellData -> {
            String permisos = cellData.getValue().getPermisos().stream()
                    .map(Permisos::getNombre)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(permisos);
        });

        listaPermisos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cargarPermisos();
        cargarRoles();
        agregarBotonesAcciones();
    }

    @FXML
    private void mostrarPermisos(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Permisos.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Permisos");
        stage.show();

    }

    private void cargarPermisos() {
        List<Permisos> permisos = Dao.listarPermisos();
        listaPermisos.getItems().setAll(permisos);
    }

    @FXML
private void guardarRol(ActionEvent event) {
    String nombre = txtNombreRol.getText().trim();
    String descripcion = txtDescripcionRol.getText().trim();
    List<Permisos> permisosSeleccionados = listaPermisos.getSelectionModel().getSelectedItems();

    StringBuilder errores = new StringBuilder();

    if (nombre.isEmpty() || nombre.length() < 3 || nombre.length() > 50) {
        errores.append("- El nombre del rol debe tener entre 3 y 50 caracteres.\n");
    }

    if (permisosSeleccionados.isEmpty()) {
        errores.append("- Debe asignar al menos un permiso al rol.\n");
    }

    if (errores.length() > 0) {
        Utils.mostrarAlerta(Alert.AlertType.ERROR, "Errores de validación", errores.toString());
        return;
    }

    if (rolEditando == null) {
        Rol nuevoRol = new Rol(nombre, descripcion);
        nuevoRol.getPermisos().addAll(permisosSeleccionados);
        Dao.insertarRolConPermisos(nuevoRol);
        Dao.registrarBitacora(Sesion.getUsuarioActual(), "Creó nuevo rol: " + nombre, "Roles y Permisos", "R");
    } else {
        rolEditando.setNombre(nombre);
        rolEditando.setDescripcion(descripcion);
        rolEditando.getPermisos().clear();
        rolEditando.getPermisos().addAll(permisosSeleccionados);
        Dao.actualizarRol(rolEditando);
        Dao.registrarBitacora(Sesion.getUsuarioActual(), "Editó rol: " + nombre, "Roles y Permisos", "U");
        rolEditando = null;
    }

    Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Rol guardado correctamente.");
    
    cargarRoles();
}


    private void cargarRoles() {
        tablaRoles.getItems().clear();
        List<Rol> roles = Dao.listarRoless();
        tablaRoles.getItems().addAll(roles);
    }

    private void agregarBotonesAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<Rol, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox contenedor = new HBox(10, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(event -> {
                    Rol rol = getTableView().getItems().get(getIndex());
                       editarRol(rol);
                });

                btnEliminar.setOnAction(event -> {
                    Rol rol = getTableView().getItems().get(getIndex());
                       eliminarRol(rol);
                });

                contenedor.setStyle("-fx-alignment: center;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(contenedor);
                }
            }
        });
    }
    
    
    private void editarRol(Rol rol) {
    rolEditando = rol;
    txtNombreRol.setText(rol.getNombre());
    txtDescripcionRol.setText(rol.getDescripcion());

   
    listaPermisos.getSelectionModel().clearSelection();
    for (Permisos permiso : rol.getPermisos()) {
        for (Permisos item : listaPermisos.getItems()) {
            if (item.getId() == permiso.getId()) {
                listaPermisos.getSelectionModel().select(item);
            }
        }
    }
    
    
}
    
    
    private void eliminarRol(Rol rol) {
    Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
    alerta.setTitle("Eliminar Rol");
    alerta.setHeaderText(null);
    alerta.setContentText("¿Deseas eliminar el rol \"" + rol.getNombre() + "\"?");

    alerta.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            Dao.eliminarRol(rol.getId());
            Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Rol eliminado correctamente.");
            cargarRoles();
        }
    });
}

    @FXML
    private void verHistorial(ActionEvent event) {
        
         try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Bitacora.fxml"));
        Parent root = loader.load();

        BitacoraController controller = loader.getController();
        controller.setModuloFiltro("Roles y Permisos");

        Stage stage = new Stage();
        stage.setTitle("Historial de cambios");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        System.err.println("Error al abrir historial: " + e.getMessage());
    }
         
         
    }


}
