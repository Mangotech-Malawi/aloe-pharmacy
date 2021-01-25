/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.model;

import java.time.LocalDate;

/**
 *
 * @author Senze
 */
public class EXSettings extends User{
    private int excellent;
    private int better;
    private int good;
    private int bad;
    private int worse;
    
    public EXSettings(int excellent,int better,int good,
            int bad, int worse, String username, String regDate ){
       this.excellent = excellent;
       this.better = better;
       this.good = good;
       this.bad = bad;
       this.worse = worse;
       setUsername(username);
       setRegDate(regDate);
    }
    public EXSettings(){
      
    }

    public void setExcellent(int excellent) {
        this.excellent = excellent;
    }

    public void setBetter(int better) {
        this.better = better;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public void setBad(int bad) {
        this.bad = bad;
    }

    public void setWorse(int worse) {
        this.worse = worse;
    }

    public int getExcellent() {
        return excellent;
    }

    public int getBetter() {
        return better;
    }

    public int getGood() {
        return good;
    }

    public int getBad() {
        return bad;
    }

    public int getWorse() {
        return worse;
    }
    public String insertSettings(){
        //Fixed primary key is 1
          String query = "INSERT INTO expirySettings (setNo,excellent,better,good,bad,worse,regDate,username)" 
                + " VALUES (" + "'"+ 1 + "'," + "'" + getExcellent() + "',"  + "'" + 
                  getBetter() + "',"  + "'" + getGood() + "',"  + "'" + getBad() +
                  "',"  + "'" + getWorse() +    "',"  + "'" + getRegDate() + "'," 
                  + "'" +getUsername() +"');";
       return query;
    }
    public String updateSettings(){
          String query = "UPDATE expirySettings SET  excellent = '" + getExcellent() + "',better = '" + getBetter()
                    + "',good = '" + getGood()  + "',bad = '" + getBad()  + "',worse= '" + getWorse() 
              + "',regDate= '" + getRegDate() + "',username='" + getUsername()  + "' where setNo = '" + 1 + "';";
        return query;
    }
}
