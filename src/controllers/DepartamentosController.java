/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.Departamento;
import clases.Sesion;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
    @FXML
    private TableView<Departamento> tablaDepartamentos;
    @FXML
    private TableColumn<Departamento, Integer> colId;
    @FXML
    private TableColumn<Departamento, String> colNombre;
    @FXML
    private TableColumn<Departamento, String> colDescripcion;
    private ObservableList<Departamento> departamentos = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Departamento, Void> colAcciones;
    private Departamento departamentoEditando = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colDescripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescripcion()));

        cargarDepartamentos();
        agregarBotonesDepartamento();
    }

    @FXML
    private void saveDepartamentos(ActionEvent event) {
        String nombre = tituloDep.getText().trim();
        String descripcion = descDep.getText().trim();

        StringBuilder errores = new StringBuilder();

        if (nombre.isEmpty() || nombre.length() < 3 || nombre.length() > 50) {
            errores.append("- El nombre debe tener entre 3 y 50 caracteres.\n");
        }

        if (errores.length() > 0) {
            Utils.mostrarAlerta(Alert.AlertType.ERROR, "Errores de validación", errores.toString());
            return;
        }

        if (departamentoEditando == null) {
            Departamento nuevo = new Departamento(nombre, descripcion);
            Dao.insertarDepartamento(nuevo);
             Dao.registrarBitacora(Sesion.getUsuarioActual(), "Nuevo departamento: " +nombre , "Departamentos", "D");
            Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Departamento guardado correctamente.");
        } else {
            departamentoEditando.setNombre(nombre);
            departamentoEditando.setDescripcion(descripcion);
            Dao.actualizarDepartamento(departamentoEditando);
             Dao.registrarBitacora(Sesion.getUsuarioActual(), "Actualización departamento: " +nombre , "Departamentos", "D");
            Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Actualizado", "Departamento editado correctamente.");
            departamentoEditando = null;
        }
            limpiarCampos();
        cargarDepartamentos();
    }

    private void cargarDepartamentos() {
        List<Departamento> lista = Dao.listarDepartamentos();
        tablaDepartamentos.getItems().clear();
        tablaDepartamentos.getItems().addAll(lista);
    }

    private void agregarBotonesDepartamento() {
        colAcciones.setCellFactory(param -> new TableCell<Departamento, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(event -> {
                    Departamento depto = getTableView().getItems().get(getIndex());
                    editarDepartamento(depto);
                });

                btnEliminar.setOnAction(event -> {
                    Departamento depto = getTableView().getItems().get(getIndex());
                    eliminarDepartamento(depto);
                });

                hbox.setStyle("-fx-alignment: center;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });
    }

    private void eliminarDepartamento(Departamento departamento) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar Departamento");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Seguro que deseas eliminar a " + departamento.getNombre() + "?");

        alerta.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Dao.eliminarDepartamento(departamento.getId());
                 Dao.registrarBitacora(Sesion.getUsuarioActual(), "Elimino departamento: " + departamento.getId(), "Departamentos", "D");
                Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "Departamento exitosamente.");
                cargarDepartamentos();
            }
        });
    }

    private void editarDepartamento(Departamento departamento) {
        departamentoEditando = departamento;
        tituloDep.setText(departamento.getNombre());
        descDep.setText(departamento.getDescripcion());
    }

    private void limpiarCampos() {
        tituloDep.clear();
        descDep.clear();
        departamentoEditando = null;
    }

    @FXML
    private void verHistorial(ActionEvent event) {
        
              try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Bitacora.fxml"));
        Parent root = loader.load();

        BitacoraController controller = loader.getController();
        controller.setModuloFiltro("Departamentos");

        Stage stage = new Stage();
        stage.setTitle("Historial de cambios");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        System.err.println("Error al abrir historial: " + e.getMessage());
    }
    }
}
