/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.transaction;

import static aloe.controller.LoginController.username;
import aloe.controller.receptionist.transaction.MyTransactionsController;
import static aloe.controller.receptionist.transaction.MyTransactionsController.transList;
import aloe.model.QueryManager;
import aloe.model.Transaction;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class ViewTransactionsController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TableView<Transaction> tblTransactions;
    @FXML
    private TableColumn<Transaction, Integer> transNoCol;
    @FXML
    private TableColumn<Transaction, String> transByCol;
    @FXML
    private TableColumn<Transaction, String> dateCol;
    @FXML
    private TableColumn<Transaction, String> timeCol;
    @FXML
    private TableColumn<Transaction, Integer> numberOfSalesCol;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
                    loadColData();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }   
    
    private void initCols(){
        transNoCol.setCellValueFactory(new PropertyValueFactory<>("transNo"));
        transByCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("transaction_date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        numberOfSalesCol.setCellValueFactory(new PropertyValueFactory<>("numOfSales"));
    }
    private void loadColData(){
          QueryManager Query = new QueryManager();
        String transQuery = "SELECT * from transactions";
        ResultSet rs1 = Query.getDataQuery(transQuery);
        try {
            transList.clear();
            while(rs1.next()){
                int transNo = rs1.getInt("transNo");
                String transBy = rs1.getString("trans_by");
                LocalDateTime datetime = LocalDateTime.parse(rs1.getString("trans_date"));
                String date = datetime.getDayOfMonth() + " " + 
                        datetime.getMonth().name() + ", " + datetime.getYear();
                String time = datetime.getHour() + " : " + datetime.getMinute() 
                        + " : " + datetime.getSecond() + " : " + datetime.getNano();
                String salesQuery = "select count(*) as no_of_sales "
                        + "FROM sales where transNo =" + transNo;
                ResultSet rs2 = Query.getDataQuery(salesQuery);
                if(rs2.next()){
                    int sales = rs2.getInt("no_of_sales");
                    transList.add(new Transaction(transNo,transBy,date,time,sales));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblTransactions.getItems().setAll(transList);
    }

    @FXML
    private void searchOnList(KeyEvent event) {
         FilteredList <Transaction> filteredList = new FilteredList<>(transList,p->true);  //wrap all drugs in filtered List 
            txtSearch.textProperty().addListener((ObservableValue,oldValue,newValue)->{
                  filteredList.setPredicate((Predicate<? super Transaction>) trans ->{  //set predicate when the text in the search field changes
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }  
                    String filterLowerCase = newValue.toLowerCase();
                    
                    if ((trans.getTransNo() + "").contains(filterLowerCase)){
                        return true;
                    }
                    if (trans.getUsername().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (trans.getTransaction_date().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
               
                    if ((trans.getNumOfSales() + "").toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    tblTransactions.setPlaceholder(new Text("No items match your search"));
                    return false;
                  });
                  SortedList<Transaction> sortedList = new SortedList<>(filteredList); 
                  sortedList.comparatorProperty().bind(tblTransactions.comparatorProperty());
                  tblTransactions.getItems().setAll(sortedList);
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
    }

    @FXML
    private void btnDescAction(ActionEvent event) {
    }

    @FXML
    private void btnViewLargeTableAction(ActionEvent event) {
    }
    
}
