/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;

import static aloe.controller.pharmacist.stock.DashboardController.drugNamesList;
import aloe.model.Entry;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class PackThresholdNotificationsController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXListView<String> drugNamesListView;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadDrugNames();
    }    
    
    private void loadDrugNames(){
         drugNamesListView.getItems().setAll(drugNamesList);
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        stackPane.getScene().getWindow().hide();
    }
    
}
