/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin;

import aloe.controller.LoginController;
import static aloe.controller.LoginController.username;
import aloe.model.PopWindow;
import static aloe.model.PopWindow.loc;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Student
 */
public class HomeController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private JFXButton btnLogOut;
    @FXML
    private JFXButton btnExit;
    @FXML
    private JFXButton btnManageUsers;
    @FXML
    private JFXButton btnSettings;
    @FXML
    private JFXButton btnViewLog;
    PopWindow window = new PopWindow();  
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnMaximize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private Label lblUsername;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         lblUsername.setText("Logged in as : " + LoginController.username);
        if(PopWindow.primaryStage.isMaximized()){
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(minimizeIcon);
        }else{
            FontAwesomeIconView maxmizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE);
            maxmizeIcon.setGlyphSize(17);
            maxmizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(maxmizeIcon);
        }
        loc.clear();
     
    }    

    @FXML
    private void btnLogOutAction(ActionEvent event) {
        window = new PopWindow();
        PopWindow.loc.push("/aloe/view/admin/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/Login.fxml", "Aloe : Login Form", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/Login.fxml", "Aloe : Login Form", true,false,false,false);
        }
        rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnExitAction(ActionEvent event) {
        rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnManageUsersAction(ActionEvent event) {
      //calling the manage user window
        window = new PopWindow();
        PopWindow.loc.push("/aloe/view/admin/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/admin/ManageUsers.fxml", "Aloe : Manage Users", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/admin/ManageUsers.fxml", "Aloe : Manage Users", true,false,false,false);
        }
        rootPane.getScene().getWindow().hide();
        
    }

    @FXML
    private void btnSettingsAction(ActionEvent event) {
      window = new PopWindow();
        PopWindow.loc.push("/aloe/view/admin/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/admin/AdminSettings.fxml", "Aloe : Manage Users", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/admin/AdminSettings.fxml", "Aloe : Manage Users", true,false,false,false);
        }
        rootPane.getScene().getWindow().hide();
        
    }

    @FXML
    private void btnViewLogAction(ActionEvent event) {
  
      window = new PopWindow();
        PopWindow.loc.push("/aloe/view/admin/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/admin/Log.fxml", "Aloe : Manage Users", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/admin/Log.fxml", "Aloe : Manage Users", true,false,false,false);
        }
        rootPane.getScene().getWindow().hide();
    }

    private void btnSystemAction(ActionEvent event) {
      
      window = new PopWindow();
        PopWindow.loc.push("/aloe/view/admin/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/admin/SystemSettings.fxml", "Aloe : Manage Users", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/admin/SystemSettings.fxml", "Aloe : Manage Users", true,false,false,false);
        }
        rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnMnimizeAction(ActionEvent event) {
         PopWindow.primaryStage.setIconified(true);
    }

    @FXML
    private void btnMaximizeAction(ActionEvent event) {
          if(PopWindow.primaryStage.isMaximized()){
            PopWindow.primaryStage.setMaximized(false);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(minimizeIcon);
        }else{
            PopWindow.primaryStage.setMaximized(true);
            FontAwesomeIconView maxmizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT);
            maxmizeIcon.setGlyphSize(17);
            maxmizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(maxmizeIcon);
        }
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
          rootPane.getScene().getWindow().hide();
    }
    
  
    
}
