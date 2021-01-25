/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager.charts;

import aloe.controller.admin.userLog.MyLogController;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import org.jfree.chart.JFreeChart;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class TodaySalesController implements Initializable {

     @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnAreaChart;
    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXButton btnPrint;
    @FXML
    private JFXButton btnVL;
    
      
    public static LineChart<String,Number> lineChart;
    public static AreaChart<String,Number> areaChart;
    public static String onView;//keeps the type of chart that is being view
    JFreeChart linePrintedChart;
    JFreeChart areaPrintedChart;
    @FXML
    private JFXButton btnLineChart;
    private MenuButton monthMenuBtn;
    @FXML
    private ComboBox<String> monthCombo;
    ObservableList<String> monthList = FXCollections.observableArrayList();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
          loadMonth();
        btnLineChart.setUnderline(false);
        loadLineChartData(LocalDate.now().getMonth().name()); //Load the method that sets up Bar Chart Data
 
        onView = "Line";
      
        
    }
    private void loadMonth(){
        monthList.addAll("January","February","March","April","May",
                "June","July","August","September","October","November","December");
        monthCombo.getItems().setAll(monthList);
    }   
    
    @FXML
    private void btnLineChartAction(ActionEvent event) {
        loadLineChartData(LocalDate.now().getMonth().name()); //Load the method that sets up Bar Chart Data
        btnLineChart.setUnderline(false);
        btnAreaChart.setUnderline(true);
        onView = "Line";
    }
     private void loadLineChartData(String month){
       try {  
        //Defining the x axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Days Of Month");
            
        //Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Sales Money");
        LocalDate nowDate = LocalDate.now();
        int count = nowDate.getMonth().maxLength();
        ObservableList<Double> salesList = FXCollections.observableArrayList();
        lineChart = new LineChart<>(xAxis,yAxis);
        for(int index = 1; index <= count; index++){
            salesList.add(0.0);
        }
       
       
        XYChart.Series series = new XYChart.Series<>();
        QueryManager Query = new QueryManager();
        String transQuery = "SELECT transNo,trans_date FROM"
                + " transactions;";
        ResultSet rs1 = Query.getDataQuery(transQuery);
        int i = 1;
       
            while(rs1.next()){
                int transNo = rs1.getInt(1);
                String salesQuery = "SELECT SUM(charge) FROM sales WHERE transNo ='" + transNo + "'";
                ResultSet rs2 = Query.getDataQuery(salesQuery);
                if(rs2.next()){
                     LocalDateTime date = LocalDateTime.parse(rs1.getString(2));
                    if((month.equalsIgnoreCase(date.getMonth().name()) ) 
                            && (nowDate.getYear() == date.getYear())){
                        if((date.getMonthValue() == nowDate.getMonthValue()) &&
                            date.getYear() == nowDate.getYear()){
                            salesList.set(date.getDayOfMonth() -1, rs2.getDouble(1));
                        }else{

                        }
                    }
                }             
            }
           for(int a = 1; a <= count;a++){
             System.out.println("Day " + a + " Value " + salesList.get(a-1) );
             series.getData().add((new XYChart.Data(a + "", salesList.get(a-1))));
           }
           series.setName("My Monthly Sales Progress");
            lineChart.getData().setAll(series);
          
        } catch (Exception ex) {
            Logger.getLogger(MyLogController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            //If the is no data in chart do not load it on border pane
            if(lineChart.getData().isEmpty()){
                borderPane.setCenter(new Label("No Data Loaded"));
            }else{
                borderPane.setCenter(lineChart);
            }
        }
       
    }
     
    private void loadAreaChartData(String month){
       try {  
        //Defining the x axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Days Of Month");
            
        //Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Sales Money");
        LocalDate nowDate = LocalDate.now();
        int count = nowDate.getMonth().maxLength();
        ObservableList<Double> salesList = FXCollections.observableArrayList();
        areaChart = new AreaChart<>(xAxis,yAxis);
        for(int index = 1; index <= count; index++){
            salesList.add(0.0);
        }
       
       
        XYChart.Series series = new XYChart.Series<>();
        QueryManager Query = new QueryManager();
        String transQuery = "SELECT transNo,trans_date FROM transactions;";
        ResultSet rs1 = Query.getDataQuery(transQuery);
        int i = 1;
       
            while(rs1.next()){
                int transNo = rs1.getInt(1);
                String salesQuery = "SELECT SUM(charge) FROM sales WHERE transNo ='" + transNo + "'";
                ResultSet rs2 = Query.getDataQuery(salesQuery);
                if(rs2.next()){
                     LocalDateTime date = LocalDateTime.parse(rs1.getString(2));
                    if((month.equalsIgnoreCase(date.getMonth().name()) ) 
                            && (nowDate.getYear() == date.getYear())){
                        if((date.getMonthValue() == nowDate.getMonthValue()) &&
                            date.getYear() == nowDate.getYear()){
                            salesList.set(date.getDayOfMonth() -1, rs2.getDouble(1));
                        }else{

                        }
                    }
                }
               
                    
            }
           for(int a = 1; a <= count;a++){
             System.out.println("Day " + a + " Value " + salesList.get(a-1) );
             series.getData().add((new XYChart.Data(a + "", salesList.get(a-1))));
           }
           series.setName("My Monthly Sales Progress");
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
    private void btnAreaChartAction(ActionEvent event) {
        loadAreaChartData(LocalDate.now().getMonth().name()); //Load the method that sets up Bar Chart Data
        btnLineChart.setUnderline(true);
        btnAreaChart.setUnderline(false);
        onView = "Line";
    }

    @FXML
    private void btnPrintAction(ActionEvent event) {
    }

    @FXML
    private void btnVLAction(ActionEvent event) {
        PopWindow window = new PopWindow();
        window.loadSmallWindow("/aloe/view/manager/charts/"
                + "todaySales.fxml", onView 
                + " chart | Comparison between various "
                + " drugs on entries quantity ", true,true,true,false);
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

    @FXML
    private void monthComboAction(ActionEvent event) {
        loadLineChartData(monthCombo.getValue());
    }
    
}
