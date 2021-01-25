/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe;

import aloe.controller.SplashScreenController;
import aloe.model.Folder;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Student
 */
public class Aloe extends Application {

    public static Stage primary_stage;

    @Override
    public void start(Stage primaryStage) {
        primary_stage = primaryStage;
        Folder folder = new Folder();
        folder.createFolders("Aloe");

        folder.createFolders("Aloe\\Barcodes");
        folder.createFolders("Aloe\\Charts");
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/aloe/view/SplashScreen.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(SplashScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Scene scene = new Scene(root);
        // scene.setFill(Color.TRANSPARENT);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        // stage.initStyle(StageStyle.TRANSPARENT);
        stage.sizeToScene();

        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
