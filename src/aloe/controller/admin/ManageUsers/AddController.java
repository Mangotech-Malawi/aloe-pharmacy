/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.ManageUsers;

import aloe.model.QueryManager;
import aloe.model.User;
import aloe.model.Verifier;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class AddController implements Initializable {
     ObservableList<String> privilege = FXCollections.observableArrayList();
    @FXML
    private StackPane stackPane;
    @FXML
    private JFXTextField txtFirstname;
    @FXML
    private JFXTextField txtLastname;
    @FXML
    private JFXTextField txtEmailAddress;
    @FXML
    private JFXRadioButton radMale;
    @FXML
    private JFXRadioButton radFemale;
    @FXML
    private DatePicker dobPicker;
  
    @FXML
    private JFXTextField txtPNum;
    @FXML
    private JFXTextField txtDefUsername; //Default username when a user wants to login for the first time
    @FXML
    private JFXTextField txtDefPassword;//Default password when a user wants to login for the first time
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private Label lblStatus;
    private String gender = "male";
    @FXML
    private JFXComboBox<String> privilegeCombo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadComboData();
    }    
   private void loadComboData(){
       privilege.addAll("manager","pharmacist","receptionist");
       privilegeCombo.getItems().addAll(privilege);
   }
   private void loadLabelData(){
       
   }
    @FXML
    private void btnSaveAction(ActionEvent event) {
        if(isValid()){//If all fields are filled execution of statements continues
            String firstname = txtFirstname.getText();
            String lastname = txtLastname.getText();
            String emailAddress = txtEmailAddress.getText();
            String dob = dobPicker.getValue().toString();
          
            String pnum = txtPNum.getText();
            String defUsername = txtDefUsername.getText();
            String defPassword = txtDefPassword.getText();
            Verifier  verify = new Verifier();
            if((verify.checkNumbers(pnum)) || (verify.checkLetters(firstname)) 
                    || verify.validateEmail(emailAddress) || verify.checkLetters(lastname) ) { //Validating information entered
                User user = new User(defUsername,defPassword,emailAddress,
                firstname,lastname,gender,dob,pnum,privilegeCombo.getValue(),LocalDate.now().toString());
                QueryManager Query = new QueryManager();
                if(Query.insertStatement(user.addUser())){
                     Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Information ");
                    alert.setContentText("User added succesfully");
                    alert.showAndWait();
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                   alert.setHeaderText("Error");
                   alert.setContentText("Failed to add user");
                   alert.showAndWait();
                }
                     
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Entry Error");
                alert.setContentText("Please correct values");
                alert.showAndWait();
            }         
        }else{//Shows error message if all fields are not filled
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Entry Error");
            alert.setContentText("Please enter all details");
            alert.showAndWait();
        }
    }
    private boolean isValid(){ //Verifies if the user has entered all details
      if(txtFirstname.getText().trim().isEmpty() || txtLastname.getText().trim().isEmpty() ||
                txtEmailAddress.getText().trim().isEmpty() || dobPicker.getValue()==null
                        || txtPNum.getText().trim().isEmpty()
                        || txtDefUsername.getText().trim().isEmpty()
                        || txtDefPassword.getText().trim().isEmpty() || privilegeCombo.getValue().isEmpty()){
          return false; // return false if one/more fields are not filled
    }else{
          return true; // return true if all fields are filled
      }
    }
    

    @FXML
    private void btnCancelAction(ActionEvent event) { // Clearing fields on the form 
        txtFirstname.clear();
        txtLastname.clear();
        txtEmailAddress.clear();
        radMale.setSelected(true);
        radFemale.setSelected(false);
        dobPicker.setValue(null);
        txtPNum.clear();
        txtDefUsername.clear();
        txtDefPassword.clear();
    }

    @FXML
    private void radMaleSelected(ActionEvent event) {
        if(radMale.isSelected()){
            radFemale.setSelected(false);
            gender = "male";
        }
    }

    @FXML
    private void radFemaleSelected(ActionEvent event) {
       if(radFemale.isSelected()){
           radMale.setSelected(false);
           gender ="female";
       }
    }
    
}
