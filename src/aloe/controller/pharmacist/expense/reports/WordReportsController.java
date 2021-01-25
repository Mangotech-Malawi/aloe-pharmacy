/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.expense.reports;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
public class WordReportsController implements Initializable {

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
    private ComboBox<String> yearMasterCombo;
    @FXML
    private VBox btnDrugsPricesAction;
    @FXML
    private ComboBox<String> monthCombo;
    @FXML
    private ComboBox<String> yearCombo;
    @FXML
    private DatePicker expenseDatePicker;
    @FXML
    private Label lblReportType;
    @FXML
    private JFXButton btnExportToWord;
    ObservableList<String> monthList = FXCollections.observableArrayList();
    ObservableList<String> yearList = FXCollections.observableArrayList();
    private String selected = "Master";
    @FXML
    private JFXRadioButton radMonthly;
    @FXML
    private JFXRadioButton radDaily;
    LocalDate nowDate = LocalDate.now();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        yearMasterCombo.setValue(nowDate.getYear() + "");
        monthCombo.setValue(nowDate.getMonth().name().toLowerCase());
        yearCombo.setValue(nowDate.getYear() + "");
        expenseDatePicker.setValue(nowDate);
        loadMonths();
        loadYears(); 
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
        yearMasterCombo.getItems().setAll(yearList);
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
    private void yearMasterComboAction(ActionEvent event) {
      
    }


    @FXML
    private void monthComboAction(ActionEvent event) {
    }

    @FXML
    private void yearComboAction(ActionEvent event) {
    }
    
    @FXML
    private void radMasterSelected(ActionEvent event) {
        selected ="Master";
        radMonthly.setSelected(false);
        radDaily.setSelected(false);
        yearMasterCombo.setDisable(false);
        yearMasterCombo.setValue(nowDate.getYear()+"");
        yearCombo.setDisable(true);
        monthCombo.setDisable(true);
        expenseDatePicker.setDisable(true);
        loadLabelSelected(selected);
    }

    @FXML
    private void radMonthlySelected(ActionEvent event) {
         selected ="Monthly";
        radMaster.setSelected(false);
        radDaily.setSelected(false);
        yearMasterCombo.setDisable(true);
        yearCombo.setDisable(false);
        yearCombo.setValue(nowDate.getYear()+"");
        monthCombo.setDisable(false);
        monthCombo.setValue(nowDate.getMonth().name().toLowerCase());
        expenseDatePicker.setDisable(true);
        loadLabelSelected(selected);
    }

    @FXML
    private void radDailySelected(ActionEvent event) {
        selected ="Daily";
        radMaster.setSelected(false);
        radMonthly.setSelected(false);
        yearMasterCombo.setDisable(true);
        yearCombo.setDisable(true);
        monthCombo.setDisable(true);
        expenseDatePicker.setDisable(false);
        expenseDatePicker.setValue(nowDate);
        loadLabelSelected(selected);
    }
    
     private void loadLabelSelected(String selected){
        switch(selected){
            case "Master" : lblReportType.setText("Expenses Master");break;
            case "Monthly" : lblReportType.setText("Expenses Monthly");break;
            case "Daily" : lblReportType.setText("Expenses Daily");break;
            }
    }
    
    @FXML
    private void btnExportToWordAction(ActionEvent event) {
        switch(selected){
            case "Master" : generateMasterReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Expenses Reports\\" 
                            + yearMasterCombo.getValue() +" Expenses Master Report.docx");break;
            case "Monthly" :  generateMonthlyReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Expenses Reports\\" + monthCombo.getValue() + " " 
                            + yearCombo.getValue()+" Expenses Report.docx");break;
            case "Daily" :  generateDailyReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Expenses Reports\\" + expenseDatePicker.getValue() + " Expense report.docx");break;
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
            run.setText("Expenses Master Report | " + yearMasterCombo.getValue());
            
            //create table
            XWPFTable table = document.createTable();
            
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Expense No");
            tableRowOne.addNewTableCell().setText("Date");
            tableRowOne.addNewTableCell().setText("Category");
            tableRowOne.addNewTableCell().setText("Amount");
            
            String expenseQuery = "SELECT expenseNo,amount,category,regDate FROM "
                    + "expenses WHERE expenseNo NOT IN (SELECT expenseNo FROM expenses_del)";
            ResultSet rs1 = Query.getDataQuery(expenseQuery);
            double totalAmount = 0.0;
            while(rs1.next()){
                LocalDate regDate  = LocalDate.parse( rs1.getString(4));
                if((regDate.getYear()+"").equalsIgnoreCase(yearMasterCombo.getValue())){
                    
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(rs1.getString("expenseNo"));
                    row.getCell(1).setText(regDate.getDayOfMonth() + " " 
                            + regDate.getMonth().name() + ", " + regDate.getYear());
                    row.getCell(2).setText(rs1.getString(3));
                    double amount = rs1.getDouble(2);
                    row.getCell(3).setText(NumberFormat.getInstance().format(amount));
                    totalAmount = totalAmount + amount;
                }
            }
             XWPFTableRow row = table.createRow();
            row.getCell(0).setText("");
            row.getCell(1).setText("");
            row.getCell(2).setText("Total");
            row.getCell(3).setText(NumberFormat.getInstance().format(totalAmount));
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generateMonthlyReport(String filename){
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
            run.setText("Expenses Month Report | " + monthCombo.getValue() + " " 
                    + yearCombo.getValue());
            
            //create table
            XWPFTable table = document.createTable();
        
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Expense No");
            tableRowOne.addNewTableCell().setText("Date");
            tableRowOne.addNewTableCell().setText("Category");
            tableRowOne.addNewTableCell().setText("Amount");
            
            String expenseQuery = "SELECT expenseNo,amount,category,regDate FROM "
                    + "expenses WHERE expenseNo NOT IN (SELECT expenseNo FROM expenses_del)";
            ResultSet rs1 = Query.getDataQuery(expenseQuery);
            double totalAmount = 0.0;
            while(rs1.next()){
                LocalDate regDate  = LocalDate.parse( rs1.getString(4));
                if((regDate.getYear()+"").equalsIgnoreCase(yearCombo.getValue())
                        && regDate.getMonth().name().equalsIgnoreCase(monthCombo.getValue())){
                    
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(rs1.getString("expenseNo"));
                    row.getCell(1).setText(regDate.getDayOfMonth() + " " 
                            + regDate.getMonth().name() + ", " + regDate.getYear());
                    row.getCell(2).setText(rs1.getString(3));
                    double amount = rs1.getDouble(2);
                    row.getCell(3).setText(NumberFormat.getInstance().format(amount));
                    totalAmount = totalAmount + amount;
                }
            }
             XWPFTableRow row = table.createRow();
            row.getCell(0).setText("");
            row.getCell(1).setText("");
            row.getCell(2).setText("Total");
            row.getCell(3).setText(NumberFormat.getInstance().format(totalAmount));
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    private void generateDailyReport(String filename){
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
            run.setText("Expenses Daily Report | " + expenseDatePicker.getValue() + "");
            
            //create table
            XWPFTable table = document.createTable();
        
            //create first row
            XWPFTableRow tableRowOne = table.getRow(0);
            tableRowOne.getCell(0).setText("Expense No");
            tableRowOne.addNewTableCell().setText("Date");
            tableRowOne.addNewTableCell().setText("Category");
            tableRowOne.addNewTableCell().setText("Amount");
            
            String expenseQuery = "SELECT expenseNo,amount,category,regDate FROM "
                    + "expenses WHERE expenseNo NOT IN (SELECT expenseNo FROM expenses_del)";
            ResultSet rs1 = Query.getDataQuery(expenseQuery);
            double totalAmount = 0.0;
            while(rs1.next()){
                LocalDate regDate  = LocalDate.parse( rs1.getString(4));
                
                if(regDate.toString().equalsIgnoreCase(expenseDatePicker.getValue().toString())){
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(rs1.getString("expenseNo"));
                    row.getCell(1).setText(regDate.getDayOfMonth() + " " 
                            + regDate.getMonth().name() + ", " + regDate.getYear());
                    row.getCell(2).setText(rs1.getString(3));
                    double amount = rs1.getDouble(2);
                    row.getCell(3).setText(NumberFormat.getInstance().format(amount));
                    totalAmount = totalAmount + amount;
                }
                
            }
             XWPFTableRow row = table.createRow();
            row.getCell(0).setText("");
            row.getCell(1).setText("");
            row.getCell(2).setText("Total");
            row.getCell(3).setText(NumberFormat.getInstance().format(totalAmount));
            document.write(out);
            out.close();
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
   
}
