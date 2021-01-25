/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;

import aloe.model.Barcode;
import aloe.model.Notification;
import aloe.model.Pack;
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
public class ViewPacksController implements Initializable {
     public static ObservableList<Pack> packList = FXCollections.observableArrayList();
      public static int index =-1;
    @FXML
    private StackPane stackPane;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private TableView<Pack> tblPacks;
    @FXML
    private TableColumn<Pack, JFXCheckBox> selectCol;
    @FXML
    private TableColumn<Pack ,String> packIdCol;
    @FXML
    private TableColumn<Pack,String> nameCol;
    @FXML
    private TableColumn<Pack ,String> categoryCol;
    @FXML
    private TableColumn<Pack ,Integer> quantityCol;
    @FXML
    private TableColumn<Pack ,Double> sellingPriceCol;
    @FXML
    private TableColumn<Pack ,String> expiryDateCol;
    @FXML
    private TableColumn<Pack ,JFXButton> updateCol;
    @FXML
    private TableColumn<Pack,JFXButton> deleteCol;
    @FXML
    private TableColumn<Pack, Integer> numOfPacksCol;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXCheckBox selectAllChkBox;
    @FXML
    private JFXButton btnDelete;
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
    private JFXButton btnCreateBarcodes;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initCols();
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                int iteration;
                for (iteration = 0; iteration <= 40; iteration++){
                    if (isCancelled()){
                        break;
                    }
                    updateProgress(iteration, 40);
               
                    Thread.sleep(10);
                }
                return iteration;  
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
    private void initCols(){
        selectCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        packIdCol.setCellValueFactory(new PropertyValueFactory<>("packId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        numOfPacksCol.setCellValueFactory(new PropertyValueFactory<>("numOfPacks"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        sellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice")); 
        expiryDateCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate")); // Description Button
        updateCol.setCellValueFactory(new PropertyValueFactory<>("update"));  // Drug update button
        deleteCol.setCellValueFactory(new PropertyValueFactory<>("delete")); // delt
    }
    private void loadDataCols(String sortType){
        QueryManager Query = new QueryManager();
        String packQuery = "SELECT * FROM packs ORDER BY packId " + sortType + ";";
        packList.clear();
        try{
        ResultSet rs1 = Query.getDataQuery(packQuery);
                while(rs1.next()){
                    int packId = rs1.getInt("packId");
                    String packDelQuery = "SELECT * from packs_del where packId='" + packId + "';";
                    ResultSet rs = Query.getDataQuery(packDelQuery);
                    if(rs.next()){
                    }else{
                        String batchNo = rs1.getString("batchNo");
                        String entryQuery = "SELECT * FROM entries where batchNo = '" + batchNo + "'";
                        System.out.println("Was Run");
                        ResultSet rs2 = Query.getDataQuery(entryQuery);
                        if(rs2.next()){
                           String drugId = rs2.getString("id");
                           String drugQuery = "SELECT * FROM Drugs where id = '" + drugId +"'";
                           ResultSet rs3 = Query.getDataQuery(drugQuery);
                           if(rs3.next()){
                               String name = rs3.getString("name");
                               String category = rs3.getString("firstCategory");
                               int numOfPacks = rs1.getInt("numOfPacks");
                               int quantity = rs1.getInt("quantity");
                               double price = rs1.getDouble("price");
                               String expiryDate = rs2.getString("expiryDate");
                               JFXCheckBox select = createCheckBox(packId + "");
                               JFXButton update = createUpdateBtn(packId + "");
                               JFXButton delete = createDelBtn(packId);
                               packList.add(new Pack(select,packId,name,category,numOfPacks,quantity,price,expiryDate,update,delete));
                           }
                        }
                  }
             }
            tblPacks.getItems().setAll(packList);
         } catch (SQLException ex) {
             Logger.getLogger(ViewPacksController.class.getName()).log(Level.SEVERE, null, ex);
         }
      
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
                      for(Pack pack : packList){
                          if((pack.getPackId() + "").equals(id)){
                              index = packList.indexOf(pack);
                               window.loadSmallWindow("/aloe/view/pharmacist/stock/newPack.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                       ViewPacksController.index = -1;
                                                        packList.clear();
                                                        initCols();
                                                        loadDataCols("ASC");
                                                    
                                                    }}); 
                          }
                      }
                            
             }});
         return update;
    }
      private JFXButton createDelBtn(int id){
         JFXButton del = new JFXButton();
            del.setPrefWidth(80);
            del.setId(id+"");
            del.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:red;");
            del.setGraphic(minimizeIcon);
            del.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                        QueryManager Query = new QueryManager();
                        Pack pack = new Pack();
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setHeaderText("Deletion Message");
                        alert.setContentText("Do you really want to delete this pack?");
                        alert.showAndWait();
                        if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                            if(Query.execAction(pack.deletePack(id))){
                                packList.clear();
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
    @FXML
    private void searchOnList(KeyEvent event) {
         FilteredList <Pack> filteredList = new FilteredList<>(packList,p->true);  //wrap all drugs in filtered List 
            txtSearch.textProperty().addListener((ObservableValue,oldValue,newValue)->{
                  filteredList.setPredicate((Predicate<? super Pack>) pack ->{  //set predicate when the text in the search field changes
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }  
                    String filterLowerCase = newValue.toLowerCase();
                    
                    if ((pack.getPackId() + "").contains(filterLowerCase)){
                        return true;
                    }
                    if (pack.getName().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (pack.getCategory().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (pack.getExpiryDate().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    tblPacks.setPlaceholder(new Text("No items match your search"));
                    return false;
                  });
                  SortedList<Pack> sortedList = new SortedList<>(filteredList); 
                  sortedList.comparatorProperty().bind(tblPacks.comparatorProperty());
                  tblPacks.getItems().setAll(sortedList);
            });
    }

    @FXML
    private void selectAllChkBoxAction(ActionEvent event) {
           if(selectAllChkBox.isSelected()){
            for(Pack pack : packList){
            JFXCheckBox check = pack.getSelect();
            check.setSelected(true);
            pack.setSelect(check);
            packList.set(packList.indexOf(pack),pack);
           }
          selectAllChkBox.setText("UNSELECT ALL");
        }else{
            for(Pack pack : packList){
            JFXCheckBox check = pack.getSelect();
            check.setSelected(false);
            pack.setSelect(check);
            packList.set(packList.indexOf(pack),pack);
           }
           selectAllChkBox.setText("SELECT ALL");
        }
      
        tblPacks.getItems().clear();
        tblPacks.getItems().setAll(packList);
    }

    @FXML
    private void btnDeleteAction(ActionEvent event) {
          QueryManager Query = new QueryManager();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Delete Message");
            alert.setContentText("Are you sure you want \n to delete selected pack or packs");
            alert.showAndWait();
            boolean isAllDeleted= false;
            if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                for(Pack pack : packList){
                    if(pack.getSelect().isSelected()){
                        if(Query.execAction(pack.deleteEntry(pack.getPackId()))){
                          isAllDeleted = true;
                        }else{
                          isAllDeleted = false;
                          break;
                        }
                    }
                }
                
                if(isAllDeleted){
                     alert.setHeaderText("Deletion Successfull");
                     alert.setContentText("You have deleted selected pack or packs");
                     alert.showAndWait();
                     selectAllChkBox.setSelected(false);
                     selectAllChkBox.setText("SELECT ALL");
                     initCols();
                     loadDataCols("ASC");
                }else{
                     Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setHeaderText("Restore Failed");
                    alert2.setContentText("Failed to delete pack or packs,\nCheck if any pack was selected");
                    alert2.showAndWait();
                }
                
            }             
    }

    @FXML
    private void btnCreateBarcodesAction(ActionEvent event) {
           Notification notify = new Notification(5,
                           "Barcodes Creation","You have initiated packs barcodes creation");
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
                    new FileOutputStream(System.getProperty("user.home") + "\\Documents\\Aloe\\Barcodes\\" +"Packs Barcodes.pdf"));
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            
             Paragraph p1 = new Paragraph("BARCODES FOR DRUG PACKS");
             p1.setSpacingAfter(50);
             document.add(p1);
              Barcode128 code128 = new Barcode128();
             QueryManager Query = new QueryManager();
             String packQuery = "SELECT * FROM packs";
             ResultSet rs1 = Query.getDataQuery(packQuery);
             int count = 0;
             boolean isCreated = false;
             while(rs1.next()){
                 int packId = rs1.getInt("packId");
                 String packDelQuery = "SELECT * FROM packs_del where packId ='" + packId + "';";
                 ResultSet rs2 = Query.getDataQuery(packDelQuery);
                 if(rs2.next()){
                     
                 }else{
                     String batchNo = rs1.getString("batchNo");
                     String entryQuery = "SELECT * FROM entries where batchNo ='" + batchNo + "';";
                     ResultSet rs3 = Query.getDataQuery(entryQuery);
                     if(rs3.next()){
                         String drugId = rs3.getString("id");
                         
                         String drugQuery = "SELECT * FROM drugs where id ='" + drugId + "'";
                         ResultSet rs4 = Query.getDataQuery(drugQuery);
                         if(rs4.next()){
                              String name = rs4.getString("name");
                              String category = rs4.getString("firstCategory");
                             

                              LocalDate regDate = LocalDate.parse(rs3.getString("regDate"));
                              LocalDate  expiryDate = LocalDate.parse(rs3.getString("expiryDate"));

                              String code = packId + drugId + regDate.getDayOfMonth() +
                                      regDate.getMonthValue() + regDate.getYear() + expiryDate.getDayOfMonth()
                              + expiryDate.getMonthValue() + expiryDate.getYear();
                              code128.setCode(code);
                              code128.setGuardBars(true);


                              Barcode barcode  = new Barcode(code,packId,"false");
                              if(Query.insertStatement(barcode.insertBarcode())){
                                  count++;
                                  isCreated = true;
                                  Paragraph p2 = new Paragraph("PackNo                  : " + packId
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
             }
             document.close();
             if(isCreated){
                   Notification notify = new Notification(10,
                           "Barcodes Created Successfully","You have created " + count + " barcodes for packs");
                   notify.start();
             }else{
                   Notification notify = new Notification(10,
                           "No Barcode Created","You have created " + count + " barcodes for packs" 
                                   + "\n There is no new pack");
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
        window.loadSmallWindow("/aloe/view/pharmacist/stock/reports/packExcelReports.fxml", "", 
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
        window.loadSmallWindow("/aloe/view/pharmacist/stock/reports/packPDFReports.fxml", "", 
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
        window.loadSmallWindow("/aloe/view/pharmacist/stock/reports/packWordReportGenerator.fxml", "", 
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
        window.loadSmallWindow("/aloe/view/pharmacist/stock/viewPacks.fxml", "", 
                true,true,true,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
                }}); 
        
    }
    
}
