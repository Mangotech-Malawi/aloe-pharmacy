/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.model;

/**
 *
 * @author pc
 */
public class Sale extends Transaction {
    private int saleNo;
    private int quantity;
    private double order_price;
    public Sale(){}
    public Sale(int transNo,int id,int quantity,double charge, double orderPrice,String item){
        setTransNo(transNo);
        setId(id);
        this.quantity = quantity;
        setCharge(charge);
        this.order_price = orderPrice;
        setItem(item);
    }
     public Sale(int saleNo,String item,String name,int quantity,double charge, String date){
        setSaleNo(saleNo);
        setItem(item);
        setName(name);
        setTransaction_date(date);
        this.quantity = quantity;
        setCharge(charge);
    }
     public Sale(int saleNo,String item,String username,String name,int quantity,double charge, String date){
        setSaleNo(saleNo);
        setItem(item);
        setName(name);
        setUsername(username);
        setTransaction_date(date);
        this.quantity = quantity;
        setCharge(charge);
    }


    public void setSaleNo(int saleNo) {
        this.saleNo = saleNo;
    }

    public void setOrder_price(double order_price) {
        this.order_price = order_price;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    public double getOrder_price() {
        return order_price;
    }
    
    public int getSaleNo() {
        return saleNo;
    }
    public String addSale(){
          String query = "INSERT INTO sales (transNo,id,quantity,charge,orderPrice,item)" +
                  " VALUES (" + "'" + getTransNo()+ "','" + getId() 
                  + "','" + getQuantity() + "','" + getCharge() + "','" + getOrder_price() 
                  + "','" + getItem() +"');";
       return query;
    }
    
}
