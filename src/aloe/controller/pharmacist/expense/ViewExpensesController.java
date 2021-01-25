/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.expense;

import aloe.model.Expense;
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
import static jdk.nashorn.internal.codegen.ObjectClassGenerator.pack;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class ViewExpensesController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private JFXCheckBox selectAllChkBox;
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
    private TableColumn<Expense, JFXCheckBox> selectCol;
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
    @FXML
    private TableColumn<Expense, JFXButton> updateCol;
    @FXML
    private TableColumn<Expense, JFXButton> deleteCol;
    public static int index=0;
    public static ObservableList<Expense> expenseList = FXCollections.observableArrayList();
    @FXML
    private JFXButton btnDelete;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
    private void initCols(){
       selectCol.setCellValueFactory(new PropertyValueFactory<>("select"));
       expenseNoCol.setCellValueFactory(new PropertyValueFactory<>("expenseNo"));
       amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
       categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
       regDateCol.setCellValueFactory(new PropertyValueFactory<>("regDate"));
       descriptionCol.setCellValueFactory(new PropertyValueFactory<>("descBtn"));
       updateCol.setCellValueFactory(new PropertyValueFactory<>("update"));
       deleteCol.setCellValueFactory(new PropertyValueFactory<>("delete"));
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
                    expenseList.add(new Expense(createCheckBox(expenseNo +""),expenseNo,amount,category,description,regDate,
                    createDescBtn(expenseNo +""),createUpdateBtn(expenseNo +""),createDelBtn(expenseNo +"")));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ViewExpensesController.class.getName()).log(Level.SEVERE, null, ex);
        }
       tblExpenses.getItems().setAll(expenseList);
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
                      for(Expense expense : expenseList){
                          if((expense.getExpenseNo() +"").equals(id)){
                              index = expenseList.indexOf(expense);
                               window.loadSmallWindow("/aloe/view/pharmacist/expense/updateExpense.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                        initCols();
                                                        loadDataCols("ASC");
                                                    
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
                        Expense expense = new Expense();
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setHeaderText("Deletion Message");
                        alert.setContentText("Do you really want to delete this expense?");
                        alert.showAndWait();
                        if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                            
                            
                            if(Query.execAction(expense.delExpense(Integer.parseInt(id)))){
                                if(Query.execAction(expense.updateExpenseIsDel(Integer.parseInt(id)))){
                                    expenseList.clear();
                                    initCols();
                                    loadDataCols("ASC");
                                }else{
                                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                                    alert2.setHeaderText("Deletion Error");
                                    alert2.setContentText("Failed to delete expense");
                                    alert2.showAndWait();
                                }
                              
                            }else{
                                 Alert alert2 = new Alert(Alert.AlertType.ERROR);
                                alert2.setHeaderText("Deletion Error");
                                alert2.setContentText("Failed to delete expense");
                                alert2.showAndWait();
                            }
                        }    
             }});
         return del;
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

    @FXML
    private void selectAllChkBoxAction(ActionEvent event) {
         if(selectAllChkBox.isSelected()){
            for(Expense expense : expenseList){
            JFXCheckBox check = expense.getSelect();
            check.setSelected(true);
            expense.setSelect(check);
            expenseList.set(expenseList.indexOf(expense),expense);
           }
          selectAllChkBox.setText("UNSELECT ALL");
        }else{
            for(Expense expense : expenseList){
            JFXCheckBox check = expense.getSelect();
            check.setSelected(false);
            expense.setSelect(check);
            expenseList.set(expenseList.indexOf(expense),expense);
           }
           selectAllChkBox.setText("SELECT ALL");
        }
      
        tblExpenses.getItems().clear();
        tblExpenses.getItems().setAll(expenseList);
    }

    @FXML
    private void btnDeleteAction(ActionEvent event) {
        QueryManager Query = new QueryManager();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Delete Message");
            alert.setContentText("Are you sure you want \n to delete selected expense or expenses");
            alert.showAndWait();
            boolean isAllDeleted= false;
            if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                for(Expense expense : expenseList){
                    if(expense.getSelect().isSelected()){
                       
                       if(Query.execAction(expense.delExpense(expense.getExpenseNo()))){
                           if (Query.execAction(expense.updateExpenseIsDel(expense.getExpenseNo()))){
                                 isAllDeleted = true;
                             }else{
                                isAllDeleted = false;
                                break;
                              }
                            }
                        }
  
                    }
                }
                
                if(isAllDeleted){
                     alert.setHeaderText("Deletion Successfull");
                     alert.setContentText("You have deleted selected expense or expenses");
                     alert.showAndWait();
                     selectAllChkBox.setSelected(false);
                     selectAllChkBox.setText("SELECT ALL");
                     initCols();
                     loadDataCols("ASC");
                }else{
                     Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setHeaderText("Restore Failed");
                    alert2.setContentText("Failed to delete expense or expenses,\nCheck if any expense was selected");
                    alert2.showAndWait();
                }
                            
    }

    @FXML
    private void btnExcelAction(ActionEvent event) {
        PopWindow window  = new PopWindow();
        Effect effect = new GaussianBlur(4.0);
        primaryStage.getScene().getRoot().setEffect(effect);
        window.loadSmallWindow("/aloe/view/pharmacist/expense/reports/excelReports.fxml", "", 
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
        window.loadSmallWindow("/aloe/view/pharmacist/expense/reports/pdfReports.fxml", "", 
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
        window.loadSmallWindow("/aloe/view/pharmacist/expense/reports/wordReports.fxml", "", 
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
        window.loadSmallWindow("/aloe/view/pharmacist/expense/viewExpenses.fxml", "", 
                true,true,true,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
                    Effect effect = new GaussianBlur(0.0);
                    primaryStage.getScene().getRoot().setEffect(effect);      
            }}); 
    }
    
}
