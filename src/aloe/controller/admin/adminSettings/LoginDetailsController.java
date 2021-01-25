/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.adminSettings;

import aloe.controller.LoginController;
import aloe.model.QueryManager;
import aloe.model.Transaction;
import aloe.model.User;
import aloe.model.Verifier;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class LoginDetailsController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
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
    @FXML
    private JFXTextField txtDefUsername;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXPasswordField curPassword;
    @FXML
    private JFXPasswordField newPassword;
    @FXML
    private JFXPasswordField confirmPassword;
    private String gender;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadLabelData();
    }    
    private void loadLabelData(){ // getting admin current details
        try {
            QueryManager Query = new QueryManager();
            String query = "SELECT * FROM users where username = '"
                    + LoginController.username + "';"; // get data from user table using username used for logged in
            ResultSet rs = Query.getDataQuery(query);

            if (rs.next()) {
                txtFirstname.setText(rs.getString("firstname"));
                txtLastname.setText(rs.getString("lastname"));
                txtEmailAddress.setText(rs.getString("email"));
                txtDefUsername.setText(rs.getString("username"));
                String date = (rs.getString("dob"));
                if (date.equalsIgnoreCase("none")) {
                    dobPicker.setPromptText("Date of Birth");
                } else {
                    dobPicker.setValue(LocalDate.parse(date));
                }
                txtPNum.setText(rs.getString("contact"));
                String gender = rs.getString("gender");
                if (gender.equalsIgnoreCase("female")) {
                    radFemale.setSelected(true);
                    radMale.setSelected(false);
                } else {
                    radFemale.setSelected(false);
                    radMale.setSelected(true);
                }
                System.out.println("We in");
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    @FXML
    private void radMaleSelected(ActionEvent event) {
         if(radMale.isSelected()){
            radFemale.setSelected(false);
            this.gender = "male";
        }
    }

    @FXML
    private void radFemaleSelected(ActionEvent event) {
          if(radFemale.isSelected()){
           radMale.setSelected(false);
           this.gender ="female";
       }
    }

    @FXML
    private void btnSaveAction(ActionEvent event) {
        if(isValid()){
            String firstname = txtFirstname.getText();
            String lastname = txtLastname.getText();
            String emailAddress = txtEmailAddress.getText();
            String dob = dobPicker.getValue().toString();
            String newPass = newPassword.getText().trim();
            String curPass = curPassword.getText().trim();
            String confPass = confirmPassword.getText().trim();
            String pnum = txtPNum.getText();
            String defUsername = txtDefUsername.getText();
            String Password = curPassword.getText();
            QueryManager Query = new QueryManager();
            Verifier  verify = new Verifier();
            if((verify.checkNumbers(pnum)) || (verify.checkLetters(firstname)) 
                    || verify.validateEmail(emailAddress) || verify.checkLetters(lastname) ) { //Validating information entered
                if(curPass.equals(LoginController.password)){
               if(!newPass.isEmpty()){
                  if(confPass.equals(newPass)){
                         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setHeaderText("Confirm Login Details ");
                            alert.setContentText("Are sure you want to change these details");
                            alert.showAndWait();
                            if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                            User user = new User(defUsername,newPass,firstname,lastname,emailAddress,gender,dob,pnum);
                             //Search if the user updated is a receptionist to uupdate the trans_by column
                             String query = "SELECT * FROM users "
                                     + "WHERE username ='" + 
                                     LoginController.username + "' and privilege ='" + "receptionist'" ;
                             ResultSet rs1 = Query.getDataQuery(query);
                            try {
                                if(rs1.next()){
                                     Transaction trans = new Transaction();
                                     if(Query.execActionUpdate(trans.updateTransaction
                                            (defUsername,LoginController.username ))){
                                             if(Query.execActionUpdate(user.updateSelfUserPassword
                                               (LoginController.username, curPass))){
                                                LoginController.username = user.getUsername();
                                                LoginController.password = user.getPassword();
                                                loadLabelData();
                                                curPassword.clear();
                                                newPassword.clear();
                                                confirmPassword.clear();
                                          }
                                  }
                              }else{
                                       if(Query.execActionUpdate(user.updateSelfUserPassword
                                               (LoginController.username, curPass))){
                                                LoginController.username = user.getUsername();
                                                LoginController.password = user.getPassword();
                                                loadLabelData();
                                                curPassword.clear();
                                                newPassword.clear();
                                                confirmPassword.clear();
                                          }
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(LoginDetailsController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                         
                       
                     }else{
                           Alert alert2 = new Alert(Alert.AlertType.ERROR);
                            alert2.setHeaderText("Entry Error");
                            alert2.setContentText("Update Failed ");
                            alert2.showAndWait();
                         }
                  }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Confirmation Error");
                        alert.setContentText("Please confirm your password");
                        alert.showAndWait();
                  }
               }else{
                   
                      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setHeaderText("Confirm Login Details ");
                            alert.setContentText("Are sure you want to change these details");
                            alert.showAndWait();
                            
                            if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                            
                            User user = new User(firstname,lastname,emailAddress,gender,dob,pnum);
                            user.setUsername(defUsername);
                            
                              String query = "SELECT * FROM users "
                                     + "WHERE username ='" + 
                                     LoginController.username + "' and privilege ='" + "receptionist'" ;
                               ResultSet rs1 = Query.getDataQuery(query);
                                try {
                                if(rs1.next()){
                                     Transaction trans = new Transaction();
                                     if(Query.execActionUpdate(trans.updateTransaction
                                            (defUsername,LoginController.username ))){
                                              if(Query.execActionUpdate(user.updateSelfUser(LoginController.username,
                                               curPass))){
                                                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                                                    alert2.setHeaderText("Information ");
                                                    alert2.setContentText("Details Updated Successfully");
                                                    alert2.showAndWait();
                                                    LoginController.username = user.getUsername();
                                                    loadLabelData();
                                                    curPassword.clear();
                                                    newPassword.clear();
                                                    confirmPassword.clear();
                                                    }else{
                                                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                                                       alert2.setHeaderText("Entry Error");
                                                       alert2.setContentText("Update Failed");
                                                       alert2.showAndWait();
                                                }
                                  }
                              }else{
                                       if(Query.execActionUpdate(user.updateSelfUser(LoginController.username,
                                               curPass))){
                                                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                                                    alert2.setHeaderText("Information ");
                                                    alert2.setContentText("Details Updated Successfully");
                                                    alert2.showAndWait();
                                                    LoginController.username = user.getUsername();
                                                    loadLabelData();
                                                    curPassword.clear();
                                                    newPassword.clear();
                                                    confirmPassword.clear();
                                                    }else{
                                                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                                                       alert2.setHeaderText("Entry Error");
                                                       alert2.setContentText("Update Failed");
                                                       alert2.showAndWait();
                                                }
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(LoginDetailsController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                             
                             
                   }
               }
                }else{
                     Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Entry Error");
                    alert.setContentText("Incorrect current password");
                    alert.showAndWait();
                }     
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Entry Error");
                alert.setContentText("Please enter correct values");
                alert.showAndWait();
            }         
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Entry Error");
            alert.setContentText("Please enter all details "
                    + "\n ---------------------------------------------------"
                    + "\nMake sure you confirm password if you are changing it");
            alert.showAndWait();
        }
        
    }
    private boolean isValid(){ //Verifies if the user has entered all details
      if(txtFirstname.getText().trim().isEmpty() || txtLastname.getText().trim().isEmpty() ||
                txtEmailAddress.getText().trim().isEmpty() || dobPicker.getValue()==null
                        || txtPNum.getText().trim().isEmpty()
                        || txtDefUsername.getText().trim().isEmpty()
                        || curPassword.getText().trim().isEmpty()){
         
          return false; // return false if one/more fields are not filled
    }else{
           if(!newPassword.getText().trim().isEmpty()){
              if(!confirmPassword.getText().trim().isEmpty()){
                  return true;
              }else{
                  return false;
              }
          }
          return true; // return true if all fields are filled
      }
    }
    

    @FXML
    private void btnCancelAction(ActionEvent event) {
    }
    
}
