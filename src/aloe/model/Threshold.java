/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.model;

/**
 *
 * @author Senze
 */
public class Threshold extends Drug {
    private int entryQty;
    private int numOfPacks;
    public Threshold(){
       
    }
    public Threshold(int id){
        setId(id);
    } 
    public int getEntryQty() {
        return entryQty;
    }

    public void setEntryQty(int entryQty) {
        this.entryQty = entryQty;
    }

    public int getNumOfPacks() {
        return numOfPacks;
    }

    public void setNumOfPacks(int numOfPacks) {
        this.numOfPacks = numOfPacks;
    }
    
    public String insertEntryThreshold(){
        String query = "INSERT INTO entriesThresholds (id,entryQty) VALUES('" + getId() 
                + "','" + getEntryQty() + "');";
        return query;
    }
    public String updateEntryThresHold(int id){
        String query = "UPDATE entriesThresholds SET entryQty='" + 
                getEntryQty() + "' WHERE id='" + id + "';";
        return query;
    }
    
     public String insertPackThreshold(){
        String query = "INSERT INTO packsThresholds (id,numOfPacks) VALUES('" + getId() 
                + "','" + getNumOfPacks() + "');";
        return query;
    }
    public String updatePackThresHold(int id){
        String query = "UPDATE packsThresholds SET numOfPacks ='" +
                getNumOfPacks() + "' WHERE id='" + id + "';";
        return query;
    }
    
}
