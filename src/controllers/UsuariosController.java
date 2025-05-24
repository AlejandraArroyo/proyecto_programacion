/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Administrador;
import clases.Rol;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import clases.Dao;
import clases.Departamento;
import clases.Persona;
import clases.Sesion;
import clases.Tecnico;
import clases.Usuario;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
    private TextField user;
    @FXML
    private TextField passUsuario;
    @FXML
    private ComboBox<Rol> rolUsuario;
    @FXML
    private ComboBox<Departamento> departamentoUsuario;
    @FXML
    private Label etiquetaDepartamento;
    @FXML
    private TableView<Persona> tablaUsuarios;
    @FXML
    private TableColumn<Persona, String> colNombre;
    @FXML
    private TableColumn<Persona, String> colUsuario;
    @FXML
    private TableColumn<Persona, String> colRol;
    @FXML
    private TableColumn<Persona, String> colEstado;
    @FXML
    private TableColumn<Persona, Void> colAcciones;

    private Persona usuarioEditando = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // cargarRoles();

        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombreUsuario()));
        colRol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRol().getNombre()));
        colEstado.setCellValueFactory(cellData -> {
            String estado = cellData.getValue().getEstado() == 'A' ? "Activo" : "Inactivo";
            return new SimpleStringProperty(estado);
        });

        cargarUsuarios();
        agregarBotonesAcciones();
        departamentoUsuario.setVisible(false);
        etiquetaDepartamento.setVisible(false);

        for (Rol rol : Dao.listarRoles()) {
            rolUsuario.getItems().add(rol);
        }

        for (Departamento departamento : Dao.listarDepartamentos()) {
            departamentoUsuario.getItems().add(departamento);
        }

        rolUsuario.setOnAction(event -> {
            Rol rolSeleccionado1 = rolUsuario.getValue();

            if (rolSeleccionado1 != null && rolSeleccionado1.getNombre().equalsIgnoreCase("Tecnico")) {
                departamentoUsuario.setVisible(true);
                etiquetaDepartamento.setVisible(true);
            } else {
                departamentoUsuario.setVisible(false);
                etiquetaDepartamento.setVisible(false);
                departamentoUsuario.setValue(null);
            }
        });

    

    //// cargar usuarios aqui 
        
        
        

    }
    
    private void agregarBotonesAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<Persona, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(event -> {
                    Persona persona = getTableView().getItems().get(getIndex());
                    editarUsuario(persona);
                });

                btnEliminar.setOnAction(event -> {
                    Persona persona = getTableView().getItems().get(getIndex());
                    eliminarUsuario(persona);
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

    @FXML

    private void guardarUsuario(ActionEvent event) {
        StringBuilder errores = new StringBuilder();

        if (Utils.campoVacio(nombreUsuario) || nombreUsuario.getText().trim().length() < 3 || nombreUsuario.getText().length() > 100) {
            errores.append("- El nombre de usuario debe contener entre 3 y 100 caracteres.\n");
        }

        if (Utils.campoVacio(correoUsuario)) {
            errores.append("- El correo electrónico es obligatorio.\n");
        }

        if (Utils.campoVacio(user)) {
            errores.append("- El nombre de usuario es obligatorio.\n");
        }

        if (Utils.campoVacio(passUsuario)) {
            errores.append("- La contraseña es obligatoria.\n");
        } else if (!Utils.password(passUsuario.getText())) {
            errores.append("- La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial.\n");
        }

        Rol rolSeleccionados = rolUsuario.getValue();
        if (rolSeleccionados != null && rolSeleccionados.getNombre().equalsIgnoreCase("Técnico") && departamentoUsuario.getValue() == null) {
            errores.append("- Debe seleccionar un departamento para técnicos.\n");
        }

        if (usuarioEditando == null) {
            if (Dao.existeCorreo(correoUsuario.getText().trim())) {
                errores.append("- El correo electrónico ya está registrado.\n");
            }

            if (Dao.existeNombreUsuario(user.getText().trim())) {
                errores.append("- El nombre de usuario ya está registrado.\n");
            }
        }

        if (errores.length() > 0) {
            Utils.mostrarAlerta(Alert.AlertType.ERROR, "Errores de validación", errores.toString());
            return;
        }

        String nombre = nombreUsuario.getText().trim();
        String correo = correoUsuario.getText().trim();
        String nombreDeUsuario = user.getText().trim();
        String contraseña = passUsuario.getText().trim();
        Rol rolSeleccionado = rolUsuario.getValue();

        if (usuarioEditando == null) {
            Persona nuevoUsuario;
            if (rolSeleccionado != null && rolSeleccionado.getNombre().equalsIgnoreCase("Técnico")) {
                Departamento departamento = departamentoUsuario.getValue();
                if (departamento == null) {
                    Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar un departamento para técnicos.");
                    return;
                }
                nuevoUsuario = new Tecnico(nombre, correo, nombreDeUsuario, contraseña, rolSeleccionado, departamento);
            } else {
                nuevoUsuario = new Persona(nombre, correo, contraseña, nombreDeUsuario, rolSeleccionado) {
                };
            }

            Dao.insertarUsuario(nuevoUsuario);
            Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Excelente", "Usuario guardado.");
             Dao.registrarBitacora(Sesion.getUsuarioActual(), "Usuario nuevo : " + nombre, "Gestion de Usuarios", "U");
        } else {
            usuarioEditando.setNombre(nombre);
            usuarioEditando.setCorreo(correo);
            usuarioEditando.setNombreUsuario(nombreDeUsuario);
            usuarioEditando.setContra(contraseña);
            usuarioEditando.setRol(rolSeleccionado);

            if (usuarioEditando instanceof Tecnico && departamentoUsuario.getValue() != null) {
                ((Tecnico) usuarioEditando).setDepartamento(departamentoUsuario.getValue());
            }

            Dao.actualizarUsuario(usuarioEditando);
            Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Actualizado", "Usuario actualizado correctamente.");
            Dao.registrarBitacora(Sesion.getUsuarioActual(), "Edito al usuario : " + nombre, "Gestion de Usuarios", "U");
            usuarioEditando = null;
        }

        limpiar();
        cargarUsuarios();
    }

    private void cargarRoles() {
        List<Rol> roles = Dao.listarRoles();
        rolUsuario.getItems().setAll(roles);
    }

    private void cargarUsuarios() {
        List<Persona> usuarios = Dao.listarUsuarios();
        tablaUsuarios.getItems().clear();
        tablaUsuarios.getItems().addAll(usuarios);
    }

    private void limpiar() {
        nombreUsuario.clear();
        correoUsuario.clear();
        user.clear();
        passUsuario.clear();
        rolUsuario.setValue(null);
        departamentoUsuario.setValue(null);
        departamentoUsuario.setVisible(false);
        etiquetaDepartamento.setVisible(false);
    }

    private void eliminarUsuario(Persona persona) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar Usuario");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Seguro que deseas eliminar a " + persona.getNombre() + "?");

        alerta.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Dao.eliminarUsuario(persona.getIdentificacion());
                Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "Usuario eliminado exitosamente.");
                Dao.registrarBitacora(Sesion.getUsuarioActual(), "Elimino a usuario usuario: " + persona.getNombre(), "Gestion de Usuarios", "U");
                cargarUsuarios();
            }
        });
    }

    private void editarUsuario(Persona persona) {
        usuarioEditando = persona;
        nombreUsuario.setText(persona.getNombre());
        correoUsuario.setText(persona.getCorreo());
        user.setText(persona.getNombreUsuario());
        passUsuario.setText(persona.getContra());
        rolUsuario.setValue(persona.getRol());

        if (persona instanceof Tecnico tecnico) {
            departamentoUsuario.setValue(tecnico.getDepartamento());
            departamentoUsuario.setVisible(true);
            etiquetaDepartamento.setVisible(true);
        } else {
            departamentoUsuario.setVisible(false);
            etiquetaDepartamento.setVisible(false);
        }
    }

    @FXML
    private void verHistorial(ActionEvent event) {
        
              try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Bitacora.fxml"));
        Parent root = loader.load();

        BitacoraController controller = loader.getController();
        controller.setModuloFiltro("Gestion de Usuarios");

        Stage stage = new Stage();
        stage.setTitle("Historial de cambios");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        System.err.println("Error al abrir historial: " + e.getMessage());
    }
        
    }

}
