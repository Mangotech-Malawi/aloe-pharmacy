/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.receptionist;

import static aloe.controller.LoginController.logInDate;
import static aloe.controller.LoginController.username;
import aloe.model.Folder;
import aloe.model.PopWindow;
import static aloe.model.PopWindow.loc;
import aloe.model.UserLog;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Student
 */
public class HomeController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label lblUsername;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnMaximize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton btnLogOut;
    @FXML
    private JFXButton btnExit;
    @FXML
    private JFXButton btnPos;
    @FXML
    private JFXButton btnSettings;
    PopWindow window = new PopWindow();
    @FXML
    private JFXButton btnTransaction;
    @FXML
    private JFXProgressBar progressBar;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Folder folder = new Folder();
        folder.createFolders("Aloe\\Sales Reports\\");
        
        lblUsername.setText("Logged in as " + username);
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
    private void btnLogOutAction(ActionEvent event) {
            window = new PopWindow();
        PopWindow.loc.push("/aloe/view/receptionist/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/Login.fxml", "Aloe : Login Form", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/Login.fxml", "Aloe : Login Form", true,false,false,false);
        }
           UserLog userLog = new UserLog();
        userLog.updateActivities(logInDate,", Loggeded Out In Receptionist Home");
        userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());
        rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnExitAction(ActionEvent event) {
        rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnPosAction(ActionEvent event) {
          Task<Integer> task = new Task<Integer>(){
            @Override
            protected Integer call() throws Exception {
                int iterations;
                for (iterations = 0; iterations <= 100;iterations++){
                    if (isCancelled()){
                        break;
                    }
                    updateProgress(iterations, 100);
                   
                    try{
                      Thread.sleep(15);
                      
                    } catch(InterruptedException ex){
                        if (isCancelled()){
                            break;
                        }
                    }
                }
                return iterations;    
            }
            
        };
      
        progressBar.progressProperty().bind(task.progressProperty());
        
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                loadPosPanel();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        
    }
    
    private void loadPosPanel(){
          window = new PopWindow();
        PopWindow.loc.push("/aloe/view/receptionist/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/receptionist/Pos.fxml", "Aloe : Manage Users", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/receptionist/Pos.fxml", "Aloe : Manage Users", true,false,false,false);
        }
         window.getPrimaryStage().setOnHidden(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
                UserLog userLog = new UserLog();
                userLog.updateActivities(logInDate,", Exited in Receptionist Point Of Sale");
                userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());   
        }}); 
        rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnSettingsAction(ActionEvent event) {
         Task<Integer> task = new Task<Integer>(){
            @Override
            protected Integer call() throws Exception {
                int iterations;
                for (iterations = 0; iterations <= 100;iterations++){
                    if (isCancelled()){
                        break;
                    }
                    updateProgress(iterations, 100);
                   
                    try{
                      Thread.sleep(15);
                      
                    } catch(InterruptedException ex){
                        if (isCancelled()){
                            break;
                        }
                    }
                }
                return iterations;    
            }
            
        };
      
        progressBar.progressProperty().bind(task.progressProperty());
        
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                 loadSettingsPanel();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
       
    }
    private void loadSettingsPanel(){
        window = new PopWindow();
        PopWindow.loc.push("/aloe/view/receptionist/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/receptionist/settings.fxml", "Aloe : Manage Users", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/receptionist/settings.fxml", "Aloe : Manage Users", true,false,false,false);
        }
        window.getPrimaryStage().setOnHidden(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
                UserLog userLog = new UserLog();
                userLog.updateActivities(logInDate,", Exited in Receptionist Settings");
                userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());   
        }}); 
        rootPane.getScene().getWindow().hide();            
    }

    @FXML
    private void btnTransactionAction(ActionEvent event) {
           Task<Integer> task = new Task<Integer>(){
            @Override
            protected Integer call() throws Exception {
                int iterations;
                for (iterations = 0; iterations <= 100;iterations++){
                    if (isCancelled()){
                        break;
                    }
                    updateProgress(iterations, 100);
                   
                    try{
                      Thread.sleep(15);
                      
                    } catch(InterruptedException ex){
                        if (isCancelled()){
                            break;
                        }
                    }
                }
                return iterations;    
            }
            
        };
      
        progressBar.progressProperty().bind(task.progressProperty());
        
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                loadTransactionPanel();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
       
    }
    
    private void loadTransactionPanel(){
          window = new PopWindow();
            PopWindow.loc.push("/aloe/view/receptionist/Home.fxml");
            if(PopWindow.primaryStage.isMaximized()){
                window.loadWindow("/aloe/view/receptionist/Transactions.fxml", "Aloe : Manage Users", true,true,false,false);
            }else{
                window.loadWindow("/aloe/view/receptionist/Transactions.fxml", "Aloe : Manage Users", true,false,false,false);
            }
            window.getPrimaryStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                    UserLog userLog = new UserLog();
                    userLog.updateActivities(logInDate,", Exited in Reeceptionist Transactions Panel");
                    userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());   
            }}); 
             rootPane.getScene().getWindow().hide();      
    }
}
