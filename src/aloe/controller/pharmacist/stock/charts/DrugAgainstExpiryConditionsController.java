/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock.charts;

import aloe.model.Notification;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
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
public class DrugAgainstExpiryConditionsController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnPieChart;
    @FXML
    private JFXButton btnBarChart;
    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXButton btnPrint;
    @FXML
    private JFXButton btnVL;
    @FXML
    private JFXButton btnStackedBarChart;
    public static String onView;
    public static BarChart<String,Number> barChart;
    public static StackedBarChart<String,Number> stackedBarChart;
    public static PieChart pieChart;
   
    JFreeChart printedStackedBarChart;
    JFreeChart printedPieChart;
    JFreeChart printedBarChart;
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        btnStackedBarChart.setUnderline(false);
        loadStackedBarChartData(); //Load the method that sets up Stacked Chart Data
        onView = "Stacked";
    }    

       @FXML
    private void btnStackedBarChartAction(ActionEvent event) {
        loadStackedBarChartData(); //Load the method that sets up Stacked Chart Data
        btnStackedBarChart.setUnderline(false);
        btnBarChart.setUnderline(true);
        btnPieChart.setUnderline(true);
        onView = "Stacked";
    }
    
    private void loadStackedBarChartData(){
         try {
            //Defining the x axis
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Drug Names");
            
            //Defining the y axis
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Quantiy of drugs in Entries");
            
            //Creating the bar chart
            stackedBarChart = new StackedBarChart<>(xAxis,yAxis);
            stackedBarChart.setTitle("Stacked Chart | Comparison between various "
                    + " drugs on entries expiry date conditions");
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            //Prepare XYChart.Series objects by setting data
             XYChart.Series<String,Number> series1 = new XYChart.Series<>();
             XYChart.Series<String,Number> series2 = new XYChart.Series<>();
             XYChart.Series<String,Number> series3 = new XYChart.Series<>();
             XYChart.Series<String,Number> series4 = new XYChart.Series<>();
             XYChart.Series<String,Number> series5 = new XYChart.Series<>();
             
             series1.setName("Excellent");
             series2.setName("Very Good");
             series3.setName("Good");
             series4.setName("Bad");
             series5.setName("worse");
            
            //Connecting to database
            QueryManager Query = new QueryManager();
           
            String drugQuery = "SELECT id,name FROM drugs WHERE id IN "
                    + "(SELECT id FROM entries ) AND id NOT IN (SELECT id FROM drugs_del)";
            ResultSet rs1 = Query.getDataQuery(drugQuery);
            while(rs1.next()){
                String id = rs1.getString("id");
                String entryQuery = "SELECT expiryDate,quantity FROM entries "
                        + "WHERE batchNo NOT IN (SELECT batchNo FROM entries_del) AND id ='"+ id + "'";
                ResultSet rs2 = Query.getDataQuery(entryQuery);
                 int excellent = 0;
                int veryGood = 0;
                int good = 0;
                int bad = 0;
                int worse = 0;
                while(rs2.next()){
                   LocalDate expiryDate = LocalDate.parse(rs2.getString(1));
                   LocalDate  nowDate = LocalDate.now();
                   int daysToExpiry =  Integer.parseInt(expiryDate.toEpochDay() + "") - 
                           Integer.parseInt(nowDate.toEpochDay() + "") ;
                   String exSettingsQuery = "SELECT * FROM expirySettings";
                   ResultSet rs3 = Query.getDataQuery(exSettingsQuery);
                   if(rs3.next()){
                        if(daysToExpiry >= rs3.getInt("excellent")){
                            
                            excellent = excellent + rs2.getInt(2);
                            series1.getData().add(new XYChart.Data<>(rs1.getString(2),excellent));
                            dataset.addValue(excellent, "EXCELLENT", rs1.getString(2));
                        }else if((daysToExpiry >= rs3.getInt("better")) && 
                                (daysToExpiry < rs3.getInt("excellent") )){
                            
                            veryGood = veryGood + rs2.getInt(2);
                            dataset.addValue(veryGood, "VERY GOOD", rs1.getString(2));
                            series2.getData().add(new XYChart.Data<>(rs1.getString(2),veryGood));
                            
                        }else if((daysToExpiry >= rs3.getInt("good")) && 
                                (daysToExpiry < rs3.getInt("better") )){
                            
                            good = good + rs2.getInt(2);
                            dataset.addValue(good, "GOOD", rs1.getString(2));
                            series3.getData().add(new XYChart.Data<>(rs1.getString(2),good));
                           
                        }else if((daysToExpiry >= rs3.getInt("bad")) && 
                                (daysToExpiry < rs3.getInt("good") )){
                            bad = bad + rs2.getInt(2);
                            dataset.addValue(bad, "BAD", rs1.getString(2));
                            series4.getData().add(new XYChart.Data<>(rs1.getString(2),bad));
                        }else if(daysToExpiry < rs3.getInt("bad")){
                           
                            worse = worse + rs2.getInt(2);
                             dataset.addValue(worse, "WORSE", rs1.getString(2));
                            series5.getData().add(new XYChart.Data<>(rs1.getString(2),worse));
                           
                        }
                   }
                }
                
            }
            
           stackedBarChart.getData().setAll(series1,series2,series3,series4,series5);
           //Create Bar Chart to be printed out
           printedStackedBarChart = ChartFactory.createStackedBarChart("Stacked Chart Entries Conditions",
            "Drugs Names", "Number Of Entries Quantity", dataset,
            PlotOrientation.VERTICAL, false, true, false);
           
         
        } catch (SQLException ex) {
            Logger.getLogger(DrugAgainstEntriesController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            //If the is no data in chart do not load it on border pane
            if(stackedBarChart.getData().isEmpty()){
                borderPane.setCenter(new Label("No Data Loaded"));
            }else{
                borderPane.setCenter(stackedBarChart);
              
            }
        }
       
    }
    
    @FXML
    private void btnPieChartAction(ActionEvent event) {
        loadPieChartData(); //Load the method that sets up Bar Chart Data
        btnStackedBarChart.setUnderline(true);
        btnBarChart.setUnderline(true);
        btnPieChart.setUnderline(false);
        onView = "Pie";
    }
    private void loadPieChartData(){
          try {
          //Decraring observable list object
            ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList();
            
            //Declaring
             DefaultPieDataset dataset = new DefaultPieDataset();
            
            //Connecting to database
            QueryManager Query = new QueryManager();
       
                String entryQuery = "SELECT expiryDate,quantity FROM entries "
                        + "WHERE batchNo NOT IN (SELECT batchNo FROM entries_del) ";
                ResultSet rs1 = Query.getDataQuery(entryQuery);
                 int excellent = 0;
                int better = 0;
                int good = 0;
                int bad = 0;
                int worse = 0;
                while(rs1.next()){
                   LocalDate expiryDate = LocalDate.parse(rs1.getString(1));
                   LocalDate  nowDate = LocalDate.now();
                   int daysToExpiry =  Integer.parseInt(expiryDate.toEpochDay() + "") - 
                           Integer.parseInt(nowDate.toEpochDay() + "") ;
                   String exSettingsQuery = "SELECT * FROM expirySettings";
                   ResultSet rs3 = Query.getDataQuery(exSettingsQuery);
                   if(rs3.next()){
                        if(daysToExpiry >= rs3.getInt("excellent")){
                            excellent = excellent + rs1.getInt(2); 
                        }else if((daysToExpiry >= rs3.getInt("better")) && 
                                (daysToExpiry < rs3.getInt("excellent") )){
                            better = better + rs1.getInt(2);
                          
                        }else if((daysToExpiry >= rs3.getInt("good")) && 
                                (daysToExpiry < rs3.getInt("better") )){
                            good = good + rs1.getInt(2);
                           
                           
                        }else if((daysToExpiry >= rs3.getInt("bad")) && 
                                (daysToExpiry < rs3.getInt("good") )){
                            bad = bad +  rs1.getInt(2);
                           
                        }else if( daysToExpiry < rs3.getInt("bad")){
                             worse = worse +  rs1.getInt(2);
                           
                        }
                   }
                }
            if(excellent > 0){
                pieChartData.add(new PieChart.Data("excellent",excellent));
                dataset.setValue("excellent",excellent);  
            }
            if(better > 0){
                pieChartData.add(new PieChart.Data("better",better));
                dataset.setValue("better",excellent);
            }
              
            if(good > 0){
                pieChartData.add(new PieChart.Data("good",good));
                dataset.setValue("good",good);
            }
            
            if(bad > 0){
                pieChartData.add(new PieChart.Data("bad",bad));
                dataset.setValue("bad",bad);
            }
            
            if(worse > 0){
                 pieChartData.add(new PieChart.Data("worse",worse));
            dataset.setValue("worse",worse);
            }
          
           
         // Create pie chart to be printed
            printedPieChart = ChartFactory.createPieChart("Pie Chart Entries Conditions " + " drugs entries quantity",
            dataset, true, true, false);
            //Creating a pie chart
            pieChart = new PieChart(pieChartData);
            
            printedPieChart.setElementHinting(true);
            
            //Setting the title of the Pie Chart
            pieChart.setTitle("Pie Chart Entries Conditions" + " drugs on entries quantity");
            
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
            //If the is no data in chart do not load it on border pane
            if(pieChart.getData().isEmpty()){
                borderPane.setCenter(new Label("No Data Loaded"));
            }else{
                borderPane.setCenter(pieChart);
              
            }
        }
       
    }

    @FXML
    private void btnBarChartAction(ActionEvent event) {
        loadBarChartData(); //Load the method that sets up Bar Chart Data
        btnStackedBarChart.setUnderline(true);
        btnBarChart.setUnderline(false);
        btnPieChart.setUnderline(true);
        onView = "Bar";
    }
    
     
    private void loadBarChartData(){
         try {
            //Defining the x axis
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Drug Names");
            
            //Defining the y axis
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Quantiy of drugs in Entries");
            
            //Creating the bar chart
            barChart = new BarChart<>(xAxis,yAxis);
            barChart.setTitle("Bar Chart | Comparison between various "
                    + " drugs on entries expiry date conditions");
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            //Prepare XYChart.Series objects by setting data
             XYChart.Series<String,Number> series1 = new XYChart.Series<>();
             XYChart.Series<String,Number> series2 = new XYChart.Series<>();
             XYChart.Series<String,Number> series3 = new XYChart.Series<>();
             XYChart.Series<String,Number> series4 = new XYChart.Series<>();
             XYChart.Series<String,Number> series5 = new XYChart.Series<>();
             
             series1.setName("Excellent");
             series2.setName("Very Good");
             series3.setName("Good");
             series4.setName("Bad");
             series5.setName("worse");
            
            //Connecting to database
            QueryManager Query = new QueryManager();
           
            String drugQuery = "SELECT id,name FROM drugs WHERE id IN "
                    + "(SELECT id FROM entries ) AND id NOT IN (SELECT id FROM drugs_del)";
            ResultSet rs1 = Query.getDataQuery(drugQuery);
            while(rs1.next()){
                String id = rs1.getString("id");
                String entryQuery = "SELECT expiryDate,quantity FROM entries "
                        + "WHERE batchNo NOT IN (SELECT batchNo FROM entries_del) AND id ='"+ id + "'";
                ResultSet rs2 = Query.getDataQuery(entryQuery);
                 int excellent = 0;
                int veryGood = 0;
                int good = 0;
                int bad = 0;
                int worse = 0;
                while(rs2.next()){
                   LocalDate expiryDate = LocalDate.parse(rs2.getString(1));
                   LocalDate  nowDate = LocalDate.now();
                   int daysToExpiry =  Integer.parseInt(expiryDate.toEpochDay() + "") - 
                           Integer.parseInt(nowDate.toEpochDay() + "") ;
                   String exSettingsQuery = "SELECT * FROM expirySettings";
                   ResultSet rs3 = Query.getDataQuery(exSettingsQuery);
                   if(rs3.next()){
                        if(daysToExpiry >= rs3.getInt("excellent")){
                            excellent = excellent + rs2.getInt(2);
                            series1.getData().add(new XYChart.Data<>(rs1.getString(2),excellent));
                            dataset.addValue(excellent, "EXCELLENT", rs1.getString(2));
                            }else if((daysToExpiry >= rs3.getInt("better")) && 
                                    (daysToExpiry < rs3.getInt("excellent") )){
                            veryGood = veryGood + rs2.getInt(2);
                            dataset.addValue(veryGood, "VERY GOOD", rs1.getString(2));
                            series2.getData().add(new XYChart.Data<>(rs1.getString(2),veryGood));
                        }else if((daysToExpiry >= rs3.getInt("good")) && 
                                (daysToExpiry < rs3.getInt("better") )){
                            good = good + rs2.getInt(2);
                            dataset.addValue(good, "GOOD", rs1.getString(2));
                            series3.getData().add(new XYChart.Data<>(rs1.getString(2),good));
                           
                        }else if((daysToExpiry >= rs3.getInt("bad")) && 
                                (daysToExpiry < rs3.getInt("good") )){
                            bad = bad + rs2.getInt(2);
                            dataset.addValue(bad, "BAD", rs1.getString(2));
                            series4.getData().add(new XYChart.Data<>(rs1.getString(2),bad));
                        }else if(daysToExpiry < rs3.getInt("bad")){
                            worse = worse + rs2.getInt(2);
                             dataset.addValue(worse, "WORSE", rs1.getString(2));
                            series5.getData().add(new XYChart.Data<>(rs1.getString(2),worse));

                        }
                   }
                }
                
            }
            
           barChart.getData().setAll(series1,series2,series3,series4,series5);
           //Create Bar Chart to be printed out
           printedBarChart = ChartFactory.createBarChart("Bar Chart Entries Conditions",
            "Drugs Names", "Number Of Entries Quantity", dataset,
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
                         Thread.sleep(2000);
                      
                         Platform.runLater(new Runnable() {
                             @Override
                             public void run() {
                                if(onView.equalsIgnoreCase("Stacked")){
                                    String fileName = System.getProperty("user.home") 
                                     + "\\Documents\\Aloe\\Charts\\" +"Entries expiry Conditions stacked bar chart"+ ".pdf";
                                          convertToPdf( printedStackedBarChart,1920,1080,fileName);
                                 }else if (onView.equalsIgnoreCase("Bar")){
                                    String fileName = System.getProperty("user.home") 
                                    + "\\Documents\\Aloe\\Charts\\" +"Entries expiry conditions bar chart"+ ".pdf";
                                    convertToPdf(printedBarChart,1920,1080,fileName);
                                 }else{
                                      String fileName = System.getProperty("user.home") 
                                    + "\\Documents\\Aloe\\Charts\\" +"Entries expiry conditions pie chart"+ ".pdf";
                                    convertToPdf(printedPieChart,1920,1080,fileName);
                                 }
                             }
                         });
                   return null;
               }
           };
           new Thread(task).start();
        
    }
     public static void convertToPdf(JFreeChart chart,
        int width, int height, String filename) {
        Document document = new Document(new Rectangle(width, height));
        boolean isCreated = false;
        try {
        PdfWriter writer;
        writer = PdfWriter.getInstance(document,
        new FileOutputStream(filename));
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        PdfTemplate tp = cb.createTemplate(width, height);
        Graphics2D g2d = tp.createGraphics(width, height,new DefaultFontMapper());
        Rectangle2D r2d = new Rectangle2D.Double(0, 0, width, height);
        chart.draw(g2d, r2d);
        g2d.dispose();
        cb.addTemplate(tp, 0, 0);
          isCreated = true;
        }
        catch(Exception e) {
        e.printStackTrace();
        }
        
        if(isCreated){
            Notification notify = new Notification(1,
              "Chart Printing successful","You have successfully printed chart to pdf");
            notify.start();
        }else{
            Notification notify = new Notification(1,
              "Chart Printing Failed","Printing chart to pdf failed");
            notify.start();
        }
        document.close();
    }

    @FXML
    private void btnVLAction(ActionEvent event) {
        PopWindow window = new PopWindow();
        window.loadSmallWindow("/aloe/view/pharmacist/stock/charts/viewLargeDrugExpiryConditions.fxml", onView 
                + " chart of expiry conditions on "
                + " drugs entries quantity ", true,true,true,false);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            if(onView.equalsIgnoreCase("Stacked")){
                
               borderPane.setCenter(stackedBarChart);
               btnStackedBarChart.setUnderline(false);
               btnPieChart.setUnderline(true);
               btnBarChart.setUnderline(true);
               onView = "Stacked";
            }else if (onView.equalsIgnoreCase("Bar")){
               borderPane.setCenter(barChart);
               btnBarChart.setUnderline(false);
               btnPieChart.setUnderline(true);
               btnStackedBarChart.setUnderline(true);
               onView = "Bar";
             }else{
               borderPane.setCenter(pieChart);
               btnBarChart.setUnderline(true);
               btnPieChart.setUnderline(false);
               btnStackedBarChart.setUnderline(true);
               onView ="Pie";
            }
        }}); 
    }

  
}
