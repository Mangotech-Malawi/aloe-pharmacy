/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.expense;

import aloe.model.QueryManager;
import eu.hansolo.medusa.Gauge;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.StackPane;
import jfxtras.scene.control.gauge.linear.SimpleMetroArcGauge;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class DashboardController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private SimpleMetroArcGauge dailyExpenseGauge;
    @FXML
    private Label lblDailyExpense;
    @FXML
    private SimpleMetroArcGauge monthlyExpenseGauge;
    @FXML
    private Label lblMonthlyExpense;
    @FXML
    private SimpleMetroArcGauge yearlyExpenseGauge;
    @FXML
    private Label lblYearlyExpense;
    @FXML
    private MenuButton menuBtnExpensive;
    @FXML
    private MenuButton menuBtnFrequent;
    @FXML
    private Label lblFrequent;
    @FXML
    private MenuButton menuBtnAvg;
    @FXML
    private MenuButton menuBtnLeastSpentOn;
    @FXML
    private Label lblLeastSpentOn;
    @FXML
    private Label lblMostExpense;
    @FXML
    private Label lblMostExpensiveCat;
    @FXML
    private Label lblFrequentCat;
    @FXML
    private Label lblLeastSpentOnCat;
    @FXML
    private Label lblAvg;
    @FXML
    private Label lblTotalExpenses;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        setGauges();
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
                                    setAllExpenses();
                                    setMostExpensive("year");
                                    setFrequentSpentOn("year");
                                    setLeastSpentOn("year");
                                    lblAvg.setText("" + getAverageExpense("year")); 
                             }
                         });
                   return null;
               }
           };
           new Thread(task).start();
        }catch(Exception ex){
            
        }
    }
    
    private void setAllExpenses(){
        QueryManager Query = new QueryManager();
        String expenseQuery = "SELECT * FROM expenses";
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        //Expenses
        double year =0;
        double month = 0;
        double day = 0;
        double total = 0;
        try {
            while(rs1.next()){
               int expenseNo = rs1.getInt("expenseNo");
               String expenseDelQuery ="SELECT expenseNo FROM expenses_del WHERE expenseNo ='" + expenseNo + "'";
               ResultSet rs2 = Query.getDataQuery(expenseDelQuery);
               if(rs2.next()){
                   //Do'nt do anything
               }else{
                   LocalDate regDate  = LocalDate.parse(rs1.getString("regDate"));
                   LocalDate nowDate = LocalDate.now();
                   if(regDate.getYear() == nowDate.getYear()){
                       year = year + rs1.getDouble("amount");
                   }
                   if((regDate.getYear() == nowDate.getYear()) &&
                           (regDate.getMonthValue() == nowDate.getMonthValue())){
                       month = month + rs1.getDouble("amount");
                   }
                   if((regDate.getYear() == nowDate.getYear()) &&
                           (regDate.getMonthValue() == nowDate.getMonthValue())
                           && (regDate.getDayOfMonth() == nowDate.getDayOfMonth())){
                       day = day + rs1.getDouble("amount");
                   }
                   total = total + rs1.getDouble("amount");
               }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dailyExpenseGauge.setValue(day);
        dailyExpenseGauge.setMaxValue(month);//Setting of daily expense gauge maximum value to be the total month expenses
        lblDailyExpense.setText("MWK" + NumberFormat.getInstance().format(day)); 
        monthlyExpenseGauge.setValue(month);
        monthlyExpenseGauge.setMaxValue(year);//Setting of month expense gauge maximum value to be the total year expenses
        lblMonthlyExpense.setText("MWK" + NumberFormat.getInstance().format(month));
        yearlyExpenseGauge.setValue(year);
        yearlyExpenseGauge.setMaxValue(total); //Setting of year expense gauge maximum value to be the total expenses
        lblYearlyExpense.setText("MWK" + NumberFormat.getInstance().format(year));
        lblTotalExpenses.setText("MWK"+NumberFormat.getInstance().format(total));
    }
    
    @FXML
    private void menuBtnExpensiveYearAction(ActionEvent event) {
        menuBtnExpensive.setText("Most Expensive This Year");
        setMostExpensive("year");
    }
   
    @FXML
    private void menuBtnExpensiveMonthAction(ActionEvent event) {
        menuBtnExpensive.setText("Most Expensive This Month");
        setMostExpensive("month");
    }

    @FXML
    private void menuBtnExpensiveDayAction(ActionEvent event) {
        menuBtnExpensive.setText("Most Expensive This Day");
        setMostExpensive("day");
    }

     private void setMostExpensive(String choice){
        QueryManager Query = new QueryManager();
      // " SELECT artist_name FROM artist WHERE artist_id = " +
      // "(SELECT artist_id FROM album WHERE album_name = "In A Silent Way");"
      
        String expenseQuery = "SELECT expenseNo,regDate,category,SUM(amount) FROM expenses "
                + "GROUP BY category,regDate ORDER BY amount DESC;";
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        double amount = 0.0;
        String category = "Nothing";
        try {
            while(rs1.next()){
              int expenseNo = rs1.getInt("expenseNo");
              String expenseDelQuery = "SELECT expenseNo FROM "
                      + "expenses_del WHERE expenseNo='" +expenseNo + "'";
              ResultSet rs2 = Query.getDataQuery(expenseDelQuery);
              if(rs2.next()){
                  //Do nothing
              }else{
                    LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                    LocalDate nowDate = LocalDate.now();
                    if(choice.equalsIgnoreCase("year")){
                         if((date.getYear() == nowDate.getYear())){
                        System.out.println(rs1.getString("category") + " | " 
                        +rs1.getDouble(4) + rs1.getString("regDate"));
                        amount = rs1.getDouble(4);
                        category = rs1.getString("category");
                        break;
                       } 
                    }else if(choice.equalsIgnoreCase("month")){
                         if((date.getYear() == nowDate.getYear()) &&
                            (date.getMonthValue() == nowDate.getMonthValue())){

                            amount = rs1.getDouble(4);
                            category = rs1.getString("category");
                            break;
                        } 
                    }else{
                         if((date.getYear() == nowDate.getYear()) &&
                            (date.getMonthValue() == nowDate.getMonthValue()) &&
                                 (date.getDayOfMonth() == nowDate.getMonthValue())){
                            amount = rs1.getDouble(4);
                            category = rs1.getString("category");
                            break;
                       } 
                    }
              }
            }
          
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        lblMostExpensiveCat.setText(category);
        lblMostExpense.setText("MWK" + amount);
    }
    
       private void setFrequentSpentOn(String choice){
        QueryManager Query = new QueryManager();
      
        String expenseQuery = "SELECT expenseNo,regDate,category,COUNT(category) "
                + "FROM expenses WHERE isDeleted = '0' "
                + "GROUP BY category,regDate ORDER BY COUNT(category) DESC  ";
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        int frequency = 0;
        String category = "Nothing";
        try {
            while(rs1.next()){
          
                    LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                    LocalDate nowDate = LocalDate.now();
                    if(choice.equalsIgnoreCase("year")){
                         if((date.getYear() == nowDate.getYear())){
                        System.out.println(rs1.getString("category") + " | " 
                        +rs1.getDouble(4) + rs1.getString("regDate"));
                        frequency = rs1.getInt(4);
                        category = rs1.getString("category");
                        break;
                       } 
                    }else if(choice.equalsIgnoreCase("month")){
                         if((date.getYear() == nowDate.getYear()) &&
                            (date.getMonthValue() == nowDate.getMonthValue())){
               
                            frequency = rs1.getInt(4);
                            category = rs1.getString("category");
                            break;
                        } 
                    }else{
                         if((date.getYear() == nowDate.getYear()) &&
                            (date.getMonthValue() == nowDate.getMonthValue()) &&
                                 (date.getDayOfMonth() == nowDate.getMonthValue())){
                            frequency = rs1.getInt(4);
                            category = rs1.getString("category");
                            break;
                       } 
                    }
            }
          
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        lblFrequentCat.setText(category);
        lblFrequent.setText("" + frequency);
    }
    @FXML
    private void menuBtnFreqYearAction(ActionEvent event) {
        menuBtnFrequent.setText("Most Spent On This Year");
        setFrequentSpentOn("year");
    }

    @FXML
    private void menuBtnFreqMonthAction(ActionEvent event) {
        menuBtnFrequent.setText("Most Spent On This Month");
        setFrequentSpentOn("month");
    }

    @FXML
    private void menuBtnFreqTodayAction(ActionEvent event) {
        menuBtnFrequent.setText("Most Spent On This Month");
        setFrequentSpentOn("day");
    }

    @FXML
    private void menuBtnLeastYearAction(ActionEvent event) {
        menuBtnLeastSpentOn.setText("Least Spent On This Year");
        setLeastSpentOn("year");
    }

    @FXML
    private void menuBtnLeastMonthAction(ActionEvent event) {
        menuBtnLeastSpentOn.setText("Least Spent On This Month");
        setLeastSpentOn("month");
    }

    @FXML
    private void menuBtnLeastDayAction(ActionEvent event) {
        menuBtnLeastSpentOn.setText("Least Spent On This Day");
        setLeastSpentOn("day");
    }
    
    private void setLeastSpentOn(String choice){
        QueryManager Query = new QueryManager();
      
        String expenseQuery = "SELECT expenseNo,regDate,category,COUNT(category) "
                + "FROM expenses WHERE isDeleted = '0' "
                + "GROUP BY category ORDER BY COUNT(category) ASC;";
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        int leastSpentOn = 0;
        String category = "Nothing";
        int count = 0;
        try {
            while(rs1.next()){
                   
                    LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                    LocalDate nowDate = LocalDate.now();
                    if(choice.equalsIgnoreCase("year")){
                         if((date.getYear() == nowDate.getYear())){
                        System.out.println(rs1.getString("category") + " | " 
                        +rs1.getDouble(4) + rs1.getString("regDate"));
                        
                        leastSpentOn = rs1.getInt(4);
                        category = rs1.getString("category");
                        break;
                       } 
                    }else if(choice.equalsIgnoreCase("month")){
                         if((date.getYear() == nowDate.getYear()) &&
                            (date.getMonthValue() == nowDate.getMonthValue())){
                            
                            leastSpentOn  = rs1.getInt(4);
                            category = rs1.getString("category");
                            break;
                        } 
                    }else{
                         if((date.getYear() == nowDate.getYear()) &&
                            (date.getMonthValue() == nowDate.getMonthValue()) &&
                                 (date.getDayOfMonth() == nowDate.getMonthValue())){
                            leastSpentOn  = rs1.getInt(4);
                            category = rs1.getString("category");
                            break;
                       } 
                    }
            }
          
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        lblLeastSpentOnCat.setText(category);
        lblLeastSpentOn.setText("" + leastSpentOn );
    }
    @FXML
    private void menuBtnAvgYearAction(ActionEvent event) {
         menuBtnAvg.setText("Least Spent On This Month");
         lblAvg.setText("" + getAverageExpense("year")); 
    }

    @FXML
    private void menuBtnAvgMonthAction(ActionEvent event) {
         menuBtnAvg.setText("Least Spent On This Month");
         lblAvg.setText("" + getAverageExpense("month")); 
    }

    @FXML
    private void menuBtnAvgDayAction(ActionEvent event) {
         menuBtnAvg.setText("Least Spent On This Month");
         lblAvg.setText("" + getAverageExpense("day")); 
    }
    
    private double getAverageExpense(String choice){
       
        double total = 0.0;
        int numOfExpenses = 0;
        QueryManager Query = new QueryManager();
        String expenseQuery = "SELECT * FROM expenses where isDeleted ='0'";
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        try {
            while(rs1.next()){
                  
                    LocalDate date = LocalDate.parse(rs1.getString("regDate"));
                    LocalDate nowDate = LocalDate.now();
                    if(choice.equalsIgnoreCase("year")){
                         if((date.getYear() == nowDate.getYear())){
                           numOfExpenses++;
                          total = total + rs1.getDouble("amount");
                       } 
                    }else if(choice.equalsIgnoreCase("month")){
                         if((date.getYear() == nowDate.getYear()) &&
                            (date.getMonthValue() == nowDate.getMonthValue())){
                            total = total + rs1.getDouble("amount");
                            numOfExpenses++;
                        } 
                    }else{
                         if((date.getYear() == nowDate.getYear()) &&
                            (date.getMonthValue() == nowDate.getMonthValue()) &&
                                 (date.getDayOfMonth() == nowDate.getMonthValue())){
                                numOfExpenses++;
                             total = total + rs1.getDouble("amount");
                       } 
                    }
              
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
       double average = 0.0;
       if(numOfExpenses > 0){
           average = total/numOfExpenses;
       }
       return average;
    }

    
}
