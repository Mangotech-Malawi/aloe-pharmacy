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
public class Verifier {
   private String expression;
   
   public Verifier(){}
   public Verifier(String expression){
       this.expression = expression;
   }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
   
    public boolean validateEmail(String expression){
       return expression.matches("^[a-zA-Z0-9_\\-\\.]+@[a-zA-z0-9\\-]+\\.[a-zA-Z0-9\\-\\.]+$");
    }
    
    public boolean validateContact(String expression){
        return expression.matches("[0-9]+") || ((expression.length()>=10) && (expression.length()<15));
    }
    public boolean checkLetters(String expression){
        return expression.matches("[a-zA-Z]+[0-9]");
    }
   
    public boolean checkNumbers(String expression){
        return expression.matches("[0-9]+");
    }
}
