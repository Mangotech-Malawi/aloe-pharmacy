/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.model;

/**
 *
 * @author Senzenjani
 */
public class Transaction extends Cart{
    private int transNo;
    private String transaction_date;
    private String time;
    private int numOfSales;
    public Transaction(){}
    public Transaction(String username, String date) {
        this.transaction_date = date;
        this.username = username;
    }
    public Transaction(int transNo,String date,String time, int numOfSales) {
        this.transaction_date = date;
        this.transNo = transNo;
        this.time = time;
        this.numOfSales = numOfSales;
    }
     public Transaction(int transNo,String username,String date,String time, int numOfSales) {
        this.transaction_date = date;
        setUsername(username);
        this.transNo = transNo;
        this.time = time;
        this.numOfSales = numOfSales;
    }
    public void setTransNo(int transNo) {
        this.transNo = transNo;
    }
    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setNumOfSales(int numOfSales) {
        this.numOfSales = numOfSales;
    }
    
    public int getTransNo() {
        return transNo;
    }
    public String getTransaction_date() {
        return transaction_date;
    }

    public String getTime() {
        return time;
    }

    public int getNumOfSales() {
        return numOfSales;
    }
    

  

    //@Override
    public int getQuantity() {
       return 0;
    }

    @Override
    public String getUsername() {
        return username;
    }

   
    /*The method returns an insert query String when called*/
    public String addTransaction(){
          String query = "INSERT INTO transactions (trans_by,trans_date)" +
                  " VALUES (" + "'" + getUsername() + "','" + getTransaction_date() +"');";
       return query;
    }
    
    public String updateTransaction(String newUsername,String oldUsername){
        String query = "UPDATE transactions set trans_by = '" + 
                newUsername + "' where  trans_by ='" + oldUsername +"';";
        return query;
    }
}
