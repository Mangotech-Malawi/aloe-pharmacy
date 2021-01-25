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
public class Accounts extends Transaction {
    private double income;
    private double outCome;
    private double profit;
    private double loss;

    public Accounts(double income, double outCome, double profit, double loss,String date) {
        this.income = income;
        this.outCome = outCome;
        this.profit = profit;
        this.loss = loss;
        setRegDate(date);
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public void setOutCome(double outCome) {
        this.outCome = outCome;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public void setLoss(double loss) {
        this.loss = loss;
    }

    public double getIncome() {
        return income;
    }

    public double getOutCome() {
        return outCome;
    }

    public double getProfit() {
        return profit;
    }

    public double getLoss() {
        return loss;
    }
    
    
       /*The method returns an insert query String when called*/
    public String addAccounts(){
        String query = "";//Insert query comes here
        return query;
    }
    
   /*The method returns an update query String when called*/
     public String updateAccounts(String password){
        String query = "";//update query comes here
        return query;
    }
    
      /*The method returns an delete query String when called*/
     public String deleteAccounts(String password){
        String query = "";//delete query comes here
        return query;
    }
    
    
    
        
}
