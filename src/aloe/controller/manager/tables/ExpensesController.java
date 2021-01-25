/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager.tables;

import aloe.controller.pharmacist.expense.ViewExpensesController;
import static aloe.controller.pharmacist.expense.ViewExpensesController.expenseList;
import static aloe.controller.pharmacist.expense.ViewExpensesController.index;
import aloe.model.Expense;
import aloe.model.PopWindow;
import static aloe.model.PopWindow.primaryStage;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
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
public class ExpensesController implements Initializable {

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
    private TableView<Expense> tblExpenses;
     @FXML
    private TableColumn<Expense, Integer> expenseNoCol;
    @FXML
    private TableColumn<Expense, Double> amountCol;
    @FXML
    private TableColumn<Expense, String> categoryCol;
    @FXML
    private TableColumn<Expense, String> regDateCol;
    @FXML
    private TableColumn<Expense, JFXButton> descriptionCol;

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
                   loadDataCols("ASC");
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }    

    @FXML
    private void searchOnList(KeyEvent event) {
        FilteredList <Expense> filteredList = new FilteredList<>(expenseList,p->true);  //wrap all drugs in filtered List 
            txtSearch.textProperty().addListener((ObservableValue,oldValue,newValue)->{
                  filteredList.setPredicate((Predicate<? super Expense>) expense ->{  //set predicate when the text in the search field changes
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }  
                    String filterLowerCase = newValue.toLowerCase();
                    
                    if ((expense.getExpenseNo()+"").contains(filterLowerCase)){
                        return true;
                    }
                    if (expense.getCategory().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                 
                    tblExpenses.setPlaceholder(new Text("No items match your search"));
                    return false;
                  });
                  SortedList<Expense> sortedList = new SortedList<>(filteredList); 
                  sortedList.comparatorProperty().bind(tblExpenses.comparatorProperty());
                  tblExpenses.getItems().setAll(sortedList);
            });
    }
     private void initCols(){
       expenseNoCol.setCellValueFactory(new PropertyValueFactory<>("expenseNo"));
       amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
       categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
       regDateCol.setCellValueFactory(new PropertyValueFactory<>("regDate"));
       descriptionCol.setCellValueFactory(new PropertyValueFactory<>("descBtn"));
    }
    private void loadDataCols(String sort){
        QueryManager Query = new QueryManager();//Connecting to database
        String expenseQuery = "SELECT * FROM expenses ORDER BY expenseNo " + sort + ";";
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        try {
            expenseList.clear();
            while(rs1.next()){
                int expenseNo = rs1.getInt("expenseNo");
                String expenseDelQuery = "SELECT expenseNo FROM expenses_del WHERE expenseNo='" + expenseNo + "';";
                ResultSet rs2 = Query.getDataQuery(expenseDelQuery);
                if(rs2.next()){
                    //Do not add it on list because it is deleted
                }else{
                    double amount  = rs1.getDouble("amount");
                    String category = rs1.getString("category");
                    String description = rs1.getString("description");
                    LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                    String regDate = date.getDayOfMonth() +  " " + 
                            date.getMonth().name() + ", " +  date.getYear();
                    expenseList.add(new Expense(expenseNo,amount,category,description,regDate,
                    createDescBtn(expenseNo +"")));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ViewExpensesController.class.getName()).log(Level.SEVERE, null, ex);
        }
       tblExpenses.getItems().setAll(expenseList);
    }
     private JFXButton createDescBtn(String id){
            JFXButton desc = new JFXButton();
            desc.setPrefWidth(80);
            desc.setId(id);
            desc.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill: #16410a;");
            desc.setGraphic(minimizeIcon);
            desc.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                      PopWindow window = new PopWindow();
                      for(Expense expense : expenseList){
                          if((expense.getExpenseNo() +"").equals(id)){
                              index = expenseList.indexOf(expense);
                               window.loadSmallWindow("/aloe/view/pharmacist/expense/expenseDescription.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                        loadDataCols("ASC");
                                                    }}); 
                          }
                      }
                            
             }});
         return desc;
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
        window.loadSmallWindow("/aloe/view/manager/tables/expenses.fxml", "", 
                true,true,true,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
                }}); 
        
    }
    
}
