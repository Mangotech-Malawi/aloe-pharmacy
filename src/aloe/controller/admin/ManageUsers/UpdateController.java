/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.ManageUsers;

import static aloe.controller.admin.ManageUsers.ViewController.index;
import static aloe.controller.admin.ManageUsers.ViewController.userList;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import aloe.model.User;
import aloe.model.Verifier;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class UpdateController implements Initializable {
    ObservableList<String> privilege = FXCollections.observableArrayList();
    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXTextField txtFirstname;
    @FXML
    private JFXTextField txtLastname;
    @FXML
    private JFXTextField txtEmailAddress;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private JFXTextField txtPNum;
    @FXML
    private JFXRadioButton radMale;
    @FXML
    private JFXRadioButton radFemale;
    private String gender = "male";
    @FXML
    private JFXComboBox<String> privilegeCombo;
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
        loadComboData();
    }   
      private void loadComboData(){
       privilege.addAll("manager","pharmacist","receptionist");
       privilegeCombo.getItems().addAll(privilege);
   }
    private void loadLabelData(){
        dobPicker.setValue(LocalDate.parse(userList.get(index).getDob()));
        txtFirstname.setText(userList.get(index).getFirstname());
        txtLastname.setText(userList.get(index).getLastname());
        txtEmailAddress.setText(userList.get(index).getEmail());
        privilegeCombo.setValue(userList.get(index).getPrivilege());
        txtPNum.setText(userList.get(index).getContact());
        if(userList.get(index).getGender().equalsIgnoreCase("male")){
            radMale.setSelected(true);
            radFemale.setSelected(false);
        }else{
            radMale.setSelected(false);
            radFemale.setSelected(true);
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

    @FXML
    private void btnSaveAction(ActionEvent event) {
         if(isValid()){//If all fields are filled execution of statements continues
            String firstname = txtFirstname.getText();
            String lastname = txtLastname.getText();
            String emailAddress = txtEmailAddress.getText();
            String dob = dobPicker.getValue().toString();
          
            String pnum = txtPNum.getText();
           
            Verifier  verify = new Verifier();
            if((verify.checkNumbers(pnum)) || (verify.checkLetters(firstname)) 
                    || verify.validateEmail(emailAddress) || verify.checkLetters(lastname) ) { //Validating information entered
                User user = new User(firstname,lastname,emailAddress,gender,dob,pnum,privilegeCombo.getValue());
                QueryManager Query = new QueryManager();
                if(Query.insertStatement(user.updateUser(userList.get(index).getUsername()))){
                     Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Information ");
                    alert.setContentText("User Updated succesfully");
                    alert.showAndWait();
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                   alert.setHeaderText("Error");
                   alert.setContentText("Failed to update user");
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
                txtEmailAddress.getText().trim().isEmpty() || dobPicker.getValue().toString().isEmpty() 
                        || txtPNum.getText().trim().isEmpty() || privilegeCombo.getValue().isEmpty()){
          return false; // return false if one/more fields are not filled
    }else{
          return true; // return true if all fields are filled
      }
    }

    @FXML
    private void btnCancelAction(ActionEvent event) {
        txtFirstname.clear();
        txtLastname.clear();
        txtEmailAddress.clear();
        radMale.setSelected(true);
        radFemale.setSelected(false);
        dobPicker.setValue(null);
        txtPNum.clear();
    }
    
}
