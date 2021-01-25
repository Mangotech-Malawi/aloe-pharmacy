/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.ManageUsers;

import aloe.model.QueryManager;
import aloe.model.User;
import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import jfxtras.scene.control.ListView;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class SetPrivilegeController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private ListView<String> managerList;
    @FXML
    private ListView<String> pharmacistList;
    @FXML
    private ListView<String> receptionistList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        loadData();
    }    
    
    private void loadData(){
        QueryManager Query = new QueryManager();
        String query = "SELECT * from users where privilege !='admin'";
        ResultSet rs1 = Query.getDataQuery(query);
        try {
            managerList.getItems().clear();
            receptionistList.getItems().clear();
            pharmacistList.getItems().clear();
            while(rs1.next()){
                switch(rs1.getString("privilege")){
                    case "manager" : managerList.getItems().add(rs1.getString("username"));break;
                    case "receptionist" : receptionistList.getItems().add(rs1.getString("username"));break;
                    case "pharmacist" : pharmacistList.getItems().add(rs1.getString("username"));break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SetPrivilegeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void onSetToPharmacist(ActionEvent event) {
        try{
         String manager = managerList.getSelectionModel().getSelectedItem();
         String receptionist = receptionistList.getSelectionModel().getSelectedItem();
            if(manager!= null){
                switchPrivilege(manager,"pharmacist");
                loadData();
            }
            else if(receptionist!=null){
                switchPrivilege(receptionist,"pharmacist");
                loadData();
            }
            else
                {
                     Alert alert = new Alert(Alert.AlertType.WARNING);
                     alert.setHeaderText("Selection Warning");
                     alert.setContentText("Please select on List \n If there are users");
                     alert.showAndWait();
               } 
        }catch(NullPointerException ex){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Selection Warning");
            alert.setContentText("Please select row on List");
            alert.showAndWait();        
        }
    }

    @FXML
    private void onSetToReceptionist(ActionEvent event) {
         try{
         String manager = managerList.getSelectionModel().getSelectedItem();
         String pharmacist = pharmacistList.getSelectionModel().getSelectedItem();
            if(manager!= null){
                switchPrivilege(manager,"receptionist");
                loadData();
            }
            else if(pharmacist!=null){
                switchPrivilege(pharmacist,"receptionist");
                loadData();
            }
            else
                {
                     Alert alert = new Alert(Alert.AlertType.WARNING);
                     alert.setHeaderText("Selection Warning");
                     alert.setContentText("Please select on List \n If there are users");
                     alert.showAndWait();
               } 
        }catch(NullPointerException ex){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Selection Warning");
            alert.setContentText("Please select row on List");
            alert.showAndWait();        
        }
    }

    @FXML
    private void onSetToManager(ActionEvent event) {
        try{
         String receptionist = receptionistList.getSelectionModel().getSelectedItem();
         String pharmacist = pharmacistList.getSelectionModel().getSelectedItem();
            if(receptionist!= null){
                switchPrivilege(receptionist,"manager");
                loadData();
            }
            else if(pharmacist!=null){
                switchPrivilege(pharmacist,"manager");
                loadData();
            }
            else
                {
                     Alert alert = new Alert(Alert.AlertType.WARNING);
                     alert.setHeaderText("Selection Warning");
                     alert.setContentText("Please select on List \n If there are users");
                     alert.showAndWait();
               } 
        }catch(NullPointerException ex){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Selection Warning");
            alert.setContentText("Please select row on List");
            alert.showAndWait();        
        }
    }
    
    private void switchPrivilege(String username,String privilege){
        User user  = new User();
                user.setUsername(username);
                QueryManager Query = new QueryManager();
                if(Query.execAction(user.updatePrivilege(privilege))){
                     Alert alert = new Alert(Alert.AlertType.INFORMATION);
                     alert.setHeaderText("Information");
                     alert.setContentText("You have successfully \n"
                             + " set " + username + " to " + privilege);
                     alert.showAndWait();
                }else{
                     Alert alert = new Alert(Alert.AlertType.ERROR);
                     alert.setHeaderText("Failed to set Privilege");
                     alert.setContentText("Sorry try again or report bug");
                     alert.showAndWait();
                }
    }
    
}
