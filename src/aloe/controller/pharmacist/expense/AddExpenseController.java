/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.expense;

import aloe.model.Expense;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
public class AddExpenseController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXTextField txtAmount;
    @FXML
    private JFXComboBox<String> expenseCatCombo;
    @FXML
    private JFXTextArea txtDescription;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
    private final ObservableList<String> catList = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadCategories();
    }    
    private void loadCategories(){
        catList.addAll("Eletricity","Food","Water","Transport","Airtime");
        expenseCatCombo.getItems().setAll(catList);
    }

    @FXML
    private void btnSaveAction(ActionEvent event) {
        if(isValid()){
            
            try{
                double amount  = Double.parseDouble(txtAmount.getText().trim());
                String category = expenseCatCombo.getValue();
                String description = txtDescription.getText().trim();
                Expense expense = new Expense(amount,category,description,LocalDate.now().toString());
                
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Confirm Adding Expense ");
                alert.setContentText("Are You Sure you want to add this expense details");
                alert.showAndWait();
                if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                  QueryManager  Query = new QueryManager();//Connectung to database
                  if(Query.insertStatement(expense.addExpense())){
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Success!! ");
                        alert.setContentText("Expense added succesfully \nYou can view it in expenses table");
                        alert.showAndWait();
                  }else{
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error ");
                        alert.setContentText("Failed to add expense");
                        alert.showAndWait();
                  }
                }
                
            }catch(NumberFormatException ex){//Throws an exception if number conversion fails
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Entry Error");
                alert.setContentText("Please enter correct details");
                alert.showAndWait();
            }
        }else{//Shows error if all fields are not filled in
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Entry Error");
            alert.setContentText("Please enter all details");
            alert.showAndWait();
        }
    }
     
    private boolean isValid(){ //Verifies if the user has entered all details
      if(txtAmount.getText().trim().isEmpty() || txtDescription.getText().trim().isEmpty() 
              || expenseCatCombo.getValue().isEmpty()){
          return false; // return false if one/more fields are not filled
    }else{
          return true; // return true if all fields are filled
      }
    }
    @FXML
    private void btnCancelAction(ActionEvent event) {
        txtAmount.clear();
        txtDescription.clear();
        expenseCatCombo.setValue(null);
        expenseCatCombo.setPromptText("Expense Category");
    }
    
}
