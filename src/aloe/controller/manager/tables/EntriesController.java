/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager.tables;

import aloe.controller.pharmacist.stock.ViewDrugsController;
import aloe.controller.pharmacist.stock.ViewEntriesController;
import static aloe.controller.pharmacist.stock.ViewEntriesController.entryList;
import static aloe.controller.pharmacist.stock.ViewEntriesController.insertSettings;
import aloe.model.Entry;
import aloe.model.PopWindow;
import static aloe.model.PopWindow.primaryStage;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class EntriesController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private ProgressBar progressBar;
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
    private TableView<Entry> tblEntries;
    @FXML
    private TableColumn<Entry,Integer> batchNoCol;
    @FXML
    private TableColumn<Entry, String> nameCol;
    @FXML
    private TableColumn<Entry, String> categoryCol;
    @FXML
    private TableColumn<Entry, Integer> quantityCol;
    @FXML
    private TableColumn<Entry, String> supplierNameCol;
    @FXML
    private TableColumn<Entry, String> expiryDateCol;
    @FXML
    private TableColumn<Entry, JFXButton> conditionCol;

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
    
     private void initCols(){
        batchNoCol.setCellValueFactory(new PropertyValueFactory<>("batchNo"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        supplierNameCol.setCellValueFactory(new PropertyValueFactory<>("supplierName")); 
        expiryDateCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate")); // Description Button
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
                   
                      
                         
                         //Calculating number of days to expiry
                         LocalDate nowDate = LocalDate.now();
                         LocalDate date = LocalDate.parse(rs1.getString("expiryDate"));
                         int daysToExpiry = Integer.parseInt(date.toEpochDay() + "") - Integer.parseInt(nowDate.toEpochDay() + "") ;
                         JFXButton condition = createConditionBtn(getCondition(Query,daysToExpiry ));
                         entryList.add(new Entry(batchNo,name,category,quantity,
                         supplierName,expiryDate,regDate,condition));
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
        window.loadSmallWindow("/aloe/view/manager/tables/entries.fxml", "", 
                true,true,true,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
                }}); 
        
    }
  
}
