/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller;

import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import aloe.model.ControlledScreen;


/**
 *
 * @author Senzenjani
 */
public class ScreensController extends StackPane {
    //Holds the scrreens to be displayed
    
    private HashMap<String, Node> screens = new HashMap<>();
    
    public ScreensController(){
        super();
    }
    
    //Add the screen to the collection
    public void addScreen(String name, Node screen){
        screens.put(name,screen);
    }
    
    //Returns the nodw with the appropriate name
    public Node getScreen(String name){
        return screens.get(name);
    }
    
    //Loads the fxml, add the screen  to the screens collection and
    //finally injects the screenPane to the controller
    
    public boolean loadsScreen(String name, String resource){
        try{
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent)myLoader.load();
            ControlledScreen  myScreenControler = ((ControlledScreen)myLoader.getController());
            myScreenControler.setScreenParent(this);
            addScreen(name,loadScreen);
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
   
   
   public boolean setScreen(final String name){
       if(screens.get(name)!=null){
           final DoubleProperty opacity = opacityProperty();
           
           if(!getChildren().isEmpty()){
               Timeline fade = new Timeline(
               new KeyFrame(Duration.ZERO, new KeyValue(opacity,1.0)),
               new KeyFrame(new Duration(1000), new EventHandler<ActionEvent>()
               {
                  public void  handle(ActionEvent t){
                      getChildren().remove(0);
                      getChildren().add(0,screens.get(name));
                      Timeline  fadeIn = new Timeline(
                              new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                              new KeyFrame (new Duration(400), new KeyValue(opacity,1.0)));
                              fadeIn.play();
                              } 
               }, new KeyValue(opacity,0.0))); 
               fade.play();
           }else{
               setOpacity(0.0);
               getChildren().add(screens.get(name)); // no else been displayed, then just show
               Timeline fadeIn = new Timeline(
               new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
               new KeyFrame(new Duration(2500), new KeyValue(opacity, 1.0)));
               fadeIn.play();
           }
           return true;
       }else{
           System.out.println("Screen has not been loaded!! \n");
           return false;
       }
   }
   public boolean unloadScreen(String name){
       if(screens.remove(name) == null){
           System.out.println("Screen didn't exist");
           return false;
       }else{
           return true;
       }
   }
}
