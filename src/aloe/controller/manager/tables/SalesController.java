/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager.tables;


import aloe.model.PopWindow;
import static aloe.model.PopWindow.primaryStage;
import aloe.model.QueryManager;
import aloe.model.Sale;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
public class SalesController implements Initializable {

   
    @FXML
    private StackPane stackPane;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TableView<Sale> tblSales;
    @FXML
    private TableColumn<Sale, Integer> saleNoCol;
    @FXML
    private TableColumn<Sale, String> drugNameCol;
    @FXML
    private TableColumn<Sale, Integer> quantityCol;
    @FXML
    private TableColumn<Sale, Double> chargeCol;
    @FXML
    private TableColumn<Sale, String> soldDateTimeCol;
      public static ObservableList<Sale> salesList = FXCollections.observableArrayList();
    @FXML
    private Label lblStatus;
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
    private TableColumn<Sale,String> itemTypeCol;

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
                    Thread.sleep(61);
                }
                return iteration;  
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                    loadColData("ASC");
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
       
    }    
    
    private void initCols(){
        saleNoCol.setCellValueFactory(new PropertyValueFactory<>("saleNo"));
        itemTypeCol.setCellValueFactory(new PropertyValueFactory<>("item")); 
        drugNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        chargeCol.setCellValueFactory(new PropertyValueFactory<>("charge"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        soldDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("transaction_date")); 
    }
    private void loadColData(String sortType){
        QueryManager Query = new QueryManager();
        String mySalesQuery = "SELECT * FROM sales ORDER BY saleNo " + sortType + ";";
        ResultSet rs1 = Query.getDataQuery(mySalesQuery);
        try {
            salesList.clear();
            while(rs1.next()){
              int saleNo = rs1.getInt("saleNo");
              int transNo = rs1.getInt("transNo");
              int quantity = rs1.getInt("quantity");
              String item = rs1.getString("item");
              double charge = rs1.getDouble("charge");
              String transQuery = "SELECT * from transactions "
                      + "where transNo = " + transNo + ";";
              ResultSet rs2 = Query.getDataQuery(transQuery);
              if(rs2.next()){
                  String transDate = rs2.getString("trans_date");
                  String id = rs1.getString("id");
                  String entryQuery = "SELECT * FROM entries where batchNo ='" + id + "'";
                  ResultSet rs3 = Query.getDataQuery(entryQuery);
                  if(rs3.next()){
                      String drugId = rs3.getString("id");
                      String drugQuery = "SELECT name FROM drugs where id ='" + drugId + "';";
                      ResultSet rs4 = Query.getDataQuery(drugQuery);
                      if(rs4.next()){
                           String name = rs4.getString("name");
                           salesList.add( new Sale(saleNo,item,name,quantity,charge,transDate));
                      }
                  }else{
                      String packsQuery = "SELECT * FROM packs where packId ='" + id + "'";
                      ResultSet rs4 = Query.getDataQuery(packsQuery);
                      if(rs4.next()){
                          String batchNo = rs4.getString("batchNo");
                          String query = "SELECT drugId from entries where batchNo ='" + batchNo + "'";
                          ResultSet rs5 = Query.getDataQuery(query);
                          if(rs5.next()){
                                String drugQuery = "SELECT name FROM drugs"
                                  + " where id ='" + rs5.getString("drugId") + "'";
                                ResultSet rs6 = Query.getDataQuery(drugQuery);
                                if(rs6.next()){
                                    String name = rs6.getString("name");
                                    //int transNo,String name,int quantity,double charge, String date
                                    salesList.add( new Sale(saleNo,item,name,quantity,charge,transDate));
                                } 
                          }
                         
                      }
                  }
                  
              }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SalesController.class.getName()).log(Level.SEVERE, null, ex);
        }
       tblSales.getItems().setAll(salesList);
    }
    @FXML
    private void searchOnList(KeyEvent event) {
        FilteredList <Sale> filteredList = new FilteredList<>(salesList,p->true);  //wrap all drugs in filtered List 
            txtSearch.textProperty().addListener((ObservableValue,oldValue,newValue)->{
                  filteredList.setPredicate((Predicate<? super Sale>) sale ->{  //set predicate when the text in the search field changes
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }  
                    String filterLowerCase = newValue.toLowerCase();
                    
                    if ((sale.getSaleNo() + "").contains(filterLowerCase)){
                        return true;
                    }
                    if (sale.getName().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (sale.getItem().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if ((sale.getCharge() + "").toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if ((sale.getQuantity() + "").toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    tblSales.setPlaceholder(new Text("No items match your search"));
                    return false;
                  });
                  SortedList<Sale> sortedList = new SortedList<>(filteredList); 
                  sortedList.comparatorProperty().bind(tblSales.comparatorProperty());
                  tblSales.getItems().setAll(sortedList);
            });
    }

    @FXML
    private void btnExcelAction(ActionEvent event) {
    }

    @FXML
    private void btnPdfAction(ActionEvent event) {
    }

    @FXML
    private void btnWordAction(ActionEvent event) {
    }

    @FXML
    private void btnSortAscAction(ActionEvent event) {
         loadColData("ASC");
    }

    @FXML
    private void btnDescAction(ActionEvent event) {
         loadColData("DESC");
    }

    @FXML
    private void btnViewLargeTableAction(ActionEvent event) {
         PopWindow window  = new PopWindow();
        Effect effect = new GaussianBlur(4.0);
        primaryStage.getScene().getRoot().setEffect(effect);
        window.loadSmallWindow("/aloe/view/manager/tables/sales.fxml", "", 
                true,true,true,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
                }}); 
    }
    
}
