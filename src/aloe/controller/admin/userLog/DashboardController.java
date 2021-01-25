/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.admin.userLog;

import aloe.model.QueryManager;
import eu.hansolo.medusa.Gauge;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.*;
import static java.time.temporal.ChronoUnit.MINUTES;
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
    private SimpleMetroArcGauge totalLogsGauge;
    @FXML
    private Label lblTotalLogs;
    @FXML
    private SimpleMetroArcGauge monthTotalLogsGauge;
    @FXML
    private Label lblmonthLogs;
    @FXML
    private SimpleMetroArcGauge todayTotalLogsGauge;
    @FXML
    private Label lblTodayLogs;
    @FXML
    private MenuButton menuBtnTotal;
    @FXML
    private Label lblChoice;
    @FXML
    private Label lblAvgLogs;
    @FXML
    private Gauge mostLogGauge;
    @FXML
    private Label lblMostLogUsername;
    @FXML
    private Gauge leastLogGauge;
    @FXML
    private Label lblLeastLogUsername;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
       loadDashBoardData();
    } 
     private void loadDashBoardData(){
        try{
              Task<Void> task = new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                         Thread.sleep(1000);
                      
                         Platform.runLater(new Runnable() {
                             @Override
                             public void run() {
                                    setAllLogGauges();
                                    getMostLoggedInUser();
                                    getLeastLoggedInUser();
                                    menuBtnTotal.setText("Year AVG Time Spent");
                                    lblChoice.setText(LocalDate.now().getYear() + "");
                                    double minutes = setAvgTimeSpent("year");
                                    if(minutes > 1){
                                         lblAvgLogs.setText( minutes+ " MINS");
                                    }else{
                                        lblAvgLogs.setText( minutes+ " MIN");
                                    }
                             }
                         });
                   return null;
               }
           };
           new Thread(task).start();
        }catch(Exception ex){
            
        }
     }
     private void setAllLogGauges(){
        QueryManager Query = new QueryManager();
        String expenseQuery = "SELECT * FROM userLogs";
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        //Expenses
        int month = 0;
        int day = 0;
        int total = 0;
        try {
            while(rs1.next()){
                LocalDateTime nowDate = LocalDateTime.now();
                LocalDateTime regDate  = LocalDateTime.parse(rs1.getString("logInDate"));
                if((regDate.getYear() == nowDate.getYear()) &&
                        (regDate.getMonthValue() == nowDate.getMonthValue())){
                    month++;
                }
                if((regDate.getYear() == nowDate.getYear()) &&
                        (regDate.getMonthValue() == nowDate.getMonthValue()) &&
                       (regDate.getDayOfMonth()== nowDate.getDayOfMonth())){
                    day++;
                }
                total++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
      
       lblTotalLogs.setText(NumberFormat.getInstance().format(total));
       monthTotalLogsGauge.setMaxValue(total);
       monthTotalLogsGauge.setValue(month);
       lblmonthLogs.setText(NumberFormat.getInstance().format(month) + " logs");
       todayTotalLogsGauge.setMaxValue(month);
       todayTotalLogsGauge.setValue(day);
       lblTodayLogs.setText(NumberFormat.getInstance().format(day) + " " + " logs" );
     }
   
    private void getMostLoggedInUser(){
           QueryManager Query = new QueryManager();
      
        String userLogQuery = "SELECT username,COUNT(username) "
                + "FROM userlogs "
                + "GROUP BY username ORDER BY COUNT(username) DESC  ";
        ResultSet rs1 = Query.getDataQuery(userLogQuery);
        int frequency = 0;
        String username = "Nothing";
        int total = 0;
        try {
            while(rs1.next()){
               frequency = rs1.getInt(2);
               username = rs1.getString("username");
               break;
            }
            String userLogCountQuery = "SELECT COUNT(*) as no_of_rows FROM userlogs;";
             ResultSet rs2 = Query.getDataQuery(userLogCountQuery);
             if(rs2.next()){
                 total = rs2.getInt("no_of_rows");
             }
          
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        double logPercent = 0.0;
        if(total > 0){
            logPercent  = (frequency * 100) / total;
        }
        lblMostLogUsername.setText(username);
        mostLogGauge.setValue(logPercent);
        
    }
      private void getLeastLoggedInUser(){
           QueryManager Query = new QueryManager();
      
        String userLogQuery = "SELECT username,COUNT(username) "
                + "FROM userlogs "
                + "GROUP BY username ORDER BY COUNT(username) ASC  ";
        ResultSet rs1 = Query.getDataQuery(userLogQuery);
        long frequency = 0;
        String username = "Nothing";
        long total = 0;
        try {
            while(rs1.next()){
               frequency = rs1.getInt(2);
               username = rs1.getString("username");
               break;
            }
            String userLogCountQuery = "SELECT COUNT(*) as no_of_rows FROM userlogs;";
             ResultSet rs2 = Query.getDataQuery(userLogCountQuery);
             if(rs2.next()){
                 total = rs2.getInt("no_of_rows");
             }
          
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        double logPercent = 0.0;
        if(total > 0){
            logPercent  = (frequency * 100) / total;
        }
        lblLeastLogUsername.setText(username);
        leastLogGauge.setValue(logPercent);
        
    }
    @FXML
    private void menuBtnYearAction(ActionEvent event) {
        menuBtnTotal.setText("Year AVG Time Spent");
        lblChoice.setText(LocalDate.now().getYear() + "");
        double minutes = setAvgTimeSpent("year");
        if(minutes > 1){
             lblAvgLogs.setText( minutes+ " MINS");
        }else{
            lblAvgLogs.setText( minutes+ " MIN");
        }
       
    }

    @FXML
    private void menuBtnMonthAction(ActionEvent event) {
        menuBtnTotal.setText("Month AVG Time Spent");
        LocalDate nowDate = LocalDate.now();
        lblChoice.setText(nowDate.getMonth().name()
              + ", "  + nowDate.getYear() );
        double minutes = setAvgTimeSpent("month");
        if(minutes > 1){
             lblAvgLogs.setText( minutes+ " MINS");
        }else{
            lblAvgLogs.setText( minutes+ " MIN");
        }
    }

    @FXML
    private void menuBtnDayAction(ActionEvent event) {
         menuBtnTotal.setText("Day AVG Time Spent");
         LocalDate nowDate = LocalDate.now();
        lblChoice.setText(nowDate.getDayOfMonth() + " " + nowDate.getMonth().name()
              + ", "  + nowDate.getYear() );
        double minutes = setAvgTimeSpent("day");
        if(minutes > 1){
             lblAvgLogs.setText( minutes+ " MINS");
        }else{
            lblAvgLogs.setText( minutes+ " MIN");
        }
    }
    private double setAvgTimeSpent(String choice){
        double total = 0;
        int numOfLogs = 0;
        QueryManager Query = new QueryManager();
        String expenseQuery = "SELECT * FROM userLogs;";
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        try {
            while(rs1.next()){
                  
                    LocalDateTime date = LocalDateTime.parse(rs1.getString("logInDate"));
                    LocalDateTime nowDate = LocalDateTime.now();
                    if(choice.equalsIgnoreCase("year")){
                         if((date.getYear() == nowDate.getYear())){
                           numOfLogs++;
                          LocalDateTime logInDate = LocalDateTime.parse(rs1.getString("logInDate"));
                          LocalDateTime logOutDate = LocalDateTime.parse(rs1.getString("logOutDate"));
                          Temporal outTemp = logOutDate;
                          Temporal inTemp = logInDate;
                          total = total + inTemp.until(outTemp, MINUTES);
                          System.out.println("Difference is " + inTemp.until(outTemp, MINUTES));
                          System.out.println("Total is " + total);
                       } 
                    }else if(choice.equalsIgnoreCase("month")){
                         if((date.getYear() == nowDate.getYear()) &&
                            (date.getMonthValue() == nowDate.getMonthValue())){
                            numOfLogs++;
                            LocalDateTime logInDate = LocalDateTime.parse(rs1.getString("logInDate"));
                            LocalDateTime logOutDate = LocalDateTime.parse(rs1.getString("logOutDate"));
                            Temporal outTemp = logOutDate;
                            Temporal inTemp = logInDate;
                            total = total + inTemp.until(outTemp, MINUTES);
                        } 
                    }else{
                         if((date.equals(nowDate))){
                            numOfLogs++;
                            LocalDateTime logInDate = LocalDateTime.parse(rs1.getString("logInDate"));
                            LocalDateTime logOutDate = LocalDateTime.parse(rs1.getString("logOutDate"));
                            Temporal outTemp = logOutDate;
                            Temporal inTemp = logInDate;
                            total = total + inTemp.until(outTemp, MINUTES);
                            
                       } 
                    }
              
            }
        } catch (SQLException ex) {
            Logger.getLogger(aloe.controller.pharmacist.expense.DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
       double average = 0;
       if(numOfLogs > 0){
           System.out.println("Mins are" +  total);
           average = total/numOfLogs;
       }
       return average;
    }
    
}
