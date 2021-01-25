/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.expense;

import static aloe.controller.pharmacist.expense.ViewExpensesController.expenseList;
import static aloe.controller.pharmacist.expense.ViewExpensesController.index;
import aloe.model.Expense;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class UpdateExpenseController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnClose;
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
        loadLabelData();
    } 
     private void loadCategories(){
        catList.addAll("Eletricity","Food","Water","Transport","Airtime");
        expenseCatCombo.getItems().setAll(catList);
    }
    private void loadLabelData(){
       try {
            QueryManager Query = new QueryManager();
            String expenseQuery = "SELECT description FROM "
                    + "expenses where expenseNo ='" + expenseList.get(index).getExpenseNo() + "';";
            ResultSet rs1 = Query.getDataQuery(expenseQuery);
            if(rs1.next()){
                lblStatus.setText("Update Expense No. " + expenseList.get(index).getExpenseNo() +"");
                txtAmount.setText(""+expenseList.get(index).getAmount());
                expenseCatCombo.setValue(expenseList.get(index).getCategory());
                txtDescription.setText(rs1.getString("description"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ExpenseDescriptionController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
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
            try{
                double amount  = Double.parseDouble(txtAmount.getText().trim());
                String category = expenseCatCombo.getValue();
                String description = txtDescription.getText().trim();
                Expense expense = new Expense(amount,category,description);
                
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Confirm Expense Update ");
                alert.setContentText("Are You Sure you want to add this expense details");
                alert.showAndWait();
                if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                  QueryManager  Query = new QueryManager();//Connectung to database
                  if(Query.insertStatement(expense.updateExpese(expenseList.get(index).getExpenseNo() ))){
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Success!! ");
                        alert.setContentText("Expense updated succesfully \nYou can view it in expenses table");
                        alert.showAndWait();
                        stackPane.getScene().getWindow().hide();
                  }else{
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error ");
                        alert.setContentText("Failed to update expense");
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
