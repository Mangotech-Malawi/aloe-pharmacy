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
 * @author Senzenjani
 */
public class Drug extends User {
    private int id;
    private String name;
    private String category;
    protected double orderPrice;
    private double sellingPrice;
    private String description;
    private String secCategory;
    private String unitOfMeasurement;
    private JFXButton descBtn;
    private JFXButton entry;
    private JFXButton thresHoldBtn;
  
    public Drug(){}
    public Drug(String name, String category,String secCategory,
            double sellingPrice,double orderPrice,String unitOfMeasurement,String description) {
        this.secCategory = secCategory;
        this.name = name;
        this.category = category;
        this.sellingPrice = sellingPrice;
        this.orderPrice = orderPrice;
        this.description = description;
        this.unitOfMeasurement = unitOfMeasurement;
    }

    //Custructor for displaying drug details on a table
      public Drug(JFXCheckBox select,int id, String name, String category, double sellingPrice,
              double orderPrice,String unitOfMeasurement,String description, JFXButton thresholdBtn,
              JFXButton descBtn,JFXButton entry,JFXButton update, JFXButton delete) {
        setSelect(select);
        this.id = id;
        this.name = name;
        this.category = category;
        this.sellingPrice = sellingPrice;
        this.orderPrice = orderPrice;
        this.description = description;
        this.unitOfMeasurement = unitOfMeasurement;
        this.descBtn = descBtn;
        this.thresHoldBtn = thresholdBtn;
        setUpdate(update);
        setDelete(delete);
        this.entry = entry;
    }
       public Drug(int id, String name, String category,String secCategory, double sellingPrice,
              double orderPrice,String unitOfMeasurement,String description,
              JFXButton descBtn) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.secCategory = secCategory;
            this.sellingPrice = sellingPrice;
            this.orderPrice = orderPrice;
            this.unitOfMeasurement = unitOfMeasurement;
            this.description = description;
            this.descBtn = descBtn;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
    

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setSecCategory(String secCategory) {
        this.secCategory = secCategory;
    }
    

    public void setDescBtn(JFXButton descBtn) {
        this.descBtn = descBtn;
    }
    public void setEntry(JFXButton entry) {
        this.entry = entry;
    }
    
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }
    public double getOrderPrice() {
        return orderPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }
    public String getDescription() {
        return description;
    }

    public String getSecCategory() {
        return secCategory;
    }

    @Override
    public int getId() {
        return id;
    }

    public JFXButton getThresHoldBtn() {
        return thresHoldBtn;
    }

    public void setThresHoldBtn(JFXButton thresHoldBtn) {
        this.thresHoldBtn = thresHoldBtn;
    }
    
   

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }
    

    public JFXButton getDescBtn() {
        return descBtn;
    }

    public JFXButton getEntry() {
        return entry;
    }
    
    
   
   /*The method returns an insert query String when called*/
    public String addDrug(){
           
        String query = "INSERT INTO drugs (name,firstCategory,secCategory,sellingPrice,orderPrice,measurement,description)" 
                + " VALUES (" + "'" + getName() + "',"
                + "'" + getCategory() + "'," + "'" + getSecCategory() + "',"  + "'" + getSellingPrice() + "',"
                + "'" + getOrderPrice()+  "'," +"'" + getUnitOfMeasurement() + "',"  + "'" +getDescription() +"');";
       return query;
       
    }
  
   /*The method returns an update query String when called*/
     public String updateDrug(int id){
        String query = "UPDATE drugs SET name = '" + getName()
                    + "',firstCategory = '" + getCategory() + "',secCategory = '" + getSecCategory() 
                +  "',sellingPrice = '" + getSellingPrice() 
                +  "',orderPrice = '" + getOrderPrice() + "', measurement ='" + getUnitOfMeasurement() 
                          + "',description= '" + getDescription() + "' where id = '" + id + "';";
        return query;
    }
     
      /*The method returns an delete query String when called*/
    public String deleteDrug(int id){
        String query = "INSERT INTO drugs_del (id,delDate)" +
                  " VALUES (" + "'"+ id + "'," +"'" + LocalDateTime.now().toString() +"');";
          return query;
    }
    
}
