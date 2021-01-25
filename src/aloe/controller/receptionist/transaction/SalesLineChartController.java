/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.receptionist.transaction;

import static aloe.controller.receptionist.transaction.SalesChartController.areaChart;
import static aloe.controller.receptionist.transaction.SalesChartController.lineChart;
import static aloe.controller.receptionist.transaction.SalesChartController.onView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class SalesLineChartController implements Initializable {

    @FXML
    private BorderPane borderPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        if(onView.equalsIgnoreCase("Line")){
            
            borderPane.setCenter(lineChart);
        }else if (onView.equalsIgnoreCase("Area")){
            borderPane.setCenter(areaChart);
        }
    }    
       
    
}
