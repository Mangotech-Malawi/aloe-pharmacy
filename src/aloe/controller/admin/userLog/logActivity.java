/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.userLog;

import static aloe.controller.admin.userLog.LogViewController.*;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class logActivity implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private Label lblUsername;
    @FXML
    private JFXTextArea txtActivities;
    @FXML
    private Label lblPrivilege;
    @FXML
    private Label logNum;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadLabelData();
    }    
     private void loadLabelData(){
        String username = userLogList.get(index).getUsername();
        lblUsername.setText(username);
        lblPrivilege.setText(userLogList.get(index).getPrivilege());
        logNum.setText( "Log # " + userLogList.get(index).getLogNumber() + "");
        QueryManager Query = new QueryManager();
        String logQuery = "SELECT activities FROM userLogs WHERE username = '" + username + "';";
        ResultSet rs = Query.getDataQuery(logQuery);
        try {
            if(rs.next()){
                txtActivities.setText(rs.getString("activities"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(logActivity.class.getName()).log(Level.SEVERE, null, ex);
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
    
}
