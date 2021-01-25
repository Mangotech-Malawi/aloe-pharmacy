/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;

import static aloe.controller.pharmacist.stock.ViewDrugsController.drugList;
import static aloe.controller.pharmacist.stock.ViewDrugsController.index;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import aloe.model.Threshold;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class ThresholdController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private JFXButton btnClose;
    @FXML
    private Label lblDrugName;
    @FXML
    private JFXTextField txtEntryQuantity;
    @FXML
    private JFXTextField txtPackQuantity;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnReset;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lblDrugName.setText(drugList.get(index).getName());
        loadThresholds(drugList.get(index).getId());
    }    
    
    private void loadThresholds(int id){
        try
        {QueryManager Query = new QueryManager();
            String entryThresholdQuery = "SELECT entryQty FROM"
                    + " entriesThresholds WHERE id ='" + id + "';";
            ResultSet rs1 =  Query.getDataQuery(entryThresholdQuery);
            if(rs1.next()){
                txtEntryQuantity.setText(rs1.getString(1));
            }
            
             String packsThresholdQuery = "SELECT numOfPacks FROM"
                    + " packsThresholds WHERE id ='" + id + "';";
            ResultSet rs2 =  Query.getDataQuery(packsThresholdQuery);
            if(rs2.next()){
                txtPackQuantity.setText(rs2.getString(1));
            }
        }catch(SQLException ex){
            
        }
    }
    @FXML
    private void btnMnimizeAction(ActionEvent event) {
          PopWindow.primaryStage.setIconified(true);
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        stackPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnSaveAction(ActionEvent event) {
        if(isValid()){
            try{
                QueryManager Query = new QueryManager();
                int entryQty = Integer.parseInt(txtEntryQuantity.getText().trim());
                int packQuantity = Integer.parseInt(txtPackQuantity.getText().trim());
                
                Threshold threshold = new Threshold();
                threshold.setEntryQty(entryQty);
                threshold.setNumOfPacks(packQuantity);
                if(Query.execAction(threshold.updateEntryThresHold
                        (drugList.get(index).getId())) && Query.execAction(threshold.updatePackThresHold
                        (drugList.get(index).getId())) ){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Thresholds Updated Successfully");
                    alert.setContentText("You have updated drug threshold levels succesfully");
                    alert.showAndWait();
                }
            }catch(NumberFormatException ex){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Entry Error");
                alert.setContentText("Please enter integers only");
                alert.showAndWait();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Entry Error");
            alert.setContentText("Please enter all details");
            alert.showAndWait();
        }
    }
    
    private boolean isValid(){
           if(txtEntryQuantity.getText().trim().isEmpty() || txtPackQuantity.getText().trim().isEmpty()){
                return false; // return false if one/more fields are not filled
            }else{
                  return true; // return true if all fields are filled
              }
    }

    @FXML
    private void btnResetAction(ActionEvent event) {
         if(isValid()){
            try{
                QueryManager Query = new QueryManager();
               
                
                Threshold threshold = new Threshold();
                threshold.setEntryQty(10);
                threshold.setNumOfPacks(20);
                if(Query.execAction(threshold.updateEntryThresHold
                        (drugList.get(index).getId())) && Query.execAction(threshold.updatePackThresHold
                        (drugList.get(index).getId())) ){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Thresholds Updated Successfully");
                    alert.setContentText("You have updated drug threshold levels succesfully");
                    alert.showAndWait();
                    
                }
            }catch(NumberFormatException ex){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Entry Error");
                alert.setContentText("Please enter integers only");
                alert.showAndWait();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Entry Error");
            alert.setContentText("Please enter all details");
            alert.showAndWait();
        }
    }
    
}
