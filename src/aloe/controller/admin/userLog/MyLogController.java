/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.userLog;

import static aloe.controller.LoginController.username;
import aloe.model.QueryManager;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class MyLogController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private LineChart<Integer, Integer> monthChart;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadChartData();
    } 
    private void loadChartData(){
          LocalDateTime nowDate = LocalDateTime.now();
           int count = nowDate.getMonth().maxLength();

         ObservableList<Integer> logList = FXCollections.observableArrayList();
        
        for(int index = 1; index <= count; index++){
            logList.add(0);
        }
       
       
          XYChart.Series series = new XYChart.Series<>();
          QueryManager Query = new QueryManager();
          String logQuery = "SELECT logInDate,COUNT(inDate) FROM userlogs  WHERE username ='" 
                  + username + "' GROUP BY inDate;";
          ResultSet rs1 = Query.getDataQuery(logQuery);
                   int i = 1;
        try {
            while(rs1.next()){
                  
                LocalDateTime date = LocalDateTime.parse(rs1.getString("logInDate"));
                if((date.getMonthValue() == nowDate.getMonthValue()) &&
                        date.getYear() == nowDate.getYear()){
                     System.out.println("We are here");
                    logList.set(date.getDayOfMonth() -1, rs1.getInt(2));
                }else{
                     
                }
                    
            }
           for(int a = 1; a <= count;a++){
             System.out.println("Day " + a + " Value " + logList.get(a-1) );
             series.getData().add((new XYChart.Data(a + "", logList.get(a-1))));
           }
          
        } catch (Exception ex) {
            Logger.getLogger(MyLogController.class.getName()).log(Level.SEVERE, null, ex);
        }
        monthChart.getData().setAll(series);
    }
    
}
