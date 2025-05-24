/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.EstadoTicket;
import clases.FlujoTrabajo;
import clases.Sesion;
import clases.TransicionEstado;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class FlujosTrabajoController implements Initializable {

    @FXML
    private TextField txtNombreFlujo;
    @FXML
    private ListView<EstadoTicket> listEstados;

    @FXML
    private ComboBox<EstadoTicket> comboOrigen;

    @FXML
    private ComboBox<EstadoTicket> comboDestino;
    @FXML
    private TextField txtAccionExtra;
    @FXML
    private Button btnGuardarFlujo;
    @FXML
    private Button btnCancelar;

    private ObservableList<TransicionEstado> transiciones = FXCollections.observableArrayList();
    @FXML
    private TableView<TransicionEstado> tablaTransiciones;
    @FXML
    private TableColumn<TransicionEstado, String> colOrigen;
    @FXML
    private TableColumn<TransicionEstado, String> colDestino;
    @FXML
    private TableColumn<TransicionEstado, String> colRegla;
    @FXML
    private CheckBox checkNotificar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<EstadoTicket> estados = Dao.listarEstados();
        listEstados.getItems().setAll(estados);
        comboOrigen.getItems().setAll(estados);
        comboDestino.getItems().setAll(estados);
        listEstados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colOrigen.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOrigen().getNombre()));
        colDestino.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDestino().getNombre()));
        colRegla.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRestriccion()));

        tablaTransiciones.setItems(transiciones);
    }

    @FXML
    private void agregarTransicion() {
        EstadoTicket origen = comboOrigen.getValue();
        EstadoTicket destino = comboDestino.getValue();
        String restriccion = txtAccionExtra.getText().trim();

        if (origen == null || destino == null || origen.equals(destino)) {
            Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debes seleccionar estados válidos.");
            return;
        }

        TransicionEstado nueva = new TransicionEstado(origen, destino, restriccion);
        if (!transiciones.contains(nueva)) {
            transiciones.add(nueva);
        } else {
            Utils.mostrarAlerta(Alert.AlertType.WARNING, "Duplicado", "Esa transición ya fue agregada.");
        }
    }

    @FXML
    private void guardarFlujo() {
        String nombre = txtNombreFlujo.getText().trim();
        ObservableList<EstadoTicket> seleccionados = listEstados.getSelectionModel().getSelectedItems();

        StringBuilder errores = new StringBuilder();
        if (nombre.isEmpty()) {
            errores.append("- El nombre del flujo es obligatorio.\n");
        }
        if (seleccionados == null || seleccionados.isEmpty()) {
            errores.append("- Selecciona al menos un estado.\n");
        }
        if (transiciones.isEmpty()) {
            errores.append("- Agrega al menos una transición.\n");
        }

        if (errores.length() > 0) {
            Utils.mostrarAlerta(Alert.AlertType.ERROR, "Errores de validación", errores.toString());
            return;
        }

        FlujoTrabajo flujo = new FlujoTrabajo(nombre, seleccionados, transiciones);
        flujo.setCreador(Sesion.getUsuarioActual());

        Dao.insertarFlujoTrabajo(flujo);
        Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Flujo guardado correctamente.");
        limpiar();
    }

    @FXML
    private void limpiar() {
        txtNombreFlujo.clear();
        listEstados.getSelectionModel().clearSelection();
        comboOrigen.setValue(null);
        comboDestino.setValue(null);
        txtAccionExtra.clear();
        transiciones.clear();
    }

}
