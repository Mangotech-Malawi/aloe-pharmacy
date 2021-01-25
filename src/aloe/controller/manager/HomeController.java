/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager;

import static aloe.controller.LoginController.logInDate;
import static aloe.controller.LoginController.username;
import static aloe.controller.manager.dashboards.MasterController.service;
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
 * @author Senze
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
    private JFXButton btnDashboards;
    @FXML
    private JFXButton btnCharts;
    @FXML
    private JFXButton btnTables;
    @FXML
    private JFXButton btnSettings;
    PopWindow window = new PopWindow();
    @FXML
    private JFXProgressBar progressBar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
         UserLog userLog = new UserLog();
         userLog.updateActivities(logInDate,", Exited in Manager Home");
         userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());
         rootPane.getScene().getWindow().hide();
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
           loc.clear();
        UserLog userLog = new UserLog();
        userLog.updateActivities(logInDate,", Logged Out");
        userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());
        rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnExitAction(ActionEvent event) {
          UserLog userLog = new UserLog();
         userLog.updateActivities(logInDate,", Exited in Manager Home");
         userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());
         rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnDashboardsAction(ActionEvent event) {
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
                loadDashboardPanel();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    private void loadDashboardPanel(){
        window = new PopWindow();
        PopWindow.loc.push("/aloe/view/manager/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/manager/dashboards.fxml", "Aloe : Manage Users", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/manager/dashboards.fxml", "Aloe : Manage Users", true,false,false,false);
        }
          window.getPrimaryStage().setOnHidden(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
                UserLog userLog = new UserLog();
                userLog.updateActivities(logInDate,", Exited in Dashboards Panel");
                userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());   
        }}); 
        
        rootPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnChartsAction(ActionEvent event) {
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
                loadChartsPanel();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadChartsPanel(){
        window = new PopWindow();
        PopWindow.loc.push("/aloe/view/manager/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/manager/charts.fxml", "Aloe : Manage Users", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/manager/charts.fxml", "Aloe : Manage Users", true,false,false,false);
        }
          window.getPrimaryStage().setOnHidden(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
                UserLog userLog = new UserLog();
                userLog.updateActivities(logInDate,", Exited in Charts Panel");
                userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());   
        }}); 
        
        rootPane.getScene().getWindow().hide();
    }
    @FXML
    private void btnTablesAction(ActionEvent event) {
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
                loadTablesPanel();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
   
     private void loadTablesPanel(){
        window = new PopWindow();
        PopWindow.loc.push("/aloe/view/manager/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/manager/tables.fxml", "Aloe : Manage Users", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/manager/tables.fxml", "Aloe : Manage Users", true,false,false,false);
        }
          window.getPrimaryStage().setOnHidden(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
                UserLog userLog = new UserLog();
                userLog.updateActivities(logInDate,", Exited in Tables Panel");
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
        PopWindow.loc.push("/aloe/view/manager/Home.fxml");
        if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/manager/settings.fxml", "Aloe : Manage Users", true,true,false,false);
        }else{
            window.loadWindow("/aloe/view/manager/settings.fxml", "Aloe : Manage Users", true,false,false,false);
        }
          window.getPrimaryStage().setOnHidden(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
                UserLog userLog = new UserLog();
                userLog.updateActivities(logInDate,", Exited in Settings Panel");
                userLog.updateLogOutTime(logInDate, LocalDateTime.now().toString());   
        }}); 
        
        rootPane.getScene().getWindow().hide();
    }
    
}
