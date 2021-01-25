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
public class PackWordReportGeneratorController implements Initializable {

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
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Packs Master Report.docx");break;
            case "Packs" :  generatePacksQuantityReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Packs Quantity Report.docx");break;
            case "Expired" :  generateExpiredPacksReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Expired Packs Report.docx");break;
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
            run.setText("Packs Master Report | " + LocalDate.now().toString());
            
            //create table
            XWPFTable table = document.createTable();
            
     
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Pack No");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText("Condition");
            tableRowOne.addNewTableCell().setText("Order Price /Drug");
            tableRowOne.addNewTableCell().setText("Selling Price /Pack");
            tableRowOne.addNewTableCell().setText("Quantity/Pack");
            tableRowOne.addNewTableCell().setText("No. of packs");
            tableRowOne.addNewTableCell().setText("Total Price");
            
            
            String entriesQuery = "SELECT * FROM packs WHERE packId NOT IN (SELECT packId FROM packs_del)";
            ResultSet rs1 = Query.getDataQuery(entriesQuery);
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
                        XWPFTableRow row = table.createRow();
                        row.getCell(0).setText(rs1.getString("packId"));
                        row.getCell(1).setText(rs3.getString("name"));
                        row.getCell(2).setText(getCondition(Query,daysToExpiry));
                        row.getCell(3).setText(NumberFormat.getInstance().format(rs3.getDouble("orderPrice")));
                       
                        double sellingPrice = rs1.getDouble("price");
                        int numOfPacks = rs1.getInt("numOfPacks");
                        
                        //Calculating price of the whole quantity of entry
                        double totalPrice =  sellingPrice * numOfPacks;
                        
                        row.getCell(4).setText(NumberFormat.getInstance().format(sellingPrice));
                        row.getCell(5).setText(rs1.getString("quantity"));
                        row.getCell(6).setText(numOfPacks + "");
                        row.getCell(7).setText( NumberFormat.getInstance().format(totalPrice));
                        
                     }
                 }
             
            }
            
            
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generatePacksQuantityReport(String filename){
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
            run.setText("Packs Quantity Report | " + LocalDate.now().toString());
            
            //create table
            XWPFTable table = document.createTable();
            
     
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Pack No");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText("Condition");
            tableRowOne.addNewTableCell().setText("Quantity/Pack");
            tableRowOne.addNewTableCell().setText("No. of packs");
            tableRowOne.addNewTableCell().setText("Total Quantity");
            
            
            String entriesQuery = "SELECT * FROM packs WHERE packId NOT IN (SELECT packId FROM packs_del)";
            ResultSet rs1 = Query.getDataQuery(entriesQuery);
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
                        XWPFTableRow row = table.createRow();
                        row.getCell(0).setText(rs1.getString("packId"));
                        row.getCell(1).setText(rs3.getString("name"));
                        row.getCell(2).setText(getCondition(Query,daysToExpiry));
                      
                        int numOfPacks = rs1.getInt("numOfPacks");
                        
                        int quantity = rs1.getInt("quantity");
                        
                        int totalQty = numOfPacks * quantity;
                        
                        row.getCell(3).setText(NumberFormat.getInstance().format(quantity));
                        row.getCell(4).setText(numOfPacks + "");
                        row.getCell(5).setText( NumberFormat.getInstance().format(totalQty));
                        
                     }
                 }
             
            }
            
            
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generateExpiredPacksReport(String filename){
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
            run.setText("Expired Packs Report | " + LocalDate.now().toString());
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Pack No");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText("Order Price /Drug");
            tableRowOne.addNewTableCell().setText("Selling Price /Pack");
            tableRowOne.addNewTableCell().setText("Quantity/Pack");
            tableRowOne.addNewTableCell().setText("No. of packs");
            tableRowOne.addNewTableCell().setText("Total Price");
            
            String entriesQuery = "SELECT * FROM packs WHERE packId NOT IN (SELECT packId FROM packs_del)";
            ResultSet rs1 = Query.getDataQuery(entriesQuery);
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
                            XWPFTableRow row = table.createRow();
                            row.getCell(0).setText(rs1.getString("packId"));
                            row.getCell(1).setText(rs3.getString("name"));

                            row.getCell(2).setText(NumberFormat.getInstance().format(rs3.getDouble("orderPrice")));

                            double sellingPrice = rs1.getDouble("price");
                            int numOfPacks = rs1.getInt("numOfPacks");

                            //Calculating price of the whole quantity of entry
                            double totalPrice =  sellingPrice * numOfPacks;

                            row.getCell(3).setText(NumberFormat.getInstance().format(sellingPrice));
                            row.getCell(4).setText(rs1.getString("quantity"));
                            row.getCell(5).setText(numOfPacks + "");
                            row.getCell(6).setText( NumberFormat.getInstance().format(totalPrice));  
                        }
                       
                     }
                 }  
            }
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
