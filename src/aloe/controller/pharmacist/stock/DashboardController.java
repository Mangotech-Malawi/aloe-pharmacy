/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;

import static aloe.controller.pharmacist.stock.ViewEntriesController.insertSettings;
import aloe.model.Notification;
import aloe.model.PopWindow;
import static aloe.model.PopWindow.childStage;
import static aloe.model.PopWindow.primaryStage;
import aloe.model.QueryManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import jfxtras.scene.control.gauge.linear.SimpleMetroArcGauge;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class DashboardController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;

    @FXML
    private SimpleMetroArcGauge entryRevenueGauge;
    @FXML
    private SimpleMetroArcGauge packsRevenueGauge;
    @FXML
    private Label lblTotalRevenue;
    @FXML
    private MenuButton menuBtnTotal;
    @FXML
    private Label lblTotalEntries;
    @FXML
    private Label lblTotalPacks;
    @FXML
    private SimpleMetroArcGauge numOfEntriesGauge;
    @FXML
    private SimpleMetroArcGauge numOfPacksGauge;
    @FXML
    private Label lblExValue;
    @FXML
    private ProgressBar exProgressBar;
    @FXML
    private Label lblBetterValue;
    @FXML
    private Label lblGoodValue;
    @FXML
    private ProgressBar betterProgressBar;
    @FXML
    private ProgressBar goodProgressBar;
    @FXML
    private Label lblBadValue;
    @FXML
    private ProgressBar badProgressBar;
    @FXML
    private Label lblWorseValue;
    @FXML
    private ProgressBar worseProgressBar;
    private JFXListView<String> thresholdListView;
    public static ObservableList<String> namesList = FXCollections.observableArrayList();
    public static ObservableList<String> drugNamesList = FXCollections.observableArrayList();
    public static ObservableList<String> drugEntriesNamesList = FXCollections.observableArrayList();
     public static ObservableList<String> expiredEntryList = FXCollections.observableArrayList();
    @FXML
    private JFXButton btnExpiryNotification;
    @FXML
    private JFXButton btnEntryNotification;
    @FXML
    private JFXButton btnPackNotification;
    private int  expiryQty;
    private int  packsThresholdCount;
    private int  entryThresholdCount;
    @FXML
    private Label lblPacksTotalRevenue;
    @FXML
    private Label lblEntryTotalRevenue;

 
    /**
     * Initializes the controller class.
     */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        insertSettings();// Making sure that drug default expirySettings area available if not added
        setGauges();
        setConditionProgressBars();
        lblTotalEntries.setText("" + getEntryQty("year"));
        lblTotalPacks.setText("" + getPackQty("year"));
        loadPacksNotification();
        loadEntryNotification();
        loadExpiryNotification();
    }
    
    private void setConditionProgressBars(){
        int count =0;
        int excellent = 0;
        int better = 0;
        int good = 0;
        int bad = 0;
        int worse = 0;
           QueryManager Query = new QueryManager();
        String entryQuery = "SELECT * FROM entries";
        ResultSet rs1 = Query.getDataQuery(entryQuery);
        try { 
            while(rs1.next()){
                String entriesDelQuery = "SELECT * FROM entries_del where "
                        + "batchNo ='" + rs1.getString("batchNo") + "'";
                ResultSet rs2 = Query.getDataQuery(entriesDelQuery);
                if(rs2.next()){
                    //Dont do anything because the entry is deleted
                }else{
                    LocalDate nowDate = LocalDate.now();
                    LocalDate date = LocalDate.parse(rs1.getString("expiryDate"));
                    int daysToExpiry = Integer.parseInt(date.toEpochDay() + "") - Integer.parseInt(nowDate.toEpochDay() + "") ; 
                    String settingsQuery = "SELECT * FROM expirySettings";
                    ResultSet rs3 = Query.getDataQuery(settingsQuery);
                    if(rs3.next()){
                        if(daysToExpiry >= rs3.getInt("excellent")){
                            excellent++;
                        }else if(daysToExpiry >= rs3.getInt("better") && daysToExpiry < rs3.getInt("excellent")){
                            better++;
                        }else if(daysToExpiry >= rs3.getInt("good") && daysToExpiry < rs3.getInt("better")){
                            good++;
                        }else if(daysToExpiry >= rs3.getInt("bad") && daysToExpiry < rs3.getInt("good")){
                            bad++;
                        }else{
                            worse++;
                        }
                        count++;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //passes parameters to set progress bars for conditions;
        setExProgressBar(count,excellent);
        setBetterProgressBar(count,better);
        setGoodProgressBar(count,good);
        setBadProgressBar(count,bad);
        setWorseProgressBar(count,worse);
    }
    private void setExProgressBar(int count,int excellent){//Progress bar for excellent conditions
         if(count > 0){
                int barValue = (excellent * 100)/count; // getting the bar value
                Task<Integer> task = new Task<Integer>(){
                   @Override
                   protected Integer call() throws Exception {
                       int iterations;
                       for (iterations = 0; iterations < barValue;iterations++){
                           if (isCancelled()){
                               break;
                           }
                           updateProgress(iterations, 100);
                          
                           try{
                             Thread.sleep(61);
                           } catch(InterruptedException ex){
                               if (isCancelled()){
                                   break;
                               }
                           }
                       }
                       return iterations;    
                   }

               };
                 lblExValue.setText("%" + barValue);
                exProgressBar.progressProperty().bind(task.progressProperty());
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                          
                    }
                });
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
        }
    }
     private void setBetterProgressBar(int count,int better){//Progress bar for excellent conditions
         if(count > 0){
                int barValue = (better * 100)/count; // getting the bar value
                Task<Integer> task = new Task<Integer>(){
                   @Override
                   protected Integer call() throws Exception {
                       int iterations;
                       for (iterations = 0; iterations < barValue;iterations++){
                           if (isCancelled()){
                               break;
                           }
                           updateProgress(iterations, 100);
                          
                           try{
                             Thread.sleep(61);
                           } catch(InterruptedException ex){
                               if (isCancelled()){
                                   break;
                               }
                           }
                       }
                       return iterations;    
                   }

               };
                 lblBetterValue.setText("%" + barValue);
                betterProgressBar.progressProperty().bind(task.progressProperty());
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                          
                    }
                });
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
        }
    }
      private void setGoodProgressBar(int count,int good){//Progress bar for excellent conditions
         if(count > 0){
                int barValue = (good * 100)/count; // getting the bar value
                Task<Integer> task = new Task<Integer>(){
                   @Override
                   protected Integer call() throws Exception {
                       int iterations;
                       for (iterations = 0; iterations < barValue;iterations++){
                           if (isCancelled()){
                               break;
                           }
                           updateProgress(iterations, 100);
                          
                           try{
                             Thread.sleep(61);
                           } catch(InterruptedException ex){
                               if (isCancelled()){
                                   break;
                               }
                           }
                       }
                       return iterations;    
                   }

               };
                 lblGoodValue.setText("%" + barValue);
                goodProgressBar.progressProperty().bind(task.progressProperty());
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                          
                    }
                });
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
        }
    }
     private void setBadProgressBar(int count,int bad){//Progress bar for excellent conditions
         if(count > 0){
                int barValue = (bad * 100)/count; // getting the bar value
                Task<Integer> task = new Task<Integer>(){
                   @Override
                   protected Integer call() throws Exception {
                       int iterations;
                       for (iterations = 0; iterations < barValue;iterations++){
                           if (isCancelled()){
                               break;
                           }
                           updateProgress(iterations, 100);
                          
                           try{
                             Thread.sleep(61);
                           } catch(InterruptedException ex){
                               if (isCancelled()){
                                   break;
                               }
                           }
                       }
                       return iterations;    
                   }

               };
                lblBadValue.setText("%" + barValue);
                badProgressBar.progressProperty().bind(task.progressProperty());
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                          
                    }
                });
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
        }
    }
    
      private void setWorseProgressBar(int count,int worse){//Progress bar for excellent conditions
         if(count > 0){
                int barValue = (worse * 100)/count; // getting the bar value
                Task<Integer> task = new Task<Integer>(){
                   @Override
                   protected Integer call() throws Exception {
                       int iterations;
                       for (iterations = 0; iterations < barValue;iterations++){
                           if (isCancelled()){
                               break;
                           }
                           updateProgress(iterations, 100);
                          
                           try{
                             Thread.sleep(61);
                           } catch(InterruptedException ex){
                               if (isCancelled()){
                                   break;
                               }
                           }
                       }
                       return iterations;    
                   }

               };
                lblWorseValue.setText("%" + barValue);
                worseProgressBar.progressProperty().bind(task.progressProperty());
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                          
                    }
                });
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
        }
    }
    private void setGauges(){
        try{
              Task<Void> task = new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                         Thread.sleep(1000);
                      
                         Platform.runLater(new Runnable() {
                             @Override
                             public void run() {
                                   // setGraphValue();
                                    setTotalRevenueGauge();
                             }
                         });
                   return null;
               }
           };
           new Thread(task).start();
        }catch(Exception ex){
            
        }
    }
    private void setTotalRevenueGauge(){
        double totalRevenue = setTotalEntryRevenueGauge() + setTotalPackRevenueGauge();
          lblTotalRevenue.setText(NumberFormat.getInstance()
                  .format(totalRevenue));
          entryRevenueGauge.setMaxValue(totalRevenue);
          packsRevenueGauge.setMaxValue(totalRevenue);
          double totalEntriesAndPacks = numOfEntriesGauge.getValue() + numOfPacksGauge.getValue();
          numOfEntriesGauge.setMaxValue(totalEntriesAndPacks);
          numOfPacksGauge.setMaxValue(totalEntriesAndPacks);
    } 
    private double setTotalEntryRevenueGauge(){
           QueryManager Query = new QueryManager();
            String query1 = "SELECT * FROM entries";
            double revenue = 0.0;
            try {
             
              ResultSet rs1 = Query.getDataQuery(query1);
              int count = 0;
              while(rs1.next()){
                  String batchNo = rs1.getString("batchNo");
                  String query2 = "SELECT batchNo FROM entries_del where batchNo ='" + batchNo + "'";
                   ResultSet rs2 = Query.getDataQuery(query2);
                     if(rs2.next()){
                         //Dont add it on gauge because it the entry id exist in in deleted entries
                     }else{
                        String drugId = rs1.getString("id");
                        int quantity = rs1.getInt("quantity");
                        String query3 = "SELECT * FROM drugs where id = '" + drugId +"'";
                        ResultSet rs3 = Query.getDataQuery(query3);
                        if(rs3.next()){
                           double totalPrice = quantity * rs3.getDouble("sellingPrice");
                           revenue = revenue + totalPrice;
                           count++;
                        }
                     }
              }
               System.out.println("Revenue is MK" + revenue);
               entryRevenueGauge.setValue(revenue);
               
                lblEntryTotalRevenue.setText("MWK " +NumberFormat.getInstance().format(revenue));
               numOfEntriesGauge.setValue(count);
            } catch (SQLException ex) {
               Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
          return revenue;  
    }
    private double setTotalPackRevenueGauge(){
           QueryManager Query = new QueryManager();
          String query1 = "SELECT * FROM packs";
            double revenue = 0.0;
            int count=0;
            try {
            
              ResultSet rs1 = Query.getDataQuery(query1);
              while(rs1.next()){
                  String packDelQuery = "SELECT * FROM packs_del "
                          + "where packId='" + rs1.getString("packId") + "';";
                  ResultSet rs2 = Query.getDataQuery(packDelQuery);
                  if(rs2.next()){
                      //Dont add the price because the packId exist in packs_del
                  }else{
                        double totalPrice = rs1.getInt("numOfPacks") * rs1.getDouble("price");
                        revenue = revenue + totalPrice; 
                        count++;
                  }
      
              }
              //System.out.println("Revenue is MK" + revenue);
              packsRevenueGauge.setValue(revenue);
               lblPacksTotalRevenue.setText("MWK " +NumberFormat.getInstance().format(revenue));
               numOfPacksGauge.setValue(count);
            } catch (SQLException ex) {
               Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
          return revenue;
    }
   
    @FXML
    private void menuBtnYearAction(ActionEvent event) {
        menuBtnTotal.setText("Total Added This Year");
        lblTotalEntries.setText("" + getEntryQty("year"));
        lblTotalPacks.setText("" + getPackQty("year"));
    }
    
    private int getEntryQty(String choice){
        int entryQty = 0;
         QueryManager Query = new QueryManager();
        String entryQuery = "SELECT * FROM entries";
        ResultSet rs1 = Query.getDataQuery(entryQuery);
         LocalDate date = LocalDate.now();
        try {
            while(rs1.next()){
                String batchNo = rs1.getString("batchNo");
                String entryDelQuery = "SELECT * FROM entries_del where batchNo ='" + batchNo +  "'";
                ResultSet rs2 = Query.getDataQuery(entryDelQuery);
                if(rs2.next()){
                  
                }else{
                   
                    LocalDate regDate = LocalDate.parse(rs1.getString("regDate"));
                    if(choice.equalsIgnoreCase("year")){
                        if(date.getYear() == regDate.getYear()){
                             entryQty++;
                         }
                    }else if(choice.equalsIgnoreCase("month") && date.getYear() == regDate.getYear()){
                        if(date.getMonthValue() == regDate.getMonthValue()){
                             entryQty++;
                         }
                    }else{
                        if(date.equals(regDate)){
                             entryQty++;
                        }
                    }
                }     
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entryQty;
    }
    private int getPackQty(String choice){
        int packQty = 0;
          QueryManager Query = new QueryManager();
        String entryQuery = "SELECT * FROM packs";
        ResultSet rs1 = Query.getDataQuery(entryQuery);
         LocalDateTime date = LocalDateTime.now();
        try {
            while(rs1.next()){
                String entryDelQuery = "SELECT * FROM packs_del where packId = '" + rs1.getString("packId") + "';";
                ResultSet rs2 = Query.getDataQuery(entryDelQuery);
                if(rs2.next()){
                  
                }else{
                   
                    LocalDateTime regDate = LocalDateTime.parse(rs1.getString("regDate"));
                    if(choice.equalsIgnoreCase("year")){
                        if(date.getYear() == regDate.getYear()){
                             packQty++;
                         }
                    }else if(choice.equalsIgnoreCase("month") && date.getYear() == regDate.getYear()){
                        if(date.getMonthValue() == regDate.getMonthValue()){
                             packQty++;
                         }
                    }else{
                        if(date.equals(regDate)){
                             packQty++;
                        }
                    }
                }     
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return packQty;
    }
    @FXML
    private void menuBtnMonthAction(ActionEvent event) {
          menuBtnTotal.setText("Total Added This Month");
        lblTotalEntries.setText("" + getEntryQty("month"));
        lblTotalPacks.setText("" + getPackQty("month"));
    }

    @FXML
    private void menuBtnDayAction(ActionEvent event) {
        menuBtnTotal.setText("Total Added This Day");
        lblTotalEntries.setText("" + getEntryQty("day"));
        lblTotalPacks.setText("" + getPackQty("day"));
    }
    
    private void loadExpiryNotification(){
        try{
            expiredEntryList.clear();
            expiredEntryList.add("LIST OF EXPIRED ENTRIES");
            expiredEntryList.add("________________________");
            QueryManager Query = new QueryManager();
            String entryQuery = "SELECT id,batchNo,quantity,expiryDate FROM entries"
                    + " WHERE batchNo NOT IN (SELECT batchNo FROM entries_del)";
            ResultSet rs1 = Query.getDataQuery(entryQuery);
            expiryQty = 0;
            while(rs1.next()){ 
                LocalDate date = LocalDate.parse(rs1.getString(4));
                LocalDate nowDate = LocalDate.now();
                int daysToExpiry = Integer.parseInt(date.toEpochDay() + "") - Integer.parseInt(nowDate.toEpochDay() + "") ; 
                String settingsQuery = "SELECT * FROM expirySettings";
                ResultSet rs3 = Query.getDataQuery(settingsQuery);
                
                if(rs3.next()){
                    if(daysToExpiry < rs3.getInt("bad")){
                        expiredEntryList.add("BatchNo: " + rs1.getString(2) + "  ---------> " + rs1.getInt(3) + " drugs");
                        expiryQty++;
                    }

                }
            }
             if(expiryQty > 0){
                btnExpiryNotification.setText(""+expiryQty);
                 Notification notify = new Notification(20,
                           "Drug Entries Expiry notification",expiryQty + " drug entries have expired");
                 
                 notify.start();
            }
        }catch(SQLException ex){
        }
    }
    @FXML
    private void btnExpiryNotificationAction(ActionEvent event) {
         PopWindow window  = new PopWindow();
        Effect effect = new GaussianBlur(4.0);
        primaryStage.getScene().getRoot().setEffect(effect);
        window.loadSmallWindow("/aloe/view/pharmacist/stock/expiryDateNotifications.fxml", "", 
                true,false,false,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
              }}); 
        
    }
    
     private void loadEntryNotification(){
        try{
               QueryManager Query = new QueryManager();
            drugEntriesNamesList.clear();
            String entriesThresholdsQuery = "SELECT id,entryQty  FROM entriesthresholds WHERE id IN "
                    + " (SELECT id FROM drugs) AND id NOT IN (SELECT id FROM drugs_del);";
            ResultSet rs1 = Query.getDataQuery(entriesThresholdsQuery);
            entryThresholdCount = 0;
            while(rs1.next()){
                int id = rs1.getInt(1);
                int quantity = rs1.getInt(2);
                String entriesQuery = "SELECT SUM(quantity) FROM entries"
                        + " WHERE id ='" + id + "' AND batchNo NOT IN (SELECT batchNo FROM entries_del)";
                ResultSet rs2 = Query.getDataQuery(entriesQuery);
                if(rs2.next()){
                    int entryQty = rs2.getInt(1);
                    if(entryQty <= quantity){
                        String drugQuery = "SELECT name FROM drugs WHERE id ='"+ id +"' AND id NOT IN"
                                + " (SELECT id FROM drugs_del)";
                        ResultSet rs3 = Query.getDataQuery(drugQuery);
                        if(rs3.next()){
                            drugEntriesNamesList.add(rs3.getString(1) + " ----> " + entryQty);
                            entryThresholdCount++;
                        }
                    }
             
                }
            }
             if(packsThresholdCount > 0){
                btnEntryNotification.setText(""+entryThresholdCount);
                 Notification notify = new Notification(20,
                           "Entry threshold notification",entryThresholdCount + " drugs have reached entry threshold");
                 
                 notify.start();
            }
        }catch(SQLException ex){
             Logger.getLogger(PackThresholdNotificationsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void btnEntryNotificationAction(ActionEvent event) {
        PopWindow window  = new PopWindow();
        Effect effect = new GaussianBlur(4.0);
        primaryStage.getScene().getRoot().setEffect(effect);
        window.loadSmallWindow("/aloe/view/pharmacist/stock/entryNotifications.fxml", "", 
                true,false,false,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
              }}); 
        
    }
    
   
    private void loadPacksNotification(){
         try {
             drugNamesList.clear();
           QueryManager Query = new QueryManager();
            String packsThresholdsQuery = "SELECT id,numOfPacks  FROM packsthresholds WHERE id IN "
                    + " (SELECT id FROM drugs) AND id NOT IN (SELECT id FROM drugs_del);";
            ResultSet rs1 = Query.getDataQuery(packsThresholdsQuery);
            packsThresholdCount = 0;
            while(rs1.next()){
                int id  = rs1.getInt(1);
                int thresholdQty = rs1.getInt(2);
                String entriesQuery = "SELECT batchNo FROM entries "
                        + "WHERE id='" + id + "' AND batchNo NOT IN (SELECT batchNo FROM entries_del)";
                ResultSet rs2 = Query.getDataQuery(entriesQuery);
                int quantity = 0;
                while(rs2.next()){
                    String packQuery = "SELECT SUM(numOfPacks) FROM packs WHERE batchNo ='" 
                            + rs2.getInt(1) + "' AND packId NOT IN (SELECT packId FROM packs_del) ;";
                    ResultSet rs3 = Query.getDataQuery(packQuery);
                    if(rs3.next()){
                       quantity = quantity + rs3.getInt(1);
                    }
                }
                if(quantity <= thresholdQty){
                    String drugsQuery = "SELECT name FROM drugs WHERE id ='" + id + "' AND id IN" 
                       + " (SELECT id FROM drugs) AND id NOT IN (SELECT id FROM drugs_del);";
                    ResultSet rs4 = Query.getDataQuery(drugsQuery);
                    if(rs4.next()){
                        String name = rs4.getString(1);
                         packsThresholdCount++;
                        drugNamesList.add(name);
                    }
                   
                }
                
            }
            if(packsThresholdCount > 0){
                btnPackNotification.setText(""+packsThresholdCount);
                 Notification notify = new Notification(20,
                           "Packs threshold notification",packsThresholdCount + " drugs have reached packs threshold");
                 
                 notify.start();
            }
        } catch (SQLException ex) {
            Logger.getLogger(PackThresholdNotificationsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void btnPackNotificationAction(ActionEvent event) {
        PopWindow window  = new PopWindow();
        Effect effect = new GaussianBlur(4.0);
        primaryStage.getScene().getRoot().setEffect(effect);
        window.loadSmallWindow("/aloe/view/pharmacist/stock/packThresholdNotifications.fxml", "", 
                true,false,false,true);
        window.getChildStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                        Effect effect = new GaussianBlur(0.0);
                        primaryStage.getScene().getRoot().setEffect(effect);      
              }}); 
        
    }
    
    
    
    
}
