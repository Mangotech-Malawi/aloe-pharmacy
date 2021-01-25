/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.expense.charts;

import aloe.controller.admin.userLog.MyLogController;
import aloe.model.PopWindow;

import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import org.jfree.chart.JFreeChart;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class DailyExpenseChartController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnLineChart;
    @FXML
    private JFXButton btnAreaChart;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ComboBox<String> yearCombo;
    @FXML
    private ComboBox<String> monthCombo;
    @FXML
    private JFXButton btnPrint;
    @FXML
    private JFXButton btnVL;
    
      
      
    public static LineChart<String,Number> lineChart;
    public static AreaChart<String,Number> areaChart;
    public static String onView;//keeps the type of chart that is being view
    JFreeChart linePrintedChart;
    JFreeChart areaPrintedChart;
     ObservableList<String> monthList = FXCollections.observableArrayList();
     ObservableList<String> yearList = FXCollections.observableArrayList();
     LocalDate nowDate = LocalDate.now();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadMonths();
        loadYears();
        btnLineChart.setUnderline(false);
        yearCombo.setValue(nowDate.getYear() +"");
        monthCombo.setValue(nowDate.getMonth().name().toLowerCase());
        loadLineChartData(monthCombo.getValue(),yearCombo.getValue()+""); //Load the method that sets up Bar Chart Data
        onView = "Line";
        
    }    
     private void loadMonths(){
        monthList.addAll("January","February","March","April","May",
                "June","July","August","September","October","November","December");
        monthCombo.getItems().setAll(monthList);
    }   
     private void loadYears(){
        yearList.addAll("2016","2017","2018","2019","2020",
                "2021","2022","2023","2024","2025","2026","2027","2028");
        yearCombo.getItems().setAll(yearList);
    }
     
    private void loadLineChartData(String month, String year){
           try {  
        //Defining the x axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Days Of Month");
        
    
        //Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount (MWK)");
  
        int count = nowDate.getMonth().maxLength();
        ObservableList<Double> salesList = FXCollections.observableArrayList();
        lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setTitle("Expenses Progress of " + month + " | " + year);
        for(int index = 1; index <= count; index++){
            salesList.add(0.0);
        }
       
       
        XYChart.Series series = new XYChart.Series<>();
        QueryManager Query = new QueryManager();
        String expenseQuery = "SELECT SUM(amount), regDate FROM expenses "
                + "WHERE expenseNo NOT IN (SELECT expenseNo FROM expenses_del) GROUP BY regDate" ;
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        while(rs1.next()){
          LocalDate date = LocalDate.parse(rs1.getString("regDate"));
          if(date.getMonth().name().equalsIgnoreCase(month) && 
                  (date.getYear() + "").equalsIgnoreCase(year)){
             salesList.set(date.getDayOfMonth() -1, rs1.getDouble(1));
          }
        }
        
        for(int a = 1; a <= count;a++){
          System.out.println("Day " + a + " Value " + salesList.get(a-1) );
          series.getData().add((new XYChart.Data(a + "", salesList.get(a-1))));
        }
        series.setName("My Monthly Sales Progress");
         lineChart.getData().setAll(series);
          
        } catch (Exception ex) {
            Logger.getLogger(DailyExpenseChartController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            //If the is no data in chart do not load it on border pane
            if(lineChart.getData().isEmpty()){
                borderPane.setCenter(new Label("No Data Loaded"));
            }else{
                borderPane.setCenter(lineChart);
            }
        }
    }
    
    private void loadAreaChartData(String month, String year){
         try {  
        //Defining the x axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Days Of Month");
            
       
        //Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Expense Money");
        
        int count = nowDate.getMonth().maxLength();
        ObservableList<Double> salesList = FXCollections.observableArrayList();
        areaChart = new AreaChart<>(xAxis,yAxis);
         areaChart.setTitle("Expenses Progress of " + month + " | " + year);
        for(int index = 1; index <= count; index++){
            salesList.add(0.0);
        }
 
        XYChart.Series series = new XYChart.Series<>();
        QueryManager Query = new QueryManager();
         String expenseQuery = "SELECT SUM(amount), regDate FROM expenses "
                + "WHERE expenseNo NOT IN (SELECT expenseNo FROM expenses_del) GROUP BY regDate" ;
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        
        while(rs1.next()){
          LocalDate date = LocalDate.parse(rs1.getString("regDate"));
          if(date.getMonth().name().equalsIgnoreCase(month) && 
                  (date.getYear() + "").equalsIgnoreCase(year)){
             salesList.set(date.getDayOfMonth() -1, rs1.getDouble(1));
          }
        }
        
        int i = 1;
         for(int a = 1; a <= count;a++){
             System.out.println("Day " + a + " Value " + salesList.get(a-1) );
             series.getData().add((new XYChart.Data(a + "", salesList.get(a-1))));
           }
           series.setName("Monthly Expense Progress");
            areaChart.getData().setAll(series);
          
        } catch (Exception ex) {
            Logger.getLogger(MyLogController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            //If the is no data in chart do not load it on border pane
            if(areaChart.getData().isEmpty()){
                borderPane.setCenter(new Label("No Data Loaded"));
            }else{
                borderPane.setCenter(areaChart);
            }
        }
    }
    @FXML
    private void btnLineChartAction(ActionEvent event) {
        loadLineChartData(monthCombo.getValue(),yearCombo.getValue());//Load the method that sets up Bar Chart Data
        btnLineChart.setUnderline(false);
        btnAreaChart.setUnderline(true);
        onView = "Line";
    }

    @FXML
    private void btnAreaChartAction(ActionEvent event) {
         loadAreaChartData(monthCombo.getValue(),yearCombo.getValue()); //Load the method that sets up Bar Chart Data
        btnLineChart.setUnderline(true);
        btnAreaChart.setUnderline(false);
        onView = "Area";
    }


    @FXML
    private void yearComboAction(ActionEvent event) {
         if(onView.equalsIgnoreCase("Line")){
              loadLineChartData(monthCombo.getValue(),yearCombo.getValue());
           }else{
              loadAreaChartData(monthCombo.getValue(),yearCombo.getValue());                       
         } 
    }

    @FXML
    private void monthComboAction(ActionEvent event) {
          if(onView.equalsIgnoreCase("Line")){
              loadLineChartData(monthCombo.getValue(),yearCombo.getValue());
           }else{
              loadAreaChartData(monthCombo.getValue(),yearCombo.getValue());                       
         } 
    }

    @FXML
    private void btnPrintAction(ActionEvent event) {
    }

    @FXML
    private void btnVLAction(ActionEvent event) {
         PopWindow window = new PopWindow();
        window.loadSmallWindow("/aloe/view/pharmacist/expense/charts"
                + "/dailyExpenseChartLarge.fxml", onView 
                + " Char on expense progress", true,true,true,false);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            if(onView.equalsIgnoreCase("Line")){
               borderPane.setCenter(lineChart);
               btnLineChart.setUnderline(false);
               btnAreaChart.setUnderline(true);
               onView = "Line";
            }else{
               borderPane.setCenter(areaChart);
               btnLineChart.setUnderline(true);
               btnAreaChart.setUnderline(false);
             }
        }}); 
    }
    
}
