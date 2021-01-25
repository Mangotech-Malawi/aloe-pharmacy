/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock.reports;

import aloe.controller.pharmacist.stock.ViewEntriesController;
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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class EntryPDFReportsController implements Initializable {

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
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Master Report.pdf");break;
            case "Entries" :  generateEntriesQuantityReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Quantity Report.pdf");break;
            case "EntriesTotal" :  generateTotalEntriesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Total Report.pdf");break;
            case "Expiry" :  generateExpiryReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Entries Expiry Report.pdf");break;
        }
    }
    
    private void generateMasterReport(String filename){
         try {
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
            document.add(new Paragraph("DRUG ENTRIES MASTER REPORT | " + formatedDate,font));
            
            PdfPTable table = new PdfPTable(7);
            float[] widths = { 60f, 90f, 60, 60f, 90f, 90f, 120f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            Font font3 = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            
            PdfPCell batchNoCol = new PdfPCell(new Paragraph("Batch No.",font1));
            table.addCell(batchNoCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
            table.addCell(nameCol).setBorder(0);
                 
            PdfPCell daysToExpiryCol = new PdfPCell(new Paragraph("Condition",font1));
            table.addCell(daysToExpiryCol).setBorder(0);
            
            PdfPCell orderPrice = new PdfPCell(new Paragraph( "Order Price",font1));
            table.addCell(orderPrice).setBorder(0);
            
            PdfPCell sellPriceCol = new PdfPCell(new Paragraph( "Sell Price",font1));
            table.addCell(sellPriceCol).setBorder(0);
        
            PdfPCell quantityCol = new PdfPCell(new Paragraph("Quantity",font1));
            table.addCell(quantityCol).setBorder(0);
            
            PdfPCell totalPriceCol = new PdfPCell(new Paragraph( "Total Price",font1));
            table.addCell(totalPriceCol).setBorder(0);
            
       
            
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(7);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            QueryManager Query = new QueryManager();
            String entriesQuery = "SELECT * FROM entries WHERE batchNo NOT IN (SELECT batchNo FROM entries_del)";
            ResultSet rs1 = Query.getDataQuery(entriesQuery);
            double allTotalPrice = 0;
            int totalQty = 0;
            while(rs1.next()){
                
               int id = rs1.getInt("id");
               String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
               ResultSet rs2 = Query.getDataQuery(drugQuery);
               if(rs2.next()){
                   
                    PdfPCell batchNoCell = new PdfPCell(new Paragraph(rs1.getString("batchNo"),font2));
                    table.addCell(batchNoCell).setBorder(0);
                    
                    PdfPCell nameCell = new PdfPCell(new Paragraph(rs2.getString("name"),font2));
                    table.addCell(nameCell).setBorder(0);
                    
                    LocalDate nowDate = LocalDate.now();
                    LocalDate expiryDate = LocalDate.parse(rs1.getString("expiryDate"));
                    
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "") - Integer.parseInt(nowDate.toEpochDay() + "") ;
                    PdfPCell daysToExpiryCell = new PdfPCell(new Paragraph(getCondition(Query,daysToExpiry),font2));
                    table.addCell(daysToExpiryCell).setBorder(0);
                    
       
                    PdfPCell orderPriceCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().
                    format(rs2.getDouble("orderPrice")),font2));
                    table.addCell(orderPriceCell).setBorder(0);
                    
                    double sellingPrice = rs2.getDouble("sellingPrice");
                    PdfPCell sellPriceCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().
                            format(rs2.getDouble("sellingPrice")),font2));
                    table.addCell(sellPriceCell).setBorder(0);
                    
                    int quantity = rs1.getInt("quantity");
                    
                    //Calculating price of the whole quantity of entry
                    double totalPrice =  sellingPrice * quantity;
                    
                    //Adding the whole total prices of entries
                    allTotalPrice = allTotalPrice + totalPrice;
                    
                    //AAdding the whole quantities of entries
                    totalQty = totalQty + quantity;
                    
                    PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                    table.addCell(quantityCell).setBorder(0);
                       
                    PdfPCell totalPriceCell = new PdfPCell(new Paragraph("" + NumberFormat.getInstance().
                            format(totalPrice)  ,font2));
                    table.addCell(totalPriceCell).setBorder(0);
                   
                    PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                    spaceCell2.setColspan(7);
                    table.addCell(spaceCell2).setBorder(0);
               }
               
            }
            
            PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
            spaceCell2.setColspan(4);
            table.addCell(spaceCell2).setBorder(0);
            
                 
            PdfPCell labelCell = new PdfPCell(new Paragraph("TOTAL",font));
            table.addCell(labelCell ).setBorder(0);
            
            PdfPCell totalQtyCell = new PdfPCell(new Paragraph("" + totalQty,font3));
            table.addCell(totalQtyCell );
            
            PdfPCell totalPriceCell = new PdfPCell(new Paragraph(NumberFormat.getCurrencyInstance()
                    .format(allTotalPrice),font3));
            table.addCell(totalPriceCell);
            
            document.add(table);
            document.close();
        } catch (FileNotFoundException ex) {
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
    
    
    private void generateEntriesQuantityReport(String filename){
                try {
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
               document.add(new Paragraph("DRUG ENTRIES QUANTITY | " + formatedDate,font));

               PdfPTable table = new PdfPTable(4);
               float[] widths = { 100f, 100f, 100f, 100f};
               table.setTotalWidth(widths);
               table.setLockedWidth(true);

               Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
               Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
               Font font3 = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
               BaseFont bf = font1.getCalculatedBaseFont(false);

               PdfPCell batchNoCol = new PdfPCell(new Paragraph("Batch No.",font1));
               table.addCell(batchNoCol).setBorder(0);

               PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
               table.addCell(nameCol).setBorder(0);

               PdfPCell daysToExpiryCol = new PdfPCell(new Paragraph("Condition",font1));
               table.addCell(daysToExpiryCol).setBorder(0);

               PdfPCell quantityCol = new PdfPCell(new Paragraph("Quantity",font1));
               table.addCell(quantityCol).setBorder(0);

               PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
               spaceCell1.setColspan(4);
               table.addCell(spaceCell1).setBorder(0);

               table.setSpacingBefore(15f);

               table.setSpacingAfter(50f);

               table.setHeaderRows(1);
               QueryManager Query = new QueryManager();
               String entriesQuery = "SELECT * FROM entries WHERE batchNo NOT IN (SELECT batchNo FROM entries_del)";
               ResultSet rs1 = Query.getDataQuery(entriesQuery);
               int totalQty = 0;
               while(rs1.next()){

                  int id = rs1.getInt("id");
                  String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
                  ResultSet rs2 = Query.getDataQuery(drugQuery);
                  if(rs2.next()){

                       PdfPCell batchNoCell = new PdfPCell(new Paragraph(rs1.getString("batchNo"),font2));
                       table.addCell(batchNoCell).setBorder(0);

                       PdfPCell nameCell = new PdfPCell(new Paragraph(rs2.getString("name"),font2));
                       table.addCell(nameCell).setBorder(0);

                       LocalDate nowDate = LocalDate.now();
                       LocalDate expiryDate = LocalDate.parse(rs1.getString("expiryDate"));

                       int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "") - Integer.parseInt(nowDate.toEpochDay() + "") ;
                       PdfPCell daysToExpiryCell = new PdfPCell(new Paragraph(getCondition(Query,daysToExpiry),font2));
                       table.addCell(daysToExpiryCell).setBorder(0);

                       int quantity = rs1.getInt("quantity");
                       totalQty = totalQty + quantity;

                       PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                       table.addCell(quantityCell).setBorder(0);

                       PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                       spaceCell2.setColspan(7);
                       table.addCell(spaceCell2).setBorder(0);
                  }

               }

               PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
               spaceCell2.setColspan(2);
               table.addCell(spaceCell2).setBorder(0);


               PdfPCell labelCell = new PdfPCell(new Paragraph("TOTAL",font));
               table.addCell(labelCell ).setBorder(0);

               PdfPCell totalQtyCell = new PdfPCell(new Paragraph("" + totalQty,font3));
               table.addCell(totalQtyCell );

               document.add(table);
               document.close();
           } catch (FileNotFoundException ex) {
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
    
    private void generateTotalEntriesReport(String filename){
              try {
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
               document.add(new Paragraph("DRUG ENTRIES TOTAL QUANTITY | " + formatedDate,font));

               PdfPTable table = new PdfPTable(3);
               float[] widths = { 120f, 120f , 120f};
               table.setTotalWidth(widths);
               table.setLockedWidth(true);

               Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
               Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
               Font font3 = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
               BaseFont bf = font1.getCalculatedBaseFont(false);

               PdfPCell idCol = new PdfPCell(new Paragraph("Id No.",font1));
               table.addCell(idCol).setBorder(0);

               PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
               table.addCell(nameCol).setBorder(0);

         
               PdfPCell quantityCol = new PdfPCell(new Paragraph("Quantity",font1));
               table.addCell(quantityCol).setBorder(0);

               PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
               spaceCell1.setColspan(3);
               table.addCell(spaceCell1).setBorder(0);

               table.setSpacingBefore(15f);

               table.setSpacingAfter(50f);

               table.setHeaderRows(1);
               QueryManager Query = new QueryManager();
               String entriesQuery = "SELECT id,batchNo,SUM(quantity),expiryDate FROM entries "
                       + "WHERE batchNo NOT IN (SELECT batchNo FROM entries_del) GROUP BY id ";
               ResultSet rs1 = Query.getDataQuery(entriesQuery);
               int totalQty = 0;
               while(rs1.next()){
                  int id = rs1.getInt("id");
                  String drugQuery = "SELECT name FROM drugs where id ='" + id + "';";
                  ResultSet rs2 = Query.getDataQuery(drugQuery);
                  if(rs2.next()){

                       PdfPCell idCell = new PdfPCell(new Paragraph(rs1.getString("id"),font2));
                       table.addCell(idCell).setBorder(0);

                       PdfPCell nameCell = new PdfPCell(new Paragraph(rs2.getString("name"),font2));
                       table.addCell(nameCell).setBorder(0);

                       int quantity = rs1.getInt(3);
                       totalQty = totalQty + quantity;

                       PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString(3),font2));
                       table.addCell(quantityCell).setBorder(0);

                       PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                       spaceCell2.setColspan(3);
                       table.addCell(spaceCell2).setBorder(0);
                  }

               }

               PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
               table.addCell(spaceCell2).setBorder(0);


               PdfPCell labelCell = new PdfPCell(new Paragraph("TOTAL",font));
               table.addCell(labelCell ).setBorder(0);

               PdfPCell totalQtyCell = new PdfPCell(new Paragraph("" + totalQty,font3));
               table.addCell(totalQtyCell );

               document.add(table);
               document.close();
           } catch (FileNotFoundException ex) {
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
    
    private void generateExpiryReport(String filename){
           try {
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
            document.add(new Paragraph("EXPIRED DRUGS REPORT | " + formatedDate,font));
            
            PdfPTable table = new PdfPTable(6);
            float[] widths = { 60f, 90f,60f, 90f, 90f, 120f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            Font font3 = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            
            PdfPCell batchNoCol = new PdfPCell(new Paragraph("Batch No.",font1));
            table.addCell(batchNoCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
            table.addCell(nameCol).setBorder(0);
                     
            PdfPCell orderPrice = new PdfPCell(new Paragraph( "Order Price",font1));
            table.addCell(orderPrice).setBorder(0);
            
            PdfPCell sellPriceCol = new PdfPCell(new Paragraph( "Sell Price",font1));
            table.addCell(sellPriceCol).setBorder(0);
        
            PdfPCell quantityCol = new PdfPCell(new Paragraph("Quantity",font1));
            table.addCell(quantityCol).setBorder(0);
            
            PdfPCell totalPriceCol = new PdfPCell(new Paragraph( "Total Price",font1));
            table.addCell(totalPriceCol).setBorder(0);
            
       
            
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(6);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            QueryManager Query = new QueryManager();
            String entriesQuery = "SELECT * FROM entries WHERE batchNo NOT IN (SELECT batchNo FROM entries_del)";
            ResultSet rs1 = Query.getDataQuery(entriesQuery);
            double allTotalPrice = 0;
            int totalQty = 0;
            while(rs1.next()){
                
               int id = rs1.getInt("id");
               String drugQuery = "SELECT name,sellingPrice,orderPrice FROM drugs where id ='" + id + "';";
               ResultSet rs2 = Query.getDataQuery(drugQuery);
               if(rs2.next()){
                   
                    
                    LocalDate nowDate = LocalDate.now();
                    LocalDate expiryDate = LocalDate.parse(rs1.getString("expiryDate"));
                    int daysToExpiry = Integer.parseInt(expiryDate.toEpochDay() + "") - Integer.parseInt(nowDate.toEpochDay() + "") ;
                    
                    if(getCondition(Query,daysToExpiry).equalsIgnoreCase("worse")){
                            PdfPCell batchNoCell = new PdfPCell(new Paragraph(rs1.getString("batchNo"),font2));
                            table.addCell(batchNoCell).setBorder(0);

                            PdfPCell nameCell = new PdfPCell(new Paragraph(rs2.getString("name"),font2));
                            table.addCell(nameCell).setBorder(0);

                            PdfPCell orderPriceCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().
                            format(rs2.getDouble("orderPrice")),font2));
                            table.addCell(orderPriceCell).setBorder(0);

                            double sellingPrice = rs2.getDouble("sellingPrice");
                            PdfPCell sellPriceCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().
                                    format(rs2.getDouble("sellingPrice")),font2));
                            table.addCell(sellPriceCell).setBorder(0);

                            int quantity = rs1.getInt("quantity");
                            
                            System.out.println("Its expired");

                            //Calculating price of the whole quantity of entry
                            double totalPrice =  sellingPrice * quantity;

                            //Adding the whole total prices of entries
                            allTotalPrice = allTotalPrice + totalPrice;

                            //AAdding the whole quantities of entries
                            totalQty = totalQty + quantity;

                            PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                            table.addCell(quantityCell).setBorder(0);

                            PdfPCell totalPriceCell = new PdfPCell(new Paragraph("" + NumberFormat.getInstance().
                                    format(totalPrice)  ,font2));
                            table.addCell(totalPriceCell).setBorder(0);

                            PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                            spaceCell2.setColspan(6);
                            table.addCell(spaceCell2).setBorder(0);
                    }else{
                        System.out.println("Its not expired");
                    }   
               }  
            }
            
            PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
            spaceCell2.setColspan(3);
            table.addCell(spaceCell2).setBorder(0);
            
                 
            PdfPCell labelCell = new PdfPCell(new Paragraph("TOTAL",font));
            table.addCell(labelCell ).setBorder(0);
            
            PdfPCell totalQtyCell = new PdfPCell(new Paragraph("" + totalQty,font3));
            table.addCell(totalQtyCell );
            
            PdfPCell totalPriceCell = new PdfPCell(new Paragraph(NumberFormat.getInstance()
                    .format(allTotalPrice),font3));
            table.addCell(totalPriceCell);
            
            document.add(table);
            document.close();
        } catch (FileNotFoundException ex) {
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
