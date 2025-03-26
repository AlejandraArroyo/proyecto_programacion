/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package progra_proyecto;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Alejandra Arroyo
 */
public class Progra_proyecto extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
     
      
      Parent root = FXMLLoader.load(getClass().getResource("/vistas/Login.fxml"));
      
      Scene scene = new Scene(root,725,669);
      
      primaryStage.setTitle("Hola mundo");
      primaryStage.setScene(scene);
      primaryStage.show();
      
      
      
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

