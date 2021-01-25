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
public class Report extends Transaction{
    private String name;
    private String reportNo;
    private String content;

    public Report(){}
    public Report(String name, String reportNo, String content,String date) {
        this.name = name;
        this.reportNo = reportNo;
        this.content = content;
        setRegDate(date);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getReportNo() {
        return reportNo;
    }

    public String getContent() {
        return content;
    }
    
       /*The method returns an insert query String when called*/
    public String addReport(){
         String query = "INSERT INTO reports (name,reportNo,content,date"
                + ") VALUES (" + "'"+ getName()+ "'," + "'" + getReportNo() + 
                  "'," +"'" +getContent()  +  "'," +"'" +getRegDate() +"');";
       return query;
    }
    
   /*The method returns an update query String when called*/
     public String updateReport(String reportNo){
          String query = "UPDATE reports SET  name = '" + getName() + "',reportNo = '" + getReportNo() + 
              "',content = '" + getContent() +   "',content = '" + getRegDate() + 
              "' where regDate = '" + reportNo+ "';";
        return query;
    }
    
      /*The method returns an delete query String when called*/
     public String deleteReport(String id){
        String query = "DELETE from reports where id =" + id;//delete query comes here
        return query;
    }
    
    
    
    
}
