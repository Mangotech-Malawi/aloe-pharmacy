/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock.reports;

import aloe.controller.pharmacist.stock.ViewEntriesController;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class EntryWordReportsController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXRadioButton radMaster;
    @FXML
    private VBox btnDrugsPricesAction;
    @FXML
    private JFXRadioButton radEntriesQty;
    @FXML
    private VBox btnDrugsCategories;
    @FXML
    private JFXRadioButton radTotalQty;
    @FXML
    private JFXRadioButton radEntryExpiry;
    @FXML
    private Label lblReportType;
    @FXML
    private JFXButton btnPrint;
    @FXML
    private JFXButton btnExportToPdf;
    
    private String selected = "Master";
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnMnimizeAction(ActionEvent event) {
        PopWindow.childStage.setIconified(true);
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {   
        stackPane.getScene().getWindow().hide();
    }

    @FXML
    private void radMasterSelected(ActionEvent event) {
        selected ="Master";
        radEntriesQty.setSelected(false);
        radTotalQty.setSelected(false);
        radEntryExpiry.setSelected(false);
        loadLabelSelected(selected);
    }

    @FXML
    private void radEntriesQtySelected(ActionEvent event) {
        selected ="Entries";
        radMaster.setSelected(false);
        radTotalQty.setSelected(false);
        radEntryExpiry.setSelected(false);
        loadLabelSelected(selected);
    }

    @FXML
    private void radTotalQtySelected(ActionEvent event) {
        selected ="EntriesTotal";
        radMaster.setSelected(false);
        radEntriesQty.setSelected(false);
        radEntryExpiry.setSelected(false);
        loadLabelSelected(selected);
    }

    @FXML
    private void radEntryExpirySelected(ActionEvent event) {
        selected ="Expiry";
        radMaster.setSelected(false);
        radEntriesQty.setSelected(false);
        radTotalQty.setSelected(false);
        loadLabelSelected(selected);
    }
    
    private void loadLabelSelected(String selected){
        switch(selected){
            case "Master" : lblReportType.setText("Entries Master");break;
            case "Entries" : lblReportType.setText("Entries Quantity");break;
            case "EntriesTotal" : lblReportType.setText("Total Etries");break;
            case "Expiry" : lblReportType.setText("Expired Entries");break;
        }
    }
    

    @FXML
    private void btnPrintAction(ActionEvent event) {
    }

    @FXML
    private void btnExportToPdfAction(ActionEvent event) {
        switch(selected){
            case "Master" : generateMasterReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Master Report.docx");break;
            case "Entries" :  generateEntriesQuantityReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Quantity Report.docx");break;
            case "EntriesTotal" :  generateTotalEntriesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Total Report.docx");break;
            case "Expiry" :  generateExpiryReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Expiry Report.docx");break;
        } 
    }
    
    private void generateMasterReport(String filename){
         try{
            //Connecting to database 
            QueryManager Query = new QueryManager();
            
            //Blank Document
            XWPFDocument document = new XWPFDocument();
            
            //Write the document in the file system 
            FileOutputStream out = new FileOutputStream(new File(filename));
            
            //Create Paragraph
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Entries Master Report | " + LocalDate.now().toString());
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Id");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText("Condition");
            tableRowOne.addNewTableCell().setText(NumberFormat.getCurrencyInstance().getCurrency().getCurrencyCode() + "Order Price");
            tableRowOne.addNewTableCell().setText(NumberFormat.getCurrencyInstance().getCurrency().getCurrencyCode()+"Selling Price");
            tableRowOne.addNewTableCell().setText("Quantity");
            tableRowOne.addNewTableCell().setText("Total");
            
            String entriesQuery = "SELECT * FROM entries WHERE batchNo NOT IN (SELECT batchNo FROM entries_del)";
            ResultSet rs = Query.getDataQuery(entriesQuery);
            int i = 1;
            double allTotalPrice = 0;
            int totalQty = 0;
            while(rs.next()){
                
                      
               int id = rs.getInt("id");
               String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
               ResultSet rs2 = Query.getDataQuery(drugQuery);
               
               if(rs2.next()){
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(rs.getString("batchNo"));
                    row.getCell(1).setText(rs2.getString("name"));
                    
                      
                    LocalDate nowDate = LocalDate.now();
                    LocalDate expiryDate = LocalDate.parse(rs.getString("expiryDate"));
                    
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "") - 
                            Integer.parseInt(nowDate.toEpochDay() + "") ;
                    
                    double sellingPrice = rs2.getDouble("sellingPrice");
                    
                    int quantity = rs.getInt("quantity");
                    
                    //Calculating price of the whole quantity of entry
                    double totalPrice =  sellingPrice * quantity;
                    
                    //Adding the whole total prices of entries
                    allTotalPrice = allTotalPrice + totalPrice;
                    
                    //AAdding the whole quantities of entries
                    totalQty = totalQty + quantity;
                    
                    row.getCell(2).setText(getCondition(Query,daysToExpiry));
                    row.getCell(3).setText(NumberFormat.getInstance().format(sellingPrice));
                    row.getCell(4).setText(NumberFormat.getInstance().format(rs2.getDouble("orderPrice")));
                    row.getCell(5).setText(quantity+"");
                    row.getCell(6).setText(NumberFormat.getInstance().format(totalPrice));
               }  
            }
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText("");
            row.getCell(1).setText("");
            row.getCell(2).setText("");
            row.getCell(3).setText("");
            row.getCell(4).setText("Total");
            row.getCell(5).setText(totalQty + "");
            row.getCell(6).setText(NumberFormat.getInstance().format(allTotalPrice));
            
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void  generateEntriesQuantityReport(String filename){
             try{
            //Connecting to database 
            QueryManager Query = new QueryManager();
            
            //Blank Document
            XWPFDocument document = new XWPFDocument();
            
            //Write the document in the file system 
            FileOutputStream out = new FileOutputStream(new File(filename));
            
            //Create Paragraph
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Entries Quantity Report | " + LocalDate.now().toString());
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Batch No.");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText("Condition");
            tableRowOne.addNewTableCell().setText("Quantity");
          
            
            String entriesQuery = "SELECT * FROM entries WHERE batchNo NOT IN (SELECT batchNo FROM entries_del)";
            ResultSet rs = Query.getDataQuery(entriesQuery);
            int i = 1;
            double allTotalPrice = 0;
            int totalQty = 0;
            while(rs.next()){
                
                      
               int id = rs.getInt("id");
               String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
               ResultSet rs2 = Query.getDataQuery(drugQuery);
               
               if(rs2.next()){
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(rs.getString("batchNo"));
                    row.getCell(1).setText(rs2.getString("name"));
                       
                    LocalDate nowDate = LocalDate.now();
                    LocalDate expiryDate = LocalDate.parse(rs.getString("expiryDate"));
                    
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "") - 
                            Integer.parseInt(nowDate.toEpochDay() + "") ;
                    
                    double sellingPrice = rs2.getDouble("sellingPrice");
                    
                    int quantity = rs.getInt("quantity");
                    
                    //Calculating price of the whole quantity of entry
                    double totalPrice =  sellingPrice * quantity;
                    
                    //Adding the whole total prices of entries
                    allTotalPrice = allTotalPrice + totalPrice;
                    
                    //AAdding the whole quantities of entries
                    totalQty = totalQty + quantity;
                    
                    row.getCell(2).setText(getCondition(Query,daysToExpiry));
                    row.getCell(3).setText(quantity+"");
               }
               
            }
             XWPFTableRow row = table.createRow();
            row.getCell(0).setText("");
            row.getCell(1).setText("");
            row.getCell(2).setText("Total");
            row.getCell(3).setText("" + totalQty);
                
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generateTotalEntriesReport(String filename){
             try{
            //Connecting to database 
            QueryManager Query = new QueryManager();
            
            //Blank Document
            XWPFDocument document = new XWPFDocument();
            
            //Write the document in the file system 
            FileOutputStream out = new FileOutputStream(new File(filename));
            
            //Create Paragraph
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Entries Quantity Per Drug Report | " + LocalDate.now().toString());
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Id");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText("Quantity");
          
            
            String entriesQuery = "SELECT id,batchNo,SUM(quantity),expiryDate FROM entries WHERE batchNo NOT IN (SELECT batchNo FROM entries_del) GROUP BY id";
            ResultSet rs = Query.getDataQuery(entriesQuery);
            int i = 1;
            double allTotalPrice = 0;
            int totalQty = 0;
            while(rs.next()){
                
                      
               int id = rs.getInt("id");
               String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
               ResultSet rs2 = Query.getDataQuery(drugQuery);
               
               if(rs2.next()){
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(rs.getString("id"));
                    row.getCell(1).setText(rs2.getString("name"));
                
                    
                    double sellingPrice = rs2.getDouble("sellingPrice");
                    
                    int quantity = rs.getInt(3);
                    
                    //Calculating price of the whole quantity of entry
                    double totalPrice =  sellingPrice * quantity;
                    
                    //Adding the whole total prices of entries
                    allTotalPrice = allTotalPrice + totalPrice;
                    
                    //AAdding the whole quantities of entries
                    totalQty = totalQty + quantity;
                    
                  
                    row.getCell(2).setText(quantity+"");
               }
               
            }
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText("");
            row.getCell(1).setText("Total");
            row.getCell(2).setText("" + totalQty);
            
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generateExpiryReport(String filename){
         try{
            //Connecting to database 
            QueryManager Query = new QueryManager();
            
            //Blank Document
            XWPFDocument document = new XWPFDocument();
            
            //Write the document in the file system 
            FileOutputStream out = new FileOutputStream(new File(filename));
            
            //Create Paragraph
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Expired Entries | " + LocalDate.now().toString());
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Batch No");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText(NumberFormat.getCurrencyInstance().getCurrency().getCurrencyCode() + "Order Price");
            tableRowOne.addNewTableCell().setText(NumberFormat.getCurrencyInstance().getCurrency().getCurrencyCode()+"Selling Price");
            tableRowOne.addNewTableCell().setText("Quantity");
            tableRowOne.addNewTableCell().setText("Total");
            
            String entriesQuery = "SELECT * FROM entries WHERE batchNo NOT IN (SELECT batchNo FROM entries_del)";
            ResultSet rs = Query.getDataQuery(entriesQuery);
            int i = 1;
            double allTotalPrice = 0;
            int totalQty = 0;
            while(rs.next()){
                
                      
               int id = rs.getInt("id");
               String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
               ResultSet rs2 = Query.getDataQuery(drugQuery);
               
               if(rs2.next()){
                   
                      
                    LocalDate nowDate = LocalDate.now();
                    LocalDate expiryDate = LocalDate.parse(rs.getString("expiryDate"));
                    
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "") - 
                            Integer.parseInt(nowDate.toEpochDay() + "") ;
                    
                    if(getCondition(Query,daysToExpiry).equalsIgnoreCase("worse")){
                               XWPFTableRow row = table.createRow();
                               row.getCell(0).setText(rs.getString("batchNo"));
                               row.getCell(1).setText(rs2.getString("name"));

                               double sellingPrice = rs2.getDouble("sellingPrice");

                               int quantity = rs.getInt("quantity");

                               //Calculating price of the whole quantity of entry
                               double totalPrice =  sellingPrice * quantity;

                               //Adding the whole total prices of entries
                               allTotalPrice = allTotalPrice + totalPrice;

                               //AAdding the whole quantities of entries
                               totalQty = totalQty + quantity;

                               row.getCell(2).setText(NumberFormat.getInstance().format(sellingPrice));
                               row.getCell(3).setText(NumberFormat.getInstance().format(rs2.getDouble("orderPrice")));
                               row.getCell(4).setText(quantity+"");
                               row.getCell(5).setText(NumberFormat.getInstance().format(totalPrice));
                      }
               }
       
            }
                  XWPFTableRow row = table.createRow();
                row.getCell(0).setText("");
                row.getCell(1).setText("");
                row.getCell(2).setText("");
                row.getCell(3).setText("Total");
                row.getCell(4).setText(totalQty + "");
                row.getCell(5).setText(NumberFormat.getInstance().format(allTotalPrice));
                
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
      private String getCondition(QueryManager Query, int daysToExpiry){
         try {
             String settingsQuery = "SELECT * FROM expirySettings";
             ResultSet rs1 = Query.getDataQuery(settingsQuery);
             if(rs1.next()){
                 if(daysToExpiry >= rs1.getInt("excellent")){
                     return "excellent";
                 }else if(daysToExpiry >= rs1.getInt("better") && daysToExpiry < rs1.getInt("excellent") ){
                     return "better";
                 }else if(daysToExpiry >= rs1.getInt("good") && daysToExpiry < rs1.getInt("better")){
                     return "good";
                 }else if(daysToExpiry >= rs1.getInt("bad") && daysToExpiry < rs1.getInt("good")){
                    return "bad";
                 }else{
                     return "worse";
                 }
                 
             }
         } catch (SQLException ex) {
             Logger.getLogger(ViewEntriesController.class.getName()).log(Level.SEVERE, null, ex);
         }
       return "good";              
    }
}
