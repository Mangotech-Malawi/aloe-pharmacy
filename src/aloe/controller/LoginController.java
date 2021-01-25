/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller;

import aloe.model.QueryManager;
import aloe.model.PopWindow;
import aloe.model.User;
import aloe.model.UserLog;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Student
 */
public class LoginController implements Initializable {
    
    public static String username;
    public static String password;
    public static String logInDate;
    
    @FXML
    private BorderPane rootPane;
    @FXML
    private JFXButton btnExit;
    @FXML
    private JFXButton btnHelp;
    @FXML
    private Pane pane;
    private Circle item_image_circle;
    @FXML
    private JFXTextField Username_Field;
    @FXML
    private Label LabelVerifyPassword;
    @FXML
    private JFXPasswordField Password_Field;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnMaximize;
    @FXML
    private JFXButton btnClose;
 

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
      
    }    

    @FXML
    private void btnExitAction(ActionEvent event) {
          rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnHelpAction(ActionEvent event) {
         
    }

    @FXML
    private void FieldPasswordAction(ActionEvent event) {
           QueryManager Query = new QueryManager();
        PopWindow window = new PopWindow();
        username  = Username_Field.getText().trim();
         password = Password_Field.getText().trim();
       
        if(username.isEmpty() || password.isEmpty()){
            LabelVerifyPassword.setText("Please make sure you have entered all fields");
        }else{
            try {
             
                String query = "select count(*) as no_of_rows FROM users";
                ResultSet rs = Query.getDataQuery(query);
                if(rs.next()){
                    int found = rs.getInt("no_of_rows");
                    System.out.println("Rows found ahahaha" + found);
                    if(found > 0){
                        String query1 = "SELECT * from users ";
                        ResultSet rs1 = Query.getDataQuery(query1);
                        while(rs1.next()){
                            if(username.equals(rs1.getString("username")) && password.equals(rs1.getString("password"))){
                                String privilege = rs1.getString("privilege");
                                privilege = privilege.toLowerCase();
                                UserLog userLog = null;
                                switch(privilege){
                                      case "manager" :
                                        window.getPrimaryStage().setOnHidden(new EventHandler<WindowEvent>() {
                                        @Override
                                        public void handle(WindowEvent event) {
                                                UserLog userLog = new UserLog();
                                                userLog.updateActivities(logInDate,", Exited in Manager Home");
                                                userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());   
                                        }}); 
                                        if(PopWindow.primaryStage.isMaximized()){
                                               window.loadWindow("/aloe/view/manager/Home.fxml", "",true,true,false,false);
                                        }else{
                                             window.loadWindow("/aloe/view/manager/Home.fxml", "",true,false,false,false);
                                        }
                                     
                                        logInDate = LocalDateTime.now().toString();
                                        userLog = new UserLog(username,logInDate,"logged in...","loging In");
                                        Query.insertStatement(userLog.addLog());
                                            userLog.updateActivities(logInDate,", Accessed Manager Home");
                                        userLog.updateLogs(username);
                                        rootPane.getScene().getWindow().hide();break;
                                    case "receptionist" :
                                        window.getPrimaryStage().setOnHidden(new EventHandler<WindowEvent>() {
                                        @Override
                                        public void handle(WindowEvent event) {
                                                UserLog userLog = new UserLog();
                                                userLog.updateActivities(logInDate,", Exited in Receptionist Home");
                                                userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());   
                                        }}); 
                                        if(PopWindow.primaryStage.isMaximized()){
                                               window.loadWindow("/aloe/view/receptionist/Home.fxml", "",true,true,false,false);
                                        }else{
                                             window.loadWindow("/aloe/view/receptionist/Home.fxml", "",true,false,false,false);
                                        }
                                     
                                        logInDate = LocalDateTime.now().toString();
                                        userLog = new UserLog(username,logInDate,"logged in...","loging In");
                                        Query.insertStatement(userLog.addLog());
                                            userLog.updateActivities(logInDate,", Accessed Receptionist Home");
                                        userLog.updateLogs(username);
                                        rootPane.getScene().getWindow().hide();break;
                                    case "pharmacist" :
                                          window.getPrimaryStage().setOnHidden(new EventHandler<WindowEvent>() {
                                        @Override
                                        public void handle(WindowEvent event) {
                                                UserLog userLog = new UserLog();
                                                userLog.updateActivities(logInDate,", Exited in Pharmacist Home");
                                                userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());   
                                        }}); 
                                        if(PopWindow.primaryStage.isMaximized()){
                                            window.loadWindow("/aloe/view/pharmacist/Home.fxml", "",true,true,false,false);
                                        }else{
                                            window.loadWindow("/aloe/view/pharmacist/Home.fxml", "",true,false,false,false);
                                        }
                                     
                                        logInDate = LocalDateTime.now().toString();
                                        userLog = new UserLog(username,logInDate,"logged in...","loging In");
                                        Query.insertStatement(userLog.addLog());
                                        userLog.updateActivities(logInDate,", Accessed Stock Home");
                                        userLog.updateLogs(username);
                                        rootPane.getScene().getWindow().hide();break;
                                        
                                    case "admin" :
                                        if(PopWindow.primaryStage.isMaximized()){
                                            
                                            window.loadWindow("/aloe/view/admin/Home.fxml", "",true,true,false,false);
                                        }else{
                                            window.loadWindow("/aloe/view/admin/Home.fxml", "",true,false,false,false);
                                        }
            
                                        rootPane.getScene().getWindow().hide();break;
                                }
                            }else if(username.equals(rs1.getString("username"))){
                                if(!password.equals(rs1.getString("password"))){
                    
                                    LabelVerifyPassword.setText("Incorrect username or password");
                                }
                            }else{
                                LabelVerifyPassword.setText("Incorrect username or password");
                            
                            }
                        }
                    }else{
                        if(username.equalsIgnoreCase("username") && password.equalsIgnoreCase("password")){
                            window.loadWindow("/aloe/view/admin/Home.fxml", "",true,true,false,false);
                              User user = new User("Admin","admin",
                                "sample@sample.com","None", "None","None","None","none","admin",LocalDateTime.now().toString());
                            Query = new QueryManager();
                            Query.insertStatement(user.addUser());
                        }
                        else
                        {
                            LabelVerifyPassword.setText("System setup, enter Username as username and Password as password");
                        }
                    }
                }
           } catch (SQLException ex) {
                 LabelVerifyPassword.setText("Failed to connect to server");
           }catch (NullPointerException ex){
                LabelVerifyPassword.setText("Make sure the database server is running");
           }
           
        }         
    }

    @FXML
    private void forgotPasswordAction(ActionEvent event) {
        PopWindow window = new PopWindow();
        window.loadWindow("/pharmacysystem/view/forgotPassword_panel.fxml", "Aloe : Forgot Password Helper", true,true);
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
