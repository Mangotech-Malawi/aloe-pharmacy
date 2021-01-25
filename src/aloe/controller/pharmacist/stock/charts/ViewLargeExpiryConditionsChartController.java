/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock.charts;


import static aloe.controller.pharmacist.stock.charts.PacksExpiryConditionsController.barChart;
import static aloe.controller.pharmacist.stock.charts.PacksExpiryConditionsController.onView;
import static aloe.controller.pharmacist.stock.charts.PacksExpiryConditionsController.pieChart;
import static aloe.controller.pharmacist.stock.charts.PacksExpiryConditionsController.stackedBarChart;
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
public class ViewLargeExpiryConditionsChartController implements Initializable {

    @FXML
    private BorderPane borderPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadChart();
    }   
    private void loadChart(){
         if(onView.equalsIgnoreCase("Stacked")){
            borderPane.setCenter(stackedBarChart);
        }else if (onView.equalsIgnoreCase("Bar")){
            borderPane.setCenter(barChart);
        }else if (onView.equalsIgnoreCase("Pie")){
            borderPane.setCenter(pieChart);
        }
    }
    
}
