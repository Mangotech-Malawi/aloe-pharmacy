/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aloe.controller.pharmacist;

import static aloe.controller.LoginController.logInDate;
import static aloe.controller.LoginController.username;
import aloe.model.PopWindow;
import static aloe.model.PopWindow.loc;
import aloe.model.UserLog;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Senze
 */
public class ExpenseController implements Initializable {

    @FXML
    private StackPane stackpane;
    @FXML
    private BorderPane borderpane;
    @FXML
    private Label lblUsername;
    @FXML
    private JFXButton btnMnimize;
    @FXML
    private JFXButton btnMaximize;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton btnDashboard;
    @FXML
    private JFXButton btnAddExpense;
    @FXML
    private JFXButton btnViewExpense;
    @FXML
    private JFXButton btnBack;
    @FXML
    private JFXButton bntHome;
    @FXML
    private JFXButton btnStock;
    @FXML
    private JFXButton btnSystem;
    @FXML
    private StackPane switchPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
          lblUsername.setText("Logged in as " + username);
        if (PopWindow.primaryStage.isMaximized()) {
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(minimizeIcon);
        } else {
            FontAwesomeIconView maxmizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE);
            maxmizeIcon.setGlyphSize(17);
            maxmizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(maxmizeIcon);
        }
        setCenter("/aloe/view/pharmacist/expense/dashboard.fxml");
    }    

    @FXML
    private void btnMnimizeAction(ActionEvent event) {
        PopWindow.primaryStage.setIconified(true);
    }

    @FXML
    private void btnMaximizeAction(ActionEvent event) {
        if (PopWindow.primaryStage.isMaximized()) {
            PopWindow.primaryStage.setMaximized(false);
            FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE);
            minimizeIcon.setGlyphSize(17);
            minimizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(minimizeIcon);
        } else {
            PopWindow.primaryStage.setMaximized(true);
            FontAwesomeIconView maxmizeIcon = new FontAwesomeIconView(FontAwesomeIcon.SQUARE_ALT);
            maxmizeIcon.setGlyphSize(17);
            maxmizeIcon.setStyle("-fx-fill: #fff;");
            btnMaximize.setGraphic(maxmizeIcon);
        }
    }

    @FXML
    private void btnCloseAction(ActionEvent event) {
         stackpane.getScene().getWindow().hide();
    }

    @FXML
    private void btnDashboardAction(ActionEvent event) {
        setCenter("/aloe/view/pharmacist/expense/dashboard.fxml");
    }

    @FXML
    private void btnAddExpenseAction(ActionEvent event) {
        setCenter("/aloe/view/pharmacist/expense/addExpense.fxml");
    }

    @FXML
    private void btnViewExpenseAction(ActionEvent event) {
        setCenter("/aloe/view/pharmacist/expense/viewExpenses.fxml");
    }

    @FXML
    private void btnBackAction(ActionEvent event) {
        PopWindow window = new PopWindow();
        if (PopWindow.primaryStage.isMaximized()) {
            window.loadWindow(loc.pop(), "Pharmacy System : Transactions", true, true, false, false);
        } else {
            window.loadWindow(loc.pop(), "Pharmacy System : Transactions", true, false, false, false);

        }
        stackpane.getScene().getWindow().hide();
    }

    @FXML
    private void btnHomeAction(ActionEvent event) {
          PopWindow window = new PopWindow();
        if (PopWindow.primaryStage.isMaximized()) {
            window.loadWindow("/aloe/view/pharmacist/Home.fxml", "Pharmacy System : Home", true, true, false, false);
        } else {
            window.loadWindow("/aloe/view/pharmacist/Home.fxml", "Pharmacy System : Home", true, false, false, false);
        }
        stackpane.getScene().getWindow().hide();
    }

    @FXML
    private void btnStockAction(ActionEvent event) {
        PopWindow window = new PopWindow();
        loc.push("/aloe/view/pharmacist/expense.fxml");
        if (PopWindow.primaryStage.isMaximized()) {
            window.loadWindow("/aloe/view/pharmacist/stock.fxml", "Pharmacy System : Reports", true, true, false, false);
        } else {
            window.loadWindow("/aloe/view/pharmacist/stock.fxml", "Pharmacy System : Reports", true, false, false, false);
        }
        stackpane.getScene().getWindow().hide();
    }

    private void btnTransactionAction(ActionEvent event) {
        PopWindow window = new PopWindow();
        loc.push("/aloe/view/pharmacist/expense.fxml");
        if (PopWindow.primaryStage.isMaximized()) {
            window.loadWindow("/aloe/view/pharmacist/transaction.fxml", "Pharmacy System : Reports", true, true, false, false);
        } else {
            window.loadWindow("/aloe/view/pharmacist/transaction.fxml", "Pharmacy System : Reports", true, false, false, false);
        }
        stackpane.getScene().getWindow().hide();
    }

    @FXML
    private void btnSettingsAction(ActionEvent event) {
        PopWindow window = new PopWindow();
        loc.push("/aloe/view/pharmacist/expense.fxml");
        if (PopWindow.primaryStage.isMaximized()) {
            window.loadWindow("/aloe/view/pharmacist/settings.fxml", "Pharmacy System : Reports", true, true, false, false);
        } else {
            window.loadWindow("/aloe/view/pharmacist/settings.fxml", "Pharmacy System : Reports", true, false, false, false);
        }
        stackpane.getScene().getWindow().hide();
    }
     public void setCenter(String location) {
        switchPane.getChildren().clear();
        try {
            StackPane pane2 = FXMLLoader.load(getClass().getResource(location));
            ObservableList<Node> elements = pane2.getChildren();
            switchPane.getChildren().setAll(elements);
        } catch (IOException ex) {
            //Message comes here
        }
    }

    @FXML
    private void dailyExpenseChartAction(ActionEvent event) {
        setCenter("/aloe/view/pharmacist/expense/charts/dailyExpenseChart.fxml");
    }

    @FXML
    private void monthExpenseComparison(ActionEvent event) {
        setCenter("/aloe/view/pharmacist/expense/charts/monthlyExpenseChart.fxml");
    }

    
}
