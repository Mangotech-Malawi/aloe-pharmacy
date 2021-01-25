/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager.tables;

import aloe.controller.pharmacist.stock.ViewPacksController;
import static aloe.controller.pharmacist.stock.ViewPacksController.packList;
import aloe.model.Pack;
import aloe.model.PopWindow;
import static aloe.model.PopWindow.primaryStage;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class PacksController implements Initializable {

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
    private TableView<Pack> tblPacks;
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
    private TableColumn<Pack, Integer> numOfPacksCol;

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
        packIdCol.setCellValueFactory(new PropertyValueFactory<>("packId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        numOfPacksCol.setCellValueFactory(new PropertyValueFactory<>("numOfPacks"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        sellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice")); 
        expiryDateCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate")); // Description Button
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
                               packList.add(new Pack(packId,name,category,numOfPacks,quantity,price,expiryDate));
                           }
                        }
                  }
             }
            tblPacks.getItems().setAll(packList);
         } catch (SQLException ex) {
             Logger.getLogger(ViewPacksController.class.getName()).log(Level.SEVERE, null, ex);
         }
      
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
        window.loadSmallWindow("/aloe/view/manager/tables/packs.fxml", "", 
                true,true,true,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
                }}); 
        
    }
    
}
