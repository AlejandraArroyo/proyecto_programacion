/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import clases.Dao;
import clases.NotaTicket;
import clases.Ticket;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Alejandra Arroyo
 */
public class VistaDetalleTicketController implements Initializable {

   @FXML private Label lblTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private VBox vboxArchivos;
    @FXML private ListView<String> listHistorial;

    private Ticket ticket;
   private ListView<String> listNotas;
    @FXML
    private VBox vboxNotas;
    @FXML
    private Label lblCreador;

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        lblTitulo.setText("Título: " + ticket.getTitulo());
        txtDescripcion.setText(ticket.getDescripcion());
        lblCreador.setText("Creado por: "+ ticket.getCreador().getNombre());
        cargarArchivosAdjuntos();
        cargarHistorial();
         cargarNotas(); 
    }

    private void cargarArchivosAdjuntos() {
        vboxArchivos.getChildren().clear();
        List<String> archivos = Dao.obtenerArchivosPorTicket(ticket.getId());

        for (String nombreArchivo : archivos) {
            HBox fila = new HBox(10);
            Label lbl = new Label(nombreArchivo);
            Button btn = new Button("Descargar");

         btn.setOnAction(e -> {
    byte[] data = Dao.obtenerArchivo(ticket.getId(), nombreArchivo);
    if (data != null) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo");
        fileChooser.setInitialFileName(nombreArchivo); // Sugerencia de nombre
        File file = fileChooser.showSaveDialog(btn.getScene().getWindow());

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
                Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Descarga", "Archivo descargado correctamente.");
            } catch (IOException ex) {
                Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el archivo.");
            }
        }
    }
});

            fila.getChildren().addAll(lbl, btn);
            vboxArchivos.getChildren().add(fila);
        }
    }

 private void cargarNotas() {
    vboxNotas.getChildren().clear();
    List<NotaTicket> notas = Dao.obtenerNotasPorTickets(ticket.getId());

    for (NotaTicket nota : notas) {
        VBox notaBox = new VBox(5);
        notaBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");
        Label lblContenido = new Label("Nota: " + nota.getContenido());
        Label lblFecha = new Label("Fecha: " + nota.getFechaCreacion());
        notaBox.getChildren().addAll(lblContenido, lblFecha);

        if (nota.getNombreAdjunto() != null && !nota.getNombreAdjunto().isEmpty()) {
            HBox filaArchivo = new HBox(10);
            Label lbl = new Label(nota.getNombreAdjunto());
            Button btnDescargar = new Button("Descargar");

            btnDescargar.setOnAction(e -> {
                byte[] datos = Dao.obtenerAdjuntoNota(nota.getTicketId(), nota.getNombreAdjunto());
                if (datos != null) {
                    FileChooser fc = new FileChooser();
                    fc.setInitialFileName(nota.getNombreAdjunto());
                    File dest = fc.showSaveDialog(lbl.getScene().getWindow());
                    if (dest != null) {
                        try (FileOutputStream fos = new FileOutputStream(dest)) {
                            fos.write(datos);
                            Utils.mostrarAlerta(Alert.AlertType.INFORMATION, "Descarga", "Archivo guardado correctamente.");
                        } catch (IOException ex) {
                            Utils.mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el archivo.");
                        }
                    }
                }
            });

            filaArchivo.getChildren().addAll(lbl, btnDescargar);
            notaBox.getChildren().add(filaArchivo);
        }

        vboxNotas.getChildren().add(notaBox);
    }
}
    
    
    private void cargarHistorial() {
        listHistorial.getItems().setAll(Dao.obtenerHistorialTicket(ticket.getId()));
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
