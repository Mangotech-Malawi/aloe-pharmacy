/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.ManageUsers;

import aloe.model.PopWindow;
import aloe.model.QueryManager;
import aloe.model.User;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class ViewController implements Initializable {
    public static ObservableList<User> userList = FXCollections.observableArrayList();
    @FXML
    private StackPane stackPane;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private TableColumn<User, JFXButton> deleteCol;
    @FXML
    private TableView<User> tblUsers;
    @FXML
    private TableColumn<User, String> usernameCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User,String> firstnameCol;
    @FXML
    private TableColumn<User,String> lastnameCol;
    @FXML
    private TableColumn<User,String> contactCol;
    @FXML
    private TableColumn<User,String> genderCol;
    @FXML
    private TableColumn<User,String> dobCol;
    @FXML
    private TableColumn<User,String> regDateCol;
    @FXML
    private TableColumn<User,JFXButton> updateCol;
    @FXML
    private TableColumn<User,String> privilegeCol;
    @FXML
    private TableColumn<User,String> selectCol;
    public static int index = 0;
    @FXML
    private Label lblStatus;
    @FXML
    private ProgressBar progressBar;
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
 
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initCols();
        loadDataCols();
    } 
    private void initCols(){
        selectCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        firstnameCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastnameCol.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact")); 
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob")); 
        regDateCol.setCellValueFactory(new PropertyValueFactory<>("regDate")); 
        privilegeCol.setCellValueFactory(new PropertyValueFactory<>("privilege")); 
        updateCol.setCellValueFactory(new PropertyValueFactory<>("update"));
        deleteCol.setCellValueFactory(new PropertyValueFactory<>("delete")); 
    }
    private void loadDataCols(){
        QueryManager Query = new QueryManager();
        String userQuery = "SELECT * from users where privilege != 'admin';";
        ResultSet rs1 = Query.getDataQuery(userQuery);
        try {
            userList.clear();
            while(rs1.next()){
                String username = rs1.getString("username");
               String userDelQuery = "SELECT * FROM users_del WHERE username='" + username + "';";
               ResultSet rs2 = Query.getDataQuery(userDelQuery);
               if(rs2.next()){
                   //Do not do anything
               }else{
                    String email = rs1.getString("email");
                    String firstname = rs1.getString("firstname");
                    String lastname = rs1.getString("lastname");
                    String contact = rs1.getString("contact");
                    String gender = rs1.getString("gender");
                    String dob = rs1.getString("dob");
                    String regDate = rs1.getString("regDate");
                    String privilege = rs1.getString("privilege");
                    JFXButton delete = createDelBtn((username));
                    JFXButton update = createUpdateBtn((username));
                    userList.add(new User(createCheckBox(username),username,email,firstname,lastname,
                            gender,dob,contact,privilege,regDate,update,delete));
               } 
            }
            tblUsers.getItems().setAll(userList);
        } catch (SQLException ex) {
            Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
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
     private JFXButton createUpdateBtn(String username){
            JFXButton update = new JFXButton();
            update.setPrefWidth(300);
            update.setId(username);
            update.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE_ALT);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:  brown;");
            update.setGraphic(minimizeIcon);
            update.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                      PopWindow window = new PopWindow();
                      for(User user : userList){
                          if(user.getUsername().equals(username)){
                              index = userList.indexOf(user);
                               window.loadSmallWindow("/aloe/view/admin/ManageUsers/update.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                        userList.clear();
                                                        loadDataCols();
                                                    }}); 
                          }
                      }
                            
             }});
         return update;
    }
      private JFXButton createDelBtn(String username){
            JFXButton del = new JFXButton();
            del.setPrefWidth(300);
            del.setId(username);
            del.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:red;");
            del.setGraphic(minimizeIcon);
            del.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setHeaderText("Delete Message");
                        alert.setContentText("Are you sure you want \n to delete selected user or users");
                        alert.showAndWait();
                        if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                        QueryManager Query = new QueryManager();
                        User user = new User();
                        if(Query.execAction(user.deleteUser(username))){
                                loadDataCols();
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setHeaderText("Deletion Message");
                                alert.setContentText("User: " + username +" deleted successfully");
                                alert.showAndWait();
                               
                        }else{
                                alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText("Deletion Error");
                                alert.setContentText("Failed to delete user: " + username + "\ncheck network connection");
                                alert.showAndWait();
                        }
                     }
             }});
         return del;
    }
    @FXML
    private void searchOnList(KeyEvent event) {
          FilteredList <User> filteredList = new FilteredList<>(userList,p->true);  //wrap all drugs in filtered List 
            txtSearch.textProperty().addListener((ObservableValue,oldValue,newValue)->{
                  filteredList.setPredicate((Predicate<? super User>) user ->{  //set predicate when the text in the search field changes
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }  
                    String filterLowerCase = newValue.toLowerCase();
                    
                    if (user.getUsername().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (user.getFirstname().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (user.getLastname().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (user.getLastname().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (user.getContact().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    if (user.getPrivilege().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                     if (user.getGender().toLowerCase().contains(filterLowerCase)){
                        return true;
                    }
                    return false;
                  });
                  SortedList<User> sortedList = new SortedList<>(filteredList); 
                  sortedList.comparatorProperty().bind(tblUsers.comparatorProperty());
                  tblUsers.getItems().setAll(sortedList);
            });
    }

    @FXML
    private void selectAllChkBoxAction(ActionEvent event) {
          if(selectAllChkBox.isSelected()){
            for(User user : userList){
            JFXCheckBox check = user.getSelect();
            check.setSelected(true);
            user.setSelect(check);
            userList.set(userList.indexOf(user),user);
           }
          selectAllChkBox.setText("UNSELECT ALL");
        }else{
            for(User user : userList){
            JFXCheckBox check = user.getSelect();
            check.setSelected(false);
            user.setSelect(check);
            userList.set(userList.indexOf(user),user);
           }
           selectAllChkBox.setText("SELECT ALL");
        }
      
        tblUsers.getItems().clear();
        tblUsers.getItems().setAll(userList);
    }

    @FXML
    private void btnDeleteAction(ActionEvent event) {
          QueryManager Query = new QueryManager();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Delete Message");
            alert.setContentText("Are you sure you want \n to delete selected user or users");
            alert.showAndWait();
            boolean isAllDeleted= false;
            if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                for(User user : userList){
                    if(user.getSelect().isSelected()){
                        if(Query.execAction(user.deleteUser(user.getUsername()))){
                          isAllDeleted = true;
                        }else{
                          isAllDeleted = false;
                          break;
                        }
                    }
                }
                
                if(isAllDeleted){
                     alert.setHeaderText("Deletion Successfull");
                     alert.setContentText("You have deleted selected user or users");
                     alert.showAndWait();
                     selectAllChkBox.setSelected(false);
                     selectAllChkBox.setText("SELECT ALL");
                     initCols();
                     loadDataCols();
                }else{
                     Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setHeaderText("Restore Failed");
                    alert2.setContentText("Failed to delete entry or entries,\nCheck if any entry was selected");
                    alert2.showAndWait();
                }
                
            }             
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
