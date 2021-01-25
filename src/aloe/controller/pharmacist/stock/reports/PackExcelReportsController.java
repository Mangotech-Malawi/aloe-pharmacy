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
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class PackExcelReportsController implements Initializable {

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
    private JFXRadioButton radPacksQty;
    @FXML
    private JFXRadioButton radExpiredPacks;
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
        radPacksQty.setSelected(false);
        radExpiredPacks.setSelected(false);
        loadLabelSelected(selected);
    }

    @FXML
    private void radPacksQtySelected(ActionEvent event) {
        selected = "Packs";
        radMaster.setSelected(false);
        radExpiredPacks.setSelected(false);
        loadLabelSelected(selected);
    }

    @FXML
    private void radExpiredPacksSelected(ActionEvent event) {
        selected ="Expired";
        radMaster.setSelected(false);
        radPacksQty.setSelected(false);
        loadLabelSelected(selected);
    }
    
    private void loadLabelSelected(String selected){
        switch(selected){
            case "Master" :  lblReportType.setText("Entries Master");break;
            case "Packs" :   lblReportType.setText("Entries Quantity");break;
            case "Expired" : lblReportType.setText("Expired Packs");break;
        }
    }

    @FXML
    private void btnPrintAction(ActionEvent event) {
    }

    @FXML
    private void btnExportToPdfAction(ActionEvent event) {
        switch(selected){
            case "Master" : generateMasterReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Packs Master Report.xls");break;
            case "Packs" :  generatePacksQuantityReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Packs Quantity Report.xls");break;
            case "Expired" :  generateExpiredPacksReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Expired Packs Report.xls");break;
        }
    }
    
    private void generateMasterReport(String filename){
         try{
             
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Packs Master");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("pack No");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("Condition");
            rowhead.createCell((short)3).setCellValue("Order Price/Drug");
            rowhead.createCell((short)4).setCellValue("Sell Price/Pack");
            rowhead.createCell((short)5).setCellValue("Quantity/Pack");
            rowhead.createCell((short)6).setCellValue("No. of Packs");
             rowhead.createCell((short)7).setCellValue("Total Price");
            
            
            String entriesQuery = "SELECT * FROM packs WHERE packId NOT IN (SELECT packId FROM packs_del)";
            ResultSet rs1 = Query.getDataQuery(entriesQuery);
            int i = 1;
            while(rs1.next()){
                 int batchNo = rs1.getInt("batchNo");
                 String entryQuery = "SELECT id,expiryDate FROM entries WHERE batchNo ='" + batchNo + "';";
                 ResultSet rs2 = Query.getDataQuery(entryQuery);
                 
                 if(rs2.next()){
                    LocalDate nowDate = LocalDate.now();
                    LocalDate expiryDate = LocalDate.parse(rs2.getString("expiryDate"));
                    
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "")
                            - Integer.parseInt(nowDate.toEpochDay() + "") ;
                    int id = rs2.getInt("id");
                    String drugQuery = "SELECT name,orderPrice FROM drugs where id ='" + id + "';";
                    ResultSet rs3 = Query.getDataQuery(drugQuery);
              
                     if(rs3.next()){
                         HSSFRow row = sheet.createRow((short)i);
                        row.createCell((short)0).setCellValue(rs1.getString("packId"));
                        row.createCell((short)1).setCellValue(rs3.getString("name"));
                        row.createCell((short)2).setCellValue(getCondition(Query,daysToExpiry));
                        row.createCell((short)3).setCellValue(NumberFormat.getCurrencyInstance().format(rs3.getDouble("orderPrice")));
                        
                        double sellingPrice = rs1.getDouble("price");
                        int numOfPacks = rs1.getInt("numOfPacks");
                        
                        //Calculating price of the whole quantity of entry
                        double totalPrice =  sellingPrice * numOfPacks;
                        
                        row.createCell((short)4).setCellValue(NumberFormat.getCurrencyInstance().format(sellingPrice));
                        
                        row.createCell((short)5).setCellValue(rs1.getString("quantity"));
                        row.createCell((short)6).setCellValue(numOfPacks);
                        row.createCell((short)7).setCellValue(NumberFormat.getCurrencyInstance().format(totalPrice)); 
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
    
    private void generatePacksQuantityReport(String filename){
         try{
             
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Pack Quantities");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("pack No");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("Condition");
            rowhead.createCell((short)3).setCellValue("Quantity/Pack");
            rowhead.createCell((short)4).setCellValue("No. of Packs");
             rowhead.createCell((short)5).setCellValue("Total Quantity");
            
            
            String packsQuery = "SELECT * FROM packs WHERE packId NOT IN (SELECT packId FROM packs_del)";
            ResultSet rs1 = Query.getDataQuery(packsQuery);
            int i = 1;
            while(rs1.next()){
                int batchNo = rs1.getInt("batchNo");
                String entryQuery = "SELECT id,expiryDate FROM entries WHERE batchNo ='" + batchNo + "';";
                ResultSet rs2 = Query.getDataQuery(entryQuery);
                
                 if(rs2.next()){
                    LocalDate nowDate = LocalDate.now();
                    LocalDate expiryDate = LocalDate.parse(rs2.getString("expiryDate"));
                    
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "")
                            - Integer.parseInt(nowDate.toEpochDay() + "") ;
                    int id = rs2.getInt("id");
                    String drugQuery = "SELECT name,orderPrice FROM drugs where id ='" + id + "';";
                    ResultSet rs3 = Query.getDataQuery(drugQuery);
                   
                     if(rs3.next()){
                        HSSFRow row = sheet.createRow((short)i);
                        row.createCell((short)0).setCellValue(rs1.getString("packId"));
                        row.createCell((short)1).setCellValue(rs3.getString("name"));
                        row.createCell((short)2).setCellValue(getCondition(Query,daysToExpiry));
            
                        int numOfPacks = rs1.getInt("numOfPacks");
                        
                        int quantity = rs1.getInt("quantity");
                        
                        int totalQty = numOfPacks * quantity;
                        //Calculating price of the whole quantity of entry  
                        row.createCell((short)3).setCellValue(quantity);
                        row.createCell((short)4).setCellValue(numOfPacks);
                        row.createCell((short)5).setCellValue(totalQty); 
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
    
    private void generateExpiredPacksReport(String filename){
          try{
             
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Expired Packs");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("pack No");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("Order Price/Drug");
            rowhead.createCell((short)3).setCellValue("Sell Price/Pack");
            rowhead.createCell((short)4).setCellValue("Quantity/Pack");
            rowhead.createCell((short)5).setCellValue("No. of Packs");
             rowhead.createCell((short)6).setCellValue("Total Price");
            
            
            String entriesQuery = "SELECT * FROM packs WHERE packId NOT IN (SELECT packId FROM packs_del)";
            ResultSet rs1 = Query.getDataQuery(entriesQuery);
            int i = 1;
            while(rs1.next()){
                int batchNo = rs1.getInt("batchNo");
                String entryQuery = "SELECT id,expiryDate FROM entries WHERE batchNo ='" + batchNo + "';";
                ResultSet rs2 = Query.getDataQuery(entryQuery);
                
                 if(rs2.next()){
                    LocalDate nowDate = LocalDate.now();
                    LocalDate expiryDate = LocalDate.parse(rs2.getString("expiryDate"));
                    
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "")
                            - Integer.parseInt(nowDate.toEpochDay() + "") ;
                    int id = rs2.getInt("id");
                    String drugQuery = "SELECT name,orderPrice FROM drugs where id ='" + id + "';";
                    ResultSet rs3 = Query.getDataQuery(drugQuery);
                 
                     if(rs3.next()){
                      
                        if(getCondition(Query,daysToExpiry).equalsIgnoreCase("worse")){
                            HSSFRow row = sheet.createRow((short)i);
                            row.createCell((short)0).setCellValue(rs1.getString("packId"));
                            row.createCell((short)1).setCellValue(rs3.getString("name"));
                            row.createCell((short)2).setCellValue(NumberFormat.getCurrencyInstance().format(rs3.getDouble("orderPrice")));
                            
                            double sellingPrice = rs1.getDouble("price");
                            int numOfPacks = rs1.getInt("numOfPacks");

                            //Calculating price of the whole quantity of entry
                            double totalPrice =  sellingPrice * numOfPacks;

                            row.createCell((short)3).setCellValue(NumberFormat.getCurrencyInstance().format(sellingPrice));

                            row.createCell((short)4).setCellValue(rs1.getString("quantity"));
                            row.createCell((short)5).setCellValue(numOfPacks);
                            row.createCell((short)6).setCellValue(NumberFormat.getCurrencyInstance().format(totalPrice)); 
                            i++;        
                        }
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
