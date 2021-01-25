/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.ManageUsers;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class HelpController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private StackPane helpPane;
    @FXML
    private JFXButton btnWatchVideo;
    @FXML
    private JFXButton btnViewScreenshots;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
          setCenter("/aloe/view/player.fxml");
        
    }    
       public void setCenter(String location){
        helpPane.getChildren().clear();
        try{
            StackPane pane2 =FXMLLoader.load(getClass().getResource(location));
            ObservableList<Node> elements = pane2.getChildren();
            helpPane.getChildren().setAll(elements);
        
        }catch(IOException ex){
           //Message comes here
        }
    }

    @FXML
    private void btnWatchVideoAction(ActionEvent event) {
        setCenter("/aloe/view/player.fxml");
    }

    @FXML
    private void btnViewScreenshotsAction(ActionEvent event) {
    }
}
