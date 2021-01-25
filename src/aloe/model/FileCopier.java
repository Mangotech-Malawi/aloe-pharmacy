/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Senzenjani
 */

public class FileCopier {

 private String image_path;
 private String image_folder;
 private String filename;
 public FileCopier(String filename,String foldername,String oldPath){
     image_path = oldPath;
     System.out.println(image_path);
      this.image_folder = foldername;
      this.filename = filename;
 }

public void copyFile(){
    String extension = FilenameUtils.getExtension(image_path);
  
String new_image_path = System.getProperty("user.home") + "\\Documents\\Aloe\\User Profile\\" + filename + "." + extension;
   
 File sourceFile = new File(image_path);
         
 File destFile = new File(new_image_path);
    if (!sourceFile.exists()) {
   
    System.out.println("Source File Not Found!");
                  
      }

                    
/* if file not exist then create one */
       
    if (!destFile.exists()) {
  
                      try {
          
                      destFile.createNewFile();
    
                            System.out.println("Destination file doesn't exist. Creating one!");
        
                } catch (IOException e) {
                               
                       e.printStackTrace();
                       
               }
                  
  }

                   
 FileChannel source = null;
                   
 FileChannel destination = null;

                  
  try {

                      
  /**
                         * getChannel() returns unique FileChannel object associated a file
                         * output stream.
                         */
  
             source = new FileInputStream(sourceFile).getChannel();

    
                    destination = new FileOutputStream(destFile).getChannel();

             
           if (destination != null && source != null) {
           
                 destination.transferFrom(source, 0, source.size());
            
            }

                    } catch (FileNotFoundException e) {
                
                                  e.printStackTrace();
                  
          } catch (IOException e) {
                       
                e.printStackTrace();
                   
                }

                    
         
                    finally {
                        
                            
                              if (source != null) {
                                
                                     try {
                                        
                                        source.close();
                               
                                         } catch (IOException e) {
                                       
                                              e.printStackTrace();
                               
                                                 }
                       
                                            
                                            }
                       
                                      if (destination != null) {
  
                                              try {
                            
                                         destination.close();
                                
                                } catch (IOException e) {
                                    
                                        e.printStackTrace();
                               
                                   }
                        
                           }
                    }
}
 

    
   
}
