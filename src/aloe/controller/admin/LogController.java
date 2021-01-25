/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin;

import aloe.model.PopWindow;
import static aloe.model.PopWindow.loc;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Student
 */
public class LogController implements Initializable {

    @FXML
    private StackPane stackpane;
    @FXML
    private BorderPane borderpane;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnMaximize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton btnBack;
    @FXML
    private JFXButton bntHome;
    @FXML
    private JFXButton btnSettings;
    @FXML
    private JFXButton btnManageUsers;
    @FXML
    private JFXButton btnLogChart;
    @FXML
    private JFXButton btnViewLog;
    @FXML
    private StackPane switchPane;
    @FXML
    private JFXButton btnDashBoard;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         setCenter("/aloe/view/admin/userLog/logChart.fxml");
          if(PopWindow.primaryStage.isMaximized()){
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT);
            minimizeIcon.setGlyphSize(25);
            minimizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(minimizeIcon);
        }else{
            FontAwesomeIconView maxmizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE);
            maxmizeIcon.setGlyphSize(25);
            maxmizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(maxmizeIcon);
        }
          setCenter("/aloe/view/admin/userLog/dashboard.fxml");
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
            minimizeIcon.setGlyphSize(25);
            minimizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(minimizeIcon);
        }else{
            PopWindow.primaryStage.setMaximized(true);
            FontAwesomeIconView maxmizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT);
            maxmizeIcon.setGlyphSize(25);
            maxmizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(maxmizeIcon);
        }
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        stackpane.getScene().getWindow().hide();
    }


    @FXML
    private void btnBackAction(ActionEvent event) {
        PopWindow window = new PopWindow();
        if(PopWindow.primaryStage.isMaximized()){
               window.loadWindow(loc.pop(), "Pharmacy System : Transactions",true,true,false,false);
         }else{
                window.loadWindow(loc.pop(), "Pharmacy System : Transactions",true,false,false,false);
         
        }
        stackpane.getScene().getWindow().hide();
    }

    private void ExitFunction(ActionEvent event) {
        stackpane.getScene().getWindow().hide();
    }

    @FXML
    private void btnHomeAction(ActionEvent event) {
         PopWindow window = new PopWindow();
        if(PopWindow.primaryStage.isMaximized()){
              window.loadWindow("/aloe/view/admin/Home.fxml", "Pharmacy System : Home",true,true,false,false);
        }else{
               window.loadWindow("/aloe/view/admin/Home.fxml", "Pharmacy System : Home",true,false,false,false);
        }
        stackpane.getScene().getWindow().hide(); 
    }


    @FXML
    private void btnStettingsAction(ActionEvent event) {
          PopWindow window = new PopWindow();
        loc.push("/aloe/view/admin/Log.fxml");
        if(PopWindow.primaryStage.isMaximized()){
              window.loadWindow("/aloe/view/admin/AdminSettings.fxml", "Pharmacy System : Reports",true,true,false,false);
        }else{
              window.loadWindow("/aloe/view/admin/AdminSettings.fxmll", "Pharmacy System : Reports",true,false,false,false);
        }
        stackpane.getScene().getWindow().hide();
    }

    private void btnSystemAction(ActionEvent event) {
           PopWindow window = new PopWindow();
        loc.push("/aloe/view/admin/AdminSettings.fxml");
        if(PopWindow.primaryStage.isMaximized()){
              window.loadWindow("/aloe/view/admin/SystemSettings.fxml", "Pharmacy System : Reports",true,true,false,false);
        }else{
              window.loadWindow("/aloe/view/admin/SystemSettings.fxmll", "Pharmacy System : Reports",true,false,false,false);
        }
        stackpane.getScene().getWindow().hide();
    }

    @FXML
    private void btnManageUserAction(ActionEvent event) {
         PopWindow window = new PopWindow();
         loc.push("/aloe/view/admin/AdminSettings.fxml");
        if(PopWindow.primaryStage.isMaximized()){
              window.loadWindow("/aloe/view/admin/ManageUsers.fxml", "Pharmacy System : Reports",true,true,false,false);
        }else{
              window.loadWindow("/aloe/view/admin/ManageUsers.fxml", "Pharmacy System : Reports",true,false,false,false);
        }

        stackpane.getScene().getWindow().hide();
    }
    
     @FXML
    private void btnDashBoardAction(ActionEvent event) {
         setCenter("/aloe/view/admin/userLog/dashboard.fxml");
    }
    @FXML
    private void btnLogChartAction(ActionEvent event) {
        setCenter("/aloe/view/admin/userLog/logChart.fxml");
    }

    @FXML
    private void btnViewLogAction(ActionEvent event) {
           setCenter("/aloe/view/admin/userLog/logView.fxml");
    }
     public void setCenter(String location){
        switchPane.getChildren().clear();
        try{
            StackPane pane2 =FXMLLoader.load(getClass().getResource(location));
            ObservableList<Node> elements = pane2.getChildren();
            switchPane.getChildren().setAll(elements);
        }catch(IOException ex){
           //Message comes here
        }
    }

   
    
}
