package com.michaelhefner.Controller;

import com.michaelhefner.Model.Appointment;
import com.michaelhefner.Model.Customer;
import com.michaelhefner.Model.DB.Connect;
import com.michaelhefner.Model.JDBCEntries;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainPage implements Initializable {

    private Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    @FXML
    private TableView<Customer> tblCustomer;
    @FXML
    private TableColumn<Customer, String> clmCustID;
    @FXML
    private TableColumn<Customer, String> clmCustName;

    @FXML
    private TableView<Appointment> tblAppointments;
    @FXML
    private TableColumn<Appointment, String> clmAppID;
    @FXML
    private TableColumn<Appointment, String> clmAppTitle;
    @FXML
    private TableColumn<Appointment, String> clmAppStartDate;
    @FXML
    private TableColumn<Appointment, String> clmAppEndDate;


    private FilteredList<Customer> customerFilteredList;
    private FilteredList<Appointment> appointmentFilteredList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            Statement statement = Connect.getConnection().createStatement();
            initializeDB(statement);
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            String dateTime = formatter.format(localDateTime);
            statement.executeUpdate(
                    "insert into country values(0, 'Deer', '" + dateTime + "', 'bob', CONVERT(NOW(), CHAR), 'bobo')");

            ResultSet rs = statement.executeQuery("select * from country");
            while (rs.next()) {
                System.out.println(rs.getString("createDate"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*
         * Start Demo Data
         */

        Appointment newAppointment = new Appointment(1, 1, "testing title", "test #1",
                "location", "some contact", "type", "url", LocalDateTime.now(), LocalDateTime.now());
        JDBCEntries.addAppointment(newAppointment);

        Customer newCustomer = new Customer("Bob", 1, 1);
        newCustomer.setId(1);
        JDBCEntries.addCustomer(newCustomer);

        /*
         * End Demo Data
         */

        clmAppID.setCellValueFactory(new PropertyValueFactory<Appointment, String>("customerId"));
        clmAppTitle.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        clmAppStartDate.setCellValueFactory(new PropertyValueFactory<Appointment, String>("start"));
        clmAppEndDate.setCellValueFactory(new PropertyValueFactory<Appointment, String>("end"));

        appointmentFilteredList = new FilteredList<Appointment>(JDBCEntries.getAllAppointments(), appointment -> true);
        tblAppointments.setItems(appointmentFilteredList);
        tblAppointments.getSelectionModel().selectedItemProperty().addListener((observableValue, appointment, t1) -> {
//            if (t1 != null)
//                if (Inventory.lookupPart(t1.getId()).getClass() == InHouse.class)
//                    partSelectedIsInhouse = true;
//                else
//                    partSelectedIsInhouse = false;
//                partSelected = Inventory.lookupPart(t1.getId());
        });
        clmCustID.setCellValueFactory(new PropertyValueFactory<Customer, String>("id"));
        clmCustName.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));

        customerFilteredList = new FilteredList<Customer>(JDBCEntries.getAllCustomers(), customer -> true);
        tblCustomer.setItems(customerFilteredList);
        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, customer, t1) -> {
//            if (t1 != null)
//                if (Inventory.lookupPart(t1.getId()).getClass() == InHouse.class)
//                    partSelectedIsInhouse = true;
//                else
//                    partSelectedIsInhouse = false;
//                partSelected = Inventory.lookupPart(t1.getId());
        });

    }

    @FXML
    public void onExit() {
        alert.setTitle("Exit");
        alert.setHeaderText("You are exiting the application. ");
        alert.setContentText("Would you like to proceed? (select OK to proceed exit)");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Platform.exit();
                System.exit(0);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @FXML
    public void deleteApp() {

    }

    @FXML
    public void openModifyApp() {

    }

    @FXML
    public void openAddCust() {
        openStage("../View/AddCust.fxml");
    }

    @FXML
    public void openAddApp() {
        openStage("../View/AddApp.fxml");
    }


    @FXML
    public void deleteCust() {

    }

    @FXML
    public void openModifyCust() {

    }

    @FXML
    public void handleSearchCust() {

    }

    @FXML
    public void handleSearchApp() {
    }


    private void initializeDB(Statement statement) {

        try {
            statement.executeUpdate("CREATE TABLE country ("
                    + "countryId INT(10), "
                    + "country VARCHAR(50), "
                    + "createDate DATETIME, "
                    + "createBy VARCHAR(40), "
                    + "lastUpdate TIMESTAMP, "
                    + "lastUpdateBy VARCHAR(40))");
            statement.executeUpdate("CREATE TABLE city ("
                    + "cityId INT(10), "
                    + "city VARCHAR(50), "
                    + "countryId INT(10), "
                    + "createDate DATETIME, "
                    + "createBy VARCHAR(40), "
                    + "lastUpdate TIMESTAMP, "
                    + "lastUpdateBy VARCHAR(40))");
            statement.executeUpdate("CREATE TABLE address ("
                    + "addressId INT(10), "
                    + "address VARCHAR(50), "
                    + "address2 VARCHAR(50), "
                    + "cityId INT(10), "
                    + "postalCode VARCHAR(10), "
                    + "phone VARCHAR(20), "
                    + "createDate DATETIME, "
                    + "createBy VARCHAR(40), "
                    + "lastUpdate TIMESTAMP, "
                    + "lastUpdateBy VARCHAR(40))");
            statement.executeUpdate("CREATE TABLE customer ("
                    + "customerId INT(10), "
                    + "customerName VARCHAR(45), "
                    + "addressId INT(10), "
                    + "active TINYINT(1), "
                    + "createDate DATETIME, "
                    + "createBy VARCHAR(40), "
                    + "lastUpdate TIMESTAMP, "
                    + "lastUpdateBy VARCHAR(40))");
            statement.executeUpdate("CREATE TABLE appointment ("
                    + "appointmentId INT(10), "
                    + "customerId INT(10), "
                    + "userId INT(10), "
                    + "title VARCHAR(255), "
                    + "description TEXT, "
                    + "location TEXT, "
                    + "contact TEXT, "
                    + "type TEXT, "
                    + "url VARCHAR(255), "
                    + "createDate DATETIME, "
                    + "createBy VARCHAR(40), "
                    + "lastUpdate TIMESTAMP, "
                    + "lastUpdateBy VARCHAR(40))");
            statement.executeUpdate("CREATE TABLE user ("
                    + "userId INT, "
                    + "userName VARCHAR(50), "
                    + "password VARCHAR(50), "
                    + "active TINYINT(1), "
                    + "createDate DATETIME, "
                    + "createBy VARCHAR(40), "
                    + "lastUpdate TIMESTAMP, "
                    + "lastUpdateBy VARCHAR(40))");
        } catch (SQLException e) {
            System.out.println("Table Already Created");
//            e.printStackTrace();
        }

    }

    private void openStage(String stagePath) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(stagePath));
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
