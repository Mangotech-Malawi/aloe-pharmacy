/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;

import static aloe.controller.LoginController.username;
import aloe.model.EXSettings;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
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
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class ExpirySettingsController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXTextField txtExcellent;
    @FXML
    private JFXTextField txtBetter;
    @FXML
    private JFXTextField txtGood;
    @FXML
    private JFXTextField txtBad;
    @FXML
    private JFXTextField txtWorse;
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
        loadFieldsData();
    }  
    private void loadFieldsData(){
        QueryManager Query = new QueryManager();
        String settingsQuery = "SELECT * FROM expirySettings";
        ResultSet rs1 = Query.getDataQuery(settingsQuery);
        try {
            if(rs1.next()){
                txtExcellent.setText(rs1.getString("excellent"));
                txtBetter.setText(rs1.getString("better"));
                txtGood.setText(rs1.getString("good"));
                txtBad.setText(rs1.getString("bad"));
                txtWorse.setText(rs1.getString("worse"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ExpirySettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private boolean isValid(){
           if(txtExcellent.getText().trim().isEmpty() || txtBetter.getText().trim().isEmpty() ||
                txtGood.getText().trim().isEmpty() 
                        || txtBad.getText().trim().isEmpty()
                        || txtWorse.getText().trim().isEmpty()){
                return false; // return false if one/more fields are not filled
            }else{
                  return true; // return true if all fields are filled
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
               int excellent = Integer.parseInt(txtExcellent.getText().trim());
               int better = Integer.parseInt(txtBetter.getText().trim());
               int good = Integer.parseInt(txtGood.getText().trim());
               int bad = Integer.parseInt(txtBad.getText().trim());
               int worse = Integer.parseInt(txtWorse.getText().trim());
               
               //Make sure that values for conditions are arranged accordingly
               if((excellent > better) && (excellent > good)
                       && (excellent > bad) && (excellent > worse)){
                       if((better > good) && (better > bad) && (better > worse)){
                               if((good > bad) && (good > worse)){
                                    if(bad > worse){
                                         QueryManager Query = new QueryManager();
                                         EXSettings settings = new EXSettings(excellent,better,good,bad,worse,LocalDate.now().toString(),username);
                                         if(Query.execActionUpdate(settings.updateSettings())){
                                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                            alert.setHeaderText("Settings Updated Successfully");
                                            alert.setContentText("You have updated settings for \ndrug entry expiry conditions");
                                            alert.showAndWait();
                                         }else{
                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setHeaderText("Settings Updated Successfully");
                                            alert.setContentText("Bad value has"
                                                      + " to be greater than\nvalue of worse condition");
                                            alert.showAndWait();
                                         }
                                     }else{
                                          Alert alert = new Alert(Alert.AlertType.ERROR);
                                          alert.setHeaderText("Logic Error");
                                          alert.setContentText("Bad value has"
                                                  + " to be greater than\nvalue of worse condition");
                                          alert.showAndWait();
                                     }
                                }else{
                                     Alert alert = new Alert(Alert.AlertType.ERROR);
                                     alert.setHeaderText("Logic Error");
                                     alert.setContentText("Good value has"
                                             + " to be greater than\nall other values for conditions below");
                                     alert.showAndWait();
                                }
                       }else{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Logic Error");
                            alert.setContentText("Better value has"
                                    + " to be greater than\nall other values for conditions below");
                            alert.showAndWait();
                       }
               }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Logic Error");
                    alert.setContentText("Excellent value has"
                            + " to be greater than\nall other values for conditions below");
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

    @FXML
    private void btnResetAction(ActionEvent event) {
        QueryManager Query = new QueryManager();
        EXSettings settings = new EXSettings(365,182,91,30,0,username,LocalDate.now().toString());
        if(Query.execActionUpdate(settings.updateSettings())){
             Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Settings Set To Default Successfully");
            alert.setContentText("You have reset to default settings for \ndrug entry expiry conditions");
            alert.showAndWait();
            loadFieldsData();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Reset Failed");
            alert.setContentText("Settings have not been set to default");
            alert.showAndWait();
        }
    }
    
}
