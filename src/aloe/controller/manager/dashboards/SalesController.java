/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.manager.dashboards;

import static aloe.controller.LoginController.username;
import aloe.controller.receptionist.transaction.DashboardController;
import aloe.model.QueryManager;
import eu.hansolo.medusa.Gauge;
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
public class SalesController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;


    @FXML
    private SimpleMetroArcGauge monthSalesMoneyGauge;
    @FXML
    private Label lblMonthSalesMoney;
    @FXML
    private SimpleMetroArcGauge daySalesMoneyGauge;
    @FXML
    private Label lblTodaySalesMoney;
    @FXML
    private Gauge freqSoldEntryGauge;
    @FXML
    private Label lblMostSoldDrugEntryName;
    @FXML
    private MenuButton menuBtnAvg;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblAvgSaleMoney;
    @FXML
    private Gauge freqSoldPackGauge;
    @FXML
    private Label lblFreqSoldDrugPackName;
    @FXML
    private Label lblTotalSales;
    @FXML
    private Label lblTotalTransactions;

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
                                    setTotalSalesGauge();
                                    setTotalTransactionsGauge();
                                    setMonthSalesMoneyGauge();
                                    setDaySalesMoneyGauge();
                                    setMostFrequentlySoldDrug("Pack");
                                    setMostFrequentlySoldDrug("Entry");
                                    LocalDate nowDate = LocalDate.now();
                                    lblDate.setText(nowDate.getMonth().name()
                                          + ", "  + nowDate.getYear() );
                                    double avgSales = getAvgSalesMoney("year");
                                   lblAvgSaleMoney.setText(NumberFormat.getCurrencyInstance().format(avgSales));
                                
                             }
                         });
                   return null;
               }
           };
           new Thread(task).start();
        }catch(Exception ex){
            
        }
     }
    private void setTotalSalesGauge(){
        QueryManager Query = new QueryManager();
        String salesQuery = "SELECT count(*) FROM sales";
        ResultSet rs1 = Query.getDataQuery(salesQuery);
        int count = 0;
        try {
            while(rs1.next()){
               count = rs1.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        if(!(count == 1)){
            lblTotalSales.setText(count +"");
        }else{
            lblTotalSales.setText(count +"");
        }
        
    }
     private void setMonthSalesMoneyGauge(){
        QueryManager Query = new QueryManager();
        String salesQuery = "SELECT charge,transNo FROM sales";
        ResultSet rs1 = Query.getDataQuery(salesQuery);
        double money = 0;
        double yearMoney =0;
        try {
            while(rs1.next()){
                int transNo = rs1.getInt(2);
                String transQuery = "SELECT trans_date FROM "
                        + "transactions WHERE transNo ='" +transNo + "';";
                ResultSet rs2 = Query.getDataQuery(transQuery);
                LocalDate nowDate = LocalDate.now();
                if(rs2.next()){
                  
                    LocalDateTime transDate = LocalDateTime.parse(rs2.getString(1));
                    if((transDate.getMonthValue() == nowDate.getMonthValue()) &&
                            (transDate.getYear() == nowDate.getYear()))
                            money = money + rs1.getDouble(1);
                    if((transDate.getYear() == nowDate.getYear()))
                        yearMoney = yearMoney + rs1.getDouble(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        monthSalesMoneyGauge.setValue(money);
        monthSalesMoneyGauge.setMaxValue(yearMoney);
        lblMonthSalesMoney.setText(NumberFormat.getCurrencyInstance().format(money)  );
    }
       private void setDaySalesMoneyGauge(){
        QueryManager Query = new QueryManager();
        String salesQuery = "SELECT charge,transNo FROM sales";
        ResultSet rs1 = Query.getDataQuery(salesQuery);
        double money = 0;
        try {
            while(rs1.next()){
                int transNo = rs1.getInt(2);
                String transQuery = "SELECT trans_date FROM transactions "
                        + "WHERE transNo ='" +transNo + "';";
                ResultSet rs2 = Query.getDataQuery(transQuery);
                if(rs2.next()){
                    LocalDateTime nowDate = LocalDateTime.now();
                    LocalDateTime transDate = LocalDateTime.parse(rs2.getString(1));
                    if((transDate.getMonthValue() == nowDate.getMonthValue()) &&
                            (transDate.getYear() == nowDate.getYear()) && (nowDate.getDayOfMonth() == transDate.getDayOfMonth()))
                            money = money + rs1.getDouble(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        daySalesMoneyGauge.setValue(money);
        daySalesMoneyGauge.setMaxValue(monthSalesMoneyGauge.getValue());
        lblTodaySalesMoney.setText( NumberFormat.getCurrencyInstance().format(money) );
    }
     private void setTotalTransactionsGauge(){
        QueryManager Query = new QueryManager();
        String transQuery = "SELECT COUNT(*) FROM transactions ";
        ResultSet rs1 = Query.getDataQuery(transQuery);
        int count = 0;
        try {    
                if(rs1.next())
                    count = rs1.getInt(1);
            
            
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        if(!(count == 1)){
            lblTotalTransactions.setText(count + " Transactions");
        }else{
            lblTotalTransactions.setText(count + " Transaction");
        }
        
    }
    private void setMostFrequentlySoldDrug(String separator){
         QueryManager Query = new QueryManager();
      
        String userLogQuery = "SELECT transNo,id,COUNT(id) "
                + "FROM sales  WHERE item ='" + separator
                + "' GROUP BY id ORDER BY COUNT(id) DESC  ";
        ResultSet rs1 = Query.getDataQuery(userLogQuery);
        long frequency = 0;
        String id = "Nothing";
        long total = 0;
        try {
            while(rs1.next()){
               int transNo = rs1.getInt("transNo");
               String transQuery = "SELECT trans_by FROM transactions WHERE transNo ='" + transNo + "';";
               ResultSet rs2 = Query.getDataQuery(transQuery);
               if(rs2.next()){
                   
                    frequency = rs1.getInt(3);
                    id = rs1.getString("id");
                    break;
                  
               }
            }
            String salesCountQuery = "SELECT count(*) FROM sales WHERE item = '" + separator + "';";
             ResultSet rs2 = Query.getDataQuery(salesCountQuery);
             while(rs2.next()){
                  total = rs2.getInt(1);
             }
          
        } catch (SQLException ex) {
            Logger.getLogger(aloe.controller.admin.userLog.DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        double logPercent = 0.0;
        if(total > 0){
            logPercent  = (frequency * 100) / total;
             if(separator.equals("Entry")){
             lblMostSoldDrugEntryName.setText(getDrugName(id,Query));
             freqSoldEntryGauge.setValue(logPercent);
            }else{
                lblFreqSoldDrugPackName.setText(getDrugName(id,Query));
                freqSoldPackGauge.setValue(logPercent);
            }
        }
       
        
    }
    
    private String getDrugName(String id, QueryManager Query){
          String  name = "nothing";
         String entryQuery = "SELECT id from entries WHERE batchNo ='" + id + "';";
         ResultSet rs1  = Query.getDataQuery(entryQuery);
        try {
            if(rs1.next()){
                    String drugQuery = "SELECT name  FROM drugs where id ='" + rs1.getString("id") + "'";
                    ResultSet rs2 = Query.getDataQuery(drugQuery);
                    if(rs2.next()){
                        name = rs2.getString("name");
                    }
            }else{
                String packQuery = "SELECT batchNo FROM packs  WHERE packId ='" + id + "';";
                ResultSet rs2  = Query.getDataQuery(packQuery);
                if(rs2.next()){
                      entryQuery = "SELECT id from entries WHERE batchNo ='" + rs2.getString("batchNo") + "';";
                      ResultSet rs3  = Query.getDataQuery(entryQuery);
                      if(rs3.next()){
                          String drugQuery = "SELECT name  FROM drugs where id ='" + rs3.getString("id") + "'";
                          ResultSet rs4 = Query.getDataQuery(drugQuery);
                          if(rs4.next()){
                              name = rs4.getString("name");
                          }
                      }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
       return name;
    }
    @FXML
    private void menuBtnYearAction(ActionEvent event) {
        menuBtnAvg.setText("Year AVG Sales Money");
        LocalDate nowDate = LocalDate.now();
        lblDate.setText(nowDate.getMonth().name()
              + ", "  + nowDate.getYear() );
        double avgSales = getAvgSalesMoney("year");
       lblAvgSaleMoney.setText(NumberFormat.getCurrencyInstance().format(avgSales));
    }

    @FXML
    private void menuBtnMonthAction(ActionEvent event) {
       menuBtnAvg.setText("Month AVG Sales Money");
        LocalDate nowDate = LocalDate.now();
        lblDate.setText(nowDate.getMonth().name()
              + ", "  + nowDate.getYear() );
        double avgSales = getAvgSalesMoney("month");
       lblAvgSaleMoney.setText(NumberFormat.getCurrencyInstance().format(avgSales));
    }

    @FXML
    private void menuBtnDayAction(ActionEvent event) {
        menuBtnAvg.setText("Day AVG Sales Money");
        LocalDate nowDate = LocalDate.now();
        lblDate.setText(nowDate.getMonth().name()
              + ", "  + nowDate.getYear() );
        double avgSales = getAvgSalesMoney("day");
       lblAvgSaleMoney.setText(NumberFormat.getCurrencyInstance().format(avgSales));
    }
    
    private double getAvgSalesMoney(String choice){
        double total = 0;
        int numOfSales = 0;
        QueryManager Query = new QueryManager();
        String expenseQuery = "SELECT charge,transNo FROM sales;";
        ResultSet rs1 = Query.getDataQuery(expenseQuery);
        try {
             LocalDate nowDate = LocalDate.now();
            while(rs1.next()){
                 
                    int transNo = rs1.getInt(2);
                    String transQuery = "SELECT trans_date FROM transactions"
                            + " WHERE transNo ='" + transNo + "';";
                    ResultSet rs2 = Query.getDataQuery(transQuery);
                    if(rs2.next()){
            
                           LocalDateTime date = LocalDateTime.parse(rs2.getString(1));
                         
                         if(choice.equalsIgnoreCase("year")){
                                 if((date.getYear() == nowDate.getYear())){
                                  numOfSales++;
                                  total = total + rs1.getDouble(1);
                            }
                       } 
                       else if(choice.equalsIgnoreCase("month")){
                                if((date.getYear() == nowDate.getYear()) &&
                                   (date.getMonthValue() == nowDate.getMonthValue())){
                                   numOfSales++;
                                   total = total + rs1.getDouble("charge");
                                 } 
                       }else{
                                if(nowDate.equals(date.toLocalDate())){
                                   numOfSales++;
                                   total = total + rs1.getDouble("charge");
                               } 
                        }
              }
           }
        } catch (SQLException ex) {
            Logger.getLogger(aloe.controller.pharmacist.expense.DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
       double average = 0;
       if(numOfSales > 0){
           average = total/numOfSales;
       }
       return average;
    }
    
    
}
