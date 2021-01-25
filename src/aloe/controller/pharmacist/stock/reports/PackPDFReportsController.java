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
public class PackPDFReportsController implements Initializable {

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
            case "Expired" : lblReportType.setText("Expired Entries");break;
        }
    }
   

    @FXML
    private void btnPrintAction(ActionEvent event) {
    }

    @FXML
    private void btnExportToPdfAction(ActionEvent event) {
         switch(selected){
            case "Master" : generateMasterReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Packs Master Report.pdf");break;
            case "Packs" :  generatePacksQuantityReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Packs Quantity Report.pdf");break;
            case "Expired" :  generateExpiredPacksReport(System.getProperty("user.home")  
                    + "\\Documents\\Aloe\\Stock Reports\\" +"Expired Packs Report.pdf");break;
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
            document.add(new Paragraph("DRUG PACKS MASTER REPORT | " + formatedDate,font));
            
            PdfPTable table = new PdfPTable(8);
            float[] widths = { 60f, 90f, 60, 60f, 60f, 70f,  70f,100f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            Font font3 = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            
            PdfPCell batchNoCol = new PdfPCell(new Paragraph("Pack No.",font1));
            table.addCell(batchNoCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
            table.addCell(nameCol).setBorder(0);
                 
            PdfPCell daysToExpiryCol = new PdfPCell(new Paragraph("Condition",font1));
            table.addCell(daysToExpiryCol).setBorder(0);
            
            PdfPCell orderPrice = new PdfPCell(new Paragraph( "Order Price\n\n(Per Drug)",font1));
            table.addCell(orderPrice).setBorder(0);
            
            PdfPCell sellPriceCol = new PdfPCell(new Paragraph( "Sell Price\n\n(Per Pack)",font1));
            table.addCell(sellPriceCol).setBorder(0);
        
            PdfPCell quantityCol = new PdfPCell(new Paragraph("Quantity",font1));
            table.addCell(quantityCol).setBorder(0);
            
            PdfPCell numOfPacksCol = new PdfPCell(new Paragraph("No. Of Packs",font1));
            table.addCell(numOfPacksCol).setBorder(0);
            
            PdfPCell totalPriceCol = new PdfPCell(new Paragraph( "Total Price",font1));
            table.addCell(totalPriceCol).setBorder(0);
            
       
            
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(8);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            QueryManager Query = new QueryManager();
            String entriesQuery = "SELECT * FROM packs WHERE packId NOT IN (SELECT batchNo FROM entries_del)";
            ResultSet rs1 = Query.getDataQuery(entriesQuery);
            double allTotalPrice = 0;
            int totalNumOfPacks = 0;
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
                        PdfPCell batchNoCell = new PdfPCell(new Paragraph(rs1.getString("packId"),font2));
                        table.addCell(batchNoCell).setBorder(0);

                        PdfPCell nameCell = new PdfPCell(new Paragraph(rs3.getString("name"),font2));
                        table.addCell(nameCell).setBorder(0);
                        
                        PdfPCell daysToExpiryCell = new PdfPCell(new Paragraph(getCondition(Query,daysToExpiry),font2));
                        table.addCell(daysToExpiryCell).setBorder(0);

                        PdfPCell orderPriceCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().
                        format(rs3.getDouble("orderPrice")),font2));
                        table.addCell(orderPriceCell).setBorder(0);

                        double sellingPrice = rs1.getDouble("price");
                        PdfPCell sellPriceCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().
                                format(sellingPrice),font2));
                        table.addCell(sellPriceCell).setBorder(0);

                        int numOfPacks = rs1.getInt("numOfPacks");
                        
                        //Calculating price of the whole quantity of entry
                        double totalPrice =  sellingPrice * numOfPacks;

                        //Adding the whole total prices of entries
                        allTotalPrice = allTotalPrice + totalPrice;

                        //AAdding the whole quantities of entries
                        totalNumOfPacks = totalNumOfPacks + numOfPacks;

                        PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                        table.addCell(quantityCell).setBorder(0);
                        
                        PdfPCell numOfPacksCell = new PdfPCell(new Paragraph(numOfPacks+"",font2));
                        table.addCell(numOfPacksCell).setBorder(0);

                        PdfPCell totalPriceCell = new PdfPCell(new Paragraph("" + NumberFormat.getInstance().
                                format(totalPrice)  ,font2));
                        table.addCell(totalPriceCell).setBorder(0);

                        PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                        spaceCell2.setColspan(8);
                        table.addCell(spaceCell2).setBorder(0);
                  }
               
               }
              
       
            }
            
            PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
            spaceCell2.setColspan(5);
            table.addCell(spaceCell2).setBorder(0);
            
                 
            PdfPCell labelCell = new PdfPCell(new Paragraph("TOTAL",font));
            table.addCell(labelCell ).setBorder(0);
            
            PdfPCell totalNumOfPacksCell = new PdfPCell(new Paragraph("" + totalNumOfPacks,font3));
            table.addCell(totalNumOfPacksCell );
            
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
    
    private void generatePacksQuantityReport(String filename){
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
            document.add(new Paragraph("PACKS QUANTITY REPORT | " + formatedDate,font));
            
            PdfPTable table = new PdfPTable(6);
            float[] widths = { 60f,80f, 90f, 90f, 90f, 100f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            Font font3 = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            
            PdfPCell packIdCol = new PdfPCell(new Paragraph("Pack No.",font1));
            table.addCell(packIdCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
            table.addCell(nameCol).setBorder(0);
                 
            PdfPCell daysToExpiryCol = new PdfPCell(new Paragraph("Condition",font1));
            table.addCell(daysToExpiryCol).setBorder(0);
            
      
            PdfPCell quantityCol = new PdfPCell(new Paragraph("Quantity \n\n(Per Pack)",font1));
            table.addCell(quantityCol).setBorder(0);
            
            PdfPCell numOfPacksCol = new PdfPCell(new Paragraph("No. Of Packs",font1));
            table.addCell(numOfPacksCol).setBorder(0);
         
             PdfPCell totalQtyCol = new PdfPCell(new Paragraph("Total Quantity",font1));
            table.addCell(totalQtyCol).setBorder(0);
         
       
            
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(6);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            QueryManager Query = new QueryManager();
            String entriesQuery = "SELECT * FROM packs WHERE packId NOT IN (SELECT batchNo FROM entries_del)";
            ResultSet rs1 = Query.getDataQuery(entriesQuery);
            int allTotalQty = 0;
            int totalNumOfPacks = 0;
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
                        PdfPCell batchNoCell = new PdfPCell(new Paragraph(rs1.getString("packId"),font2));
                        table.addCell(batchNoCell).setBorder(0);

                        PdfPCell nameCell = new PdfPCell(new Paragraph(rs3.getString("name"),font2));
                        table.addCell(nameCell).setBorder(0);
                        
                        PdfPCell daysToExpiryCell = new PdfPCell(new Paragraph(getCondition(Query,daysToExpiry),font2));
                        table.addCell(daysToExpiryCell).setBorder(0);

                        int numOfPacks = rs1.getInt("numOfPacks");
                        
                        int quantity = rs1.getInt("quantity");
                        
                        int totalQty = numOfPacks * quantity;
                   
                        totalNumOfPacks = totalNumOfPacks + numOfPacks;
                        
                         allTotalQty = allTotalQty + totalQty;
                         
                        PdfPCell quantityCell = new PdfPCell(new Paragraph("" + quantity,font2));
                        table.addCell(quantityCell).setBorder(0);
                        
                        PdfPCell numOfPacksCell = new PdfPCell(new Paragraph(numOfPacks+"",font2));
                        table.addCell(numOfPacksCell).setBorder(0);

                        PdfPCell totalQtyCell = new PdfPCell(new Paragraph("" + NumberFormat.getInstance().
                                format(totalQty),font2));
                        table.addCell(totalQtyCell).setBorder(0);

                        PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                        spaceCell2.setColspan(6);
                        table.addCell(spaceCell2).setBorder(0);
                  }
               
               }
              
       
            }
            
            PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
            spaceCell2.setColspan(3);
            table.addCell(spaceCell2).setBorder(0);
            
                 
            PdfPCell labelCell = new PdfPCell(new Paragraph("TOTAL",font));
            table.addCell(labelCell ).setBorder(0);
            
            PdfPCell totalNumOfPacksCell = new PdfPCell(new Paragraph("" + totalNumOfPacks,font3));
            table.addCell(totalNumOfPacksCell );
            
            PdfPCell totalPriceCell = new PdfPCell(new Paragraph(NumberFormat.getInstance()
                    .format(allTotalQty),font3));
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
    
    private void generateExpiredPacksReport(String filename){
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
            
            PdfPTable table = new PdfPTable(7);
            float[] widths = { 60f, 90f, 60f, 60f, 70f,  70f,100f};
            table.setTotalWidth(widths);
            table.setLockedWidth(true);
            
            Font font1 = FontFactory.getFont("Times-Roman", 12, Font.UNDERLINE);
            Font font2 = FontFactory.getFont("Times-Roman", 10, Font.NORMAL);
            Font font3 = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
            BaseFont bf = font1.getCalculatedBaseFont(false);
            
            PdfPCell batchNoCol = new PdfPCell(new Paragraph("Pack No.",font1));
            table.addCell(batchNoCol).setBorder(0);
          
            PdfPCell nameCol = new PdfPCell(new Paragraph("Name",font1));
            table.addCell(nameCol).setBorder(0);
                       
            PdfPCell orderPrice = new PdfPCell(new Paragraph( "Order Price\n\n(Per Drug)",font1));
            table.addCell(orderPrice).setBorder(0);
            
            PdfPCell sellPriceCol = new PdfPCell(new Paragraph( "Sell Price\n\n(Per Pack)",font1));
            table.addCell(sellPriceCol).setBorder(0);
        
            PdfPCell quantityCol = new PdfPCell(new Paragraph("Quantity",font1));
            table.addCell(quantityCol).setBorder(0);
            
            PdfPCell numOfPacksCol = new PdfPCell(new Paragraph("No. Of Packs",font1));
            table.addCell(numOfPacksCol).setBorder(0);
            
            PdfPCell totalPriceCol = new PdfPCell(new Paragraph( "Total Price",font1));
            table.addCell(totalPriceCol).setBorder(0);
            
       
            
            PdfPCell spaceCell1 = new PdfPCell(new Paragraph(" "));
            spaceCell1.setColspan(7);
            table.addCell(spaceCell1).setBorder(0);
            
            table.setSpacingBefore(15f);
            
            table.setSpacingAfter(50f);
            
            table.setHeaderRows(1);
            QueryManager Query = new QueryManager();
            String entriesQuery = "SELECT * FROM packs WHERE packId NOT IN (SELECT packId FROM packs_del)";
            ResultSet rs1 = Query.getDataQuery(entriesQuery);
            double allTotalPrice = 0;
            int totalNumOfPacks = 0;
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
                                PdfPCell batchNoCell = new PdfPCell(new Paragraph(rs1.getString("packId"),font2));
                                table.addCell(batchNoCell).setBorder(0);

                                PdfPCell nameCell = new PdfPCell(new Paragraph(rs3.getString("name"),font2));
                                table.addCell(nameCell).setBorder(0);
                                PdfPCell orderPriceCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().
                                format(rs3.getDouble("orderPrice")),font2));
                                table.addCell(orderPriceCell).setBorder(0);
                                
                                double sellingPrice = rs1.getDouble("price");
                                PdfPCell sellPriceCell = new PdfPCell(new Paragraph(NumberFormat.getInstance().
                                        format(sellingPrice),font2));
                                table.addCell(sellPriceCell).setBorder(0);

                                int numOfPacks = rs1.getInt("numOfPacks");

                                //Calculating price of the whole quantity of entry
                                double totalPrice =  sellingPrice * numOfPacks;

                                //Adding the whole total prices of entries
                                allTotalPrice = allTotalPrice + totalPrice;

                                //AAdding the whole quantities of entries
                                totalNumOfPacks = totalNumOfPacks + numOfPacks;

                                PdfPCell quantityCell = new PdfPCell(new Paragraph(rs1.getString("quantity"),font2));
                                table.addCell(quantityCell).setBorder(0);

                                PdfPCell numOfPacksCell = new PdfPCell(new Paragraph(numOfPacks+"",font2));
                                table.addCell(numOfPacksCell).setBorder(0);

                                PdfPCell totalPriceCell = new PdfPCell(new Paragraph("" + NumberFormat.getInstance().
                                        format(totalPrice)  ,font2));
                                table.addCell(totalPriceCell).setBorder(0);

                                PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
                                spaceCell2.setColspan(7);
                                table.addCell(spaceCell2).setBorder(0);
                        }
                    }
               
               }
              
       
            }
            
            PdfPCell spaceCell2 = new PdfPCell(new Paragraph(" "));
            spaceCell2.setColspan(4);
            table.addCell(spaceCell2).setBorder(0);
            
                 
            PdfPCell labelCell = new PdfPCell(new Paragraph("TOTAL",font));
            table.addCell(labelCell ).setBorder(0);
            
            PdfPCell totalNumOfPacksCell = new PdfPCell(new Paragraph("" + totalNumOfPacks,font3));
            table.addCell(totalNumOfPacksCell );
            
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
