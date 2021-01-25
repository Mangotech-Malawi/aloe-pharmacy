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
 * @author pc
 */
public class Entry extends Drug {
    private int batchNo;
    private String supplierName;
    private String expiryDate;
    private int quantity;
    private JFXButton pack;
    private JFXButton btnCondition;
    private String condition;
    public Entry(){}
      public Entry(int id, int quantity, String supplierName, 
            String expiryDate,String regDate){
        setId(id);
     
        this.quantity =quantity;
        this.supplierName = supplierName;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
      
        setRegDate(regDate);
    }
      
  
    //Custructor for displaying entries on table
    public Entry(JFXCheckBox select,int batchNo,String name,String category, int quantity,String supplierName,
            String expiryDate,String regDate,JFXButton pack,
            JFXButton update, JFXButton delete,JFXButton btnCondition){
        setSelect(select);
        this.batchNo =batchNo;
        setName(name);
        setQuantity(quantity);
        setCategory(category);
        this.supplierName = supplierName;
        this.expiryDate = expiryDate;
        this.btnCondition = btnCondition;
        setRegDate(regDate);
        this.pack = pack;
        setUpdate(update);
        setDelete(delete);
        
    }
    
     public Entry(int batchNo,String name,String category, int quantity,String supplierName,
            String expiryDate,String regDate, JFXButton btnCondition){
        this.batchNo =batchNo;
        setName(name);
        setQuantity(quantity);
        setCategory(category);
        this.supplierName = supplierName;
        this.expiryDate = expiryDate;
        this.btnCondition = btnCondition;
        setRegDate(regDate);
    }
    public void setBatchNo(int batchNo) {
        this.batchNo = batchNo;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPack(JFXButton pack) {
        this.pack = pack;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setBtnCondition(JFXButton btnCondition) {
        this.btnCondition = btnCondition;
    }
    
    
    public int getBatchNo() {
        return batchNo;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public JFXButton getPack() {
        return pack;
    }

    public String getCondition() {
        return condition;
    }

    public JFXButton getBtnCondition() {
        return btnCondition;
    }
    
    
    public String newEntry(){
         String query = "INSERT INTO entries (batchNo,id,quantity,supplierName,expiryDate,regDate)" 
                + " VALUES (" + "'"+ getBatchNo()+ "'," + "'" + getId() + "',"  + "'" + getQuantity() + "',"
                + "'" + getSupplierName() + "'," + "'" + getExpiryDate()+"',"
                + "'" + getRegDate()+"');";
       return query;
       
    }
     public String updateEntry(int batchNo){
        String query = "UPDATE entries SET id = '" + getId()
                    + "',quantity = '" + getQuantity() + "',supplierName = '" + getSupplierName() 
                +  "',expiryDate = '" + getExpiryDate()
                          + "',regDate= '" + getRegDate() + "' where batchNo = '" + batchNo + "';";
        return query;
    }
    
    public String deleteEntry(int batchNo){
          String query = "INSERT INTO entries_del (batchNo,delDate)" +
                  " VALUES (" + "'"+ batchNo + "'," +"'" + LocalDateTime.now().toString() +"');";
          return query;
    }
    
    public String updateEntryQty(int quantity){
        String query = "UPDATE entries set quantity ='" + quantity + "' where batchNo ='" + getBatchNo() + "'";
        return query;
    }
     public String updateEntryQty(int quantity,int batchNo){
        System.out.println("batchNo is " + batchNo);
        String query = "UPDATE entries SET quantity =  quantity - " + quantity + " where batchNo ='" + batchNo + "'";
        return query;
    }
  
}
