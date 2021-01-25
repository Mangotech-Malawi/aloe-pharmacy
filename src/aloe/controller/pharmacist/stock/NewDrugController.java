/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist.stock;

import aloe.controller.receptionist.PosController;
import aloe.model.Drug;
import aloe.model.QueryManager;
import aloe.model.Verifier;
import com.jfoenix.controls.JFXButton;
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
import org.controlsfx.control.textfield.AutoCompletionBinding.AutoCompletionEvent;
import org.controlsfx.control.textfield.TextFields;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class NewDrugController implements Initializable {
     ObservableList<String> categoryOne = FXCollections.observableArrayList();
     ObservableList<String> categoryTwo = FXCollections.observableArrayList();
     ObservableList<String> namesList = FXCollections.observableArrayList();
    @FXML
    private StackPane stackPane;
    @FXML
    private Label lblStatus;
    private JFXTextField txtId;
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextArea txtDescription;
    @FXML
    private JFXTextField txtSellingPrice;
    @FXML
    private JFXTextField txtOrderPrice;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXComboBox<String> categoryOneCombo;
    @FXML
    private JFXComboBox<String> categoryTwoCombo;
    @FXML
    private JFXTextField txtUnitOfMeasurement;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadComboData();
        loadUnitsOfMeasurement();
        loadDrugNames();
    }
    private void loadDrugNames(){
        loadNames();
        TextFields.bindAutoCompletion(txtName,namesList);   
    }
    
    private void loadNames(){
        namesList.addAll(  "Lisinopril (Prinivil, Zestril)",
            "Lisinopril/hydrochlorothiazide (Prinzide, Zestoretic)",
            "Enalapril (Vasotec)",
            "Ramipril (Altace)",
            "Losartan (Cozaar)",
            "Olmesartan (Benicar)", 
            "Valsartan (Diovan)",  
            "Atenolol (Tenormin)",
            "Metoprolol tartrate (Lopressor)", 
            "Metoprolol succinate (Toprol XL)",
            "Nebivolol (Bystolic)",
            "Propranolol (Inderal)",
            "Carvedilol(Coreg)",
            "Nifedipine XL (Procardia XL)",
            "Amlodipine (Norvasc)",
            "Amlodipine/benazepril (Lotrel)", 
            "Diltiazem (Cardizem CD, more)",
            "Hydrochlorothiazide (Microzide)",
            "Metolazone (Zaroxolyn)",
            "Furosemide (Lasix)",
            "Torsemide (Demadex)", 
            "Bumetanide (Bumex)",
            "Spironolactone (Aldactone)", 
            "Pravastatin (Pravachol)",
            "Simvastatin (Zocor)",
            "Simvastatin/ezetimibe (Vytorin)", 
            "Atorvastatin (Lipitor)",
            "Rosuvastatin (Crestor)", 
            "Aspirin",
            "Aspirin/Dipyridamole (Aggrenox)", 
            "Clopidogrel (Plavix)", 
            "Prasugrel (Effient)", 
            "Ticagrelor (Brilinta)", 
            "Fluticasone (Flovent)", 
            "Budesonide (Pulmicort)", 
            "Salmeterol (Serevent)",
            "Formoterol (Foradil)",
            "Fluticasone/salmeterol (Advair)",
            "Budesonide/formoterol (Symbicort)",
            "Dicyclomine (Bentyl)",
            "Loperamide (Imodium)", 
            "Diphenoxylate/atropine (Lomotil)", 
            "Mesalamine (Asacol-HD, Delzicol, and more)", 
            "Ondansetron (Zofran)",
            "Prochlorperazine (Compazine)",
            "Promethazine (Phenergan)",
            "Metoclopramide (Reglan)",
            "Cinacalcet (Sensipar)",
            "Sevelamer carbonate (Renvela)",
            "Potassium chloride (Klor-Con and more)",
            "Phenytoin (Dilantin)",
            "Valproic acid (Depakote, Depakene)",
            "Lamotrigine (Lamictal)",
            "Carbamazepine (Tegretol, Carbatrol)",
            "Oxcarbazepine (Trileptal)", 
            "Levetiracetam (Keppra)", 
            "Metformin (Glucophage)",
            "Sitagliptin (Januvia)",
            "Glyburide (DiaBeta, Glynase)",
            "Glimepiride (Amaryl)", 
            "Glipizide (Glucotrol)",
            "Glyburide/metformin (Glucovance)",
            "Sitagliptin/metformin (Janumet)",
            "Timolol (Timoptic)",
            "Brimonidine (Alphagan P)",
            "Brimonidine/timolol (Combigan)",
            "Dorzolamide (Trusopt)",
            "Bimatoprost (Lumigan)",
            "Latanoprost (Xalatan)",
            "Travoprost (Travatan Z)", 
            "Adapalene (Differin)",
            "Isotretinoin",
            "Amoxicillin (Amoxil)",
            "Amoxicillin/clavulanate (Augmentin)",
            "Cephalexin (Keflex)",
            "Cefuroxime (Ceftin)",
            "Cefdinir (Omnicef)");
    }
  
    private void loadComboData(){
    
       categoryOne.addAll("Cardiovascular Drugs","Respiratory Drugs",
               "Gastrointestinal Drugs","Renal Drugs","Neurologic Drugs","Psychiatric Drugs",
               "Endocrinology Drugs","Urologic Drugs","Rheumatologic Drugs",
       "Ophthalmic and Otolaryngological Drugs","Dermatologic Drugs","Infectious Disease Drugs");
       categoryOneCombo.getItems().setAll(categoryOne);
    }
    
    private void loadUnitsOfMeasurement(){
        try {
            ObservableList<String> unitsList = FXCollections.observableArrayList();
            QueryManager Query = new QueryManager();
            String drugUnitsQuery = "SELECT measurement FROM drugs "
                    + "WHERE id NOT IN (SELECT id FROM drugs_del)";
            ResultSet rs1 = Query.getDataQuery(drugUnitsQuery);
            while(rs1.next()){
                unitsList.add(rs1.getString(1));
            }
                   TextFields.bindAutoCompletion(txtUnitOfMeasurement, unitsList);
                     
        } catch (SQLException ex) {
            Logger.getLogger(PosController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
   
    @FXML
    private void btnSaveAction(ActionEvent event) {
         if(isValid()){//If all fields are filled execution of statements continues
         
            String name = txtName.getText().trim();
            String description = txtDescription.getText().trim();
            String firstCategory  = categoryOneCombo.getValue();
            String secCategory = categoryTwoCombo.getValue();
            String unitOfMeasurement = txtUnitOfMeasurement.getText().trim();
            try{
                Double sellingPrice = Double.parseDouble(txtSellingPrice.getText().trim());
                Double orderPrice = Double.parseDouble(txtOrderPrice.getText().trim());
                Verifier  verify = new Verifier();
                 //Validating information entered
                     Drug drug = new Drug(name,firstCategory,secCategory,sellingPrice,orderPrice,unitOfMeasurement,description);
                    QueryManager Query = new QueryManager();
                    if(Query.insertStatement(drug.addDrug())){
                         Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Information ");
                        alert.setContentText("Drug added succesfully \nYou can start adding drug entries");
                        alert.showAndWait();
                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                       alert.setHeaderText("Error");
                       alert.setContentText("Failed to add drug");
                       alert.showAndWait();
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
      if(txtName.getText().trim().isEmpty() ||
                txtDescription.getText().trim().isEmpty() || txtUnitOfMeasurement.getText().trim().isEmpty()
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
     
        txtName.clear();
        txtDescription.clear();
        txtSellingPrice.clear();
        txtOrderPrice.clear();
        categoryOneCombo.setValue(null);
        categoryOneCombo.setPromptText("First category");
        categoryTwoCombo.setValue(null);
        categoryTwoCombo.setPromptText("Second Category");
        txtUnitOfMeasurement.clear();
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
        }catch(NullPointerException ex){
        }
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
