/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.userLog;


import aloe.model.QueryManager;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class LogChartController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private BarChart<Double, String> logBarChart;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private PieChart pieChart;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadBarChartData();
        loadPieChartData();
    }  
    private void loadPieChartData(){
        pieChart.setAnimated(true);
        pieChart.setLabelsVisible(true);
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
           XYChart.Series series = new XYChart.Series<>();
        QueryManager Query = new QueryManager();
        String query = "SELECT username,COUNT(username) from userLogs GROUP BY username";
        ResultSet rs1 = Query.getDataQuery(query);
        try {
      
            while(rs1.next()){
                String userDelQuery = "SELECT * FROM users_del WHERE username ='" 
                        + rs1.getString("username") + "';";
                ResultSet rs2 = Query.getDataQuery(userDelQuery);
                if(rs2.next()){
                   
                }else{
                     pieChartData.add( new PieChart.Data(rs1.getString("username")
                            ,rs1.getInt(2)));
                }
            }
             series.setName("Logs In System");
            pieChart.getData().setAll(pieChartData);
        } catch (SQLException ex) {
            Logger.getLogger(LogChartController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void loadBarChartData(){
        ObservableList<XYChart.Data> XYChartData = FXCollections.observableArrayList();
        QueryManager Query = new QueryManager();
         XYChart.Series series = new XYChart.Series<>();
        String query = "SELECT username,COUNT(username) from userLogs GROUP BY username";
        ResultSet rs1 = Query.getDataQuery(query);
        try {
        
            while(rs1.next()){
                String userDelQuery = "SELECT * FROM users_del WHERE username ='" 
                        + rs1.getString("username") + "';";
                ResultSet rs2 = Query.getDataQuery(userDelQuery);
               if(rs2.next()){
                   
                }else{
                  
                  series.setName(rs1.getString("username"));
                  series.getData().add(new XYChart.Data(rs1.getString("username"),rs1.getInt(2)));
                 
                }
                
               
            }
           
        logBarChart.getData().setAll(series);
           
        } catch (SQLException ex) {
            Logger.getLogger(LogChartController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
