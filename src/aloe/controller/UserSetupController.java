/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller;

import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class UserSetupController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnMaximize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton btnExit;
    @FXML
    private JFXButton btnHelp;
    @FXML
    private Pane pane;
    @FXML
    private JFXTextField Username_Field;
    @FXML
    private Label LabelVerifyPassword;
    @FXML
    private JFXPasswordField Password_Field;
    @FXML
    private JFXButton btnSet;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
    
    @FXML
    private void btnExitAction(ActionEvent event) {
        rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnHelpAction(ActionEvent event) {
    }

    @FXML
    private void btnSetAction(ActionEvent event) {
        String dbUsername = Username_Field.getText().trim();
        String dbPassword = Password_Field.getText().trim();
        if(dbUsername.isEmpty() || dbPassword.isEmpty()){
              LabelVerifyPassword.setText("Please enter all details");
        }else{
            QueryManager Query = new QueryManager("","");
            String query = "INSERT INTO users (username,password)" +" VALUES (" + "'"+ dbUsername + "'," + "'" + dbPassword +"');";
            System.out.println(query);
            if(Query.insertStatement(query)){
                QueryManager.dbPassword = dbPassword;
                QueryManager.dbUsername = dbUsername;
                PopWindow window = new PopWindow();
                window.loadWindow("/aloe/view/Login.fxml", "",true,true,false,false);
                rootPane.getScene().getWindow().hide();
                Query.ClearDB();
            }else{
                LabelVerifyPassword.setText("Failed to add to details");
            }
        }
    }
    
    
    
}
