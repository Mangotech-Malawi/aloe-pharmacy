/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.userLog;

import aloe.model.PopWindow;
import aloe.model.QueryManager;
import aloe.model.UserLog;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class LogViewController implements Initializable {
     public static ObservableList<UserLog> userLogList = FXCollections.observableArrayList();
     public static int index =0;
    @FXML
    private StackPane stackPane;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private TableView<UserLog> tblLogs;
    @FXML
    private TableColumn<UserLog,String> idCol;
    @FXML
    private TableColumn<UserLog,String> usernameCol;
    @FXML
    private TableColumn<UserLog,String> logindateCol;
    @FXML
    private TableColumn<UserLog,String> logOutCol;
    @FXML
    private TableColumn<UserLog,String> inTimeCol;
    @FXML
    private TableColumn<UserLog,String> outTimeCol;
    @FXML
    private TableColumn<UserLog,JFXButton> activityCol;
    @FXML
    private TableColumn<UserLog, String> privilegeCol;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initCols();
        loadColData();

    }    
    private void initCols(){
        idCol.setCellValueFactory(new PropertyValueFactory<>("logNumber"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        privilegeCol.setCellValueFactory(new PropertyValueFactory<>("privilege"));
        logindateCol.setCellValueFactory(new PropertyValueFactory<>("regDate"));
        logOutCol.setCellValueFactory(new PropertyValueFactory<>("outDate"));
        inTimeCol.setCellValueFactory(new PropertyValueFactory<>("inTime")); 
        outTimeCol.setCellValueFactory(new PropertyValueFactory<>("outTime")); // Description Button
        activityCol.setCellValueFactory(new PropertyValueFactory<>("activitiesBtn")); //New Drug entry  button
    }
    @FXML
    private void searchOnList(KeyEvent event) {
         FilteredList <UserLog> filteredList = new FilteredList<>(userLogList,p->true);  //wrap all drugs in filtered List 
            txtSearch.textProperty().addListener((ObservableValue,oldValue,newValue)->{
                  filteredList.setPredicate((Predicate<? super UserLog>) userLog ->{  //set predicate when the text in the search field changes
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }  
                    String filterLowerCase = newValue.toLowerCase();
                    
                    if (userLog.getUsername().contains(filterLowerCase)){
                        return true;
                    }
                    if (userLog.getPrivilege().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                   if (userLog.getPrivilege().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    return false;
                  });
                  SortedList<UserLog> sortedList = new SortedList<>(filteredList); 
                  sortedList.comparatorProperty().bind(tblLogs.comparatorProperty());
                  tblLogs.getItems().setAll(sortedList);
            });
    }
    private void loadColData(){
        QueryManager Query = new QueryManager();
        String query1 = "SELECT * from userLogs ";
        ResultSet rs = Query.getDataQuery(query1);
         try {
             userLogList.clear();
             while(rs.next()){
                  
                LocalDateTime inDate =  LocalDateTime.parse(rs.getString("logInDate"));
                 

                String logNum = rs.getString("logNo");
                 

                JFXButton activity = createActivityBtn(logNum);
               
                  LocalDateTime outDate =  LocalDateTime.parse(rs.getString("logOutDate"));
                    System.out.println("This One Run 5");
                String userQuery = "SELECT privilege FROM users WHERE username = '" + rs.getString("username") + "';"; 
                ResultSet rs2 = Query.getDataQuery(userQuery);
                if(rs2.next()){
                    String privilege = rs2.getString("privilege");
                      userLogList.add(new UserLog(Integer.parseInt(logNum),rs.getString("username"),privilege,inDate.getDayOfMonth() + " " + inDate.getMonth().name() + "," + inDate.getYear(),
                outDate.getDayOfMonth() + " " + outDate.getMonth().name() + "," + outDate.getYear(),
                inDate.getHour() + ":" + inDate.getMinute(),
                        outDate.getHour() + ":" + outDate.getMinute(),activity));
                }
             
         
             
             }
         } catch (SQLException ex) {
             Logger.getLogger(LogViewController.class.getName()).log(Level.SEVERE, null, ex);
         }
         tblLogs.getItems().setAll(userLogList);
    }
      private JFXButton createActivityBtn(String id){
         JFXButton activity = new JFXButton();
            activity.setPrefWidth(80);
            activity.setId(id);
            activity.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:red;");
            activity.setGraphic(minimizeIcon);
            activity.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                      PopWindow window = new PopWindow();
                      for(UserLog userLog : userLogList){
                          if((userLog.getLogNumber() + "").equals(id)){
                              index = userLogList.indexOf(userLog);
                               window.loadSmallWindow("/aloe/view/admin/userLog/logActivity.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                        userLogList.clear();
                                                        loadColData();
                                                    }}); 
                          }
                      }
                            
             }});
         return activity;
    }

    @FXML
    private void selectAllChkBoxAction(ActionEvent event) {
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
