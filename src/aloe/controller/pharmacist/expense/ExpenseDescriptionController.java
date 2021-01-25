/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.expense;

import static aloe.controller.pharmacist.expense.ViewExpensesController.expenseList;
import static aloe.controller.pharmacist.expense.ViewExpensesController.index;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
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
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class ExpenseDescriptionController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnClose;
    @FXML
    private Label lblCategory;
    @FXML
    private TextArea txtDescription;
    @FXML
    private Label lblExpenseNo;
    @FXML
    private Label lblAmount;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadData();
    }    
    private void loadData(){
        try {
          
            QueryManager Query = new QueryManager();
            String expenseQuery = "SELECT description FROM "
                    + "expenses where expenseNo ='" + expenseList.get(index).getExpenseNo() + "';";
            ResultSet rs1 = Query.getDataQuery(expenseQuery);
            if(rs1.next()){
                lblExpenseNo.setText(expenseList.get(index).getExpenseNo() +"");
                lblAmount.setText("MWK "+expenseList.get(index).getAmount());
                lblCategory.setText(expenseList.get(index).getCategory());
                txtDescription.setText(rs1.getString("description"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ExpenseDescriptionController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        stackPane.getScene().getWindow().hide();
    }
    
}
