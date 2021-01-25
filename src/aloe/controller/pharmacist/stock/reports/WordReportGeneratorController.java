/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock.reports;

import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
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
public class WordReportGeneratorController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton btnMaster;
    @FXML
    private VBox btnDrugsPricesAction;
    @FXML
    private JFXButton btnPrices;
    @FXML
    private VBox btnDrugsCategories;
    @FXML
    private JFXButton btnCategories;
    @FXML
    private JFXButton btnDescription;
    @FXML
    private JFXButton btnNames;
    @FXML
    private Label lblReportType;
    @FXML
    private JFXButton btnExportToWord;
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
    private void btnMaster(ActionEvent event) {
       selected ="Master";
       loadLabelSelected(selected);
    }

        @FXML
    private void btnPricesAction(ActionEvent event) {
       selected ="Prices";
       loadLabelSelected(selected);
    }

    @FXML
    private void btnCategoriesAction(ActionEvent event) {
       selected ="Categories";
       loadLabelSelected(selected);
    }

    @FXML
    private void btnDescriptionAction(ActionEvent event) {
       selected ="Description";
       loadLabelSelected(selected);
    }

    @FXML
    private void btnNamesAction(ActionEvent event) {
       selected ="Names";
       loadLabelSelected(selected);
    }
    
    private void loadLabelSelected(String selected){
        switch(selected){
            case "Master" : lblReportType.setText("Drugs Master");break;
            case "Prices" : lblReportType.setText("Drugs Prices");break;
            case "Categories" : lblReportType.setText("Drugs Categories");break;
            case "Description" : lblReportType.setText("Drugs Description");break;
            case "Names" : lblReportType.setText("Drugs Names");break;
        }
    }
    

    @FXML
    private void btnExportToWordAction(ActionEvent event) {
         switch(selected){
            case "Master" : generateMasterReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Details Master Report.docx");break;
            case "Prices" :  generatePricesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Prices Report.docx");break;
            case "Categories" :  generateCategoriesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Categories Report.docx");break;
            case "Description" :  generateDescriptionReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drugs Description Report.docx");break;
            case "Names" : generateNamesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Names Report.docx");break;
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
            run.setText("Drug Master Report");
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Id");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText("1st Category");
            tableRowOne.addNewTableCell().setText("2nd Category");
            tableRowOne.addNewTableCell().setText(NumberFormat.getInstance().getCurrency().getCurrencyCode() + "Order Price");
            tableRowOne.addNewTableCell().setText(NumberFormat.getInstance().getCurrency().getCurrencyCode() + "Selling Price");
            tableRowOne.addNewTableCell().setText("Unit Of Measurement");
            tableRowOne.addNewTableCell().setText("Description");
            
            String drugQuery = "SELECT * FROM drugs WHERE id "
                    + "NOT IN (SELECT id FROM drugs_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(drugQuery);
            while(rs.next()){
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(rs.getString(1));
                row.getCell(1).setText(rs.getString(2));
                row.getCell(2).setText(rs.getString(3));
                row.getCell(3).setText(rs.getString(4));
                row.getCell(4).setText(rs.getString(6));
                row.getCell(5).setText(rs.getString(5));
                row.getCell(6).setText(rs.getString(7));
                row.getCell(7).setText(rs.getString(8));
            }
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
   
    private void generatePricesReport(String filename){
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
            run.setText("Drug Prices Report");
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Id");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText(NumberFormat.getInstance().getCurrency()+ "Order Price");
            tableRowOne.addNewTableCell().setText(NumberFormat.getInstance().getCurrency()+"Selling Price");
            
            String drugQuery = "SELECT id,name,orderPrice,sellingPrice FROM drugs WHERE id "
                    + "NOT IN (SELECT id FROM drugs_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(drugQuery);
            while(rs.next()){
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(rs.getString(1));
                row.getCell(1).setText(rs.getString(2));
                row.getCell(2).setText(rs.getString(3));
                row.getCell(3).setText(rs.getString(4));
            }
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generateCategoriesReport(String filename){
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
            run.setText("Drug Categories Report");
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Id");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText("1st Category");
            tableRowOne.addNewTableCell().setText("2nd Category");
            
            String drugQuery = "SELECT id,name,firstCategory,secCategory FROM drugs WHERE id "
                    + "NOT IN (SELECT id FROM drugs_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(drugQuery);
            while(rs.next()){
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(rs.getString(1));
                row.getCell(1).setText(rs.getString(2));
                row.getCell(2).setText(rs.getString(3));
                row.getCell(3).setText(rs.getString(4));
            }
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
   
    private void generateDescriptionReport(String filename){
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
            run.setText("Drug Description Report");
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Id");
            tableRowOne.addNewTableCell().setText("Name");
            tableRowOne.addNewTableCell().setText("Description");
            
            String drugQuery = "SELECT id,name,description FROM drugs WHERE id "
                    + "NOT IN (SELECT id FROM drugs_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(drugQuery);
            while(rs.next()){
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(rs.getString(1));
                row.getCell(1).setText(rs.getString(2));
                row.getCell(2).setText(rs.getString(3));
            }
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generateNamesReport(String filename){
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
            run.setText("Drug Names Report");
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Id");
            tableRowOne.addNewTableCell().setText("Name");
            
            String drugQuery = "SELECT id,name FROM drugs WHERE id "
                    + "NOT IN (SELECT id FROM drugs_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(drugQuery);
            while(rs.next()){
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(rs.getString(1));
                row.getCell(1).setText(rs.getString(2));
            }
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
   
}
