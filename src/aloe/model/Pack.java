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
public class Pack extends Entry {
    private int packId;
    private int numOfPacks;
    public Pack(){}
    public Pack(int batchNo,int numOfPacks,int quantity, double price,String regDate){
        this.packId = packId;
        this.numOfPacks = numOfPacks;
        setBatchNo(batchNo);
        setRegDate(regDate);
        setQuantity(quantity);
        setSellingPrice(price);
    }
    //Cusrtructor for displaying packs on tables
    public Pack(JFXCheckBox select,int packId, String name,String category,int numOfPacks,int quantity,
            double price,String expiryDate, JFXButton update, JFXButton delete){
        setSelect(select);
        this.packId = packId;
        this.numOfPacks = numOfPacks;
        setName(name);
        setCategory(category);
        setQuantity(quantity);
        setSellingPrice(price);
        setExpiryDate(expiryDate);
        setUpdate(update);
        setDelete(delete);
    }
    
     public Pack(int packId, String name,String category,int numOfPacks,int quantity,
            double price,String expiryDate){
        this.packId = packId;
        this.numOfPacks = numOfPacks;
        setName(name);
        setCategory(category);
        setQuantity(quantity);
        setSellingPrice(price);
        setExpiryDate(expiryDate);
    }

    public void setPackId(int packId) {
        this.packId = packId;
    }

    public void setNumOfPacks(int numOfPacks) {
        this.numOfPacks = numOfPacks;
    }
    

    public int getPackId() {
        return packId;
    }

    public int getNumOfPacks() {
        return numOfPacks;
    }
    
    public String addPack(){
          String query = "INSERT INTO packs (batchNo,numOfPacks,quantity,regDate,price)" 
                + " VALUES (" + "'" + getBatchNo() + "',"  + "'" + 
                  getNumOfPacks() + "',"  + "'" + getQuantity() + 
                  "',"  + "'" + getRegDate() + "'," + "'" +getSellingPrice() +"');";
       return query;
    }
    public String updatePack(int packId){
        String query = "UPDATE packs SET   batchNo = '" + getBatchNo()
                    + "',numOfPacks = '" + getNumOfPacks()  + "',quantity = '" + getQuantity()  + "',price= '" + getSellingPrice() 
                + "' where packId = '" + packId + "';";
        return query;
    }
    public String deletePack(int packId){
       String query = "INSERT INTO packs_del (packId,delDate)" +
                  " VALUES (" + "'"+ packId + "'," +"'" + LocalDateTime.now().toString() +"');";
          return query;
    }
    public String updateNumOfPacks(int numOfPacks,int packId){
        String query = "UPDATE packs set numOfPacks = numOfPacks - " + numOfPacks + " where packId ='" + packId + "'";
        return query;
    }
}
