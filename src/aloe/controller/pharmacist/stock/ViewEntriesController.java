/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;


import static aloe.controller.LoginController.username;
import aloe.model.Barcode;
import aloe.model.EXSettings;
import aloe.model.Entry;
import aloe.model.Notification;
import aloe.model.PopWindow;
import static aloe.model.PopWindow.primaryStage;
import aloe.model.QueryManager;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class ViewEntriesController implements Initializable {
     public static ObservableList<Entry> entryList = FXCollections.observableArrayList();
     public static int index =0;
    @FXML
    private StackPane stackPane;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private TableView<Entry> tblEntries;
     @FXML
    private TableColumn<Entry, JFXButton> selectCol;
    @FXML
    private TableColumn<Entry, String> batchNoCol;
    @FXML
    private TableColumn<Entry, String> nameCol;
    @FXML
    private TableColumn<Entry, String> categoryCol;
    @FXML
    private TableColumn<Entry, Integer> quantityCol;
    @FXML
    private TableColumn<Entry, String> expiryDateCol;
    @FXML
    private TableColumn<Entry, JFXButton> newPackCol;
    @FXML
    private TableColumn<Entry, JFXButton> updateCol;
    @FXML
    private TableColumn<Entry, JFXButton> deleteCol;
    @FXML
    private TableColumn<Entry, String> supplierNameCol;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TableColumn<Entry, JFXButton> conditionCol;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXCheckBox selectAllChkBox;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXButton btnCreateBarcodes;
    @FXML
    private JFXButton btnExcel;
    @FXML
    private JFXButton btnPdf;
    @FXML
    private JFXButton btnWord;
    @FXML
    private JFXButton btnSortAsc;
    @FXML
    private JFXButton btnDesc;
    @FXML
    private JFXButton btnViewLargeTable;
    @FXML
    private JFXButton btnExpirySettings;
    private static int dataSize = 0;
    private static int rowsPerPage = 13;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        insertSettings();
        initCols();
        Task<Integer> task = new Task<Integer>(){
            @Override
            protected Integer call() throws Exception {
                int iterations;
                for (iterations = 0; iterations <= 40;iterations++){
                    if (isCancelled()){
                        break;
                    }
                    updateProgress(iterations, 40);
                    try{
                      Thread.sleep(10);
                    } catch(InterruptedException ex){
                        if (isCancelled()){
                            break;
                        }
                    }
                }
                return iterations;    
            }
            
        };
      
        progressBar.progressProperty().bind(task.progressProperty());
       
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                  loadDataCols("ASC");
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    } 
    public static void insertSettings(){
        QueryManager Query = new QueryManager();
        String settingsQuery = "SELECT * FROM expirySettings";
        ResultSet rs1 = Query.getDataQuery(settingsQuery);
         try {
             if(rs1.next()){
                 
             }else{
               EXSettings settings = new EXSettings(365,182,91,30,0,username,LocalDate.now().toString());
               if(Query.insertStatement(settings.insertSettings())){
                    Notification notify = new Notification(30,
                           "Expiry Settings Setup","Default expiry settings have been set");
                    notify.start();
               }
             }
         } catch (SQLException ex) {
             Logger.getLogger(ViewEntriesController.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    private void initCols(){
        selectCol.setCellValueFactory(new PropertyValueFactory<>("select")); // delte dru  
        batchNoCol.setCellValueFactory(new PropertyValueFactory<>("batchNo"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        supplierNameCol.setCellValueFactory(new PropertyValueFactory<>("supplierName")); 
        expiryDateCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate")); // Description Button
        newPackCol.setCellValueFactory(new PropertyValueFactory<>("pack")); //New Drug entry  button
        updateCol.setCellValueFactory(new PropertyValueFactory<>("update"));  // Drug update button
        deleteCol.setCellValueFactory(new PropertyValueFactory<>("delete")); // delte dru  
        conditionCol.setCellValueFactory(new PropertyValueFactory<>("btnCondition")); // delte dru  
    }
    private void loadDataCols(String sort){
        QueryManager Query = new QueryManager();
        String entryQuery = "SELECT * from Entries ORDER BY batchNo " + sort + ";";
        ResultSet rs1 = Query.getDataQuery(entryQuery);
        try {
            entryList.clear();
            while(rs1.next()){
               int batchNo = rs1.getInt("batchNo");
               String entryDelQuery = "SELECT * FROM entries_del where batchNo ='" + batchNo + "'";
               ResultSet rs = Query.getDataQuery(entryDelQuery);
               if(rs.next()){
                   //Entry will not be added on list because it exist in entries_del
               }else{
                    String drugId = rs1.getString("id");
                    String drugQuery = "SELECT * from Drugs where id = '" + drugId + "';";
                    ResultSet rs2 = Query.getDataQuery(drugQuery);
                    if(rs2.next()){
                         String name = rs2.getString("name");
                         String category = rs2.getString("firstCategory");

                         int quantity = rs1.getInt("quantity");
                         String supplierName = rs1.getString("supplierName");
                         String regDate = rs1.getString("regDate");
                         String expiryDate = rs1.getString("expiryDate");
                         JFXCheckBox select = createCheckBox(batchNo + "");
                         JFXButton delete = createDelBtn(batchNo);
                         JFXButton update = createUpdateBtn((batchNo + ""));
                         JFXButton entry = createPackBtn((batchNo + ""));
                      
                         
                         //Calculating number of days to expiry
                         LocalDate nowDate = LocalDate.now();
                         LocalDate date = LocalDate.parse(rs1.getString("expiryDate"));
                         int daysToExpiry = Integer.parseInt(date.toEpochDay() + "") - Integer.parseInt(nowDate.toEpochDay() + "") ;
                         JFXButton condition = createConditionBtn(getCondition(Query,daysToExpiry ));
                         entryList.add(new Entry(select,batchNo,name,category,quantity,
                         supplierName,expiryDate,regDate,entry,update,delete,condition));
                   }
               }
            
            }
            tblEntries.getItems().setAll(entryList);
        } catch (SQLException ex) {
            Logger.getLogger(ViewDrugsController.class.getName()).log(Level.SEVERE, null, ex);
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
     private JFXButton createUpdateBtn(String id){
            JFXButton update = new JFXButton();
            update.setPrefWidth(80);
            update.setId(id);
            update.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE_ALT);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:  brown;");
            update.setGraphic(minimizeIcon);
            update.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                      PopWindow window = new PopWindow();
                      for(Entry entry : entryList){
                          if((entry.getBatchNo() + "").equals(id)){
                              index = entryList.indexOf(entry);
                               window.loadSmallWindow("/aloe/view/pharmacist/stock/updateEntry.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                        entryList.clear();
                                                        loadDataCols("ASC");
                                                    }}); 
                          }
                      }
                            
             }});
         return update;
    }
     private JFXCheckBox createCheckBox(String id){
             JFXCheckBox checkBox = new JFXCheckBox();
             checkBox.setCheckedColor(Paint.valueOf("#0f6eb2"));
             checkBox.setPrefWidth(130);
             
             checkBox.setContentDisplay(ContentDisplay.CENTER);
             checkBox.setId(id);
             checkBox.setSelected(false);
           return checkBox;
    }
      private JFXButton createDelBtn(int id){
         JFXButton del = new JFXButton();
            del.setPrefWidth(80);
            del.setId(id + "");
            del.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:red;");
            del.setGraphic(minimizeIcon);
            del.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                        QueryManager Query = new QueryManager();
                        Entry entry = new Entry();
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setHeaderText("Deletion Message");
                        alert.setContentText("Do you really want to delete this entry?");
                        alert.showAndWait();
                        if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                            if(Query.execAction(entry.deleteEntry(id))){
                                initCols();
                                loadDataCols("ASC");
                            }else{
                                 Alert alert2 = new Alert(Alert.AlertType.ERROR);
                                alert2.setHeaderText("Deletion Error");
                                alert2.setContentText("Failed to delete entry");
                                alert2.showAndWait();
                            }
                        }    
             }});
         return del;
    }
      private JFXButton createConditionBtn(String condition){
          JFXButton btnCond = new JFXButton();
            btnCond.setPrefWidth(80);
            btnCond.setId("sa");
            btnCond.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);;
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE);
            minimizeIcon.setGlyphSize(17);
          
          
            switch(condition){
              case "excellent" :  minimizeIcon.setStyle("-fx-fill:green;");;break;
              case "better" :  minimizeIcon.setStyle("-fx-fill:orange;");;break;
              case "good" :  minimizeIcon.setStyle("-fx-fill:brown;");;break;
              case "bad" :  minimizeIcon.setStyle("-fx-fill:red;");;break;
              case "worse" :  minimizeIcon.setStyle("-fx-fill:pink;");;break;
            }
            btnCond.setGraphic(minimizeIcon);
         return btnCond;
    }
     private JFXButton createPackBtn(String id){
            JFXButton entry = new JFXButton();
            entry.setPrefWidth(80);
            entry.setId(id);
            entry.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.BRIEFCASE);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:  blue;");
            entry.setGraphic(minimizeIcon);
            entry.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                      PopWindow window = new PopWindow();
                      for(Entry entry : entryList){
                          if((entry.getBatchNo() + "").equals(id)){
                              index = entryList.indexOf(entry);
                               window.loadSmallWindow("/aloe/view/pharmacist/stock/newPack.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                        entryList.clear();
                                                        loadDataCols("ASC");
                                                    }}); 
                          }
                      }
                            
             }});
         return entry;
    }

    @FXML
    private void searchOnList(KeyEvent event) {
        FilteredList <Entry> filteredList = new FilteredList<>(entryList,p->true);  //wrap all drugs in filtered List 
            txtSearch.textProperty().addListener((ObservableValue,oldValue,newValue)->{
                  filteredList.setPredicate((Predicate<? super Entry>) entry ->{  //set predicate when the text in the search field changes
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }  
                    String filterLowerCase = newValue.toLowerCase();
                    
                    if ((entry.getBatchNo() + "").contains(filterLowerCase)){
                        return true;
                    }
                    if (entry.getName().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (entry.getCategory().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (entry.getSupplierName().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    tblEntries.setPlaceholder(new Text("No items match your search"));
                    return false;
                  });
                  SortedList<Entry> sortedList = new SortedList<>(filteredList); 
                  sortedList.comparatorProperty().bind(tblEntries.comparatorProperty());
                  tblEntries.getItems().setAll(sortedList);
            });
    }

    @FXML
    private void selectAllChkBoxAction(ActionEvent event) {
          if(selectAllChkBox.isSelected()){
            for(Entry entry : entryList){
            JFXCheckBox check = entry.getSelect();
            check.setSelected(true);
            entry.setSelect(check);
            entryList.set(entryList.indexOf(entry),entry);
           }
          selectAllChkBox.setText("UNSELECT ALL");
        }else{
            for(Entry entry : entryList){
            JFXCheckBox check = entry.getSelect();
            check.setSelected(false);
            entry.setSelect(check);
            entryList.set(entryList.indexOf(entry),entry);
           }
           selectAllChkBox.setText("SELECT ALL");
        }
      
        tblEntries.getItems().clear();
        tblEntries.getItems().setAll(entryList);
    }

    @FXML
    private void btnDeleteAction(ActionEvent event) {
         QueryManager Query = new QueryManager();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Delete Message");
            alert.setContentText("Are you sure you want \n to delete selected entry or entries");
            alert.showAndWait();
            boolean isAllDeleted= false;
            if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                for(Entry entry : entryList){
                    if(entry.getSelect().isSelected()){
                        if(Query.execAction(entry.deleteEntry(entry.getBatchNo()))){
                          isAllDeleted = true;
                        }else{
                          isAllDeleted = false;
                          break;
                        }
                    }
                }
                
                if(isAllDeleted){
                     alert.setHeaderText("Deletion Successfull");
                     alert.setContentText("You have deleted selected entry or entries");
                     alert.showAndWait();
                     selectAllChkBox.setSelected(false);
                     selectAllChkBox.setText("SELECT ALL");
                     initCols();
                     loadDataCols("ASC");
                }else{
                     Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setHeaderText("Restore Failed");
                    alert2.setContentText("Failed to delete entry or entries,\nCheck if any entry was selected");
                    alert2.showAndWait();
                }
                
            }             
    }

    @FXML
    private void btnCreateBarcodesAction(ActionEvent event) {
         Notification notify = new Notification(5,
                           "Barcodes Creation","You have initiated entries barcodes creation");
        notify.start();
        Task<Void> task = new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                         Thread.sleep(10000);
                      
                         Platform.runLater(new Runnable() {
                             @Override
                             public void run() {
                                 createBarcodes();
                             }
                         });
                   return null;
               }
           };
           new Thread(task).start();
     
    }
    
    private void createBarcodes(){
         Document document = new Document();
        try {
            PdfWriter writer  = PdfWriter.getInstance(document, 
                    new FileOutputStream(System.getProperty("user.home") + "\\Documents\\Aloe\\Barcodes\\" +"Barcode Entry.pdf"));
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            
             Paragraph p1 = new Paragraph("BARCODES FOR DRUG ENTRIES");
             p1.setSpacingAfter(50);
             document.add(p1);
             Barcode128 code128 = new Barcode128();
             QueryManager Query = new QueryManager();
             String entryQuery = "SELECT * FROM entries";
             ResultSet rs1 = Query.getDataQuery(entryQuery);
             int count = 0;
             boolean isCreated = false;
             while(rs1.next()){
                 int batchNo = rs1.getInt("batchNo");
                 String entryDelQuery = "SELECT * FROM entries_del where batchNo ='" + batchNo + "';";
                 ResultSet rs2 = Query.getDataQuery(entryDelQuery);
                 if(rs2.next()){
                     
                 }else{
                     String drugId = rs1.getString("id");
                     String drugQuery = "SELECT * FROM drugs where id ='" + drugId + "';";
                     ResultSet rs3 = Query.getDataQuery(drugQuery);
                     if(rs3.next()){
                         String name = rs3.getString("name");
                         String category = rs3.getString("firstCategory");
                        // System.out.println("umahaha");
                        
                        LocalDate regDate = LocalDate.parse(rs1.getString("regDate"));
                        LocalDate  expiryDate = LocalDate.parse(rs1.getString("expiryDate"));
                        
                        String code = batchNo + drugId + regDate.getDayOfMonth() +
                                regDate.getMonthValue() + regDate.getYear() + expiryDate.getDayOfMonth()
                        + expiryDate.getMonthValue() + expiryDate.getYear();
                        code128.setCode(code);
                        code128.setGuardBars(true);
                        
                     
                        Barcode barcode  = new Barcode(code,batchNo,"true");
                        if(Query.insertStatement(barcode.insertBarcode())){
                            count++;
                            isCreated = true;
                            Paragraph p2 = new Paragraph("BatchNo                : " + batchNo
                                                 + "\nNAME           : "+ name 
                                                 + "\nCATEGORY : " + category);
                        
                            p2.setSpacingAfter(20);
                            Paragraph p3 = new Paragraph();

                            p3.add(new Chunk(
                            code128.createImageWithBarcode(cb, null, null), 0, -10));

                            p3.setSpacingAfter(30);

                            Paragraph p4 = new Paragraph("_________________________________________________"
                                    + "__________________________");                        p4.setSpacingAfter(30);
                       
                            document.add(p2);
                            document.add(p3);
                            document.add(p4);
                        }
                     }
                 }
             }
             document.close();
             if(isCreated){
                   Notification notify = new Notification(10,
                           "Barcodes Created Successfully","You have created " + count + " barcodes for entries");
                   notify.start();
             }else{
                   Notification notify = new Notification(10,
                           "No Barcode Created","You have created " + count + " barcodes for entries"
                                   + "\n There is no new entry");
                   notify.start();
             }
            } catch (Exception e) {
            // handle exception
            }
       
    }

    @FXML
    private void btnExcelAction(ActionEvent event) {
         PopWindow window  = new PopWindow();
        Effect effect = new GaussianBlur(4.0);
        primaryStage.getScene().getRoot().setEffect(effect);
        window.loadSmallWindow("/aloe/view/pharmacist/stock/reports/entryExcelReports.fxml", "", 
                true,false,false,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
                }}); 
    }

    @FXML
    private void btnPdfAction(ActionEvent event) {
        PopWindow window  = new PopWindow();
        Effect effect = new GaussianBlur(4.0);
        primaryStage.getScene().getRoot().setEffect(effect);
        window.loadSmallWindow("/aloe/view/pharmacist/stock/reports/entryPDFReports.fxml", "", 
                true,false,false,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
                }}); 
    }

    @FXML
    private void btnWordAction(ActionEvent event) {
         PopWindow window  = new PopWindow();
        Effect effect = new GaussianBlur(4.0);
        primaryStage.getScene().getRoot().setEffect(effect);
        window.loadSmallWindow("/aloe/view/pharmacist/stock/reports/entryWordReports.fxml", "", 
                true,false,false,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
                }}); 
    }

    @FXML
    private void btnSortAscAction(ActionEvent event) {
         loadDataCols("ASC");
    }

    @FXML
    private void btnDescAction(ActionEvent event) {
         loadDataCols("DESC");
    }

    @FXML
    private void btnViewLargeTableAction(ActionEvent event) {
         PopWindow window  = new PopWindow();
        Effect effect = new GaussianBlur(4.0);
        primaryStage.getScene().getRoot().setEffect(effect);
        window.loadSmallWindow("/aloe/view/pharmacist/stock/viewEntries.fxml", "", 
                true,true,true,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
                }}); 
        
    }

    @FXML
    private void btnExpirySettingsAction(ActionEvent event) {
        PopWindow window = new PopWindow();
        window.loadSmallWindow("/aloe/view/pharmacist/stock/expirySettings.fxml", "", true,false,false,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
           @Override
           public void handle(WindowEvent event) {
                         entryList.clear();
                         loadDataCols("ASC");
                     }}); 
    }
    
}
