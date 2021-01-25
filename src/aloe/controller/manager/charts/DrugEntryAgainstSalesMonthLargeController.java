/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager.charts;

import static aloe.controller.manager.charts.MonthDrugAgainstSalesChart.barChart;
import static aloe.controller.manager.charts.MonthDrugAgainstSalesChart.onView;
import static aloe.controller.manager.charts.MonthDrugAgainstSalesChart.pieChart;
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
public class DrugEntryAgainstSalesMonthLargeController implements Initializable {

    @FXML
    private BorderPane borderPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadCharts();
    }    
     private void loadCharts(){
         if(onView.equalsIgnoreCase("Bar")){
            borderPane.setCenter(barChart);
        }else if (onView.equalsIgnoreCase("Pie")){
            borderPane.setCenter(pieChart);
        }
      
    }
    
}
