/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.model;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import java.time.LocalDateTime;

/**
 *
 * @author Senze
 */
public class Expense extends Entry{ //Extending Drug Entry Class just to reuse registration date & description
    private int expenseNo;
    private String category;
    private double amount;
    
    public Expense(){}
    //Costructor for adding expenses
    public Expense(double amount, String category,String description, String regDate){
        this.amount = amount;
        this.category = category;
        setDescription(description);
        setRegDate(regDate);
        
    }
    
      //Costructor for adding expenses
    public Expense(double amount, String category,String description){
        this.amount = amount;
        this.category = category;
        setDescription(description);
    }
     public Expense(JFXCheckBox select,int expenseNo,double amount, String category,String description, String regDate,
             JFXButton descBtn, JFXButton update, JFXButton delete){
        setSelect(select);
        this.expenseNo = expenseNo;
        this.amount = amount;
        this.category = category;
        setDescription(description);
        setRegDate(regDate);
        setDescBtn(descBtn);
        setUpdate(update);
        setDelete(delete);
    }
     public Expense(int expenseNo,double amount, String category,String description, String regDate,
             JFXButton descBtn){
    
        this.expenseNo = expenseNo;
        this.amount = amount;
        this.category = category;
        setDescription(description);
        setRegDate(regDate);
        setDescBtn(descBtn);
    }

    public void setExpenseNo(int expenseNo) {
        this.expenseNo = expenseNo;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getExpenseNo() {
        return expenseNo;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
    public String addExpense(){
          String query = "INSERT INTO expenses (amount,category,description,regDate)" +
                  " VALUES (" + "'" + getAmount()+ "','" + getCategory() 
                  + "','" + getDescription() 
                  + "','" + getRegDate() +"');";
       return query;
    }
    public String delExpense(int expenseNo){
            String query = "INSERT INTO expenses_del (expenseNo,delDate)" +
                  " VALUES (" + "'"+ expenseNo + "'," +"'" + LocalDateTime.now().toString() +"');";
          return query;
    }
    public String updateExpenseIsDel(int expenseNo){
        String query = "UPDATE expenses SET isDeleted ='1' WHERE expenseNo ='" + expenseNo + "';";
        return query;
    }
    public String updateExpese(int expenseNo){
        String query = "UPDATE expenses SET amount ='" + getAmount() + "', category ='" 
                + getCategory() + "', description='" + getDescription() + "' WHERE expenseNo ='" + expenseNo + "';";
        return query;
    }
}

