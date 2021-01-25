/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;

import static aloe.controller.pharmacist.stock.ViewDrugsController.drugList;
import static aloe.controller.pharmacist.stock.ViewDrugsController.index;
import aloe.model.Drug;
import aloe.model.PopWindow;
import aloe.model.QueryManager;
import aloe.model.Verifier;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class UpdateController implements Initializable {
      ObservableList<String> categoryOne = FXCollections.observableArrayList();
     ObservableList<String> categoryTwo = FXCollections.observableArrayList();
    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXTextField txtId;
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextArea txtDescription;
    private JFXComboBox<String> categoryCombo;
    @FXML
    private JFXTextField txtSellingPrice;
    @FXML
    private JFXTextField txtOrderPrice;
    @FXML
    private JFXComboBox<String> categoryOneCombo;
    @FXML
    private JFXComboBox<String> categoryTwoCombo;
    @FXML
    private JFXTextField txtUnitOfMeasurement;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadComboData();
        loadLabelData();
    }  
    private void loadLabelData(){
          lblStatus.setText(drugList.get(index).getName());
         txtId.setText(drugList.get(index).getId() + "");
         txtName.setText(drugList.get(index).getName());
         categoryOneCombo.setValue(drugList.get(index).getCategory());
         categoryTwoCombo.setValue(getSecondCategory(drugList.get(index).getId() + ""));
         txtDescription.setText(drugList.get(index).getDescription());
         txtSellingPrice.setText(""+drugList.get(index).getSellingPrice());
         txtOrderPrice.setText(""+drugList.get(index).getOrderPrice());
         txtUnitOfMeasurement.setText(drugList.get(index).getUnitOfMeasurement() + "");
    }
    private String getSecondCategory(String drugId){
        QueryManager Query = new QueryManager();
        String query = "SELECT secCategory FROM drugs where id = '" + drugId + "'";
        ResultSet rs = Query.getDataQuery(query);
          try {
              if(rs.next()){
                  return rs.getString("secCategory");
              } 
          } catch (SQLException ex) {
              Logger.getLogger(UpdateController.class.getName()).log(Level.SEVERE, null, ex);
          }
          return null;
    }
     private void loadComboData(){
         categoryOne.addAll("Cardiovascular Drugs","Respiratory Drugs",
               "Gastrointestinal Drugs","Renal Drugs","Neurologic Drugs","Psychiatric Drugs",
               "Endocrinology Drugs","Urologic Drugs","Rheumatologic Drugs",
       "Ophthalmic and Otolaryngological Drugs","Dermatologic Drugs","Infectious Disease Drugs");
       categoryOneCombo.getItems().setAll(categoryOne);
    }

    @FXML
    private void btnMnimizeAction(ActionEvent event) {
        PopWindow.childStage.setIconified(true);
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
           stackPane.getScene().getWindow().hide();
    }

    @FXML
    private void btnSaveAction(ActionEvent event) {
        if(isValid()){//If all fields are filled execution of statements continues
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String description = txtDescription.getText().trim();
            String firstCategory  = categoryOneCombo.getValue();
            String secCategory = categoryTwoCombo.getValue();
            String measurement = txtUnitOfMeasurement.getText().trim();
            try{
                Double sellingPrice = Double.parseDouble(txtSellingPrice.getText().trim());
                Double orderPrice = Double.parseDouble(txtOrderPrice.getText().trim());
                Verifier  verify = new Verifier();
                 //Validating information entered
         
                     Drug drug = new Drug(name,firstCategory,secCategory,sellingPrice,orderPrice,measurement,description);
                    QueryManager Query = new QueryManager();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("Confirm Update ");
                    alert.setContentText("Drug Updated succesfully");
                    alert.showAndWait();
                    if(alert.getResult().getText()!=null && alert.getResult().getText().equals("OK")){
                        if(Query.execAction(drug.updateDrug(drugList.get(index).getId()))){
                            stackPane.getScene().getWindow().hide();
                        }else{
                           Alert error = new Alert(Alert.AlertType.ERROR);
                           error.setHeaderText("Error");
                           error.setContentText("Failed to update drug");
                           error.showAndWait();
                        }      
                    }
            }catch(NumberFormatException ex){
                 Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Entry Error");
                alert.setContentText("Please enter correct details");
                alert.showAndWait();
            }
            
        }else{//Shows error message if all fields are not filled
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Entry Error");
            alert.setContentText("Please enter all details");
            alert.showAndWait();
        }
    }
    private boolean isValid(){ //Verifies if the user has entered all details
       if(txtId.getText().trim().isEmpty() || txtName.getText().trim().isEmpty() ||
                txtDescription.getText().trim().isEmpty() || txtUnitOfMeasurement.getText().isEmpty()
                        || txtSellingPrice.getText().trim().isEmpty()
                        || txtOrderPrice.getText().trim().isEmpty()
                        || categoryOneCombo.getValue().isEmpty() || categoryTwoCombo.getValue().isEmpty()){
          return false; // return false if one/more fields are not filled
       }else{
          return true; // return true if all fields are filled
      }
    }


    @FXML
    private void btnCancelAction(ActionEvent event) {
          stackPane.getScene().getWindow().hide();
    }

    @FXML
    private void OnSelected(ActionEvent event) {
          categoryTwoCombo.setValue(null);
        categoryTwoCombo.setPromptText("Second Category");
       categoryTwoCombo.setDisable(false);
       categoryTwo.clear();
       categoryTwoCombo.getItems().setAll(categoryTwo);
        
        String category = categoryOneCombo.getValue();
        System.out.println("Category : " + category);
        try{
        switch(category){
            case "Cardiovascular Drugs" : loadCat1();break;
            case "Respiratory Drugs" : loadCat2();break;
            case "Gastrointestinal Drugs" : loadCat3();break;
            case "Renal Drugs" : loadCat4();break;
            case "Neurologic Drugs" : loadCat5();break;
            case "Psychiatric Drugs" : loadCat6();break;
            case "Endocrinology Drugs" : loadCat7();break;
            case "Urologic Drugs" : loadCat8();break;
            case "Rheumatologic Drugs" : loadCat9();break;
            case "Ophthalmic and Otolaryngological Drugs" : loadCat10();break;
            case "Dermatologic Drugs" :loadCat11();break;
            case "Infectious Disease Drugs" :loadCat12();break;
          
        } 
        }catch(NullPointerException ex){}
    }
     private void loadCat1(){
        categoryTwo.addAll("Renin–Angiotensin– Aldosterone System (RAAS)",
                "Beta Blockers","Calcium Channel Blockers","Diuretics",
                "Statins","Antiplatelets","Traditional anticoagulants",
                "Novel Oral Anticoagulants (NOACs)","Antianginals","Other Cardiovascular Agents");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     private void loadCat2(){
        categoryTwo.addAll("Inhaled Corticosteroids and Long-Acting Beta Agonists"
                ,"Short-Acting Beta Agonists and Anticholinergics",
                "Other Respiratory Agents");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     private void loadCat3(){
          categoryTwo.addAll("Proton Pump Inhibitors","Histamine (H2) Receptor Blockers",
                "Diarrhea and Ulcerative Colitis","Laxatives","Nausea");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     private void loadCat4(){
         categoryTwo.addAll("Renal Medications"
                ,"Erythropoiesis-Stimulating Agents (ESAs)");
          
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     private void loadCat5(){
         categoryTwo.addAll("Headache"
                ,"Epilepsy","Opioid Analgesics",
                "Non-Steroidal Antiinflammatory Drugs (NSAIDs)",
                "Muscle relaxants","Adjunct Medications for Pain",
                "Alzheimer's Disease","Smoking Cessation");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     private void loadCat6(){
           categoryTwo.addAll("Atypical Antipsychotics"
                ,"Benzodiazepines","Selective Serotonin Reuptake Inhibitors (SSRIs)",
                " Serotonin-Norepinephrine Reuptake Inhibitors (SNRIs)",
                " Non-Benzodiazepine Hypnotics",
                " Other Antidepressants and Anti-Anxiety Agents",
                " Stimulants and Attention Deficit Hyperactivity Disorder (ADHD) Agents");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     private void loadCat7(){
          categoryTwo.addAll("Oral diabetic agents – I","Oral diabetic agents – II",
                  " Contraceptives","Corticosteroids","Injectable diabetic agents",
                  "Hormonal Therapy");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     private void loadCat8(){
          categoryTwo.addAll(" Benign Prostatic Hypertrophy – Alpha Blockers","Benign Prostatic Hypertrophy– 5-Alpha Reductase Inhibitors",
                  "Erectile Dysfunction","Overactive Bladder");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     private void loadCat9(){
             categoryTwo.addAll("Osteoporosis","Rheumatoid Arthritis",
                  "Gout");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     private void loadCat10(){
          categoryTwo.addAll("Allergies","Nasal Corticosteroids",
                  "Cough and Cold","Glaucoma","Other Ophthalmic Agents");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     
     private void loadCat11(){
             categoryTwo.addAll("Acne vulgaris");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }
     private void loadCat12(){
         categoryTwo.addAll("Antibiotics – β-Lactams","Antibiotics – Fluoroquinolones","Antibiotics – Other Agents II"
         ,"Triazole Antifungals","Antivirals – Influenza and Herpes Simplex","Antibiotics – Macrolides",
         "Antibiotics – Other Agents I","Antivirals – Human Immunodeficiency Virus (HIV)");
        categoryTwoCombo.getItems().setAll(categoryTwo);
    }

    
    
}
