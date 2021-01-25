/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock.reports;

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
public class ReportGeneratorController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private VBox btnDrugsPricesAction;
    @FXML
    private VBox btnDrugsCategories;
    @FXML
    private Label lblReportType;
    @FXML
    private JFXButton btnPrint;
    @FXML
    private JFXButton btnExportToPdf;
    private String selected = "Master";
    @FXML
    private JFXButton btnMaster;
    @FXML
    private JFXButton btnPrices;
    @FXML
    private JFXButton btnCategories;
    @FXML
    private JFXButton btnDescription;
    @FXML
    private JFXButton btnNames;

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
    private void btnPrintAction(ActionEvent event) {
    }

    @FXML
    private void btnExportToPdfAction(ActionEvent event) {
        switch(selected){
            case "Master" : generateMasterReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Details Master Report.pdf");break;
            case "Prices" :  generatePricesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Prices Report.pdf");break;
            case "Categories" :  generateCategoriesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Categories Report.pdf");break;
            case "Description" :  generateDescriptionReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drugs Description Report.pdf");break;
            case "Names" : generateNamesReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Drug Names Report.pdf");break;
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
            document.add(new Paragraph("DRUGS DETAILS MASTER REPORT | " + formatedDate,font));
            
            PdfPTable table = new PdfPTable(7);
            float[] widths = { 30f, 90f, 90f, 100f, 80f, 80f, 60f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            
            PdfPCell idCol = new PdfPCell(new Paragraph("Id",font1));
            table.addCell(idCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
            table.addCell(nameCol).setBorder(0);
            
            PdfPCell firstCatCol = new PdfPCell(new Paragraph(" 1st Category",font1));
            table.addCell(firstCatCol).setBorder(0);
            
            PdfPCell secCatCol = new PdfPCell(new Paragraph("2nd Category",font1));
            table.addCell(secCatCol).setBorder(0);
            
            PdfPCell sellPriceCol = new PdfPCell(new Paragraph("Sell Price",font1));
            table.addCell(sellPriceCol).setBorder(0);
            
            PdfPCell orderPrice = new PdfPCell(new Paragraph("Order Price",font1));
            table.addCell(orderPrice).setBorder(0);
            
            PdfPCell measurementCol = new PdfPCell(new Paragraph("Unit",font1));
            table.addCell(measurementCol).setBorder(0);
            
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(7);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            QueryManager Query = new QueryManager();
            String drugsQuery = "SELECT * FROM drugs WHERE id NOT IN (SELECT id FROM drugs_del)";
            ResultSet rs1 = Query.getDataQuery(drugsQuery);
           
            while(rs1.next()){
               PdfPCell idCell = new PdfPCell(new Paragraph(rs1.getString(1),font2));
               table.addCell(idCell).setBorder(0);
               
               PdfPCell nameCell = new PdfPCell(new Paragraph(rs1.getString(2),font2));
               table.addCell(nameCell).setBorder(0);
               
               PdfPCell firstCatCell = new PdfPCell(new Paragraph(rs1.getString(3),font2));
               table.addCell(firstCatCell).setBorder(0);
               
               PdfPCell secCatCell = new PdfPCell(new Paragraph(rs1.getString(4),font2));
               table.addCell(secCatCell).setBorder(0);
               
               PdfPCell sellPriceCell = new PdfPCell(new Paragraph(NumberFormat.getCurrencyInstance().
                       format(rs1.getDouble(5)),font2));
               table.addCell(sellPriceCell).setBorder(0);
               
               PdfPCell orderPriceCell = new PdfPCell(new Paragraph(NumberFormat.getCurrencyInstance().
                       format(rs1.getDouble(6)),font2));
               table.addCell(orderPriceCell).setBorder(0);
               
               PdfPCell unitCell = new PdfPCell(new Paragraph(rs1.getString(7),font2));
               table.addCell(unitCell).setBorder(0);
             
               
               PdfPCell descriptionCol = new PdfPCell(new Paragraph("Description",font1));
               descriptionCol.setColspan(7);
               table.addCell(descriptionCol).setBorder(0);
             
               PdfPCell descriptionCell = new PdfPCell(new Paragraph(rs1.getString(8),font2));
               descriptionCell.setColspan(7);
               table.addCell(descriptionCell).setBorder(0);
            
               
               PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
               spaceCell2.setColspan(7);
               table.addCell(spaceCell2).setBorder(0);
           
            }
            
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
    
    //Method for creating a PDF that generates drug details prices report
    private void generatePricesReport(String filename){
        
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
            document.add(new Paragraph("DRUGS PRICES REPORT | " + formatedDate,font));
            
            PdfPTable table = new PdfPTable(4);
            float[] widths = { 60f, 120f,120f,120f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            PdfPCell idCol = new PdfPCell(new Paragraph("Id",font1));
            table.addCell(idCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
            table.addCell(nameCol).setBorder(0);
            
            PdfPCell sellPriceCol = new PdfPCell(new Paragraph("Selling Price",font1));
            table.addCell(sellPriceCol).setBorder(0);
            
            PdfPCell orderPrice = new PdfPCell(new Paragraph("Order Price",font1));
            table.addCell(orderPrice).setBorder(0);
         
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(7);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            QueryManager Query = new QueryManager();
            String drugsQuery = "SELECT id,name,sellingPrice,orderPrice"
                    + " FROM drugs WHERE id NOT IN (SELECT id FROM drugs_del)";
            ResultSet rs1 = Query.getDataQuery(drugsQuery);
           
            while(rs1.next()){
                PdfPCell idCell = new PdfPCell(new Paragraph(rs1.getString(1),font2));
                table.addCell(idCell).setBorder(0);

                PdfPCell nameCell = new PdfPCell(new Paragraph(rs1.getString(2),font2));
                table.addCell(nameCell).setBorder(0);

                PdfPCell sellPriceCell = new PdfPCell(new Paragraph(NumberFormat.getCurrencyInstance().
                        format(rs1.getDouble(3)),font2));
                table.addCell(sellPriceCell).setBorder(0);

                PdfPCell orderPriceCell = new PdfPCell(new Paragraph(NumberFormat.getCurrencyInstance().
                        format(rs1.getDouble(4)),font2));
                table.addCell(orderPriceCell).setBorder(0);

                PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                spaceCell2.setColspan(4);
                table.addCell(spaceCell2).setBorder(0);
           
            }
            
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
    private void generateCategoriesReport(String filename){
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
            document.add(new Paragraph("DRUGS CATEGORIES REPORT | " + formatedDate,font));
            
            PdfPTable table = new PdfPTable(4);
            float[] widths = { 60f, 120f,120f,120f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            PdfPCell idCol = new PdfPCell(new Paragraph("Id",font1));
            table.addCell(idCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
            table.addCell(nameCol).setBorder(0);
            
            PdfPCell sellPriceCol = new PdfPCell(new Paragraph("1st Category",font1));
            table.addCell(sellPriceCol).setBorder(0);
            
            PdfPCell orderPrice = new PdfPCell(new Paragraph("2nd Category",font1));
            table.addCell(orderPrice).setBorder(0);
         
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(7);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            QueryManager Query = new QueryManager();
            String drugsQuery = "SELECT id,name,firstCategory,secCategory"
                    + " FROM drugs WHERE id NOT IN (SELECT id FROM drugs_del)";
            ResultSet rs1 = Query.getDataQuery(drugsQuery);
           
            while(rs1.next()){
                PdfPCell idCell = new PdfPCell(new Paragraph(rs1.getString(1),font2));
                table.addCell(idCell).setBorder(0);

                PdfPCell nameCell = new PdfPCell(new Paragraph(rs1.getString(2),font2));
                table.addCell(nameCell).setBorder(0);
                
                PdfPCell firstCatCell = new PdfPCell(new Paragraph(rs1.getString(3),font2));
                table.addCell(firstCatCell).setBorder(0);
               
                PdfPCell secCatCell = new PdfPCell(new Paragraph(rs1.getString(4),font2));
                table.addCell(secCatCell).setBorder(0);
              
                PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                spaceCell2.setColspan(4);
                table.addCell(spaceCell2).setBorder(0);
            }
            
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
    private void generateDescriptionReport(String filename){
         try {
            Document document = new Document(PageSize.A4, 36, 36, 90, 60);
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
            document.add(new Paragraph("DRUGS DESCRIPTION REPORT | " + formatedDate,font));
            
            PdfPTable table = new PdfPTable(3);
            float[] widths = { 60f, 120f,240f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            PdfPCell idCol = new PdfPCell(new Paragraph("Id",font1));
            table.addCell(idCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
            table.addCell(nameCol).setBorder(0);
            
            PdfPCell sellPriceCol = new PdfPCell(new Paragraph("Description",font1));
            table.addCell(sellPriceCol).setBorder(0);

         
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(3);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            QueryManager Query = new QueryManager();
            String drugsQuery = "SELECT id,name,description"
                    + " FROM drugs WHERE id NOT IN (SELECT id FROM drugs_del)";
            ResultSet rs1 = Query.getDataQuery(drugsQuery);
           
            while(rs1.next()){
                PdfPCell idCell = new PdfPCell(new Paragraph(rs1.getString(1),font2));
                table.addCell(idCell).setBorder(0);

                PdfPCell nameCell = new PdfPCell(new Paragraph(rs1.getString(2),font2));
                table.addCell(nameCell).setBorder(0);
                
                PdfPCell descriptionCell = new PdfPCell(new Paragraph(rs1.getString(3),font2));
                table.addCell(descriptionCell).setBorder(0);
                    
                PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                spaceCell2.setColspan(4);
                table.addCell(spaceCell2).setBorder(0);
            }
            
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
    private void generateNamesReport(String filename){
         try {
            Document document = new Document(PageSize.A4, 36, 36, 90, 60);
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
            document.add(new Paragraph("DRUG NAMES REPORT | " + formatedDate,font));
            
            PdfPTable table = new PdfPTable(2);
            float[] widths = { 120f,240f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 14, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 12, Font.NORMAL);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            PdfPCell idCol = new PdfPCell(new Paragraph("Id",font1));
            table.addCell(idCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
            table.addCell(nameCol).setBorder(0);
            

         
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(3);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            QueryManager Query = new QueryManager();
            String drugsQuery = "SELECT id,name"
                    + " FROM drugs WHERE id NOT IN (SELECT id FROM drugs_del) ORDER BY id ASC";
            ResultSet rs1 = Query.getDataQuery(drugsQuery);
           
            while(rs1.next()){
                PdfPCell idCell = new PdfPCell(new Paragraph(rs1.getString(1),font2));
                table.addCell(idCell).setBorder(0);

                PdfPCell nameCell = new PdfPCell(new Paragraph(rs1.getString(2),font2));
                table.addCell(nameCell).setBorder(0);
                
                PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                spaceCell2.setColspan(2);
                table.addCell(spaceCell2).setBorder(0);
            }
            
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

    
}
