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
public class Barcode extends Entry{
    private String code;
    private String isBatchNo;
    public Barcode(String code, int batchNo,String isBatchNo){
        this.code = code;
        this.isBatchNo = isBatchNo;
        setBatchNo(batchNo);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setIsBatchNo(String isBatchNo) {
        this.isBatchNo = isBatchNo;
    }
    public String getCode() {
        return code;
    }

    public String getIsBatchNo() {
        return isBatchNo;
    }
    
   
    public String insertBarcode(){
          String query = "INSERT INTO barcodes (code,batchNo,isBatchNo)" 
                + " VALUES (" + "'"+ getCode()+ "'," + "'" + getBatchNo() 
                  +  "'," + "'" + getIsBatchNo() +"');";
       return query;  
    }
}
