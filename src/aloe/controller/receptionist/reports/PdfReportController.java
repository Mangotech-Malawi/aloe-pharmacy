/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.receptionist.reports;

import static aloe.controller.LoginController.username;
import aloe.controller.pharmacist.stock.reports.ReportGeneratorController;
import aloe.model.HeaderFooterPageEvent;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class PdfReportController implements Initializable {

   
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
    ObservableList<String> monthList = FXCollections.observableArrayList();
    ObservableList<String> yearList = FXCollections.observableArrayList();
    private String selected = "Master";
    @FXML
    private JFXRadioButton radMonthly;
    @FXML
    private JFXRadioButton radDaily;
    LocalDate nowDate = LocalDate.now();
    @FXML
    private JFXButton btnExportToPDF;
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

    
    private void generateMasterReport(String filename){
            try{
            //Connecting to database 
            QueryManager Query = new QueryManager();
            
            Document document = new Document(PageSize.A4, 36, 36, 90, 90);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            
            // add header and footer
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            // write to document
            document.open();
            LocalDate date  =  LocalDate.now();
            String formatedDate = date.getDayOfMonth() + " " + 
                    date.getMonth().name() + ", " + date.getYear();
             Font font = FontFactory.getFont("Times-Roman", 16, Font.BOLD);
            document.add(new Paragraph("MASTER SALES REPORT | " + yearMasterCombo.getValue(),font));
            
            PdfPTable table = new PdfPTable(6);
            float[] widths = { 60f, 100f, 100f, 100f, 60f, 100f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            Font font3 = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            
            PdfPCell expenseNoCol = new PdfPCell(new Paragraph("Sale No.",font1));
            table.addCell(expenseNoCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Drug Name",font1));
            table.addCell(nameCol).setBorder(0);
            
            PdfPCell packEntryCol = new PdfPCell(new Paragraph("Pack/Entry",font1));
            table.addCell(packEntryCol).setBorder(0);
            
            PdfPCell dateCol = new PdfPCell(new Paragraph("Date",font1));
            table.addCell(dateCol).setBorder(0);
                 
            PdfPCell quantityCol = new PdfPCell(new Paragraph("Quantity",font1));
            table.addCell(quantityCol).setBorder(0);
         
            PdfPCell totalChargeCol = new PdfPCell(new Paragraph("Charge",font1));
            table.addCell(totalChargeCol).setBorder(0);
       
       
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(6);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            
            
            String salesQuery = "SELECT * FROM sales";
            ResultSet rs1 = Query.getDataQuery(salesQuery);
            double totalCharge = 0.0;
            while(rs1.next()){
                int transNo = rs1.getInt("transNo");
                String transQuery = "SELECT trans_date FROM transactions"
                        + " WHERE transNo ='" + transNo + "' and trans_by ='" + username + "';";
                ResultSet rs2 = Query.getDataQuery(transQuery);
                if(rs2.next()){
                    LocalDateTime regDate  = LocalDateTime.parse( rs2.getString(1));
                    if((regDate.getYear()+"").equalsIgnoreCase(yearMasterCombo.getValue())){
                        String itemType = rs1.getString("item");
                        if(itemType.equalsIgnoreCase("Pack")){
                            int packId = rs1.getInt("id");
                            String packQuery = "SELECT batchNo FROM packs WHERE packId ='" + packId +"'";
                            ResultSet rs3 = Query.getDataQuery(packQuery);
                            if(rs3.next()){
                                int batchNo = rs3.getInt(1);
                                String entriesQuery = "SELECT id FROM entries WHERE batchNo ='" + batchNo +"'";
                                ResultSet rs4 = Query.getDataQuery(entriesQuery);
                                if(rs4.next()){
                                    String id = rs4.getString(1);
                                    String drugsQuery = "SELECT name FROM drugs WHERE id ='" + id + "'";
                                    ResultSet rs5 = Query.getDataQuery(drugsQuery);
                                    if(rs5.next()){
                                        String name = rs5.getString(1);
                                         
                                        PdfPCell saleNoCell = new PdfPCell(new Paragraph(rs1.getString("saleNo"),font2));
                                        table.addCell(saleNoCell).setBorder(0);
                                         
                                        PdfPCell nameCell = new PdfPCell(new Paragraph(name,font2));
                                        table.addCell(nameCell).setBorder(0);
                                        
                                        PdfPCell itemCell = new PdfPCell(new Paragraph(rs1.getString("item"),font2));
                                        table.addCell(itemCell).setBorder(0);
                                         
                                        PdfPCell dateCell = new PdfPCell(new Paragraph(regDate.getDayOfMonth() + " " 
                                                        + regDate.getMonth().name() + ", " + regDate.getYear(),font2));
                                        table.addCell(dateCell).setBorder(0);
                                        
                                        PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                                        table.addCell(quantityCell).setBorder(0);
                                        
                                        double charge = rs1.getDouble("charge");
                                            
                                        PdfPCell chargeCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().format(charge),font2));
                                        table.addCell(chargeCell).setBorder(0);
                                        
                                        totalCharge = totalCharge + charge;
                                       
                                    }
                                }
                            }
                        }else{
                                int batchNo = rs1.getInt("id");
                                String entriesQuery = "SELECT id FROM entries WHERE batchNo ='" + batchNo +"'";
                                ResultSet rs3 = Query.getDataQuery(entriesQuery);
                                if(rs3.next()){
                                    String id = rs3.getString(1);
                                    String drugsQuery = "SELECT name FROM drugs WHERE id ='" + id + "'";
                                    ResultSet rs4 = Query.getDataQuery(drugsQuery);
                                    if(rs4.next()){
                                        String name = rs4.getString(1);
                                         
                                        PdfPCell saleNoCell = new PdfPCell(new Paragraph(rs1.getString("saleNo"),font2));
                                        table.addCell(saleNoCell).setBorder(0);
                                         
                                        PdfPCell nameCell = new PdfPCell(new Paragraph(name,font2));
                                        table.addCell(nameCell).setBorder(0);
                                        
                                        PdfPCell itemCell = new PdfPCell(new Paragraph(rs1.getString("item"),font2));
                                        table.addCell(itemCell).setBorder(0);
                                         
                                        PdfPCell dateCell = new PdfPCell(new Paragraph(regDate.getDayOfMonth() + " " 
                                                        + regDate.getMonth().name() + ", " + regDate.getYear(),font2));
                                        table.addCell(dateCell).setBorder(0);
                                        
                                        PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                                        table.addCell(quantityCell).setBorder(0);
                                        
                                        double charge = rs1.getDouble("charge");
                                       
                                        PdfPCell chargeCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().format(charge),font2));
                                        table.addCell(chargeCell).setBorder(0);
                                       
                                        totalCharge = totalCharge + charge;
                                       
                                    }
                                }
                         }  
                    }
                }
               
            }
            
            PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
            spaceCell2.setColspan(4);
            table.addCell(spaceCell2).setBorder(0);
            
                 
            PdfPCell labelCell = new PdfPCell(new Paragraph("TOTAL",font3));
            table.addCell(labelCell ).setBorder(0);
            
            PdfPCell totalChargeCell = 
                    new PdfPCell(new Paragraph(NumberFormat.getInstance().format(totalCharge),font3));
            table.addCell(totalChargeCell);
             
            document.add(table);
            document.close();
        }catch (FileNotFoundException ex) {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setHeaderText("Report Generation Failed");
               alert.setContentText("File Not Found!");
               alert.show();
           } catch (DocumentException ex) {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setHeaderText("Report Generation Failed");
               alert.setContentText("Document Exception!");
               alert.show();
           } catch (SQLException ex) {
               Logger.getLogger(ReportGeneratorController.class.getName()).log(Level.SEVERE, null, ex);
           }
    }
    
    private void generateMonthlyReport(String filename){
           try{
            //Connecting to database 
            QueryManager Query = new QueryManager();
            
            Document document = new Document(PageSize.A4, 36, 36, 90, 90);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            
            // add header and footer
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            // write to document
            document.open();
            LocalDate date  =  LocalDate.now();
  
            Font font = FontFactory.getFont("Times-Roman", 16, Font.BOLD);
            document.add(new Paragraph("MONTHLY SALES REPORT | "
                    + monthCombo.getValue() + " " + yearCombo.getValue() ,font));
            
            PdfPTable table = new PdfPTable(6);
            float[] widths = { 60f, 100f, 100f, 100f, 60f, 100f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            Font font3 = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            
            PdfPCell expenseNoCol = new PdfPCell(new Paragraph("Sale No.",font1));
            table.addCell(expenseNoCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Drug Name",font1));
            table.addCell(nameCol).setBorder(0);
            
            PdfPCell packEntryCol = new PdfPCell(new Paragraph("Pack/Entry",font1));
            table.addCell(packEntryCol).setBorder(0);
            
            PdfPCell dateCol = new PdfPCell(new Paragraph("Date",font1));
            table.addCell(dateCol).setBorder(0);
                 
            PdfPCell quantityCol = new PdfPCell(new Paragraph("Quantity",font1));
            table.addCell(quantityCol).setBorder(0);
         
            PdfPCell totalChargeCol = new PdfPCell(new Paragraph("Charge",font1));
            table.addCell(totalChargeCol).setBorder(0);
       
       
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(6);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            
            
            String salesQuery = "SELECT * FROM sales";
            ResultSet rs1 = Query.getDataQuery(salesQuery);
            double totalCharge = 0.0;
            while(rs1.next()){
                int transNo = rs1.getInt("transNo");
                String transQuery = "SELECT trans_date FROM transactions"
                        + " WHERE transNo ='" + transNo + "' and trans_by ='" + username + "';";
                ResultSet rs2 = Query.getDataQuery(transQuery);
                if(rs2.next()){
                    LocalDateTime regDate  = LocalDateTime.parse( rs2.getString(1));
                    if((regDate.getYear()+"").equalsIgnoreCase(yearCombo.getValue()) 
                        && regDate.getMonth().name().equalsIgnoreCase(monthCombo.getValue())){
                        String itemType = rs1.getString("item");
                        if(itemType.equalsIgnoreCase("Pack")){
                            int packId = rs1.getInt("id");
                            String packQuery = "SELECT batchNo FROM packs WHERE packId ='" + packId +"'";
                            ResultSet rs3 = Query.getDataQuery(packQuery);
                            if(rs3.next()){
                                int batchNo = rs3.getInt(1);
                                String entriesQuery = "SELECT id FROM entries WHERE batchNo ='" + batchNo +"'";
                                ResultSet rs4 = Query.getDataQuery(entriesQuery);
                                if(rs4.next()){
                                    String id = rs4.getString(1);
                                    String drugsQuery = "SELECT name FROM drugs WHERE id ='" + id + "'";
                                    ResultSet rs5 = Query.getDataQuery(drugsQuery);
                                    if(rs5.next()){
                                        String name = rs5.getString(1);
                                         
                                        PdfPCell saleNoCell = new PdfPCell(new Paragraph(rs1.getString("saleNo"),font2));
                                        table.addCell(saleNoCell).setBorder(0);
                                         
                                        PdfPCell nameCell = new PdfPCell(new Paragraph(name,font2));
                                        table.addCell(nameCell).setBorder(0);
                                        
                                        PdfPCell itemCell = new PdfPCell(new Paragraph(rs1.getString("item"),font2));
                                        table.addCell(itemCell).setBorder(0);
                                         
                                        PdfPCell dateCell = new PdfPCell(new Paragraph(regDate.getDayOfMonth() + " " 
                                                        + regDate.getMonth().name() + ", " + regDate.getYear(),font2));
                                        table.addCell(dateCell).setBorder(0);
                                        
                                        PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                                        table.addCell(quantityCell).setBorder(0);
                                        
                                        double charge = rs1.getDouble("charge");
                                            
                                        PdfPCell chargeCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().format(charge),font2));
                                        table.addCell(chargeCell).setBorder(0);
                                        
                                        totalCharge = totalCharge + charge;
                                       
                                    }
                                }
                            }
                        }else{
                                int batchNo = rs1.getInt("id");
                                String entriesQuery = "SELECT id FROM entries WHERE batchNo ='" + batchNo +"'";
                                ResultSet rs3 = Query.getDataQuery(entriesQuery);
                                if(rs3.next()){
                                    String id = rs3.getString(1);
                                    String drugsQuery = "SELECT name FROM drugs WHERE id ='" + id + "'";
                                    ResultSet rs4 = Query.getDataQuery(drugsQuery);
                                    if(rs4.next()){
                                        String name = rs4.getString(1);
                                         
                                        PdfPCell saleNoCell = new PdfPCell(new Paragraph(rs1.getString("saleNo"),font2));
                                        table.addCell(saleNoCell).setBorder(0);
                                         
                                        PdfPCell nameCell = new PdfPCell(new Paragraph(name,font2));
                                        table.addCell(nameCell).setBorder(0);
                                        
                                        PdfPCell itemCell = new PdfPCell(new Paragraph(rs1.getString("item"),font2));
                                        table.addCell(itemCell).setBorder(0);
                                         
                                        PdfPCell dateCell = new PdfPCell(new Paragraph(regDate.getDayOfMonth() + " " 
                                                        + regDate.getMonth().name() + ", " + regDate.getYear(),font2));
                                        table.addCell(dateCell).setBorder(0);
                                        
                                        PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                                        table.addCell(quantityCell).setBorder(0);
                                        
                                        double charge = rs1.getDouble("charge");
                                       
                                        PdfPCell chargeCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().format(charge),font2));
                                        table.addCell(chargeCell).setBorder(0);
                                       
                                        totalCharge = totalCharge + charge;
                                       
                                    }
                                }
                         }  
                    }
                }
               
            }
            
            PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
            spaceCell2.setColspan(4);
            table.addCell(spaceCell2).setBorder(0);
            
                 
            PdfPCell labelCell = new PdfPCell(new Paragraph("TOTAL",font3));
            table.addCell(labelCell ).setBorder(0);
            
            PdfPCell totalChargeCell = 
                    new PdfPCell(new Paragraph(NumberFormat.getInstance().format(totalCharge),font3));
            table.addCell(totalChargeCell);
             
            document.add(table);
            document.close();
        }catch (FileNotFoundException ex) {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setHeaderText("Report Generation Failed");
               alert.setContentText("File Not Found!");
               alert.show();
           } catch (DocumentException ex) {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setHeaderText("Report Generation Failed");
               alert.setContentText("Document Exception!");
               alert.show();
           } catch (SQLException ex) {
               Logger.getLogger(ReportGeneratorController.class.getName()).log(Level.SEVERE, null, ex);
           }
    }
    
    private void generateDailyReport(String filename){
            try{
            //Connecting to database 
            QueryManager Query = new QueryManager();
            
            Document document = new Document(PageSize.A4, 36, 36, 90, 90);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            
            // add header and footer
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            // write to document
            document.open();
            LocalDate date  =  LocalDate.now();
  
            Font font = FontFactory.getFont("Times-Roman", 16, Font.BOLD);
            document.add(new Paragraph("MONTHLY SALES REPORT | "
                    + monthCombo.getValue() + " " + yearCombo.getValue() ,font));
            
            PdfPTable table = new PdfPTable(6);
            float[] widths = { 60f, 100f, 100f, 100f, 60f, 100f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            Font font3 = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            
            PdfPCell expenseNoCol = new PdfPCell(new Paragraph("Sale No.",font1));
            table.addCell(expenseNoCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Drug Name",font1));
            table.addCell(nameCol).setBorder(0);
            
            PdfPCell packEntryCol = new PdfPCell(new Paragraph("Pack/Entry",font1));
            table.addCell(packEntryCol).setBorder(0);
            
            PdfPCell dateCol = new PdfPCell(new Paragraph("Date",font1));
            table.addCell(dateCol).setBorder(0);
                 
            PdfPCell quantityCol = new PdfPCell(new Paragraph("Quantity",font1));
            table.addCell(quantityCol).setBorder(0);
         
            PdfPCell totalChargeCol = new PdfPCell(new Paragraph("Charge",font1));
            table.addCell(totalChargeCol).setBorder(0);
       
       
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(6);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            
            
            String salesQuery = "SELECT * FROM sales";
            ResultSet rs1 = Query.getDataQuery(salesQuery);
            double totalCharge = 0.0;
            while(rs1.next()){
                int transNo = rs1.getInt("transNo");
                String transQuery = "SELECT trans_date FROM transactions"
                        + " WHERE transNo ='" + transNo + "' and trans_by ='" + username + "';";
                ResultSet rs2 = Query.getDataQuery(transQuery);
                if(rs2.next()){
                    LocalDateTime regDate  = LocalDateTime.parse( rs2.getString(1));
                    if(regDate.toLocalDate().toString().equalsIgnoreCase(expenseDatePicker.getValue().toString())){
                        String itemType = rs1.getString("item");
                        if(itemType.equalsIgnoreCase("Pack")){
                            int packId = rs1.getInt("id");
                            String packQuery = "SELECT batchNo FROM packs WHERE packId ='" + packId +"'";
                            ResultSet rs3 = Query.getDataQuery(packQuery);
                            if(rs3.next()){
                                int batchNo = rs3.getInt(1);
                                String entriesQuery = "SELECT id FROM entries WHERE batchNo ='" + batchNo +"'";
                                ResultSet rs4 = Query.getDataQuery(entriesQuery);
                                if(rs4.next()){
                                    String id = rs4.getString(1);
                                    String drugsQuery = "SELECT name FROM drugs WHERE id ='" + id + "'";
                                    ResultSet rs5 = Query.getDataQuery(drugsQuery);
                                    if(rs5.next()){
                                        String name = rs5.getString(1);
                                         
                                        PdfPCell saleNoCell = new PdfPCell(new Paragraph(rs1.getString("saleNo"),font2));
                                        table.addCell(saleNoCell).setBorder(0);
                                         
                                        PdfPCell nameCell = new PdfPCell(new Paragraph(name,font2));
                                        table.addCell(nameCell).setBorder(0);
                                        
                                        PdfPCell itemCell = new PdfPCell(new Paragraph(rs1.getString("item"),font2));
                                        table.addCell(itemCell).setBorder(0);
                                         
                                        PdfPCell dateCell = new PdfPCell(new Paragraph(regDate.getDayOfMonth() + " " 
                                                        + regDate.getMonth().name() + ", " + regDate.getYear(),font2));
                                        table.addCell(dateCell).setBorder(0);
                                        
                                        PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                                        table.addCell(quantityCell).setBorder(0);
                                        
                                        double charge = rs1.getDouble("charge");
                                            
                                        PdfPCell chargeCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().format(charge),font2));
                                        table.addCell(chargeCell).setBorder(0);
                                        
                                        totalCharge = totalCharge + charge;
                                       
                                    }
                                }
                            }
                        }else{
                                int batchNo = rs1.getInt("id");
                                String entriesQuery = "SELECT id FROM entries WHERE batchNo ='" + batchNo +"'";
                                ResultSet rs3 = Query.getDataQuery(entriesQuery);
                                if(rs3.next()){
                                    String id = rs3.getString(1);
                                    String drugsQuery = "SELECT name FROM drugs WHERE id ='" + id + "'";
                                    ResultSet rs4 = Query.getDataQuery(drugsQuery);
                                    if(rs4.next()){
                                        String name = rs4.getString(1);
                                         
                                        PdfPCell saleNoCell = new PdfPCell(new Paragraph(rs1.getString("saleNo"),font2));
                                        table.addCell(saleNoCell).setBorder(0);
                                         
                                        PdfPCell nameCell = new PdfPCell(new Paragraph(name,font2));
                                        table.addCell(nameCell).setBorder(0);
                                        
                                        PdfPCell itemCell = new PdfPCell(new Paragraph(rs1.getString("item"),font2));
                                        table.addCell(itemCell).setBorder(0);
                                         
                                        PdfPCell dateCell = new PdfPCell(new Paragraph(regDate.getDayOfMonth() + " " 
                                                        + regDate.getMonth().name() + ", " + regDate.getYear(),font2));
                                        table.addCell(dateCell).setBorder(0);
                                        
                                        PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                                        table.addCell(quantityCell).setBorder(0);
                                        
                                        double charge = rs1.getDouble("charge");
                                       
                                        PdfPCell chargeCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().format(charge),font2));
                                        table.addCell(chargeCell).setBorder(0);
                                       
                                        totalCharge = totalCharge + charge;
                                       
                                    }
                                }
                         }  
                    }
                }
               
            }
            
            PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
            spaceCell2.setColspan(4);
            table.addCell(spaceCell2).setBorder(0);
            
                 
            PdfPCell labelCell = new PdfPCell(new Paragraph("TOTAL",font3));
            table.addCell(labelCell ).setBorder(0);
            
            PdfPCell totalChargeCell = 
                    new PdfPCell(new Paragraph(NumberFormat.getInstance().format(totalCharge),font3));
            table.addCell(totalChargeCell);
             
            document.add(table);
            document.close();
        }catch (FileNotFoundException ex) {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setHeaderText("Report Generation Failed");
               alert.setContentText("File Not Found!");
               alert.show();
           } catch (DocumentException ex) {
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setHeaderText("Report Generation Failed");
               alert.setContentText("Document Exception!");
               alert.show();
           } catch (SQLException ex) {
               Logger.getLogger(ReportGeneratorController.class.getName()).log(Level.SEVERE, null, ex);
           }   
    }

    @FXML
    private void btnExportToPDFAction(ActionEvent event) {
        switch(selected){
            case "Master" : generateMasterReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Sales Reports\\" 
                            + yearMasterCombo.getValue() +" My Sales Master Report.pdf");break;
            case "Monthly" :  generateMonthlyReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Sales Reports\\" + monthCombo.getValue() + " " 
                            + yearCombo.getValue()+" My Sales Report.pdf");break;
            case "Daily" :  generateDailyReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Sales Reports\\" + expenseDatePicker.getValue() + " My Sales Report.pdf");break;
        } 
    }
    

  
}
