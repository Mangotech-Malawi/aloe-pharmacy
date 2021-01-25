/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock.reports;

import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
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
public class ExcelReportGeneratorController implements Initializable {

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
    private JFXButton btnExportToExcel;
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
    private void btnExportToExcelAction(ActionEvent event) {
         switch(selected){
            case "Master" : generateMasterReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Details Master Report.xls");break;
            case "Prices" :  generatePricesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Prices Report.xls");break;
            case "Categories" :  generateCategoriesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Categories Report.xls");break;
            case "Description" :  generateDescriptionReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drugs Description Report.xls");break;
            case "Names" : generateNamesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Names Report.xls");break;
        }
    }
    
    private void generateMasterReport(String filePath){
        try{
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("New Sheet");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("Id");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("First Category");
            rowhead.createCell((short)3).setCellValue("Second Category");
            rowhead.createCell((short)4).setCellValue("Order Price");
            rowhead.createCell((short)5).setCellValue("Selling Price");
            rowhead.createCell((short)6).setCellValue("Unit Of Measurement");
             rowhead.createCell((short)7).setCellValue("Description");
            
            String drugQuery = "SELECT * FROM drugs WHERE id "
                    + "NOT IN (SELECT id FROM drugs_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(drugQuery);
            while(rs.next()){
                HSSFRow row = sheet.createRow((short)i);
                row.createCell((short)0).setCellValue(rs.getString(1));
                row.createCell((short)1).setCellValue(rs.getString(2));
                row.createCell((short)2).setCellValue(rs.getString(3));
                row.createCell((short)3).setCellValue(rs.getString(4));
                row.createCell((short)4).setCellValue(rs.getDouble(6));
                row.createCell((short)5).setCellValue(rs.getDouble(5));
                row.createCell((short)6).setCellValue(rs.getString(7));
                row.createCell((short)7).setCellValue(rs.getString(8)); 
                i++;        
            }
            FileOutputStream fileOut = new FileOutputStream(filePath);
            hwb.write(fileOut);
            fileOut.close();
            
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    private void generatePricesReport(String filePath){
          try{
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Prices");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("Id");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("Order Price");
            rowhead.createCell((short)3).setCellValue("Selling Price");       
            String drugQuery = "SELECT id,name,orderPrice,sellingPrice FROM drugs WHERE id "
                    + "NOT IN (SELECT id FROM drugs_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(drugQuery);
            while(rs.next()){
                HSSFRow row = sheet.createRow((short)i);
                row.createCell((short)0).setCellValue(rs.getString(1));
                row.createCell((short)1).setCellValue(rs.getString(2));
                row.createCell((short)2).setCellValue(rs.getDouble(3));
                row.createCell((short)3).setCellValue(rs.getDouble(4));
           
                i++;        
            }
            FileOutputStream fileOut = new FileOutputStream(filePath);
            hwb.write(fileOut);
            fileOut.close();
            
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    private void generateCategoriesReport(String filePath){
         try{
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Categories");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("Id");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("First Category");
            rowhead.createCell((short)3).setCellValue("Second Category");       
            String drugQuery = "SELECT id,name,firstCategory,secCategory FROM drugs WHERE id "
                    + "NOT IN (SELECT id FROM drugs_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(drugQuery);
            while(rs.next()){
                HSSFRow row = sheet.createRow((short)i);
                row.createCell((short)0).setCellValue(rs.getString(1));
                row.createCell((short)1).setCellValue(rs.getString(2));
                row.createCell((short)2).setCellValue(rs.getString(3));
                row.createCell((short)3).setCellValue(rs.getString(4));
                i++;        
            }
            FileOutputStream fileOut = new FileOutputStream(filePath);
            hwb.write(fileOut);
            fileOut.close();
            
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    private void generateDescriptionReport(String filePath){
          try{
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Categories");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("Id");
            rowhead.createCell((short)1).setCellValue("Name");
            rowhead.createCell((short)2).setCellValue("Description");
         
            String drugQuery = "SELECT id,name,description FROM drugs WHERE id "
                    + "NOT IN (SELECT id FROM drugs_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(drugQuery);
            while(rs.next()){
                HSSFRow row = sheet.createRow((short)i);
                row.createCell((short)0).setCellValue(rs.getString(1));
                row.createCell((short)1).setCellValue(rs.getString(2));
                row.createCell((short)2).setCellValue(rs.getString(3));
                i++;        
            }
            FileOutputStream fileOut = new FileOutputStream(filePath);
            hwb.write(fileOut);
            fileOut.close();
            
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
    private void generateNamesReport(String filePath){
          try{
            QueryManager Query = new QueryManager();
          
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Drug Names");
            
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell((short)0).setCellValue("Id");
            rowhead.createCell((short)1).setCellValue("Name");
           
            String drugQuery = "SELECT id,name FROM drugs WHERE id "
                    + "NOT IN (SELECT id FROM drugs_del)";
            int i = 1;
            ResultSet rs = Query.getDataQuery(drugQuery);
            while(rs.next()){
                HSSFRow row = sheet.createRow((short)i);
                row.createCell((short)0).setCellValue(rs.getString(1));
                row.createCell((short)1).setCellValue(rs.getString(2));
                i++;        
            }
            FileOutputStream fileOut = new FileOutputStream(filePath);
            hwb.write(fileOut);
            fileOut.close();
            
        }catch(IOException | SQLException ex){
            System.out.println(ex);
        }
    }
}
