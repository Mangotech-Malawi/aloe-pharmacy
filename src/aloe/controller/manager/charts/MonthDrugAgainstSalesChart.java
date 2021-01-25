/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager.charts;

import aloe.controller.pharmacist.stock.charts.DrugAgainstEntriesController;
import static aloe.controller.pharmacist.stock.charts.DrugAgainstEntriesController.convertToPdf;
import aloe.model.Notification;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class MonthDrugAgainstSalesChart implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private BorderPane borderPane;
    public static BarChart<String,Number> barChart;
    public static PieChart pieChart;
    
     public static String onView;//keeps the type of chart that is being view
    JFreeChart printedBarChart;
    JFreeChart printedPieChart;
    @FXML
    private JFXButton btnBarChart;
    @FXML
    private JFXButton btnPieChart;
    @FXML
    private JFXCheckBox viewAllChkBox;
    @FXML
    private ComboBox<String> monthCombo;
    @FXML
    private ComboBox<String> yearCombo;
    @FXML
    private JFXButton btnPrint;
    @FXML
    private JFXButton btnVL;
    
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
        btnBarChart.setUnderline(false);
        yearCombo.setValue(nowDate.getYear() +"");
        monthCombo.setValue(nowDate.getMonth().name().toLowerCase());
        loadBarChartData(nowDate.getMonth().name(),nowDate.getYear() +"","Partial"); //Load the method that sets up Bar Chart Data
        onView = "Bar";    
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
    private void loadBarChartData(String month,String year, String loadType){
          try {
            //Defining the x axis
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Drug Names");
            
            //Defining the y axis
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Sales Total ");
            
            //Creating the bar chart
            barChart = new BarChart<>(xAxis,yAxis);
            barChart.setTitle("Bar Chart | Comparison between various "
                    + " drugs on entries sales ");
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            //Prepare XYChart.Series objects by setting data
            XYChart.Series<String,Number> series = new XYChart.Series<>();
            
             QueryManager Query = new QueryManager();
            String drugQuery = "SELECT id,name FROM drugs WHERE "
                    + "id IN (SELECT id FROM entries) ";
            ResultSet rs1 = Query.getDataQuery(drugQuery);
            while(rs1.next()){
                String id = rs1.getString(1);
                String entryQuery = "SELECT batchNo FROM entries WHERE id ='" + id + "'";
                ResultSet rs2 = Query.getDataQuery(entryQuery);
                double charge = 0.0;
                while(rs2.next()){
                   String batchNo = rs2.getString(1);
                   String packQuery = "SELECT SUM(charge), transNo"
                           + " FROM sales WHERE id ='" + batchNo + "' and item ='Entry' GROUP BY id";
                   ResultSet rs3 = Query.getDataQuery(packQuery);
                   if(rs3.next()){
                        
       
                         if(loadType.equalsIgnoreCase("Partial")){//If the suggest type of data to load is partial
                                int transNo = rs3.getInt(2);
                                String transQuery ="SELECT trans_date FROM transactions WHERE transNo ='" + transNo + "'";
                                ResultSet rs4 = Query.getDataQuery(transQuery);
                                if(rs4.next()){
                                     LocalDateTime transDate =LocalDateTime.parse(rs4.getString(1));
                                     if(transDate.getMonth().name().equalsIgnoreCase(month) 
                                             && (transDate.getYear()+"").equalsIgnoreCase(year) ){ //use the date parameters (month,year) to filter the bar chart data
                                           charge = charge+ rs3.getDouble(1);
                                             if(charge > 0){ //If the charge is greater than 0 then add it on bar chart
                                             series.getData().add(new XYChart.Data<>(rs1.getString(2),charge));
                                             dataset.setValue(charge, "Packs", rs1.getString(2));
                                         }
                                     }
                                }
                            }else{ //Else just add all
                                 charge = charge+ rs3.getDouble(1);
                                  if(charge > 0){
                                             series.getData().add(new XYChart.Data<>(rs1.getString(2),charge));
                                             dataset.setValue(charge, "Packs", rs1.getString(2));
                                    }
                            }  
                      }
                }
            }
            //Create Bar Chart to load on borderPane
           barChart.getData().addAll(series);
           
           //Create Bar Chart to be printed out
           printedBarChart = ChartFactory.createBarChart("Bar Chart | Comparison between various drugs on entries sales",
            "Drugs", "Sales Amount(MWK)", dataset,
            PlotOrientation.VERTICAL, false, true, false);
           
        } catch (SQLException ex) {
            Logger.getLogger(DrugAgainstEntriesController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            //If the is no data in chart do not load it on border pane
            if(barChart.getData().isEmpty()){
                borderPane.setCenter(new Label("No Data Loaded"));
            }else{
                borderPane.setCenter(barChart);
            }
        }
    }
    


    @FXML
    private void btnPrintAction(ActionEvent event) {
            Notification notify = new Notification(1,
                "Chart Printing To PDF","You have initiated process of printing chart to pdf");
        notify.start();
        Task<Void> task = new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                         Thread.sleep(500);
                      
                         Platform.runLater(new Runnable() {
                             @Override
                             public void run() {
                                if(onView.equalsIgnoreCase("Bar")){
                                     String fileName = System.getProperty("user.home") 
                                     + "\\Documents\\Aloe\\Charts\\" +"Drug Names Against Entries Sales Bar Chart"+ ".pdf";
                                          convertToPdf(printedBarChart,1920,1080,fileName);
                                 }else{
                                    String fileName = System.getProperty("user.home") 
                                    + "\\Documents\\Aloe\\Charts\\" +"Drug Names Against Entries Sales Pie Chart"+ ".pdf";
                                    convertToPdf(printedPieChart,1920,1080,fileName);
                                 }
                             }
                         });
                   return null;
               }
           };
           new Thread(task).start();
    }

    @FXML
    private void btnVLAction(ActionEvent event) {
            PopWindow window = new PopWindow();
        window.loadSmallWindow("/aloe/view/manager/charts/drugEntryAgainstSalesMonthLarge.fxml", onView 
                + " chart | Comparison between various "
                + " drugs on entries sales ", true,true,true,false);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            if(onView.equalsIgnoreCase("Bar")){
                
               borderPane.setCenter(barChart);
               btnBarChart.setUnderline(false); 
               btnPieChart.setUnderline(true);
               onView = "Bar";
            }else{
               borderPane.setCenter(pieChart);
               btnBarChart.setUnderline(true);
               btnPieChart.setUnderline(false);
             }
        }}); 
    }


   @FXML
    private void btnBarChartAction(ActionEvent event) {
         loadBarChartData(nowDate.getMonth().name(),nowDate.getYear() +"","Partial"); //Load the method that sets up Bar Chart Data
        btnBarChart.setUnderline(false);
        btnPieChart.setUnderline(true);
        onView = "Bar";
    }

    @FXML
    private void btnPieChartAction(ActionEvent event) {
        loadPieChartData(nowDate.getMonth().name(),nowDate.getYear() +"","Partial");   
        btnBarChart.setUnderline(true);
        btnPieChart.setUnderline(false);
        onView = "Pie";
    }
    
    private void loadPieChartData(String month, String year,String loadType){
         try{  
        //Decraring observable list object
            ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList();
            
            //Declaring dataset object
            DefaultPieDataset dataset = new DefaultPieDataset();
            
            QueryManager Query = new QueryManager();
            String drugQuery = "SELECT id,name FROM drugs WHERE "
                    + "id IN (SELECT id FROM entries) ";
            ResultSet rs1 = Query.getDataQuery(drugQuery);
            while(rs1.next()){
                String id = rs1.getString(1);
                String entryQuery = "SELECT batchNo FROM entries WHERE id ='" + id + "'";
                ResultSet rs2 = Query.getDataQuery(entryQuery);
                double totalCharge = 0.0;
                while(rs2.next()){
                   String batchNo = rs2.getString(1);
                      String saleQuery = "SELECT SUM(charge), transNo"
                           + " FROM sales WHERE id ='" + batchNo + "' and item ='Entry' GROUP BY id";
                   ResultSet rs3 = Query.getDataQuery(saleQuery);
                   
                   if(rs3.next()){
                      totalCharge = totalCharge + rs3.getDouble(1);
                       
                      if(loadType.equalsIgnoreCase("Partial")){
                        int transNo = rs3.getInt(2);
                        String transQuery ="SELECT trans_date FROM transactions WHERE transNo ='" + transNo + "'";
                        ResultSet rs4 = Query.getDataQuery(transQuery);
                            if(rs4.next()){
                                 LocalDateTime transDate =LocalDateTime.parse(rs4.getString(1));
                                 if(transDate.getMonth().name().equalsIgnoreCase(month) 
                                         && (transDate.getYear()+"").equalsIgnoreCase(year) ){
                                      totalCharge = totalCharge+ rs3.getDouble(1);
                                 }
                            }
                        }else{
                            totalCharge = totalCharge+ rs3.getDouble(1);
                        }
                   }
                }
                if(totalCharge > 0){
                pieChartData.add(new PieChart.Data(rs1.getString(2),totalCharge));
                dataset.setValue(rs1.getString(2),totalCharge);
                }
            }
         // Create pie chart to be printed
             printedPieChart = ChartFactory.createPieChart("Pie Chart | Comparison between drugs on entries sales",
             dataset, true, true, false);
            //Creating a pie chart
            pieChart = new PieChart(pieChartData);
            
            printedPieChart.setElementHinting(true);
            
            //Setting the title of the Pie Chart
            pieChart.setTitle("Pie Chart | Comparison between drugs on entries sales");
            
            //Setting the direction to arrange the data
            pieChart.setClockwise(true);
            
            //Setting the length of the label line
            pieChart.setLabelLineLength(20);
            
            //Setting the label of the pie chart visible
            pieChart.setLabelsVisible(true);
            
            //Setting the Start angle of the pie chart
            pieChart.setStartAngle(180);
            
            pieChart.setLegendSide(Side.LEFT);
           
            
        } catch (SQLException ex) {
            Logger.getLogger(DrugAgainstEntriesController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
             if(pieChart.getData().isEmpty()){
                borderPane.setCenter(new Label("No Data Loaded"));
            }else{
                borderPane.setCenter(pieChart);
            }
        }
    }

      @FXML
    private void yearComboAction(ActionEvent event) {
        viewAllChkBox.setSelected(false);
         if(onView.equalsIgnoreCase("Bar")){
              loadBarChartData(monthCombo.getValue(),yearCombo.getValue(),"Partial");
           }else{
              loadPieChartData(monthCombo.getValue(),yearCombo.getValue(),"Partial");                       
         } 
    }

    @FXML
    private void monthComboAction(ActionEvent event) {
          viewAllChkBox.setSelected(false);
         if(onView.equalsIgnoreCase("Bar")){
              loadBarChartData(monthCombo.getValue(),yearCombo.getValue(),"Partial");
           }else{
              loadPieChartData(monthCombo.getValue(),yearCombo.getValue(),"Partial");                       
         }
    }

    @FXML
    private void viewAllChkBoxAction(ActionEvent event) { //Loads all sales if the view all check box is checked
        if(viewAllChkBox.isSelected()){
             if(onView.equalsIgnoreCase("Bar")){
              loadBarChartData(monthCombo.getValue(),yearCombo.getValue(),"All");
             }else{
              loadPieChartData(monthCombo.getValue(),yearCombo.getValue(),"All");                       
             }
            
        }else{
            if(onView.equalsIgnoreCase("Bar")){
              loadBarChartData(monthCombo.getValue(),yearCombo.getValue(),"Partial");
             }else{
              loadPieChartData(monthCombo.getValue(),yearCombo.getValue(),"Partial");                       
             }
        }
    }
    
}
