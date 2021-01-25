/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.model;

import com.jfoenix.controls.JFXButton;

/**
 *
 * @author pc
 */
public class Cart extends Pack {
    private int cartId;
    private double charge;
    private String item;
    public Cart(){}
    public Cart(String item,int cartId,String name,String category,int quantity,double charge,double orderPrice,String expiryDate, JFXButton delete){
        this.cartId = cartId;
        this.item = item;
        setName(name);
        setCategory(category);
        setQuantity(quantity);
        setOrderPrice(orderPrice);
        this.charge = charge;
        setExpiryDate(expiryDate);
        setDelete(delete);
    }
    public Cart(String name,String category,int quantity,double sellingPrice,double orderPrice,String expiryDate){
        setName(name);
        setCategory(category);
        setQuantity(quantity);
        setSellingPrice(sellingPrice);
        setOrderPrice(orderPrice);
        setExpiryDate(expiryDate);
       
    }

    public void setItem(String item) {
        this.item = item;
    }
    
    

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }
    
    public void setCharge(double charge) {
        this.charge = charge;
    }

    public double getCharge() {
        return charge;
    }

    public int getCartId() {
        return cartId;
    }

    public String getItem() {
        return item;
    }
    
    
    
}
