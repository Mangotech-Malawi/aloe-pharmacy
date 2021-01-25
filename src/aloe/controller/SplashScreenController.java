/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller;

import aloe.model.ControlledScreen;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Student
 */
public class SplashScreenController implements Initializable, ControlledScreen {

    @FXML
    private StackPane rootPane;
     ScreensController myController;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         new SplashScreen().start();
       
    } 
     public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }
       class SplashScreen extends Thread{
        @Override
        public void run(){
            try {
                Thread.sleep(5000);
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run(){
                     ScreensController mainContainer = new ScreensController();
                  
                     QueryManager Query = new QueryManager("","");
                     String query = "SELECT COUNT(*),username,password FROM users";
                     ResultSet rs = Query.getDataQuery(query);
                        try {
                            if(rs.next()){
                                if(rs.getInt(1) > 0){
                                    QueryManager.dbPassword = rs.getString(3);
                                    QueryManager.dbUsername =  rs.getString(2);
                                    PopWindow window = new PopWindow();
                                    window.loadWindow("/aloe/view/Login.fxml", "",true,true,false,false);
                                    rootPane.getScene().getWindow().hide();
                                }else{
                                     PopWindow window = new PopWindow();
                                    window.loadWindow("/aloe/view/UserSetup.fxml", "",true,true,false,false);
                                    rootPane.getScene().getWindow().hide();
                                }
                                rs.close();
                                Query.ClearDB();
                            }else{
                                PopWindow window = new PopWindow();
                                window.loadWindow("/aloe/view/UserSetup.fxml", "",true,true,false,false);
                                rootPane.getScene().getWindow().hide();
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(SplashScreenController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    
                        }
            });   
            } catch (InterruptedException ex) {
                Logger.getLogger(SplashScreenController.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        }
    
}
