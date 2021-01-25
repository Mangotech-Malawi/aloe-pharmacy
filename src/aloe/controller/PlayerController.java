/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class PlayerController implements Initializable {
    public static ObservableList<String> videoList = FXCollections.observableArrayList();  
    @FXML
    private JFXSlider movielength;
    @FXML
    private JFXButton file;
    @FXML
    private JFXSlider Volume;
    @FXML
    private MediaView mediaview;
    public static String filepath;
    public static MediaPlayer mediaplayer;
    public static int index =1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
  
        playVideo();
    }    
    private void playVideo(){
        
        File file = new File(System.getProperty("\\aloe\\resources\\videos\\manageUsers\\Add User.mp4") );
        filepath = file.toURI().toString();
        
        if(filepath!= null)
        {
            Media media = new Media(filepath);
           mediaplayer = new MediaPlayer(media);
           mediaview.setMediaPlayer(mediaplayer);
           mediaplayer.play();
           // DoubleProperty width = mediaview.fitWidthProperty();
            //DoubleProperty height = mediaview.fitHeightProperty();
            
           // width.bind(Bindings.selectDouble(mediaview.sceneProperty(), "width"));
           // height.bind(Bindings.selectDouble(mediaview.sceneProperty(), "height"));
            
        }
        
    }

     @FXML
    private void OpenFileFunction(ActionEvent event) 
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Choose a FILE (*.Mp4)", "*.Mp4", "*.Mp3", "*.Mvc");
        fileChooser.getExtensionFilters().add(filter);
        File file = new File(videoList.get(0));
        filepath = file.toURI().toString();
        
        if(filepath!= null)
        {
            Media media = new Media(filepath);
           mediaplayer = new MediaPlayer(media);
           mediaview.setMediaPlayer(mediaplayer);
           mediaplayer.play();
           // DoubleProperty width = mediaview.fitWidthProperty();
            //DoubleProperty height = mediaview.fitHeightProperty();
            
           // width.bind(Bindings.selectDouble(mediaview.sceneProperty(), "width"));
           // height.bind(Bindings.selectDouble(mediaview.sceneProperty(), "height"));
            
        }
        
    }

    @FXML
    private void PlayFunction(ActionEvent event) 
    {
        try{
           mediaplayer.play();
        }catch(NullPointerException ex){
            
        }
    }

    @FXML
    private void PauseFunction(ActionEvent event) 
    {
         try{
             mediaplayer.pause();
        }catch(NullPointerException ex){
            
        }
    }

    @FXML
    private void BackwardFunction(ActionEvent event) 
    {
       
    }

    @FXML
    private void StopFunction(ActionEvent event) 
    {
        
       
    }
    public static void stop(){
        try{
              mediaplayer.stop();
        }catch(NullPointerException ex){
            
        }
    }
    @FXML
    private void FastForwardFunction(ActionEvent event) 
    {
        
    }
    
}
