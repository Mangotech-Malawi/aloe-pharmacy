/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.expense.charts;

import static aloe.controller.manager.charts.DrugPacksAgainstSalesController.onView;
import static aloe.controller.manager.charts.DrugPacksAgainstSalesController.pieChart;
import aloe.controller.pharmacist.stock.charts.DrugAgainstEntriesController;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class MonthlyExpenseChartController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnBarChart;
    @FXML
    private JFXButton btnPieChart;
    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXCheckBox viewAllChkBox;
    @FXML
    private ComboBox<String> yearCombo;
    @FXML
    private ComboBox<String> monthCombo;
    @FXML
    private JFXButton btnPrint;
    @FXML
    private JFXButton btnVL;
     public static BarChart<String,Number> barChart;
    public static PieChart pieChart;
    
     public static String onView;//keeps the type of chart that is being view
    JFreeChart printedBarChart;
    JFreeChart printedPieChart;
    
     ObservableList<String> monthList = FXCollections.observableArrayList();
     ObservableList<String> yearList = FXCollections.observableArrayList();
     LocalDate nowDate = LocalDate.now();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         // TODO
        loadMonths();// Loads months to combo box
        loadYears();//Loads years to combo box
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
            xAxis.setLabel("Expense Name");
            
            //Defining the y axis
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Sales Total ");
            
            //Creating the bar chart
            barChart = new BarChart<>(xAxis,yAxis);
            barChart.setTitle("Comparison between "
                    + " expenses name against amount for " + month + " | " + year);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            //Prepare XYChart.Series objects by setting data
            XYChart.Series<String,Number> series1 = new XYChart.Series<>();
            XYChart.Series<String,Number> series2 = new XYChart.Series<>();
            XYChart.Series<String,Number> series3 = new XYChart.Series<>();
            XYChart.Series<String,Number> series4 = new XYChart.Series<>();
            XYChart.Series<String,Number> series5 = new XYChart.Series<>();
            XYChart.Series<String,Number> series6 = new XYChart.Series<>();
            
            QueryManager Query = new QueryManager();
            String categoryQuery = "SELECT category,amount,regDate FROM expenses WHERE expenseNo"
                    + " NOT IN(SELECT expenseNo FROM expenses_del)";
            ResultSet rs1 = Query.getDataQuery(categoryQuery);
            double airtime = 0.0;
            double water = 0.0;
            double electricity = 0.0;
            double food = 0.0;
            double transport =0.0;
            double others =0.0;
            while(rs1.next()){
                switch(rs1.getString("category")){
                case "Airtime" :if(loadType.equalsIgnoreCase("Partial")){  
                       LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                       if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                         airtime = airtime + rs1.getDouble("amount");
                       }
                       }else{
                          airtime = airtime + rs1.getDouble("amount");
                       }break;
                
                case "Water" :if(loadType.equalsIgnoreCase("Partial")){  
                       LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                       if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                         water = water + rs1.getDouble("amount");
                       }
                       }else{
                          water = water + rs1.getDouble("amount");
                       }break;
                case "Electricity" :if(loadType.equalsIgnoreCase("Partial")){  
                       LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                       if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                         electricity = electricity + rs1.getDouble("amount");
                       }
                       }else{
                          electricity = electricity + rs1.getDouble("amount");
                       }break;
                case "Food" :if(loadType.equalsIgnoreCase("Partial")){  
                       LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                       if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                         food = food + rs1.getDouble("amount");
                       }
                       }else{
                         food = food + rs1.getDouble("amount");
                       }break;
                
                case "Transport" :if(loadType.equalsIgnoreCase("Partial")){  
                      LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                      if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                        transport = transport + rs1.getDouble("amount");
                      }
                      }else{
                         transport = transport + rs1.getDouble("amount");
                      }break;
                
                case "Others" :if(loadType.equalsIgnoreCase("Partial")){  
                    LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                    if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                      others = others + rs1.getDouble("amount");
                    }
                    }else{
                      others = others + rs1.getDouble("amount");
                    }break;
                  
                  
                }
               
            }
             series1.getData().add(new XYChart.Data<>("Airtime",airtime));
             dataset.setValue(airtime, "Packs", "Airtime");
             
            series2.getData().add(new XYChart.Data<>("Water",water));
             dataset.setValue(water, "Packs","Water" );
             
              series3.getData().add(new XYChart.Data<>("Electricity",electricity));
             dataset.setValue(electricity, "Packs", "Electricity");
             
            series4.getData().add(new XYChart.Data<>("Food",food));
             dataset.setValue(food, "Packs", "Food");
             
             series5.getData().add(new XYChart.Data<>("Transport",transport));
             dataset.setValue(transport, "Packs", "Transport");
             
            series6.getData().add(new XYChart.Data<>("Others",others));
             dataset.setValue(others, "Packs", "Others");
            //Create Bar Chart to load on borderPane
           barChart.getData().addAll(series1,series2,series3,series4,series5,series6);
           
           //Create Bar Chart to be printed out
           printedBarChart = ChartFactory.createBarChart("Bar Chart | Comparison between various drugs on packs sales",
            "Drugs", "Sales Amount(MWK)", dataset,
            PlotOrientation.VERTICAL, false, true, false);
           
        } catch (SQLException ex) {
            Logger.getLogger(MonthlyExpenseChartController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            //If the is no data in chart do not load it on border pane
            if(barChart.getData().isEmpty()){
                borderPane.setCenter(new Label("No Data Loaded"));
            }else{
                borderPane.setCenter(barChart);
            }
        }
    }
     
    private void loadPieChartData(String month,String year, String loadType){
          try{  
        //Decraring observable list object
            ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList();
            
            //Declaring dataset object
             DefaultPieDataset dataset = new DefaultPieDataset();
             
             QueryManager Query = new QueryManager();
             String categoryQuery = "SELECT category,amount,regDate FROM expenses WHERE expenseNo"
                    + " NOT IN(SELECT expenseNo FROM expenses_del)";
            ResultSet rs1 = Query.getDataQuery(categoryQuery);
            double airtime = 0.0;
            double water = 0.0;
            double electricity = 0.0;
            double food = 0.0;
            double transport =0.0;
            double others =0.0;
            while(rs1.next()){
                switch(rs1.getString("category")){
                case "Airtime" :if(loadType.equalsIgnoreCase("Partial")){  
                       LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                       if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                         airtime = airtime + rs1.getDouble("amount");
                       }
                       }else{
                          airtime = airtime + rs1.getDouble("amount");
                       }break;
                
                case "Water" :if(loadType.equalsIgnoreCase("Partial")){  
                       LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                       if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                         water = water + rs1.getDouble("amount");
                       }
                       }else{
                          water = water + rs1.getDouble("amount");
                       }break;
                case "Electricity" :if(loadType.equalsIgnoreCase("Partial")){  
                       LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                       if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                         electricity = electricity + rs1.getDouble("amount");
                       }
                       }else{
                          electricity = electricity + rs1.getDouble("amount");
                       }break;
                case "Food" :if(loadType.equalsIgnoreCase("Partial")){  
                       LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                       if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                         food = food + rs1.getDouble("amount");
                       }
                       }else{
                         food = food + rs1.getDouble("amount");
                       }break;
                
                case "Transport" :if(loadType.equalsIgnoreCase("Partial")){  
                      LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                      if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                        transport = transport + rs1.getDouble("amount");
                      }
                      }else{
                         transport = transport + rs1.getDouble("amount");
                      }break;
                
                case "Others" :if(loadType.equalsIgnoreCase("Partial")){  
                    LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                    if(date.getMonth().name().equalsIgnoreCase(month) && (date.getYear() + "").equalsIgnoreCase(year)){    
                      others = others + rs1.getDouble("amount");
                    }
                    }else{
                      others = others + rs1.getDouble("amount");
                    }break;
                  
                  
                }
               
            }
            if(airtime > 0){
              pieChartData.add(new PieChart.Data("Airtime",airtime));
              dataset.setValue("Airtime",airtime);
            }
             if(water > 0){
              pieChartData.add(new PieChart.Data("Water",water));
              dataset.setValue("Water",water);
            } 
             if(electricity > 0){
              pieChartData.add(new PieChart.Data("Electricity",electricity));
              dataset.setValue("Electricity",electricity);
            }
             if(food > 0){
              pieChartData.add(new PieChart.Data("Food",food));
              dataset.setValue("Food",food);
            }
             if(transport > 0){
              pieChartData.add(new PieChart.Data("Transport",transport));
              dataset.setValue("Transport",transport);
            }
           if(others > 0){
              pieChartData.add(new PieChart.Data("Others",others));
              dataset.setValue("Others",others);
            }
          
            
            
        // Create pie chart to be printed
             printedPieChart = ChartFactory.createPieChart("Pie Chart | Comparison between drugs on packs sales",
             dataset, true, true, false);
            //Creating a pie chart
            pieChart = new PieChart(pieChartData);
            
            printedPieChart.setElementHinting(true);
            
            //Setting the title of the Pie Chart
            pieChart.setTitle("Pie Chart | Comparison between drugs on packs sales");
            
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
            Logger.getLogger(MonthlyExpenseChartController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
             if(pieChart.getData().isEmpty()){
                borderPane.setCenter(new Label("No Data Loaded"));
            }else{
                borderPane.setCenter(pieChart);
            }
        }
    }
    

    @FXML
    private void btnBarChartAction(ActionEvent event) {
          loadBarChartData(monthCombo.getValue(),yearCombo.getValue(),"Partial"); //Load the method that sets up Bar Chart Data
        btnBarChart.setUnderline(false);
        btnPieChart.setUnderline(true);
        onView = "Bar";
    }

    @FXML
    private void btnPieChartAction(ActionEvent event) {
        loadPieChartData(monthCombo.getValue(),yearCombo.getValue(),"Partial");   
        btnBarChart.setUnderline(true);
        btnPieChart.setUnderline(false);
        onView = "Pie";
    }

    @FXML
    private void viewAllChkBoxAction(ActionEvent event) {
         if(viewAllChkBox.isSelected()){
             if(onView.equalsIgnoreCase("Bar")){
              loadBarChartData(monthCombo.getValue(),yearCombo.getValue(),"All");
             }else{
              loadPieChartData(monthCombo.getValue(),yearCombo.getValue(),"All");                       
             }
            
        }else{
            if(onView.equalsIgnoreCase("Bar")){
              loadBarChartData(monthCombo.getValue(),yearCombo.getValue(),"Patial");
             }else{
              loadPieChartData(monthCombo.getValue(),yearCombo.getValue(),"Partial");                       
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
    private void btnPrintAction(ActionEvent event) {
    }

    @FXML
    private void btnVLAction(ActionEvent event) {
         PopWindow window = new PopWindow();
        window.loadSmallWindow("/aloe/view/pharmacist/expense/charts/monthlyExpenseChartLarge.fxml", onView 
                + " chart | Comparison between various "
                + " drugs on packs sales ", true,true,true,false);
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
    
}
