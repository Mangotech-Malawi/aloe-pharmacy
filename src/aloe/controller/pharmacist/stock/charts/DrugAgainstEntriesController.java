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
import javafx.scene.chart.ScatterChart;
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
public class DrugAgainstEntriesController implements Initializable {

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
    private JFXButton btnPrint;
    @FXML
    private JFXButton btnVL;

    
    public static BarChart<String,Number> barChart;
    public static ScatterChart<String,Number> scatterChart;
    public static PieChart pieChart;
    public static String onView;//keeps the type of chart that is being view
    JFreeChart printedBarChart;
    JFreeChart printedPieChart;
    JFreeChart printedScatterChart;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        btnBarChart.setUnderline(false);
        loadBarChartData(); //Load the method that sets up Bar Chart Data
        onView = "Bar";
    }    

    @FXML
    private void btnBarChartAction(ActionEvent event) {
        loadBarChartData(); //Load the method that sets up Bar Chart Data
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
                    + " drugs on entries quanity");
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            //Prepare XYChart.Series objects by setting data
            XYChart.Series<String,Number> series = new XYChart.Series<>();
            
            //Connecting to database
            QueryManager Query = new QueryManager();
            //Nested query for getting SUM of drug Qtys in drug entries/drug
            String entryQtyQuery = "SELECT id,SUM(quantity) FROM entries WHERE batchNo "
                    + "NOT IN (SELECT batchNo from entries_del) GROUP BY id;";
            ResultSet rs1 = Query.getDataQuery(entryQtyQuery);//Getting the result of the query
            while(rs1.next()){
                String id = rs1.getString(1);
                 //Query to get drug name from drugs table if the id is not present in drugs_del table
                String drugQuery = "SELECT name FROM drugs WHERE (id ='" + id  + 
                        "' AND id NOT IN (SELECT id FROM drugs_del));"; 
                ResultSet rs2 = Query.getDataQuery(drugQuery); // Getting the result
                if(rs2.next()){ //If drug exists
                    //Adding the data retrieved on series
                    series.getData().add(new XYChart.Data<>(rs2.getString(1),rs1.getInt(2)));
                    dataset.setValue(rs1.getInt(2), "students", rs2.getString(1));
                }
            }
            //Create Bar Chart to load on borderPane
           barChart.getData().addAll(series);
           
           //Create Bar Chart to be printed out
           printedBarChart = ChartFactory.createBarChart("Bar Chart | Comparison between various drugs on entries quanity",
            "Drugs", "Number Of Entries Quantity", dataset,
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
    private void btnPieChartAction(ActionEvent event) {
        
        btnBarChart.setUnderline(true);
        btnPieChart.setUnderline(false);
        loadPieChartData();
        onView = "Pie";
    }
    private void  loadPieChartData(){
        try {
            //Decraring observable list object
            ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList();
            
            //Declaring
             DefaultPieDataset dataset = new DefaultPieDataset();
            //Connecting to database
            QueryManager Query = new QueryManager();
            //Nested query for getting SUM of drug Qtys in drug entries/drug
            String entryQtyQuery = "SELECT id,SUM(quantity) FROM entries WHERE batchNo "
                    + "NOT IN (SELECT batchNo from entries_del) GROUP BY id;";
            ResultSet rs1 = Query.getDataQuery(entryQtyQuery);//Getting the result of the query
            while(rs1.next()){
                String id = rs1.getString(1);
                //Query to get drug name from drugs table if the id is not present in drugs_del table
                String drugQuery = "SELECT name FROM drugs WHERE (id ='" + id  +
                        "' AND id NOT IN (SELECT id FROM drugs_del));"; 
                ResultSet rs2 = Query.getDataQuery(drugQuery); // Getting the result
                if(rs2.next()){ //If drug exists
                    //Adding the data retrieved on series
                    pieChartData.add(new PieChart.Data(rs2.getString(1),rs1.getInt(2)));
                    dataset.setValue(rs2.getString(1),rs1.getInt(2));
                }
            }
            // Create pie chart to be printed
             printedPieChart = ChartFactory.createPieChart("Pie Chart | Comparison between various " + " drugs on entries quantity",
             dataset, true, true, false);
            //Creating a pie chart
            pieChart = new PieChart(pieChartData);
            
            printedPieChart.setElementHinting(true);
            
            //Setting the title of the Pie Chart
            pieChart.setTitle("Pie Chart | Comparison between varios " + " drugs on entries quantity");
            
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
                                     + "\\Documents\\Aloe\\Charts\\" +"Drug Names Against Entries Quantity Bar Chart"+ ".pdf";
                                          convertToPdf(printedBarChart,1920,1080,fileName);
                                 }else{
                                    String fileName = System.getProperty("user.home") 
                                    + "\\Documents\\Aloe\\Charts\\" +"Drug Names Against Entries Quantity Pie Chart"+ ".pdf";
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
        window.loadSmallWindow("/aloe/view/pharmacist/stock/charts/drugAgainstEntriesLargeCharts.fxml", onView 
                + " chart | Comparison between various "
                + " drugs on entries quantity ", true,true,true,false);
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
               onView = "Pie";
             }
        }}); 
    }

    
    
}
