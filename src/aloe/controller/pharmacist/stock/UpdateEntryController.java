/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;

import static aloe.controller.pharmacist.stock.ViewEntriesController.entryList;
import static aloe.controller.pharmacist.stock.ViewEntriesController.index;
import aloe.model.Entry;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import aloe.model.Verifier;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.textfield.TextFields;


/**
 * FXML Controller class
 *
 * @author pc
 */
public class UpdateEntryController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXTextField txtBatchNo;
    @FXML
    private JFXTextField txtDrugId;
    @FXML
    private JFXTextField txtQuantity;
    @FXML
    private DatePicker entryDatePicker;
    @FXML
    private JFXTextField txtSupplierName;
    @FXML
    private DatePicker expiryDatePicker;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadLabelData();
        loadSupplierNames();
    }  
    private void loadSupplierNames(){
     try {
         ObservableList<String> unitsList = FXCollections.observableArrayList();
         QueryManager Query = new QueryManager();
         String drugSupplierNamesQuery = "SELECT supplierName FROM entries";
         ResultSet rs1 = Query.getDataQuery(drugSupplierNamesQuery);
         while(rs1.next()){
             unitsList.add(rs1.getString(1));
         }
                TextFields.bindAutoCompletion(txtSupplierName, unitsList);

     } catch (SQLException ex) {
         Logger.getLogger(NewEntryController.class.getName()).log(Level.SEVERE, null, ex);
     }

    }
    private void loadLabelData(){
        lblStatus.setText(entryList.get(index).getName());
        txtBatchNo.setText(entryList.get(index).getBatchNo() + "");
        txtDrugId.setText(getId(entryList.get(index).getBatchNo() + ""));
        txtSupplierName.setText(entryList.get(index).getSupplierName());
        txtQuantity.setText(""+ entryList.get(index).getQuantity());
        entryDatePicker.setValue(LocalDate.parse(entryList.get(index).getRegDate()));
        expiryDatePicker.setValue(LocalDate.parse(entryList.get(index).getExpiryDate()));
    }
    private String getId(String batchNo){
        QueryManager Query = new QueryManager();
        String query = "SELECT id from entries where batchNo = '" + batchNo + "';";
        ResultSet rs = Query.getDataQuery(query);
        String id = "id";
        try {
            if(rs.next()){
                id = rs.getString("id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpdateEntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
    @FXML
    private void btnMnimizeAction(ActionEvent event) {
        PopWindow.childStage.setIconified(true);
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        stackPane.getScene().getWindow().hide();
    }
    
    @FXML
    private void btnSaveAction(ActionEvent event) {
        if(isValid()){
          
            String drugId = txtDrugId.getText().trim();
            String supplierName = txtSupplierName.getText().trim();
            String entryDate = entryDatePicker.getValue().toString();
            String expiryDate = expiryDatePicker.getValue().toString();
            if(!((expiryDatePicker.getValue().isBefore(entryDatePicker.getValue()) ||
                    expiryDatePicker.getValue().isEqual(entryDatePicker.getValue())))){
                    try{
                        int batchNo = Integer.parseInt(txtBatchNo.getText().trim());
                        Integer quantity = Integer.parseInt(txtQuantity.getText().trim());
                        Verifier  verify = new Verifier();
                         //Validating information entered

                            Entry entry = new Entry(Integer.parseInt(drugId),quantity,supplierName,expiryDate,entryDate);
                            QueryManager Query = new QueryManager();
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setHeaderText("Confirm Drug Entry Update ");
                            alert.setContentText("Entry Updated succesfully");
                            alert.showAndWait();
                            if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                                if(Query.execActionUpdate(entry.updateEntry(entryList.get(index).getBatchNo()))){
                                    stackPane.getScene().getWindow().hide();
                                }else{
                                   Alert error = new Alert(Alert.AlertType.ERROR);
                                   error.setHeaderText("Error");
                                   error.setContentText("Failed to Update Entry");
                                   error.showAndWait();
                                }      
                            }
                    }catch(NumberFormatException ex){
                         Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Entry Error");
                        alert.setContentText("Please enter correct quantity");
                        alert.showAndWait();
                    }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Entry Error");
                alert.setContentText("Please choose entry date \n and expiry date correctly");
                alert.showAndWait();  
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Entry Error");
            alert.setContentText("Please enter all details");
            alert.showAndWait();
        }
    }
    
    private boolean isValid(){ //Verifies if the user has entered all details
      if(txtBatchNo.getText().trim().isEmpty() || txtDrugId.getText().trim().isEmpty() ||
                txtQuantity.getText().trim().isEmpty() 
                        || txtSupplierName.getText().trim().isEmpty()
                        || entryDatePicker.getValue().toString().isEmpty()
                        || expiryDatePicker.getValue().toString().isEmpty()){
          return false; // return false if one/more fields are not filled
    }else{
          return true; // return true if all fields are filled
      }
    }

    @FXML
    private void btnCancelAction(ActionEvent event) {
         stackPane.getScene().getWindow().hide();
    }
    
    
}
