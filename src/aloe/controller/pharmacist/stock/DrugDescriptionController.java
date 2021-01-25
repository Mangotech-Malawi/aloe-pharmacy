/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;

import static aloe.controller.pharmacist.stock.ViewDrugsController.drugList;
import static aloe.controller.pharmacist.stock.ViewDrugsController.index;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class DrugDescriptionController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnClose;
    @FXML
    private Label lblId;
    @FXML
    private Label lblName;
    @FXML
    private Label lblCategory;
    @FXML
    private TextArea txtDescription;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadLabelData();
    }    
    private void loadLabelData(){
         System.out.println("the index is" + index);
         lblId.setText(drugList.get(index).getId()  + "");
         lblName.setText(drugList.get(index).getName());
         lblCategory.setText(drugList.get(index).getCategory());
         txtDescription.setText(drugList.get(index).getDescription());
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
         stackPane.getScene().getWindow().hide();
    }
    
}
