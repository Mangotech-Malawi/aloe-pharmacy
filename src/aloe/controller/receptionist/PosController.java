/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.receptionist;

import aloe.controller.LoginController;
import static aloe.controller.LoginController.username;
import static aloe.controller.pharmacist.stock.ViewEntriesController.entryList;
import aloe.model.Cart;
import aloe.model.Entry;
import aloe.model.PopWindow;
import static aloe.model.PopWindow.loc;
import aloe.model.QueryManager;
import static aloe.model.QueryManager.conn;
import aloe.model.Sale;
import aloe.model.Transaction;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import org.controlsfx.control.textfield.AutoCompletionBinding.AutoCompletionEvent;
import org.controlsfx.control.textfield.TextFields;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class PosController implements Initializable {
    public static ObservableList<Cart> cartList = FXCollections.observableArrayList();
    public static int index =0;
    public static String item;
    private BorderPane borderpane;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnMaximize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton bntHome;
    private StackPane switchPane;
    @FXML
    private Label lblUsername;
    @FXML
    private JFXTextField txtCode;
    @FXML
    private Label lblDrugName;
    @FXML
    private Label lblCategory;
    @FXML
    private Label lblQuantity;
    @FXML
    private Label lblSelling;
    @FXML
    private Label lblOrderPrice;
    @FXML
    private Label lblExpiryDate;
    @FXML
    private Label lblTotalCharge;
    @FXML
    private Label lblBalance;
    @FXML
    private JFXButton btnSettings;
    @FXML
    private JFXTextField txtQuantity;
    @FXML
    private JFXTextField txtPayment;
    @FXML
    private TableView<Cart> tblCart;
    @FXML
    private TableColumn<Cart, String> posIdCol;
    @FXML
    private TableColumn<Cart, String> drugNameCol;
    @FXML
    private TableColumn<Cart, String> categoryCol;
    @FXML
    private TableColumn<Cart, Integer> quantityCol;
    @FXML
    private TableColumn<Cart, Double> chargeCol;
    @FXML
    private TableColumn<Cart, String> expiryDateCol;
    @FXML
    private TableColumn<Cart, JFXButton> removeCol;
    @FXML
    private TableColumn<Cart, Double> orderPriceCol;
    private int batchNo; // this can also be packNo depending on the scanned drugs
    @FXML
    private JFXButton btnBack;
    @FXML
    private StackPane stackPane;
    @FXML
    private JFXButton btnAdd;
    @FXML
    private JFXButton btnTransaction;
    @FXML
    private Label lblTotalToday;
    @FXML
    private JFXButton btnDone;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXTextField txtDrugName;
    @FXML
    private JFXComboBox<String> entryCombo;
    @FXML
    private JFXComboBox<String> packCombo;
    private Cart preCart; // Used to load the label data
    private  double totalCharge = 0.00;
    private  int transNo = 0;
    @FXML
    private TableColumn<Cart, String> itemCol;
    private ObservableList<String> entryNoList = FXCollections.observableArrayList();
    private ObservableList<String> packIdList = FXCollections.observableArrayList();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lblUsername.setText("Logged in as " + username);
        if(PopWindow.primaryStage.isMaximized()){
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(minimizeIcon);
        }else{
            FontAwesomeIconView maxmizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE);
            maxmizeIcon.setGlyphSize(17);
            maxmizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(maxmizeIcon);
        }  
       initCols();
       setTransNo();
       loadDrugNames();
       loadTotalSoldToday();
    }  
    private void loadTotalSoldToday(){
        try {
            QueryManager Query = new QueryManager();
            String salesQuery = "SELECT charge,transNo FROM sales ";
            ResultSet rs1 = Query.getDataQuery(salesQuery);
            double charge = 0.0;
            LocalDate date = LocalDate.now();
            while(rs1.next()){
               String transQuery = "SELECT trans_date FROM transactions "
                        + "where transNo='" + rs1.getString(2) + "' AND trans_by ='" + username + "';";
               ResultSet rs2 = Query.getDataQuery(transQuery);
               if(rs2.next()){
                    LocalDateTime transDate = LocalDateTime.parse(rs2.getString(1));
                    if(transDate.toLocalDate().equals(date)){
                         charge = charge + rs1.getDouble(1);
                    }
               }
            }
            lblTotalToday.setText(NumberFormat.getCurrencyInstance().format(charge));
        } catch (SQLException ex) {
            Logger.getLogger(PosController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void loadDrugNames(){
        try {
            ObservableList<String> drugNameList = FXCollections.observableArrayList();
            QueryManager Query = new QueryManager();
            String drugNameQuery = "SELECT name FROM drugs where id NOT IN (SELECT id FROM drugs_del)"
                    + " AND id IN (SELECT id FROM entries WHERE batchNo NOT IN (SELECT batchNo FROM entries_del));";
            ResultSet rs1 = Query.getDataQuery(drugNameQuery);
            while(rs1.next()){
                drugNameList.add(rs1.getString(1));
            }
           
                   TextFields.bindAutoCompletion(txtDrugName, drugNameList)
                           .setOnAutoCompleted((AutoCompletionEvent<String> event) -> {
                      entryNoList.clear();
                      packIdList.clear();
                      entryCombo.getItems().clear();
                      packCombo.getItems().clear();
                      loadDrugName();        
            });
          
          
                           
        } catch (SQLException ex) {
            Logger.getLogger(PosController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
        private void initCols(){
         posIdCol.setCellValueFactory(new PropertyValueFactory<>("cartId"));
        itemCol.setCellValueFactory(new PropertyValueFactory<>("item"));
        drugNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        chargeCol.setCellValueFactory(new PropertyValueFactory<>("charge")); 
        expiryDateCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate")); 
        orderPriceCol.setCellValueFactory(new PropertyValueFactory<>("orderPrice")); 
        removeCol.setCellValueFactory(new PropertyValueFactory<>("delete")); 
    }

  
    @FXML
    private void btnMnimizeAction(ActionEvent event) {
           PopWindow.primaryStage.setIconified(true);
    }

    @FXML
    private void btnMaximizeAction(ActionEvent event) {
            if(PopWindow.primaryStage.isMaximized()){
            PopWindow.primaryStage.setMaximized(false);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(minimizeIcon);
        }else{
            PopWindow.primaryStage.setMaximized(true);
            FontAwesomeIconView maxmizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT);
            maxmizeIcon.setGlyphSize(17);
            maxmizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(maxmizeIcon);
        }
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        stackPane.getScene().getWindow().hide();
    }



    @FXML
    private void btnBackAction(ActionEvent event) {
            PopWindow window = new PopWindow();
        if(PopWindow.primaryStage.isMaximized()){
               window.loadWindow(loc.pop(), "Pharmacy System : Transactions",true,true,false,false);
         }else{
                window.loadWindow(loc.pop(), "Pharmacy System : Transactions",true,false,false,false);
         
        }
        stackPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnHomeAction(ActionEvent event) {
        PopWindow window = new PopWindow();
      if(PopWindow.primaryStage.isMaximized()){
            window.loadWindow("/aloe/view/receptionist/Home.fxml", "Pharmacy System : Home",true,true,false,false);
      }else{
             window.loadWindow("/aloe/view/receptionist/Home.fxml", "Pharmacy System : Home",true,false,false,false);
      }
      stackPane.getScene().getWindow().hide();
    }

    
    @FXML
    private void btnTransactionAction(ActionEvent event) {
          PopWindow window = new PopWindow();
         loc.push("/aloe/view/receptionist/Pos.fxml");
        if(PopWindow.primaryStage.isMaximized()){
              window.loadWindow("/aloe/view/receptionist/Transactions.fxml", "Pharmacy System : Reports",true,true,false,false);
        }else{
              window.loadWindow("/aloe/view/receptionist/Transactions.fxml", "Pharmacy System : Reports",true,false,false,false);
        }
        
        stackPane.getScene().getWindow().hide();
    }
    
    @FXML
    private void btnSettingsAction(ActionEvent event) {
          PopWindow window = new PopWindow();
         loc.push("/aloe/view/receptionist/Pos.fxml");
        if(PopWindow.primaryStage.isMaximized()){
              window.loadWindow("/aloe/view/receptionist/settings.fxml", "Pharmacy System : Reports",true,true,false,false);
        }else{
              window.loadWindow("/aloe/view/receptionist/settings.fxml", "Pharmacy System : Reports",true,false,false,false);
        }

        stackPane.getScene().getWindow().hide();
    }
    
    @FXML
    private void btnCancelAction(ActionEvent event) {
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Cancel Message ");
        alert.setContentText("Are you sure you want to cancel everything?");
        alert.showAndWait();
        if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
           lblBalance.setText(NumberFormat.getCurrencyInstance().format(0));
           lblTotalCharge.setText(NumberFormat.getCurrencyInstance().format(0));
           totalCharge = 0.00;
           cartList.clear();
           tblCart.getItems().clear();
           txtPayment.clear();
           txtQuantity.clear();
           clearControls();
        }
                
      
    }
    
    private void setTransNo(){
        try {
            QueryManager Query1 = new QueryManager();
            String lastTransNoQuery = "SELECT transNo FROM transactions ORDER BY transNo DESC LIMIT 1";
            ResultSet rs = Query1.getDataQuery(lastTransNoQuery);
            
            if(rs.next()){
                transNo = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PosController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btnDoneAction(ActionEvent event) {
        Alert transAlert = new Alert(Alert.AlertType.CONFIRMATION);
        transAlert.setHeaderText("Transactiion Message");
        transAlert.setContentText("Are you sure you want to complete transaction?");
        transAlert.showAndWait();
        if(transAlert.getResult().getText()!=null && transAlert.getResult().getText().equals("OK")){
       
            if(!cartList.isEmpty()){ //Making sure that there is something on cart to proceed transaction
                try {
                           //Making sure that payment field is entered
                            if(!txtPayment.getText().isEmpty()){
                                double payment = Double.parseDouble(txtPayment.getText().trim());
                                double charge  = totalCharge;

                                //Making sure that the payment is not less than charge of the transaction
                                if(payment >= charge){
                                   QueryManager Query = new QueryManager(); 

                                   //This variable is set to true if one or more sale has been added to database
                                   boolean isAdded = false;
                                    conn.setAutoCommit(false);
                                    String startTransQuery = "START TRANSACTION";
                                    if(Query.execAction(startTransQuery)){
                                    for(Cart cart : cartList){ //Loop for transacting all items on pos cart

                                              if(("Entry " + cart.getCartId()).equals(cart.getItem())){ // If the cart id does not have barcode but it is in entries table
                                                  System.out.println("Entry " + cart.getCartId() + " = " + cart.getItem());
                                                 //Updating the entry quantity 
                                                  if(Query.insertStatement(
                                                            cart.updateEntryQty(cart.getQuantity(),cart.getCartId()))){

                                                            //Instatiating the sale object
                                                            System.out.println(" Trans No: " + transNo + 1 ) ;
                                                            System.out.println(" Cart Id : " + cart.getCartId() ) ;
                                                           System.out.println(" Quantity : " + cart.getQuantity() ) ;
                                                                  System.out.println("Charge : " + cart.getCharge()) ;
                                                                    System.out.println("Order Price: " + cart.getOrderPrice() ) ;
                                                                    
                                                                    
                                                            Sale sale  = new Sale(transNo + 1,cart.getCartId(),cart.getQuantity(),
                                                            cart.getCharge(),cart.getOrderPrice(),"Entry");
                                                            //Add the sale details of current item
                                                            if(Query.insertStatement(sale.addSale())){
                                                                 isAdded = true;
                                                            }

                                                   }
                                             }else if(("Pack " + cart.getCartId()).equals(cart.getItem())){ // If if does not exist in entries but in packs
                                                   System.out.println("Entry " + cart.getCartId() + " = " + cart.getItem());
                                                 //Updating the pack quantity 
                                                  if(Query.insertStatement(cart.updateNumOfPacks(cart.getQuantity(),cart.getCartId()))){
                                                            //Instatiating the sale object
                                                            Sale sale  = new Sale(transNo + 1,cart.getCartId(),cart.getQuantity(),
                                                            cart.getCharge(),cart.getOrderPrice(),"Pack");
                                                            //Add the sale details of current item
                                                            if(Query.insertStatement(sale.addSale())){
                                                              isAdded = true;
                                                             }
                                                    }
                                             }


                                        }
                                            /*Complete transaction if item from cart was added to sales
                                              or show transaction failed alert
                                            */
                                            if(isAdded){

                                                //Instatiating transactiion object
                                                Transaction trans = new Transaction(LoginController.username,LocalDateTime.now().toString());

                                                //Addin transaction details to table
                                                if(Query.insertStatement(trans.addTransaction())){
                                                    String commitTransQuery = "COMMIT;";
                                                     if(Query.execAction(commitTransQuery)){
                                                        //If transaction was successful then show success alert
                                                        Query.ClearDB();
                                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                                        alert.setHeaderText("Success");
                                                        alert.setContentText("Transaction Complete!!");
                                                        alert.showAndWait();
                                                     }
                                                }else{
                                                       String commitTransQuery = "ROLLBACK;";
                                                     if(Query.execAction(commitTransQuery)){
                                                        //If transaction failed then show error alert
                                                        Query.ClearDB();

                                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                                        alert.setHeaderText("Transaction Failed");
                                                        alert.setContentText("Sorry, Transaction is incomplete");
                                                        alert.showAndWait();
                                                     }
                                                }

                                               //Clear the Pos for new Transaction
                                                lblBalance.setText(NumberFormat.getCurrencyInstance().format(0));
                                                lblTotalCharge.setText(NumberFormat.getCurrencyInstance().format(0));
                                                
                                                totalCharge = 0.00;
                                                cartList.clear();
                                                entryNoList.clear();
                                                packIdList.clear();
                                                tblCart.getItems().clear();
                                                txtPayment.clear();
                                                txtQuantity.clear();
                                             
                                            }else{
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setHeaderText("Transaction Failed");
                                                alert.setContentText("Sorry, Transaction is incomplete");
                                                alert.showAndWait();
                                            }

                                     }
                                }else{
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText("Calculation Error");
                                    alert.setContentText("The money paid is less than charge");
                                    alert.showAndWait();
                                }
                            }else{
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText("Entry Error");
                                alert.setContentText("Please enter payment to proceed!");
                                alert.showAndWait();
                            }


                } catch (SQLException ex) {
                    Logger.getLogger(PosController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                 Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Entry Error");
                alert.setContentText("There is nothing on cart \n Add items on cart first!");
                alert.showAndWait();
            }
            loadTotalSoldToday();
            setTransNo();
            loadDrugNames();
            loadTotalSoldToday();
        }
    }

    @FXML
    private void scanBarcode(KeyEvent event) {
        String code = txtCode.getText().trim();
        QueryManager Query = new QueryManager();
        String barcodeQuery = "SELECT * FROM barcodes where code ='" + code + "';";
        ResultSet rs1 = Query.getDataQuery(barcodeQuery);
        try {
            if(rs1.next()){
               if(Boolean.parseBoolean(rs1.getString("isBatchNo"))){
                   batchNo = rs1.getInt("batchNo");
                   populateEntryLabels(batchNo, Query);
              
               }else{
                  batchNo = rs1.getInt("batchNo");
                  
                  String packDel = "SELECT * FROM packs_del where packId='" + batchNo + "'";
                  ResultSet rs = Query.getDataQuery(packDel);
                  if(rs.next()){
                       //Don't get the details of deleted packs because entry is deleted 
                  }else{
                       populatePackLabels(batchNo,Query);
                  }
               }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PosController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void loadDrugName(){
        entryCombo.getItems().clear();
        packCombo.getItems().clear();
        entryNoList.clear();
        packIdList.clear();
         String drugName = txtDrugName.getText().trim();
        if(!drugName.isEmpty()){
             loadDrugEntryComboData(drugName);
       
        }
    }
   
    @FXML
    private void onDrugNameEntered(KeyEvent event) {
        loadDrugName();
    }
       @FXML
    private void onEntryNoSelected(ActionEvent event) {
        try{
            QueryManager Query = new QueryManager();
            populateEntryLabels(Integer.parseInt(entryCombo.getValue()),Query);
            packIdList.clear();
            packCombo.getItems().clear();
            loadPackComboData(entryCombo.getValue());
        }catch(Exception ex){
      
        }
    }

    @FXML
    private void onPackNoSelected(ActionEvent event) {
        try{
        QueryManager Query = new QueryManager();
        
        populatePackLabels(Integer.parseInt(packCombo.getValue()),Query);
        String drugName = txtDrugName.getText().trim();
        entryCombo.getItems().clear();
        entryNoList.clear();
        if(!drugName.isEmpty()){
             loadDrugEntryComboData(drugName);
       
        }
        }catch(Exception ex){
            
        }
    }

    private void loadDrugEntryComboData(String drugName){
       QueryManager Query = new QueryManager();
       String drugQuery = "SELECT id FROM drugs WHERE name ='" + drugName + "';";
       ResultSet rs1 = Query.getDataQuery(drugQuery);
        try {
            if(rs1.next()){
               String id = rs1.getString("id");
               String entryQuery = "SELECT batchNo FROM entries WHERE id ='" + id + "';";
               ResultSet rs2 = Query.getDataQuery(entryQuery);
               while(rs2.next()){
                   String batchNo = rs2.getString("batchNo");
                   String drugDelQuery = "SELECT * FROM entries_del WHERE batchNo ='" + batchNo + "'";
                   ResultSet rs3 = Query.getDataQuery(drugDelQuery);
                   if(rs3.next()){
                       //Don't do anything because the drug is deleted
                   }else{
                       entryNoList.add(batchNo);
                   }
               }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PosController.class.getName()).log(Level.SEVERE, null, ex);
        }
       entryCombo.getItems().setAll(entryNoList);
    }
    
    private void loadPackComboData(String batchNo){
        try {
            
            QueryManager Query = new QueryManager();
            String packQuery = "SELECT packId FROM packs WHERE batchNo = '" + batchNo + "';";
            ResultSet rs1 = Query.getDataQuery(packQuery);
            while(rs1.next()){
               String packId = rs1.getString("packId");
               String packDelQuery = "SELECT * FROM packs_del WHERE packId ='" + packId + "'";
               ResultSet rs2 = Query.getDataQuery(packDelQuery);
               if(rs2.next()){
                   //Do nothing because the pack is deleted
               }else{
                   packIdList.add(packId);
               }
            }
            packCombo.getItems().setAll(packIdList);
        } catch (SQLException ex) {
            Logger.getLogger(PosController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void populateEntryLabels(int batchNo, QueryManager Query){
        try {
            String entryDelQuery  = "SELECT * FROM entries_del where batchNo ='" + batchNo + "';";
            ResultSet rs = Query.getDataQuery(entryDelQuery);
            if(rs.next()){
                //Don't get the details of deleted entries because batchNo is deleted
            }else{
                String entryQuery = "SELECT * FROM entries where batchNo = '" + batchNo  + "';";
                ResultSet rs2 = Query.getDataQuery(entryQuery);
                if(rs2.next()){
                    String drugId = rs2.getString("id");
                    String drugQuery = "SELECT * FROM drugs where id ='" + drugId +"';";
                    ResultSet rs3 = Query.getDataQuery(drugQuery);
                    if(rs3.next()){
                       
                        this.batchNo = batchNo;
                        String name = rs3.getString("name");
                        String category = rs3.getString("firstCategory");
                        Double sellingPrice = rs3.getDouble("sellingPrice");
                        Double orderPrice = rs3.getDouble("orderPrice");
                        LocalDate date =  LocalDate.parse(rs2.getString("expiryDate"));
                        String formatedDate = date.getDayOfMonth() + " " + date.getMonth() + ", " + date.getYear();
                        int quantity = rs2.getInt("quantity");
                        //Setting pre cart details to be loaded on cart later
                        item = "Entry " + this.batchNo;
                        preCart = new Cart(name,category,quantity,sellingPrice,orderPrice,formatedDate);
                        
                        //Loading the scanned details on labels
                        lblDrugName.setText(name);
                        lblCategory.setText(category);
                        lblSelling.setText(NumberFormat.getCurrencyInstance().format(sellingPrice));
                        lblOrderPrice.setText(NumberFormat.getCurrencyInstance().format(orderPrice));
                        
                        lblExpiryDate.setText(date.getDayOfMonth() + " " + date.getMonth() + ", " + date.getYear());
                        lblQuantity.setText(rs2.getString("quantity"));
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PosController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void populatePackLabels(int packId, QueryManager Query){
        try {
            String packQuery = "SELECT * from packs where packId = '" + packId + "'";
            ResultSet rs2 = Query.getDataQuery(packQuery);
            if(rs2.next()){
                String batchNo = rs2.getString("batchNo");//Getting entryNo or batchNo of SELECTED Pack
                String entryQuery = "SELECT * FROM entries where batchNo = '" + batchNo  + "';";
                ResultSet rs3  = Query.getDataQuery(entryQuery);
                if(rs3.next()){
                    String drugId = rs3.getString("id");
                    String drugQuery = "SELECT * FROM drugs where id ='" + drugId +"';";
                    ResultSet rs4 = Query.getDataQuery(drugQuery);
                    if(rs4.next()){
                        this.batchNo = packId;
                        String name = rs4.getString("name");
                        String category = rs4.getString("firstCategory");
                        Double  sellingPrice = rs2.getDouble("price");
                        Double  orderPrice = rs4.getDouble("orderPrice") * rs2.getInt("quantity");
                        LocalDate date =  LocalDate.parse(rs3.getString("expiryDate"));
                        String formatedDate = date.getDayOfMonth() + " " + date.getMonth() + ", " + date.getYear();
                        int quantity = rs2.getInt("numOfPacks");
                        
                         //Setting pre cart details to be loaded on cart later
                        item = "Pack " + this.batchNo;
                        preCart = new Cart(name,category,quantity,sellingPrice,orderPrice,formatedDate);
                        
                        //Loading the scanned details on labels
                        lblDrugName.setText(name);
                        lblCategory.setText(category);
                        lblSelling.setText(NumberFormat.getCurrencyInstance().format(sellingPrice) + " / Pack");
                        lblOrderPrice.setText(""+ NumberFormat.getCurrencyInstance().format(orderPrice) + " / Pack");
                        lblExpiryDate.setText(formatedDate);
                        lblQuantity.setText( quantity + " Packs With " + rs2.getString("quantity") + " Drugs/Pack"  );
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PosController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

    @FXML
    private void btnAddAction(ActionEvent event) {
         if(!txtQuantity.getText().trim().isEmpty()){
             try{
             int quantity = Integer.parseInt(txtQuantity.getText().trim());
             if(quantity <= preCart.getQuantity()){
                 double charge =0;
                 boolean isOnCart = false;
                 charge =  preCart.getSellingPrice() * quantity; 
                 JFXButton delete = createDelBtn(item);
                 for(Cart cart : cartList){
                     if( (item.equalsIgnoreCase(cart.getItem()))){
                          isOnCart = true;
                     }else if((item.equalsIgnoreCase(cart.getItem()))){
                          isOnCart = true;
                       }
                 }
                 if(!isOnCart){
                     
                    cartList.add( new Cart(item,batchNo,preCart.getName(),preCart.getCategory(),quantity,charge,
                            preCart.getOrderPrice(),preCart.getExpiryDate(),delete));
                    tblCart.getItems().setAll(cartList);
                   
                    lblTotalCharge.setText("" + NumberFormat.getCurrencyInstance().format(getCharge()));
                    clearControls();
                 }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Adding On Cart Error ");
                        alert.setContentText("This Exact Drug is Ordey on Cart \n Remove On Cart and Scan Again!");
                        alert.showAndWait();
                 }
             }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Adding On Cart Error ");
                alert.setContentText("Quantity wanted is greater than Quantity in left");
                alert.showAndWait();
             }
           }catch(NumberFormatException ex){
              
           }
         }else{
             Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Cart Entry Error ");
            alert.setContentText("Please enter quantity of drugs wanted");
            alert.showAndWait();
            }
         
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
                      for(Entry entry : entryList){
                          if((entry.getBatchNo() + "").equals(id) ){
                              index = entryList.indexOf(entry);
                               window.loadSmallWindow("/aloe/view/pharmacist/stock/updateEntry.fxml", "", true,false,false,false);
                                       window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
                                          @Override
                                          public void handle(WindowEvent event) {
                                                        entryList.clear();
                                                      //  loadDataCols();
                                                    }}); 
                          }
                      }
                            
             }});
         return update;
    }
     private JFXButton createDelBtn(String item){
         JFXButton del = new JFXButton();
            del.setPrefWidth(80);
            del.setId(item);
         
            del.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.REMOVE);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill:red;");
            del.setGraphic(minimizeIcon);
            del.setOnAction(new EventHandler<ActionEvent>() {
                      @Override
                      public void handle(ActionEvent event) {
                        try{
                         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setHeaderText("Deletion Message");
                        alert.setContentText("Do you really want to delete this entry?");
                        alert.showAndWait();
                        if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                            for(Cart cart : cartList){
                                if(cart.getItem().equalsIgnoreCase(del.getId())){
                                    cartList.remove(cart);   
                                    tblCart.getItems().setAll(cartList);
                                }
                            }
                            lblTotalCharge.setText(getCharge() + "");
                        }
                        
                      
                        }catch(ConcurrentModificationException ex){}
                       
             }});
         return del;
    }
    private void clearControls(){
        lblDrugName.setText("");
        lblCategory.setText("");
        lblSelling.setText("");
        lblOrderPrice.setText("");
        lblExpiryDate.setText("");
        lblQuantity.setText("");
        txtCode.clear();
        txtDrugName.clear();
        entryCombo.setValue(null);
        entryCombo.getItems().clear();
        entryCombo.setPromptText("Batch No");
        packCombo.setValue(null);
        packCombo.getItems().clear();
        packCombo.setPromptText("Pack No");
        txtQuantity.clear();
    }
    private void  clearLabels(){
        lblDrugName.setText("");
        lblCategory.setText("");
        lblSelling.setText("");
        lblOrderPrice.setText("");
        lblExpiryDate.setText("");
        lblQuantity.setText("");
    }
    private double getCharge(){
         totalCharge = 0.00;
          for(Cart cart : cartList){
               totalCharge += cart.getCharge();
          }
        return totalCharge;
    }

    @FXML
    private void onPaymentKeyReleased(KeyEvent event) {
        try{
           double balance = Double.parseDouble(txtPayment.getText().trim()) 
                   - totalCharge;
           lblBalance.setText(NumberFormat.getCurrencyInstance().format(balance));
        }catch(NumberFormatException ex){
            
        }
    }
    
    private void generateReciept(){
    }

    

 

  
}
