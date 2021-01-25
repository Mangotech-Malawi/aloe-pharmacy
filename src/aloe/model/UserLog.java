/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.model;

import com.jfoenix.controls.JFXButton;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Senzenjani
 */
public class UserLog extends Report {
    private int logNumber;
    private String outDate;
    private String inTime;
    private String outTime;
    private String activities;
    private JFXButton activitiesBtn;

    public  UserLog(){}
    public UserLog(int logNumber,String username,String privilege, String inDate,String outDate,
            String inTime, String outTime,JFXButton activitiesBtn) {
        this.logNumber = logNumber;
        this.outTime = outTime;
        this.inTime = inTime;
        this.outDate = outDate;
        setPrivilege(privilege);
        setRegDate(inDate);
        setUsername(username);
        this.activitiesBtn = activitiesBtn;
    }
    
    public UserLog(String username, String inDate, String outDate, String activities){
        setRegDate(inDate);
        setUsername(username);
        this.outDate = outDate;
        this.activities = activities;
    }

    public void setActivitiesBtn(JFXButton activitiesBtn) {
        this.activitiesBtn = activitiesBtn;
    }

    public void setLogNumber(int logNumber) {
        this.logNumber = logNumber;
    }
    
    public void setActivities(String activities) {
        this.activities = activities;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }
    

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public int getLogNumber() {
        return logNumber;
    }
    
    public String getActivities() {
        return activities;
    }

    public String getOutDate() {
        return outDate;
    }

    public String getInTime() {
        return inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public JFXButton getActivitiesBtn() {
        return activitiesBtn;
    }
    
    
       /*The method returns an insert query String when called*/
    public String addLog(){
        
        String query = "INSERT INTO userLogs (username,logInDate,logOutDate,inDate,activities) VALUES (" + 
                "'"+ getUsername()+ "'," +"'" +getRegDate()+ "'," +"'" +getOutDate()+ "','" + LocalDate.now().toString()  +"','" +getActivities()+"');";
       return query;
    }
    
    public void updateActivities(String logInDate,String activity){
        try {
            QueryManager Query = new QueryManager();
            String query = "SELECT * from userLogs where logInDate = '" + logInDate + "'";
            ResultSet rs = Query.getDataQuery(query);
             if(rs.next()){
                 String activities = rs.getString("activities")  + activity;
               String  updateQuery = "UPDATE userlogs SET activities = '" + activities + "' where logInDate = '" + logInDate + "'";
               Query.execActionUpdate(updateQuery);
             }
               
            
        } catch (SQLException ex) {
            Logger.getLogger(UserLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void updateLogOutTime(String logInDate, String logOutDate){
        QueryManager Query = new QueryManager();
        String updateQuery = "UPDATE userlogs set logOutDate = '" + logOutDate + "' where logInDate = '" + logInDate + "';";
        Query.execActionUpdate(updateQuery);
    }
   
   /*The method returns an update query String when called*/
    // public String updateLog(String password){
       
    //}
    
      /*The method returns an delete query String when called*/
     public String deleteLog(String password){
        String query = "";//delete query comes here
        return query;
    }
    
    public void updateLogs(String username){
        QueryManager Query = new QueryManager();
        String updateQuery = "UPDATE users set logs = (logs + 1) where username = '" + username + "';";
        Query.execActionUpdate(updateQuery);
    }
    
    
    
    
}
