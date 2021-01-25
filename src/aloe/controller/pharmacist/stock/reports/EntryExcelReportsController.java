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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class EntryExcelReportsController implements Initializable {

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
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Master Report.xls");break;
            case "Entries" :  generateEntriesQuantityReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Quantity Report.xls");break;
            case "EntriesTotal" :  generateTotalEntriesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Total Report.xls");break;
            case "Expiry" :  generateExpiryReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Expiry Report.xls");break;
        }
    }
    
    private void generateMasterReport(String filename){
         try{
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Entries Master");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("BathNo");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("Condition");
            rowhead.createCell((short)3).setCellValue("Order Price");
            rowhead.createCell((short)4).setCellValue("Selling Price");
            rowhead.createCell((short)5).setCellValue("Quantity");
             rowhead.createCell((short)6).setCellValue("Total");
            
            String entriesQuery = "SELECT * FROM entries WHERE batchNo "
                    + "NOT IN (SELECT batchNo FROM entries_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(entriesQuery);
            double allTotalPrice = 0;
            int totalQty = 0;
            while(rs.next()){
                
                int id = rs.getInt("id");
                String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
                ResultSet rs2 = Query.getDataQuery(drugQuery);
                if(rs2.next()){
                    HSSFRow row = sheet.createRow((short)i);
                    row.createCell((short)0).setCellValue(rs.getString(1));
                    row.createCell((short)1).setCellValue(rs2.getString(1));
                    LocalDate expiryDate = LocalDate.parse(rs.getString("expiryDate"));
                    LocalDate nowDate = LocalDate.now();
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "") - Integer.parseInt(nowDate.toEpochDay() + "") ;
                    
                    row.createCell((short)2).setCellValue(getCondition(Query,daysToExpiry));
                    
                    double sellingPrice = rs2.getDouble("sellingPrice");
                     
                    int quantity = rs.getInt("quantity");
       
                    //Calculating price of the whole quantity of entry
                    double totalPrice =  sellingPrice * quantity;
                    
                    //Adding the whole total prices of entries
                    allTotalPrice = allTotalPrice + totalPrice;
                    
                    //AAdding the whole quantities of entries
                    totalQty = totalQty + quantity;
                    row.createCell((short)3).setCellValue(rs2.getDouble(3));
                    row.createCell((short)4).setCellValue(sellingPrice);
                    row.createCell((short)5).setCellValue(quantity);
                    row.createCell((short)6).setCellValue(totalPrice); 
                    i++;        
                }
               
            }
            FileOutputStream fileOut = new FileOutputStream(filename);
            hwb.write(fileOut);
            fileOut.close();
            
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generateEntriesQuantityReport(String filename){
          try{
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Entries Quantity Sheet");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("BathNo");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("Condition");
            rowhead.createCell((short)3).setCellValue("Quantity");
   
            
            String entriesQuery = "SELECT * FROM entries WHERE batchNo "
                    + "NOT IN (SELECT batchNo FROM entries_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(entriesQuery);
          
            while(rs.next()){
                
                int id = rs.getInt("id");
                String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
                ResultSet rs2 = Query.getDataQuery(drugQuery);
                if(rs2.next()){
                    HSSFRow row = sheet.createRow((short)i);
                    row.createCell((short)0).setCellValue(rs.getString(1));
                    row.createCell((short)1).setCellValue(rs2.getString(1));
                    LocalDate expiryDate = LocalDate.parse(rs.getString("expiryDate"));
                    LocalDate nowDate = LocalDate.now();
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "") - Integer.parseInt(nowDate.toEpochDay() + "") ;
                    
                    row.createCell((short)2).setCellValue(getCondition(Query,daysToExpiry));
                    
                    int quantity = rs.getInt("quantity");
                    row.createCell((short)3).setCellValue(quantity);
                    i++;        
                }
               
            }
            FileOutputStream fileOut = new FileOutputStream(filename);
            hwb.write(fileOut);
            fileOut.close();
            
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generateTotalEntriesReport(String filename){
             try{
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Entries Total Quantity Per Drug Sheet");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("BathNo");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("Quantity");
   
            
            String entriesQuery = "SELECT id,batchNo,SUM(quantity),expiryDate FROM entries WHERE batchNo "
                    + "NOT IN (SELECT batchNo FROM entries_del) GROUP BY id";
            int i = 1;
            ResultSet rs = Query.getDataQuery(entriesQuery);
          
            while(rs.next()){
                
                int id = rs.getInt("id");
                String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
                ResultSet rs2 = Query.getDataQuery(drugQuery);
                if(rs2.next()){
                    HSSFRow row = sheet.createRow((short)i);
                    row.createCell((short)0).setCellValue(rs.getString(1));
                    row.createCell((short)1).setCellValue(rs2.getString(1));
              
                    
                    int quantity = rs.getInt(3);
                    row.createCell((short)2).setCellValue(quantity);
                    i++;        
                }
               
            }
            FileOutputStream fileOut = new FileOutputStream(filename);
            hwb.write(fileOut);
            fileOut.close();
            
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generateExpiryReport(String filename){
         try{
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Expired Entries");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("BathNo");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("Order Price");
            rowhead.createCell((short)3).setCellValue("Selling Price");
            rowhead.createCell((short)4).setCellValue("Quantity");
             rowhead.createCell((short)5).setCellValue("Total");
            
            String entriesQuery = "SELECT * FROM entries WHERE batchNo "
                    + "NOT IN (SELECT batchNo FROM entries_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(entriesQuery);
            double allTotalPrice = 0;
            int totalQty = 0;
            while(rs.next()){
                
                int id = rs.getInt("id");
                String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
                ResultSet rs2 = Query.getDataQuery(drugQuery);
                if(rs2.next()){
                   
                    
                    LocalDate expiryDate = LocalDate.parse(rs.getString("expiryDate"));
                    LocalDate nowDate = LocalDate.now();
                    
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "") 
                            - Integer.parseInt(nowDate.toEpochDay() + "") ;
                    
                    if(getCondition(Query,daysToExpiry).equalsIgnoreCase("worse")){
                        HSSFRow row = sheet.createRow((short)i);
                        row.createCell((short)0).setCellValue(rs.getString(1));
                        row.createCell((short)1).setCellValue(rs2.getString(1));
                        double sellingPrice = rs2.getDouble("sellingPrice");

                        int quantity = rs.getInt("quantity");
                        
                        //Calculating price of the whole quantity of entry
                        double totalPrice =  sellingPrice * quantity;

                        //Adding the whole total prices of entries
                        allTotalPrice = allTotalPrice + totalPrice;

                        //AAdding the whole quantities of entries
                        totalQty = totalQty + quantity;
                        row.createCell((short)2).setCellValue(rs2.getDouble(3));
                        row.createCell((short)3).setCellValue(sellingPrice);
                        row.createCell((short)4).setCellValue(quantity);
                        row.createCell((short)5).setCellValue(totalPrice); 
                        i++;        
                    }
                   
                }
               
            }
            FileOutputStream fileOut = new FileOutputStream(filename);
            hwb.write(fileOut);
            fileOut.close();
            
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
