/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.expense.charts;

import static aloe.controller.pharmacist.expense.charts.MonthlyExpenseChartController.onView;
import static aloe.controller.pharmacist.expense.charts.MonthlyExpenseChartController.barChart;
import static aloe.controller.pharmacist.expense.charts.MonthlyExpenseChartController.pieChart;
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
public class MonthlyExpenseChartLargeController implements Initializable {

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
