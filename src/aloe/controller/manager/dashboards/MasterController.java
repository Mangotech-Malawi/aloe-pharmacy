/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager.dashboards;

import aloe.controller.pharmacist.stock.DashboardController;
import static aloe.controller.pharmacist.stock.DashboardController.expiredEntryList;
import aloe.model.QueryManager;
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
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import jfxtras.scene.control.gauge.linear.SimpleMetroArcGauge;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class MasterController extends Thread implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblProfits;
    @FXML
    private Label lblLoses;
    @FXML
    private Label lblTotalSales;
    @FXML
    private Label lblTotalExpired;
    @FXML
    private Label lblTotalExpense;
    @FXML
    private SimpleMetroArcGauge todaySalesGauge;
    @FXML
    private SimpleMetroArcGauge todayEntriesGauge;
    @FXML
    private SimpleMetroArcGauge todayPacksGauge;
    private int entriesExpired = 0;
    @FXML
    private Label lblYear;
    public static ScheduledService<Void> service;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        // Calling the dashboard thread that animates the loading of the dashboard
       
        service = new ScheduledService<Void>(){
            @Override
            protected Task<Void> createTask(){
                return new Task<Void>(){
                    @Override
                    protected Void call() throws Exception{
                        Platform.runLater(new Runnable() {
                             @Override
                             public void run() {
                                refreshDashboard();
                             }
                         });
                        return null;
                    }
                };
            }
        };
       service.setRestartOnFailure(true);
       service.setPeriod(Duration.seconds(600));
       Platform.runLater(()->(service.start()));
    } 
    private void refreshDashboard(){
           lblYear.setText(LocalDate.now().getYear() + "");
           loadDashBoardData();
    }
    
    private void loadDashBoardData(){
         try{
              Task<Void> task = new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                         Thread.sleep(1000); //Delaying the output by 1000 milliseconds
                      
                         Platform.runLater(new Runnable() {
                             @Override
                             public void run() {
                                 loadProfits(); //Loading the profits method
                                 loadLoses();//Loading the loses made through the year
                                 loadSales(); // Loading number of sales done through the year
                                 loadExpired(); //Loading expired entries through the year
                                 loadExpenses();// Loading the expenses for this year
                                 loadTodaySalesGauge();//lauding the gauge value for today sales based on month sales
                                 loadTodayEntries();
                                 loadTodayPacks();
                             }
                         });
                   return null;
               }
           };
           new Thread(task).start();
        }catch(Exception ex){
            
        }
    }
    
    
    //Method for calculating profits made
    private void loadProfits(){
        double initialProfit = calculateInitialProfit(); /*calculating the initial profit
                                                      before subtracting expenses and expired costs*/
        double expirysCost =  calculateExpiryCost(); // Returns the cost accumulated from expired drugs
        double expensesCost = calculateExpenseCost(); // Return the cost accumulated from expenses
        double calculatedProfit = initialProfit - (expirysCost + expensesCost);
        lblProfits.setText(NumberFormat.getInstance().format(calculatedProfit));
    }
    
    private double calculateInitialProfit(){
         double profit = 0.0;
        QueryManager Query = new QueryManager(); // Connecting to database
        String salesQuery = "SELECT * FROM sales"; // Query to fetch data from sales
        ResultSet rs1 = Query.getDataQuery(salesQuery); // Fetching data using the query
        
        try {// Try if there are no errors in the query
           
            while(rs1.next()){ //Repeat loop while there is still more data to the result set
                
              int transNo = rs1.getInt("transNo"); // Fetching the transaction Number of the sale
              String transQuery = "SELECT trans_date FROM"
                      + " transactions where transNo = '" + transNo + "';"; // Query for getting transaction date of the sale
              ResultSet rs2 = Query.getDataQuery(transQuery); // Fetching the transaction date   
              if(rs2.next()){ // if the transaction date exist then proceed 
                  
                  LocalDateTime  transDate = LocalDateTime.parse(
                          rs2.getString("trans_date")); //Getting the date and converting it to local datetime;
                  LocalDate nowDate = LocalDate.now(); //Generating today's date
                  
                  if(transDate.getYear() == nowDate.getYear()){ //proceed with calculation if the sale was done in the current year
                      double totalOrderPrice = rs1.getInt("quantity") * 
                              rs1.getDouble("orderPrice"); //  calculating the total order price;
                      double charge = rs1.getDouble("charge"); // Fetchig the total selling price of a sale
                      profit = profit + (charge - totalOrderPrice);
                      
                  }
                  
              }
                
            }
         
        } catch (SQLException ex) {//Catches the errors in the query and show where they are
            Logger.getLogger(MasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
       return profit;
    }
    
    private double calculateExpiryCost(){
        return packExpiredCost() + entryExpiredCost(); // adding the cost returned from expired packs and entries
    }

    private double packExpiredCost() { // Calculates the cost of expired packs
       QueryManager Query = new QueryManager();// Connecting to database
       double expiredCost = 0.0;
       String packsQuery = "SELECT * FROM packs where "
               + "packId NOT IN (SELECT packId FROM packs_del)"; //Query for select drug packs details if the pack is not deleted
       ResultSet rs1 = Query.getDataQuery(packsQuery);//Fetching the data 
        try {// Try continuing excuting the code if there are no database errors
            while(rs1.next()){//Continue fetching packs if another row still exist
                int batchNo  = rs1.getInt("batchNo"); //Fetching the batchNo associated with the pack;
                String entryQuery = "SELECT id,expiryDate FROM "
                        + "entries WHERE batchNo ='" + batchNo + "';"; //Query for getting the packs's expiryDate
                ResultSet rs2 = Query.getDataQuery(entryQuery); 
                if(rs2.next()){// if the expiry date exist the continue, otherwise skip the block of code
                    
                    LocalDate date = LocalDate.parse(rs2.getString(2)); 
                    LocalDate nowDate = LocalDate.now(); // Getting today's date
                    
                    if(date.getYear() == nowDate.getYear()){// if the expiry year is this year then calculate if the pack is expired
                        int daysToExpiry = Integer.parseInt(date.toEpochDay() + 
                                "") - Integer.parseInt(nowDate.toEpochDay() + "");// Calculating the days remaining for pack to be set as expired
                        String settingsQuery = "SELECT * FROM expirySettings";// Query for getting the settings used to calculate expiry condition
                        ResultSet rs3 = Query.getDataQuery(settingsQuery);
                        
                        if(rs3.next()){//Fetch settings if they exist
                            if(daysToExpiry < rs3.getInt("bad")){ //testing if the condition is worse, thus less than bad
                                String drugQuery = "SELECT orderPrice FROM"
                                        + " drugs where id ='" + rs2.getInt("id") + "';"; //getting the order price of the pack from drugs table
                                ResultSet rs4 = Query.getDataQuery(drugQuery);
                                if(rs4.next()){//fetch the order price if it exist
                                    double orderPrice = rs4.getDouble(1);
                                    int totalPackQty = rs1.getInt("numOfPacks") * rs1.getInt("quantity");
                                    expiredCost = expiredCost + (orderPrice * totalPackQty);
                                }
                            }

                        }
                    }
                    
                }
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(MasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
      return expiredCost;
    }
    
    private double entryExpiredCost(){ // Calculates the cost of expired entries
       QueryManager Query = new QueryManager();// Connecting to database
       double expiredCost = 0.0;
       String entriesQuery = "SELECT * FROM entries where "
               + "batchNo NOT IN (SELECT batchNo FROM entries_del)"; //Query for select drug entries details if the entry is not deleted
       ResultSet rs1 = Query.getDataQuery(entriesQuery);//Fetching the data 
        try {// Try continuing excuting the code if there are no database errors
            while(rs1.next()){ //If another row exist continue fecthing data
                LocalDate date = LocalDate.parse(rs1.getString("expiryDate")); 
                LocalDate nowDate = LocalDate.now(); // Getting today's date

                if(date.getYear() == nowDate.getYear()){// if the expiry year is this year then calculate if the entry is expired
                    int daysToExpiry = Integer.parseInt(date.toEpochDay() + 
                            "") - Integer.parseInt(nowDate.toEpochDay() + "");// Calculating the days remaining for entry to be set as expired
                    String settingsQuery = "SELECT * FROM expirySettings";// Query for getting the settings used to calculate expiry condition
                    ResultSet rs2 = Query.getDataQuery(settingsQuery);

                    if(rs2.next()){//Fetch settings if they exist
                        if(daysToExpiry < rs2.getInt("bad")){ //testing if the condition is worse, thus less than bad
                            String drugQuery = "SELECT orderPrice FROM"
                                    + " drugs where id ='" + rs1.getInt("id") + "';"; //getting the order price of the entry from drugs table
                            ResultSet rs3 = Query.getDataQuery(drugQuery);
                            if(rs3.next()){//fetch the order price if it exist
                                double orderPrice = rs3.getDouble(1);
                                int quantity = rs1.getInt("quantity");
                                expiredCost = expiredCost + (orderPrice * quantity);
                                entriesExpired++;
                            }
                        }

                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
      return expiredCost;
    }
    
    private double calculateExpenseCost(){
        QueryManager Query = new QueryManager();//connnecting to database
        double totalExpense = 0.0;
        String expensesQuery = "SELECT amount,regDate FROM expenses WHERE "
                + "expenseNo NOT IN (SELECT expenseNo FROM expenses_del)"; //Code for fetching expenses
        ResultSet rs1 = Query.getDataQuery(expensesQuery); //Fetching data
        try {
            while(rs1.next()){ //Continue fetching data if the next row exist
                LocalDate regDate = LocalDate.parse(rs1.getString(2)); // getting the date the expense was recorded
                LocalDate nowDate = LocalDate.now(); // getting the current date
                if(regDate.getYear() == nowDate.getYear()){//If the years are the same then add expense amount to total expense cost
                    totalExpense = totalExpense + rs1.getDouble(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return totalExpense;
    }
    
    //Method for calculating loses
    private void loadLoses(){
       lblLoses.setText("" + (entryExpiredCost() + packExpiredCost())); //Add the returned expired entry and pack cost
    }
    
    //Method for getting year number of sales
    private void loadSales(){
        QueryManager Query = new QueryManager(); //Connecting to database
        String salesQuery = "SELECT transNo FROM sales"; // Query for fetching transaction number of a sale
        ResultSet rs1 = Query.getDataQuery(salesQuery); //fecthing query result
        int count = 0; // Variable for counting number of sales in the current year
        try {
            while(rs1.next()){ //Continue fetching data while there is a next row
                int transNo = rs1.getInt(1); //fetting the transaction number
                String transQuery = "SELECT trans_date FROM "
                        + "transactions WHERE transNo ='" +transNo + "';"; //Query for fecthing the transaction date of a sale using the transaction number
                ResultSet rs2 = Query.getDataQuery(transQuery);
                LocalDate nowDate = LocalDate.now(); //getting the current date
                if(rs2.next()){
                    LocalDateTime transDate = LocalDateTime.parse(rs2.getString(1));
                    if(transDate.getYear() == nowDate.getYear())//if the sale belongs to the current year then increment sales count 
                        count++; 
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
       lblTotalSales.setText("" + count);
    }
    
    //Method for getting number of expired packs and entries
    private void loadExpired(){
        lblTotalExpired.setText(entriesExpired +"");
    }
    
    //Method getting total expense cost for the current year
    private void loadExpenses(){
        lblTotalExpense.setText(NumberFormat.getInstance().
                format(calculateExpenseCost())); //Getting the expenses and fromating it
    }
    
    //Method for gettting the data to for today sales gauge according to the whole month sales
    private void loadTodaySalesGauge(){
        double monthSales = getSales(1);//Returns the total amount of sales of this month
        double todaySales = getSales(2); //Retuns total amount of this day
        todaySalesGauge.setMinValue(0);
        todaySalesGauge.setMaxValue(monthSales);//Setting the maximum value of the gauge which in this case the total sales amount of month
        todaySalesGauge.setValue(todaySales);//Setting the value of the gauge which is the day total sales
       
    }
    
    //Method for getting total amount of sales depending on paramters passed (Day or month = 1 0r 2)
    private double getSales(int value){
        double totalSales = 0.0;
        QueryManager Query = new QueryManager(); // Connecting to database
        String salesQuery = "SELECT * FROM sales"; // Query to fetch data from sales
        ResultSet rs1 = Query.getDataQuery(salesQuery); // Fetching data using the query
        
        try {// Try if there are no errors in the query
           
            while(rs1.next()){ //Repeat loop while there is still more data to the result set
                
              int transNo = rs1.getInt("transNo"); // Fetching the transaction Number of the sale
              String transQuery = "SELECT trans_date FROM"
                      + " transactions where transNo = '" + transNo + "';"; // Query for getting transaction date of the sale
              ResultSet rs2 = Query.getDataQuery(transQuery); // Fetching the transaction date   
              if(rs2.next()){ // if the transaction date exist then proceed 
                  
                  LocalDateTime  transDate = LocalDateTime.parse(
                          rs2.getString("trans_date")); //Getting the date and converting it to local datetime;
                  LocalDate nowDate = LocalDate.now(); //Generating today's date
                  
                  if(value == 1){//If the the total sale of month are to be calculated
                      if((transDate.getYear() == nowDate.getYear()) &&
                              (transDate.getMonthValue() == nowDate.getMonthValue())){ //proceed with calculation if the sale was done in the current year
                      double charge = rs1.getDouble("charge"); // Fetchig the total selling price of a sale
                      totalSales = totalSales + charge; // Adding the total amount of sale to the current value of total sales
                      
                   }
                  }else{//If the total sale of day is to be calculated
                          if((transDate.getMonthValue() == nowDate.getMonthValue()) &&
                                      (transDate.getYear() == nowDate.getYear()) && (nowDate.getDayOfMonth() == transDate.getDayOfMonth())){
                                double charge = rs1.getDouble("charge"); // Fetchig the total selling price of a sale
                                totalSales = totalSales + charge; // Adding the total amount of sale to the current value of total sales
                          }
                  }
              }
                
            }
         
        } catch (SQLException ex) {//Catches the errors in the query and show where they are
            Logger.getLogger(MasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Returning total daily sales as " + totalSales);
       return totalSales;
    }
    
    private void loadTodayPacks(){
        int monthPacks = getPacks(1);//Returns the total number of entries added this month
        int todayPacks = getPacks(2); //Retuns total amount of this day
        todayPacksGauge.setMinValue(0);
        todayPacksGauge.setMaxValue(monthPacks);//Setting the maximum value of the gauge which in this case the total sales amount of month
        todayPacksGauge.setValue(todayPacks);//Setting the value of the gauge which is the day total sales
    }
     private void loadTodayEntries(){
        int monthEntries = getEntries(1);//Returns the total number of entries added this month
        int todayEntries = getEntries(2); //Retuns total amount of this day
        todayEntriesGauge.setMinValue(0);
        todayEntriesGauge.setMaxValue(monthEntries);//Setting the maximum value of the gauge which in this case the total sales amount of month
        todayEntriesGauge.setValue(todayEntries);//Setting the value of the gauge which is the day total sales
    }
    
    //Method for getting packs depending on the parameter (1 for month packs added and 2 for day entries added)
    private int getPacks(int value){
        int packQty = 0;
        QueryManager Query = new QueryManager();
        String packQuery = "SELECT * FROM packs";
        ResultSet rs1 = Query.getDataQuery(packQuery);
         LocalDateTime date = LocalDateTime.now();
        try {
            while(rs1.next()){
                String packDelQuery = "SELECT * FROM packs_del where packId = '" + rs1.getString("packId") + "';";
                ResultSet rs2 = Query.getDataQuery(packDelQuery);
                if(rs2.next()){
                  
                }else{
                   
                    LocalDateTime regDate = LocalDateTime.parse(rs1.getString("regDate"));
                    
                    if(value == 1){
                        if( date.getYear() == regDate.getYear() && date.getMonthValue() == regDate.getMonthValue()){
                             packQty++;
                         }
                    }else{
                        if((date.getMonthValue() == regDate.getMonthValue()) &&
                                      (date.getYear() == regDate.getYear()) && (date.getDayOfMonth() == regDate.getDayOfMonth())){
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
    
     //Method for getting entries depending on the parameter (1 for month entries added and 2 for day entries added)
    private int getEntries(int value){
        int entryQty = 0;
     
        QueryManager Query = new QueryManager();
        String entryQuery = "SELECT * FROM entries";
        ResultSet rs1 = Query.getDataQuery(entryQuery);
         LocalDateTime date = LocalDateTime.now();
        try {
            while(rs1.next()){
                String entryDelQuery = "SELECT * FROM entries_del where batchNo = '" + rs1.getString("batchNo") + "';";
                ResultSet rs2 = Query.getDataQuery(entryDelQuery);
                if(rs2.next()){
                  
                }else{
                   
                    LocalDate regDate = LocalDate.parse(rs1.getString("regDate"));
                    if(value == 1){
                        if( date.getYear() == regDate.getYear() && date.getMonthValue() == regDate.getMonthValue()){
                             entryQty++;
                         }
                    }else{
                        if((date.getMonthValue() == regDate.getMonthValue()) &&
                                      (date.getYear() == regDate.getYear()) && (date.getDayOfMonth() == regDate.getDayOfMonth())){
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
    
    
    
}
