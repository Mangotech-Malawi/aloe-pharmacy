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
public class Customer extends Person{
    private String regDate;
   
    
   public Customer(){}
   public Customer(String customerId, String fullname, String residence,String contact,String regDate){
       setFullname(fullname);
       setResidence(residence);
       setContact(contact);
       this.regDate = regDate;
   }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getRegDate() {
        return regDate;
    }
    
    public String insertCustomerQuery(){
         
        String query = "INSERT INTO customers (customerId,fullname,residence,contact, regDate)" +
                  " VALUES (" + "'"+ getId()+ "'," + "'" + getFullname() + 
                  "'," + "'" + getResidence() + "', "+"'" +getContact() +"'," +"'" +getRegDate() + "');";
       return query;
    }
    
}
