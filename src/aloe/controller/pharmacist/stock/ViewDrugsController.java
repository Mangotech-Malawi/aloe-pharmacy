/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;


import aloe.model.Drug;
import aloe.model.Notification;
import aloe.model.PopWindow;
import static aloe.model.PopWindow.childStage;
import static aloe.model.PopWindow.primaryStage;
import aloe.model.QueryManager;
import aloe.model.Threshold;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
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
public class ViewDrugsController implements Initializable {
    public static ObservableList<Drug> drugList = FXCollections.observableArrayList();
    public static int index = 0;
    @FXML
    private StackPane stackPane;
    @FXML
    private JFXTextField txtSearch;
   
   
    @FXML
    private TableColumn<Drug,String> idCol;
    @FXML
    private TableColumn<Drug,String> nameCol;
    @FXML
    private TableColumn<Drug,String> categoryCol;
    @FXML
    private TableColumn<Drug,String> descriptionCol;
    @FXML
    private TableColumn<Drug,Double> sellingPriceCol;
    @FXML
    private TableColumn<Drug,Double> orderPriceCol;
    @FXML
    private TableColumn<Drug,JFXButton> newEntryCol;
    @FXML
    private TableColumn<Drug,JFXButton> updateCol;
    @FXML
    private TableColumn<Drug,JFXButton> deleteCol;
    
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
    private TableColumn<Drug, String> unitCol;
    @FXML
    private TableColumn<Drug, JFXCheckBox> selectCol;
    private final int rowsPerPage = 13;
    private final int dataSize = 0;
    @FXML
    private TableView<Drug> tblDrugs;
    @FXML
    private TableColumn<Drug, JFXButton> thresholdCol;
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
      
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
                  insertThresholdDefaults();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
   private void insertThresholdDefaults(){
       try{
           QueryManager Query = new QueryManager();
           String drugQuery1 = "SELECT id FROM drugs WHERE id IN (SELECT id FROM "
                   + "entries WHERE batchNo NOT IN  "
                   + "(SELECT batchNo FROM entries_del)) AND id NOT IN (SELECT id FROM drugs_del)";
           ResultSet rs1 = Query.getDataQuery(drugQuery1);
           boolean entryDefaults = false; // Its true when atleast one entry drug entry threshold setting is set
           while(rs1.next()){
               Threshold  threshold = new Threshold(rs1.getInt(1));
               threshold.setEntryQty(10);
               System.out.println("" + rs1.getInt(1));
               if(Query.execAction(threshold.insertEntryThreshold())){
                   entryDefaults = true;
               }
           }
           if(entryDefaults){
              Notification notify = new Notification(1,
              "Entry Thresholds","Default entry threshold set");
            notify.start();
           }
           
           String packQuery = "SELECT batchNo FROM packs WHERE packId NOT IN (SELECT packId FROM " + "packs_del )";
           ResultSet rs2 = Query.getDataQuery(packQuery);
           boolean packDefaults = false; // Its true when atleast one entry drug entry threshold setting is set
           while(rs2.next()){
               String drugQuery = "SELECT id FROM entries WHERE batchNo ='" + rs2.getInt(1)
                       +"' and id NOT IN (SELECT id FROM drugs_del)"; 
               ResultSet rs3 = Query.getDataQuery(drugQuery);
               if(rs3.next()){
                    Threshold  threshold = new Threshold(rs3.getInt(1));
                    threshold.setNumOfPacks(20);
                    if(Query.execAction(threshold.insertPackThreshold())){
                        packDefaults = true;
                    }
               }
              
           }
           if(packDefaults){
              Notification notify = new Notification(1,
              "Pack Thresholds","Default pack threshold set");
            notify.start();
           }
           
           
       }catch(SQLException ex){
           System.out.println(ex);
       }
   }
    private void initCols(){
        selectCol.setCellValueFactory(new PropertyValueFactory<>("select")); // select drug checkBox
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasurement"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        sellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        orderPriceCol.setCellValueFactory(new PropertyValueFactory<>("orderPrice")); 
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("thresHoldBtn")); // Description Button
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("descBtn")); // Description Button
        newEntryCol.setCellValueFactory(new PropertyValueFactory<>("entry")); //New Drug entry  button
        updateCol.setCellValueFactory(new PropertyValueFactory<>("update"));  // Drug update button
        deleteCol.setCellValueFactory(new PropertyValueFactory<>("delete")); // delte drug button
    }
    private void loadDataCols(String sortType){
       
        QueryManager Query = new QueryManager();
        String userQuery = "SELECT * from drugs ORDER BY id " + sortType + ";";
        ResultSet rs1 = Query.getDataQuery(userQuery);
        try {
            drugList.clear();
            while(rs1.next()){
               String id = rs1.getString("id");
               String delQuery = "SELECT * FROM drugs_del where id='" + id + "';";
               ResultSet rs = Query.getDataQuery(delQuery);
               if(rs.next()){
                   //Drug details will not be added on the list because it exist in drugs_del
               }else{
                    String name = rs1.getString("name");
                    String category = rs1.getString("firstCategory");
                    Double sellingPrice = rs1.getDouble("sellingPrice");
                    Double orderPrice = rs1.getDouble("orderPrice");
                    String description = rs1.getString("description");
                    String unit = rs1.getString("measurement");
                    JFXCheckBox select = createCheckBox(id);
                    JFXButton delete = createDelBtn((id));
                    JFXButton update = createUpdateBtn((id));
                    JFXButton entry = createEntryBtn((id));
                    JFXButton descBtn = createDescBtn((id));
                    JFXButton threshold = createThresholdBtn(id);

                    drugList.add(new Drug(select,Integer.parseInt(id),name,category,sellingPrice,
                            orderPrice,unit,description,threshold,descBtn,entry,update,delete));
               }
            }
            tblDrugs.getItems().setAll(drugList);
        } catch (SQLException ex) {
            Logger.getLogger(ViewDrugsController.class.getName()).log(Level.SEVERE, null, ex);
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
                      for(Drug drug : drugList){
                            if((drug.getId()+"").equals(id)){
                                index = drugList.indexOf(drug);

                                 Effect effect = new GaussianBlur(4.0);
                                 primaryStage.getScene().getRoot().setEffect(effect);
                                 window.loadSmallWindow("/aloe/view/pharmacist/stock/update.fxml", "", true,false,false,false);
                                         window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                            @Override
                                            public void handle(WindowEvent event) {
                                                          drugList.clear();
                                                          loadDataCols("ASC");
                                                           Effect effect = new GaussianBlur(0.0);
                                                          primaryStage.getScene().getRoot().setEffect(effect);
                                                      }}); 
                            }
                      }
                            
             }});
         return update;
    }
      private JFXButton createDelBtn(String id){
         JFXButton del = new JFXButton();
            del.setPrefWidth(80);
            del.setId(id);
            del.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:red;");
            del.setGraphic(minimizeIcon);
            del.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                        QueryManager Query = new QueryManager();
                        Drug drug = new Drug();
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setHeaderText("Deletion Message");
                        alert.setContentText("Drug deleted successfully");
                        alert.showAndWait();
                        if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                            if(Query.execAction(drug.deleteDrug(Integer.parseInt(id)))){
                                initCols();
                                loadDataCols("ASC");
                            }else{
                                 Alert alert2 = new Alert(Alert.AlertType.ERROR);
                                alert2.setHeaderText("Deletion Error");
                                alert2.setContentText("Failed to delete druge");
                                alert2.showAndWait();
                            }
                        }      
             }});
         return del;
    }
     private JFXButton createEntryBtn(String id){
            JFXButton entry = new JFXButton();
            entry.setPrefWidth(80);
            entry.setId(id);
            entry.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.LEVEL_DOWN);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:  blue;");
            entry.setGraphic(minimizeIcon);
            entry.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                      PopWindow window = new PopWindow();
                      for(Drug drug : drugList){
                          if((drug.getId() + "").equals(id)){
                              index = drugList.indexOf(drug);
                               Effect effect = new GaussianBlur(4.0);
                               primaryStage.getScene().getRoot().setEffect(effect);
                               window.loadSmallWindow("/aloe/view/pharmacist/stock/newEntry.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                        Effect effect = new GaussianBlur(0.0);
                                                        primaryStage.getScene().getRoot().setEffect(effect);
                                                        drugList.clear();
                                                        loadDataCols("ASC");
                                                    }}); 
                          }
                      }
                            
             }});
         return entry;
    }
     private JFXButton createDescBtn(String id){
            JFXButton desc = new JFXButton();
            desc.setPrefWidth(80);
            desc.setId(id);
            desc.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:#16410a;");
            desc.setGraphic(minimizeIcon);
            desc.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                      PopWindow window = new PopWindow();
                      for(Drug drug : drugList){
                          if((drug.getId() + "").equals(id)){
                              index = drugList.indexOf(drug);
                                System.out.println("the indexxx is" + index);
                               Effect effect = new GaussianBlur(4.0);
                               primaryStage.getScene().getRoot().setEffect(effect);
                               window.loadSmallWindow("/aloe/view/pharmacist/stock/drugDescription.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                        Effect effect = new GaussianBlur(0.0);
                                                        primaryStage.getScene().getRoot().setEffect(effect);
                                                        drugList.clear();
                                                        loadDataCols("ASC");
                                                    }}); 
                          }
                      }
                            
             }});
         return desc;
    }
    
     private JFXButton createThresholdBtn(String id){
            JFXButton desc = new JFXButton();
            desc.setPrefWidth(80);
            desc.setId(id);
            desc.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.BALANCE_SCALE);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:#02707d;");
            desc.setGraphic(minimizeIcon);
            desc.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                      PopWindow window = new PopWindow();
                      for(Drug drug : drugList){
                          if((drug.getId() + "").equals(id)){
                              index = drugList.indexOf(drug);
                                System.out.println("the indexxx is" + index);
                               Effect effect = new GaussianBlur(4.0);
                               primaryStage.getScene().getRoot().setEffect(effect);
                               window.loadSmallWindow("/aloe/view/pharmacist/stock/threshold.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                Effect effect = new GaussianBlur(0.0);
                                                primaryStage.getScene().getRoot().setEffect(effect);
                                                drugList.clear();
                                                loadDataCols("ASC");
                                         }}); 
                          }
                      }
                            
             }});
         return desc;
    }

    @FXML
    private void searchOnList(KeyEvent event) {
         FilteredList <Drug> filteredList = new FilteredList<>(drugList,p->true);  //wrap all drugs in filtered List 
            txtSearch.textProperty().addListener((ObservableValue,oldValue,newValue)->{
                  filteredList.setPredicate((Predicate<? super Drug>) drug ->{  //set predicate when the text in the search field changes
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }  
                    String filterLowerCase = newValue.toLowerCase();
                    
                    if ((drug.getId()+ "").toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (drug.getName().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (drug.getCategory().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    tblDrugs.setPlaceholder(new Text("No items match your search"));
                    return false;
                  });
                  SortedList<Drug> sortedList = new SortedList<>(filteredList); 
                  sortedList.comparatorProperty().bind(tblDrugs.comparatorProperty());
                  tblDrugs.getItems().setAll(sortedList);
            });
    }

    @FXML
    private void selectAllChkBoxAction(ActionEvent event) {
          if(selectAllChkBox.isSelected()){
            for(Drug drug : drugList){
            JFXCheckBox check = drug.getSelect();
            check.setSelected(true);
            drug.setSelect(check);
            drugList.set(drugList.indexOf(drug),drug);
           }
          selectAllChkBox.setText("UNSELECT ALL");
        }else{
            for(Drug drug : drugList){
            JFXCheckBox check = drug.getSelect();
            check.setSelected(false);
            drug.setSelect(check);
            drugList.set(drugList.indexOf(drug),drug);
           }
           selectAllChkBox.setText("SELECT ALL");
        }
      
        tblDrugs.getItems().clear();
        tblDrugs.getItems().setAll(drugList);
    }

    @FXML
    private void btnDeleteAction(ActionEvent event) {
          QueryManager Query = new QueryManager();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Delete Message");
            alert.setContentText("Are you sure you want \n to delete selected drug details");
            alert.showAndWait();
            boolean isAllDeleted= false;
            if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                for(Drug drug : drugList){
                    if(drug.getSelect().isSelected()){
                        if(Query.execAction(drug.deleteDrug(drug.getId()))){
                          isAllDeleted = true;
                        }else{
                          isAllDeleted = false;
                          break;
                        }
                    }
                }
                
                if(isAllDeleted){
                     alert.setHeaderText("Deletion Successfull");
                     alert.setContentText("You have deleted selected drug details");
                     alert.showAndWait();
                     selectAllChkBox.setSelected(false);
                     selectAllChkBox.setText("SELECT ALL");
                     initCols();
                     loadDataCols("ASC");
                }else{
                     Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setHeaderText("Restore Failed");
                    alert2.setContentText("Failed to delete drugs,\nCheck if any drug detail was selected");
                    alert2.showAndWait();
                }
                
            }             
    }

    @FXML
    private void btnExcelAction(ActionEvent event) {
         PopWindow window  = new PopWindow();
        Effect effect = new GaussianBlur(4.0);
        primaryStage.getScene().getRoot().setEffect(effect);
        window.loadSmallWindow("/aloe/view/pharmacist/stock/reports/excelReportGenerator.fxml", "", 
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
        window.loadSmallWindow("/aloe/view/pharmacist/stock/reports/reportGenerator.fxml", "", 
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
        window.loadSmallWindow("/aloe/view/pharmacist/stock/reports/wordReportGenerator.fxml", "", 
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
        window.loadSmallWindow("/aloe/view/pharmacist/stock/viewDrugs.fxml", "", 
                true,true,true,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        btnViewLargeTable.setDisable(false);
                        primaryStage.getScene().getRoot().setEffect(effect);      
                }}); 
        
    }
    
}
