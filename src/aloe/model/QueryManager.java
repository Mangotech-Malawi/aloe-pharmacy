/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.model;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javax.swing.JOptionPane;

/**
 *
 * @author Senzenjani
 */
public class QueryManager {
    
    public static Connection conn = null;
    public static Statement stmt   = null;
    public static String dbUsername ="root";
    public static String dbPassword = "";
     public QueryManager(){
       //createConnection(ipAddress,username);
       createConnection();
     } 
     
     public QueryManager(String dbUsername,String dbPassword){
         createSqliteConnection();
       
     }
    
     public void createSqliteConnection(){
          conn=null;
          stmt= null;
          try{
            Class.forName("org.sqlite.JDBC");
            conn= DriverManager.getConnection("jdbc:sqlite:aloe.db");
            conn.setAutoCommit(true); 
            }catch (Exception e){
        }        
     }
     public void createConnection(){
          conn=null;
          stmt= null;
          try{
             Class.forName("com.mysql.jdbc.Driver");
                conn= DriverManager.getConnection("jdbc:mysql://mysql.uk.cloudlogin.co:3307/mangotech_aloe", "mangotech_aloe" ,"rN10DFo2vb");
                System.out.println("Connected Successfully");
                conn.setAutoCommit(true); 
            }catch (Exception e){
                System.out.println("Connection failed");
           }        
     }
       public void ClearDB(){
        try {
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(QueryManager.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
    /* public  void createConnection(String ipAddress, String username){
           conn=null;
        stmt= null;
        try{
            
            Class.forName("com.mysql.jdbc.Driver");
            
            System.out.println(ipAddress);
            
            conn= DriverManager.getConnection("jdbc:mysql://" +ipAddress+ ":3306/church", username,"");
            conn.setAutoCommit(true); 
             }catch (Exception e){
              Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Connection error");
                        alert.setContentText("Please make sure you have correct ip address");
                        alert.show();

        }
         
     }*/
       
    public boolean execAction(String qu)
    {
        try
        {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        }
        catch(SQLException ex)
        {
            //JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(),
            //"Error occured", JOptionPane.ERROR_MESSAGE);
            //System.out.println("Exception at execAction:datahandler" + ex.getLocalizedMessage());
            return false;
        }
        finally{}
    }
    
     public boolean execActionUpdate(String qu){   
        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(qu);
            return true;
        } catch(SQLException e){
           // Alert alert = new Alert(Alert.AlertType.ERROR);
            //alert.setHeaderText(null);
            //alert.setContentText("Exception at execAction: datahandler" + e.getMessage());
            //alert.showAndWait();
            //System.out.println(" " + e.getLocalizedMessage());
            return false;
        } finally{
        }
    }
  
   
     public boolean insertStatement(String insert_query){
            try{
         
            stmt = conn.createStatement();
            System.out.println("Our query was: " + insert_query);
            stmt.executeUpdate(insert_query);
            return true;
            
        }catch (Exception e){
           // System.err.println(e.getClass().getName()+ ": " + e.getMessage());
            return false;
        }
      
    }
    public ResultSet getDataQuery(String getQuery){
        ResultSet rs = null;
        try{
        
            stmt = conn.createStatement();
            System.out.println("Our query was: " + getQuery);
           rs = stmt.executeQuery(getQuery);
        }catch(Exception e){
            //System.err.println(e.getClass().getName()+ ": " + e.getMessage());
            //System.exit(0);
        }
        return rs;
    }
    
 
  
          void createUserTable(){
        String Table = "users";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
         /*String username, String password, String regDate, String privilege,
    String email, String firstname,String lastname,String contact)*/
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "username varchar(200) primary key,\n"
          + "password varchar(200),\n" 
          + "firstname varchar(200),\n"
          + "lastname varchar(200),\n"
          + "email varchar(200),\n"
          + "contact varchar(200),\n"
          + "gender varchar(200),\n"
          + "privilege varchar(200),\n"
          + "dob varchar(200),\n"
          + "logs int default 0,\n"  
          + "regDate varchar(200)\n"  
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
     
     void createDrugTable(){
        String Table = "drugs";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "id integer auto_increment primary key ,\n"
          + "name varchar(200) UNIQUE,\n" 
          + "firstCategory varchar(200),\n"
          + "secCategory varchar(200),\n"
          + "sellingPrice double,\n"
          + "orderPrice double,\n"
          + "measurement varchar(200),"
          + "description varchar(200)\n"
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
     
    void createEntriesThresholdsTable(){
        String Table = "entriesThresholds";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "id integer  primary key ,\n"
          + "entryQty int\n" 
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
    
       
    void createPacksThresholdsTable(){
        String Table = "packsThresholds";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "id integer  primary key ,\n"
          + "numOfPacks int\n" 
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
     
    void createEntryTable(){
        String Table = "entries";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "batchNo integer auto_increment primary key ,\n"
          + "id varchar(200),\n"
          + "quantity int,\n" 
          + "supplierName varchar(200),\n"
          + "expiryDate varchar(200),\n"
          + "regDate varchar(200)\n"
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
      void createPacksTable(){
        String Table = "packs";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "packId integer auto_increment primary key ,\n"
          + "batchNo varchar(200),\n"
          + "numOfPacks int,"
          + "quantity int,\n" 
          + "price double,\n"
          + "regDate varchar(200)\n"
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
      void createTransactionTable(){
        String Table = "transactions";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "transNo integer auto_increment primary key  ,\n"
          + "trans_by varchar(200),\n"
          + "trans_date varchar(200)\n"
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
    
      void createSalesTable(){
            String Table = "sales";  
           try
           {
               stmt = conn.createStatement();
               DatabaseMetaData dbm = conn.getMetaData();
                ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
           if(tables.next())
           {
               System.out.println("Table " + Table + " already exists..you ready to go!");
           }
           else
           {
               stmt.execute("CREATE TABLE " +Table+ "(" + " "
               + "saleNo integer auto_increment primary key  ,\n"
               + "transNo varchar(200),\n"
               + "id varchar(200),\n"
               + "quantity integer,\n"
               + "charge double,\n"
               + "orderPrice double,\n"
               + "item varchar(200)\n"
               + ")");
           } 
           }
           catch(SQLException e)
           {
               System.err.println(e.getMessage() +"-------setupDatabase");
           }
           finally{}
    }
      
     void createCustomersTable(){
        String Table = "customers";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "customerId varchar(200) primary key ,\n"
          + "fullname varchar(200),\n"
          + "residence varchar(200),\n"
          + "contact varchar(200),\n" 
          + "regDate varchar(200)\n"
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
     
     void createUserLogTable(){
        String Table = "userLogs";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
     
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "logNo integer auto_increment primary key ,\n"    
          + "username varchar(200),\n"
          + "logInDate varchar(200),\n" 
          + "logOutDate varchar(200),\n"
          + "inDate varchar(200),\n" 
          + "activities text\n"
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
       void createUserLogCountTable(){
        String Table = "userLogCount";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "username varchar(200)  primary key,\n"
          + "logs int\n" 
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
   
    void createReportTable(){
         
       String Table = "reports";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
     
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "reportNo varchar(200) primary key ,\n"
          + "name varchar(200),\n" 
          + "content varchar(200),\n"     
          + "date varchar(200)\n"  
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
    void createBarcodesTable(){
         
       String Table = "barcodes";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
       
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "code varchar(200) primary key,\n"
          + "batchNo integer,\n" 
          + "isBatchNo varchar(200) \n"     
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
    
    void createDelPacksTable(){
         
       String Table = "packs_del";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
       
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "packId integer  primary key,\n"
          + "delDate varchar(200)\n" 
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
    
     void createDelDrugsTable(){
         
       String Table = "drugs_del";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
       
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "id integer primary key,\n"
          + "delDate varchar(200)\n" 
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
     void createDelEntriesTable(){
         
       String Table = "entries_del";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
       
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "batchNo integer primary key,\n"
          + "delDate varchar(200)\n" 
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
     
    void createDelUsersTable(){
         
       String Table = "users_del";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
       
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "username varchar(200) primary key,\n"
          + "delDate varchar(200)\n" 
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
     void createExSettingsTable(){
         
       String Table = "expirySettings";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
       
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "setNo varchar(200) primary key,\n"
          + "excellent integer,\n" 
          + "better integer,\n"
          + "good integer,\n" 
          + "bad integer,\n" 
          + "worse integer,\n" 
          + "regDate varchar(200),\n" 
          + "username varchar(200)\n" 
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
     
     void createExpensesTable(){
         
       String Table = "expenses";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
       
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "expenseNo integer auto_increment primary key,\n"
          + "amount double,\n" 
          + "category varchar(200),\n"
          + "description text,\n" 
          + "regDate varchar(200),\n" 
          + "isDeleted boolean default 0\n"
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
      void createExpenseDelTable(){
         
       String Table = "expenses_del";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
       
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "expenseNo varchar(200) primary key,\n"
          + "delDate varchar(200)\n" 
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
    public void initializeTables(){
        createUserTable();
        createDrugTable();
        createTransactionTable();
        createReportTable();
        createUserLogTable();
        createEntryTable();
        createPacksTable();
        createCustomersTable();
        createUserLogCountTable();
        createBarcodesTable();
        createSalesTable();
        createDelDrugsTable();
        createDelEntriesTable();
        createDelPacksTable();
        createExSettingsTable();
        createExpensesTable();
        createExpenseDelTable();
        createDelUsersTable();
        createEntriesThresholdsTable();
        createPacksThresholdsTable();
    }
    
    public void initializeSqliteTables(){
        createDBUserTable();
    }
    
     void createDBUserTable(){
      String Table = "users";  
      try
      {
          stmt = conn.createStatement();
          DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables =dbm.getTables(null, null, Table.toUpperCase(), null);
      if(tables.next())
      {
          System.out.println("Table " + Table + " already exists..you ready to go!");
      }
      else
      {
         /*String username, String password, String regDate, String privilege,
    String email, String firstname,String lastname,String contact)*/
          stmt.execute("CREATE TABLE " +Table+ "(" + " "
          + "username varchar(200) primary key,\n"
          + "password varchar(200)\n"  
          + ")");
      } 
      }
      catch(SQLException e)
      {
          System.err.println(e.getMessage() +"-------setupDatabase");
      }
      finally{}
    }
}
