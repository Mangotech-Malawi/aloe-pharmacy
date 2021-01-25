/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager.tables;

import aloe.controller.pharmacist.stock.ViewDrugsController;
import static aloe.controller.pharmacist.stock.ViewDrugsController.drugList;
import static aloe.controller.pharmacist.stock.ViewDrugsController.index;
import aloe.model.Drug;
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
public class DrugsController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private JFXButton btnSortAsc;
    @FXML
    private JFXButton btnDesc;
    @FXML
    private JFXButton btnViewLargeTable;
    @FXML
    private TableView<Drug> tblDrugs;
    @FXML
    private TableColumn<Drug, Integer> idCol;
    @FXML
    private TableColumn<Drug, String> nameCol;
    @FXML
    private TableColumn<Drug, String> unitCol;
    @FXML
    private TableColumn<Drug, Double> sellingPriceCol;
    @FXML
    private TableColumn<Drug, Double> orderPriceCol;
    @FXML
    private TableColumn<Drug, String> firstCategoryCol;
    @FXML
    private TableColumn<Drug, String> seCategoryCol;
    @FXML
    private TableColumn<Drug, JFXButton> descCol;

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
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }    
  
     private void initCols(){
     
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasurement"));
        firstCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        seCategoryCol.setCellValueFactory(new PropertyValueFactory<>("secCategory"));
        sellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        orderPriceCol.setCellValueFactory(new PropertyValueFactory<>("orderPrice")); 
        descCol.setCellValueFactory(new PropertyValueFactory<>("descBtn")); // Description Button
        
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
                    String secCategory = rs1.getString("secCategory");
                    Double sellingPrice = rs1.getDouble("sellingPrice");
                    Double orderPrice = rs1.getDouble("orderPrice");
                    String unit = rs1.getString("measurement");
                    String description = rs1.getString("description");
                    JFXButton descBtn = createDescBtn((id));
                    drugList.add(new Drug(Integer.parseInt(id),name,category,secCategory,sellingPrice,
                            orderPrice,unit,description,descBtn));
               }
            }
            tblDrugs.getItems().setAll(drugList);
        } catch (SQLException ex) {
            Logger.getLogger(ViewDrugsController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                  if (drug.getSecCategory().toLowerCase().contains(filterLowerCase)){
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
        window.loadSmallWindow("/aloe/view/manager/tables/drugs.fxml", "", 
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
