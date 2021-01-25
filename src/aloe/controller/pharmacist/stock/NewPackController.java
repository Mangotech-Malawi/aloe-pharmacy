/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;

import static aloe.controller.pharmacist.stock.ViewEntriesController.*;
import static aloe.controller.pharmacist.stock.ViewPacksController.packList;
import aloe.model.Entry;
import aloe.model.Pack;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import static aloe.model.QueryManager.conn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class NewPackController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXTextField txtQuantity;
    @FXML
    private Label lblCalcPrice;
    @FXML
    private JFXTextField txtPrice;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
   
    @FXML
    private JFXTextField txtBatchNo;
    @FXML
    private Label lblSellingPrice;
    private int entryQuantity;
    private int quantity;
    private int numOfPacks;
    @FXML
    private Label lblRemainingQty;
    double calculatedPrice =0;
    @FXML
    private JFXTextField txtNumOfPacks;
    private int batchNo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        if(ViewPacksController.index >= 0){
             loadPackLabelData();
              calculatePrice();
        }else{
            batchNo = entryList.get(index).getBatchNo() ;
           loadLabelData() ;
        }
    } 
    
    private void loadPackLabelData(){
        lblStatus.setText(packList.get(ViewPacksController.index).getName());
        txtBatchNo.setText(getBatchNo(packList.get(ViewPacksController.index).getPackId()) +"");
         lblStatus.setText(packList.get(ViewPacksController.index).getName());
        lblSellingPrice.setText("MK"+getSellingPrice());
        lblRemainingQty.setText("" + (entryQuantity +  (packList.get(ViewPacksController.index).getNumOfPacks() * 
                        packList.get(ViewPacksController.index).getQuantity())));
        txtQuantity.setText(""+packList.get(ViewPacksController.index).getQuantity());
        txtNumOfPacks.setText("" + packList.get(ViewPacksController.index).getNumOfPacks());
        txtPrice.setText("" + packList.get(ViewPacksController.index).getSellingPrice());
     
    }
    private int getBatchNo(int packId){
        QueryManager Query = new QueryManager();
        String query = "SELECT batchNo,quantity FROM packs where packId = '" + packId +"'";
        ResultSet rs = Query.getDataQuery(query);
        try {
            if(rs.next()){
                batchNo = rs.getInt("batchNo");
                String query2 = "SELECT * from entries where batchNo = '" + batchNo +"'";
                ResultSet rs2 = Query.getDataQuery(query2);
                if(rs2.next()){
                     entryQuantity = rs2.getInt("quantity") +  + (packList.get(ViewPacksController.index).getNumOfPacks() * 
                        packList.get(ViewPacksController.index).getQuantity());
                     return batchNo;
                }
              
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(NewPackController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
   
    private void loadLabelData(){
        txtBatchNo.setText(entryList.get(index).getBatchNo() + "");
        lblStatus.setText(entryList.get(index).getName());
        lblSellingPrice.setText("MK"+getSellingPrice());
        entryQuantity = entryList.get(index).getQuantity();
        lblRemainingQty.setText("" + entryQuantity);
    }

    @FXML
    private void btnMnimizeAction(ActionEvent event) {
          PopWindow.primaryStage.setIconified(true);
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
        stackPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnSaveAction(ActionEvent event) {
         if(isValid()){
            String batchNo = txtBatchNo.getText().trim();
       
            
                    try{
                         quantity = Integer.parseInt(txtQuantity.getText().trim());
                         numOfPacks = Integer.parseInt(txtNumOfPacks.getText().trim());
                        if((quantity * numOfPacks) <= entryQuantity){
                             Double price = Double.parseDouble(txtPrice.getText().trim());
                             boolean proceed = false;
                           if(price < calculatedPrice){
                         //Validating information entered
                                 Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                 alert.setHeaderText("Confirm Price ");
                                 alert.setContentText("Are You Sure Selling Price per pack\n"
                                         + "should be less than the initial calculated price");
                                 alert.showAndWait();
                                 if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                                     proceed = true;
                                  }
                           }else{
                               proceed = true;
                           }
                           
                            if(proceed){
                                Pack pack = new Pack(Integer.parseInt(batchNo),numOfPacks,quantity,price,
                                        LocalDateTime.now().toString());
                                QueryManager Query = new QueryManager();
                                conn.setAutoCommit(false);
                                String startTransQuery = "START TRANSACTION";
                                if(Query.execAction(startTransQuery)){
                                
                                if(ViewPacksController.index >= 0 ){
                                     Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setHeaderText("Confirm Updating Drug Pack ");
                                    alert.setContentText("Do you really want to update this Pack?");
                                    alert.showAndWait();
                                    if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                                        if(Query.execAction(pack.updatePack(packList.get(ViewPacksController.index).getPackId()))){
                                            Entry entry = new Entry();
                                            entry.setBatchNo(Integer.parseInt(batchNo));
                                            System.out.println("The batch No. is"+ batchNo);
                                            if(Query.execAction(entry.updateEntryQty(entryQuantity- (quantity * numOfPacks)))){
                                                String commitTransQuery = "COMMIT;";
                                                if(Query.execAction(commitTransQuery)){
                                                    Query.ClearDB();
                                                    alert = new Alert(Alert.AlertType.INFORMATION);
                                                    alert.setHeaderText("Drug Pack Update Success");
                                                    alert.setContentText("You have successfully updated drug pack");
                                                    alert.showAndWait();
                                                      stackPane.getScene().getWindow().hide();
                                                }
                                            }else{
                                                String rollBackTransQuery = "ROLLBACK;";
                                                   if(Query.execAction(rollBackTransQuery)){

                                                    alert = new Alert(Alert.AlertType.ERROR);
                                                    alert.setHeaderText("Drug Pack Update Failed");
                                                    alert.setContentText("Sorry, drug pack updating failed");
                                                    alert.showAndWait();
                                                   }
                                            }
                                          
                                        }else{
                                           Alert error = new Alert(Alert.AlertType.ERROR);
                                           error.setHeaderText("Drug Pack Update Failed");
                                           error.setContentText("Sorry, drug pack updating failed");
                                           error.showAndWait();
                                        }      
                                    }
                                    
                                }else{
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setHeaderText("Confirm Adding Drug Pack ");
                                    alert.setContentText("Do you really want to update this Pack?");
                                    alert.showAndWait();
                                    if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                                        if(Query.insertStatement(pack.addPack())){
                                            Entry entry = new Entry();
                                            System.out.println("The batch No. is"+ batchNo);
                                            entry.setBatchNo(Integer.parseInt(batchNo));
                                            if(Query.execAction(entry.updateEntryQty(entryQuantity- (quantity * numOfPacks)))){
                                                  String commitTransQuery = "COMMIT;";
                                                 if(Query.execAction(commitTransQuery)){
                                                Query.ClearDB();
                                                alert = new Alert(Alert.AlertType.INFORMATION);
                                                alert.setHeaderText("Drug Pack Add Success");
                                                alert.setContentText("You have successfully added drug pack");
                                                alert.showAndWait();
                                                stackPane.getScene().getWindow().hide();
                                                 }
                                            }else{
                                                 String rollBackTransQuery = "ROLLBACK;";
                                                    if(Query.execAction(rollBackTransQuery)){
                                                        alert = new Alert(Alert.AlertType.ERROR);
                                                        alert.setHeaderText("Drug Pack Add Failed");
                                                        alert.setContentText("Sorry, drug pack adding failed");
                                                        alert.showAndWait();
                                                }
                                            }
                                           
                                        }else{
                                            alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setHeaderText("Drug Pack Add Failed");
                                            alert.setContentText("Sorry, drug pack adding failed");
                                            alert.showAndWait();
                                        }      
                                    }
                                }
                              }else{
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText("Drug Pack Add Failed");
                                    alert.setContentText("Sorry, drug pack adding failed");
                                    alert.showAndWait();
                                }
                            }
                        }else{
                             Alert error = new Alert(Alert.AlertType.ERROR);
                                   error.setHeaderText("Error");
                                   error.setContentText("Failed to Pack Entry \nPack quantity is greater "
                                           + "than remaining entry quantity");
                                   error.showAndWait();
                        }
                       
                    }catch(NumberFormatException ex){
                         Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Entry Error");
                        alert.setContentText("Please enter correct quantity ");
                        alert.showAndWait();
                    }catch(SQLException ex){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Entry Error");
                        alert.setContentText("Failed to prepare the database for a transaction");
                        alert.showAndWait();
                    }
           
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Entry Error");
            alert.setContentText("Please enter all details");
            alert.showAndWait();
        }
    }
    private boolean isValid(){ //Verifies if the user has entered all details
      if(txtBatchNo.getText().trim().isEmpty()  ||
                txtQuantity.getText().trim().isEmpty() 
                        || txtPrice.getText().trim().isEmpty() || txtNumOfPacks.getText().trim().isEmpty()
                        ){
          return false; // return false if one/more fields are not filled
    }else{
          return true; // return true if all fields are filled
      }
    }

    @FXML
    private void btnCancelAction(ActionEvent event) {
         stackPane.getScene().getWindow().hide();
    }
    private double getSellingPrice(){
            QueryManager Query = new QueryManager();
            double sellingPrice = 0.00;
            String query1 = "SELECT id,quantity FROM entries"
                    + " where batchNo ='" + batchNo + "';";
            ResultSet rs1 = Query.getDataQuery(query1);
            try {
                if(rs1.next()){
                    String drugId = rs1.getString("id");
                   
                    String query2 = "SELECT sellingPrice FROM Drugs where id = '" + drugId + "'";
                    ResultSet rs2 = Query.getDataQuery(query2);
                    if(rs2.next()){
                       sellingPrice = rs2.getDouble("sellingPrice");;
                    } 
                }
            } catch (SQLException ex) {
                Logger.getLogger(NewPackController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return sellingPrice;
    }
    @FXML
    private void calculatePrice(KeyEvent event) {
       calculatePrice();
    }
    
    private void calculatePrice(){
        String quantity = txtQuantity.getText().trim();
        String numberOfPacks = txtNumOfPacks.getText().trim();
      
        try{
           
             calculatedPrice = getSellingPrice() * Integer.parseInt(quantity);
             lblCalcPrice.setText("MK"+calculatedPrice);
             if(ViewPacksController.index >=0){
                     lblRemainingQty.setText("" + (entryQuantity - (Integer.parseInt(quantity)
                             * Integer.parseInt(numberOfPacks)))) ;
             }else{
                  lblRemainingQty.setText("" + (entryQuantity - (Integer.parseInt(quantity)
                             * Integer.parseInt(numberOfPacks)))) ;
             }
           
          
        }catch(NumberFormatException ex){
              lblCalcPrice.setText("MK0.00");
              lblRemainingQty.setText(""+entryQuantity);
        }
    }
    
}
