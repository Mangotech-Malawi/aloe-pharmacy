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
public class User extends Person {
  protected String username;
    private String password;
    private String password_hint;
    protected String regDate;
    private String privilege;
    private boolean isDeleted;
    public User(){}
 
    public User(String username, String password, 
    String email, String firstname,String lastname,String gender, String dob,String contact,String privilege,String regDate) {
        this.username = username;
        this.password = password;
        this.regDate = regDate;
        setGender(gender);
        setDob(dob);
        setPrivilege(privilege);
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contact = contact;
    }
    
     public User(JFXCheckBox select,String username, 
    String email, String firstname,String lastname,String gender, String dob,String contact,
    String privilege,String regDate,JFXButton update,JFXButton delete) {
        setSelect(select);
        this.username = username;
        this.regDate = regDate;
        setGender(gender);
        setDob(dob);
        setPrivilege(privilege);
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contact = contact;
        setUpdate(update);
        setDelete(delete);
    }
    public User (String firstname, String lastname,String emailAddress,
            String gender,String dob,String pnum,String privilege){
         setGender(gender);
        setDob(dob);
        this.email = emailAddress;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contact = pnum;
        this.privilege = privilege;
    }
    
    public User(String firstname,String lastname,String email,
            String gender,String dob,String pnum){
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        setGender(gender);
        setDob(dob);
        this.contact = pnum;
    }
     public User(String username, String password,String firstname,String lastname,String email,
            String gender,String dob,String pnum){
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        setGender(gender);
        setDob(dob);
        this.contact = pnum;
    }
    public User (String password){
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }
    
    public void setPassword_hint(String password_hint) {
        this.password_hint = password_hint;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPassword_hint() {
        return password_hint;
    }

    public String getRegDate() {
        return regDate;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }
    

   /*This method returns a query for adding a user */
    public String addUser(){
        String query = "INSERT INTO users (username,password, email,firstname,lastname,gender,dob,contact,privilege,regDate)" +
                  " VALUES (" + "'"+ getUsername()+ "'," + "'" + getPassword() + 
                  "'," + "'" + getEmail() + "', "+"'" +getFirstname() +"'," +"'" +getLastname() + 
                "'," +"'" +getGender()+ "'," +"'" +getDob()+ 
                "'," +"'" +getContact()+ "',"  +"'" +getPrivilege()+ "',"  +"'" +getRegDate() +"');";
       return query;
    }
    
   /*The method returns an update query String when called*/
     public String updateUser(String username){
      String query = "UPDATE users SET  firstname = '" + getFirstname() +  "',lastname = '" + getLastname() +
              "',contact = '" + getContact()  + "',email = '" + getEmail()  +"', dob ='" + getDob() 
              + "', gender ='" + getGender()+  "', privilege ='" + getPrivilege()+
              "' where username = '" + username + "';";
        return query;
    }
     public String updateSelfUser(String username,String password){
        String query = "UPDATE users SET  firstname = '" + getFirstname() +  "',lastname = '" + getLastname() +
              "',contact = '" + getContact()  + "',email = '" + getEmail()  +"', dob ='" + getDob() 
              + "', gender ='" + getGender() + "', username ='" + getUsername()  +
              "' where username = '" + username + "' and password ='" + password + "'";
        return query;
    }
       public String updateSelfUserPassword(String username,String password){
        String query = "UPDATE users SET  firstname = '" + getFirstname() +  "',lastname = '" + getLastname() +
              "',contact = '" + getContact()  + "',email = '" + getEmail()  +"', dob ='" + getDob() 
              + "', gender ='" + getGender() + "', username ='" + getUsername()  +  "',password ='" + getPassword() +
              "' where username = '" + username + "' and password ='" + password + "'";
        return query;
    }
    public String updatePrivilege(String privilege){
        String query = "UPDATE users SET privilege = '" + privilege + "' where username = '" + getUsername() + "';";
        return query;
    }
    public String UpdateSpecificUser(String username){
        String query = "UPDATE users SET  username = '" + getUsername() + "',password = '" + getPassword() + 
              "',regDate = '" + getRegDate()
                          + "',firstname = '" + getFirstname() +  "',lastname = '" + getLastname() +
              "',contact = '" + getContact()  + "',email = '" + getEmail()  + 
              "' where username = '" +  username + "';";
        return query;
    }
    
     public String updateUsernameAndPassword(String password,String username){
         String query = " UPDATE users SET username = '" + username + "', password = "
                 + "'" + password + "' where password = '" + getPassword() + "';"; 
         return query;
        }
    
    public String updateUsername(String password,String username){
        String query = "UPDATE users  SET username =  '" + getUsername() + "' where "
                + "password = '" + password + "' and  username = '" + username + "';"; 
        return query;
      }
    
    public String updatePassword(String password, String username){
        String query = "UPDATE users SET password  = '" + getPassword() + "' "
                + "where password = '" + password + "' and username = '" + username + "';";
        return query;
     }
    
    public String updateHint(String password, String username){
          String query = "UPDATE users SET  password_hint  = '" + getPassword_hint() + "' "
                + "where password = '" + password + "' and username = '" + username + "';";
        return query;
    }
    
    //The query update firstname, lastname, contac and email
    public String updateOthers(String username, String password){
        String query =  "UPDATE users SET  firstname  = '" + getFirstname() + "', lastname ='" +
                getLastname() + "', email = '" + getEmail() + "', contact='" + getContact() + "'"
                + "where password = '" + password + "' and username = '" + username + "';";
        return query;
    }
    
      /*The method returns an delete query String when called*/
     public String deleteUser(String username){
        String query = "INSERT INTO users_del (username,delDate)" +
                  " VALUES (" + "'"+ username + "'," +"'" + LocalDateTime.now().toString() +"');";
        return query;
    }
    public String restoreUser(String username){
        String query = "DELETE FROM users_del where username='" + username + "';";
        return query;
    }
     
     
    
    
}
