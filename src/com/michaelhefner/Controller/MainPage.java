package com.michaelhefner.Controller;

import com.michaelhefner.Model.*;
import com.michaelhefner.Model.DB.Connect;
import com.michaelhefner.Model.DB.Query;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MainPage implements Initializable {

    private Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    @FXML
    private TableView<Customer> tblCustomer;
    @FXML
    private TableColumn<Customer, String> clmCustID;
    @FXML
    private TableColumn<Customer, String> clmCustName;
    @FXML
    private TableColumn<Customer, String> clmCustAddress;
    @FXML
    private TableColumn<Customer, String> clmCustCity;
    @FXML
    private TableColumn<Customer, String> clmCustCountry;
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


    private Customer customerIdToBeModified;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            populateCustomerDataFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        clmAppID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        clmAppTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        clmAppStartDate.setCellValueFactory(new PropertyValueFactory<>("start"));
        clmAppEndDate.setCellValueFactory(new PropertyValueFactory<>("end"));

        FilteredList<Appointment> appointmentFilteredList = new FilteredList<>(JDBCEntries.getAllAppointments(), appointment -> true);
        tblAppointments.setItems(appointmentFilteredList);
        tblAppointments.getSelectionModel().selectedItemProperty().addListener((observableValue, appointment, t1) -> {
//            if (t1 != null)
//                if (Inventory.lookupPart(t1.getId()).getClass() == InHouse.class)
//                    partSelectedIsInhouse = true;
//                else
//                    partSelectedIsInhouse = false;
//                partSelected = Inventory.lookupPart(t1.getId());
        });
        clmCustID.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmCustName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmCustAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        clmCustCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        clmCustCountry.setCellValueFactory(new PropertyValueFactory<>("country"));

        FilteredList<Customer> customerFilteredList = new FilteredList<>(JDBCEntries.getAllCustomers(), customer -> true);
        tblCustomer.setItems(customerFilteredList);
        tblCustomer.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, customer, t1) -> customerIdToBeModified = t1);

    }

    @FXML
    public void onExit() {
        alert.setTitle("Exit");
        alert.setHeaderText("You are exiting the application. ");
        alert.setContentText("Would you like to proceed? (select OK to proceed exit)");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Platform.exit();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
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
        openStage("../View/AddCust.fxml", null);
    }

    @FXML
    public void openAddApp() {
        openStage("../View/AddApp.fxml", null);
    }

    @FXML
    public void deleteCust() throws SQLException {
        if (customerIdToBeModified != null) {
            alert.setTitle("Delete");
            alert.setHeaderText("You are about to delete " + customerIdToBeModified.getName());
            alert.setContentText("Are you sure you would like to proceed?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                Map<Integer, Object> customerMap = new HashMap<>();
                customerMap.put(1, customerIdToBeModified.getId());
                Query.executeUpdate("DELETE FROM customer WHERE customerId =?", customerMap);

                Map<Integer, Object> addressMap = new HashMap<>();
                addressMap.put(1, customerIdToBeModified.getAddressId());
                Query.executeUpdate("DELETE FROM address WHERE addressId =?", addressMap);

                Map<Integer, Object> cityMap = new HashMap<>();
                cityMap.put(1, customerIdToBeModified.getCity().getId());
                Query.executeUpdate("DELETE FROM city WHERE cityId =?", cityMap);

                Map<Integer, Object> countryMap = new HashMap<>();
                countryMap.put(1, customerIdToBeModified.getCountry().getId());
                Query.executeUpdate("DELETE FROM country WHERE countryId =?", countryMap);

                JDBCEntries.deleteCustomer(customerIdToBeModified);
                Connect.closeConnection();
            }
        }
    }

    @FXML
    public void openModifyCust() throws IOException {
        if (customerIdToBeModified != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../View/AddCust.fxml"));
            AnchorPane root = loader.load();
            AddCust addCust = loader.getController();
            addCust.isModify(customerIdToBeModified);
            openStage(null, root);
        }

    }

    @FXML
    public void handleSearchCust() {

    }

    @FXML
    public void handleSearchApp() {
    }


    private void populateCustomerDataFromDB() throws SQLException {
        ArrayList<Customer> customers = new ArrayList<>();
        ResultSet customerResultSet = Query.executeQuery("SELECT * FROM customer");

        while (customerResultSet.next()) {
            Customer customer = new Customer(customerResultSet.getString("customerName"),
                    customerResultSet.getInt("addressId"),
                    customerResultSet.getInt("active"));
            customer.setId(customerResultSet.getInt("customerId"));
            customers.add(customer);
        }
        for (Customer c : customers) {
            Map<Integer, Object> map = new HashMap<>();

            map.put(1, c.getAddressId());
            ResultSet addressResultSet = Query.executeQuery("SELECT * FROM address where addressId = ?", map);
            addressResultSet.next();
            Address address = new Address(addressResultSet.getString("address"),
                    addressResultSet.getString("address2"),
                    addressResultSet.getInt("cityId"),
                    addressResultSet.getString("postalCode"),
                    addressResultSet.getString("phone"));
            address.setId(addressResultSet.getInt("addressId"));

            Map<Integer, Object> cityMap = new HashMap<>();
            cityMap.put(1, c.getAddressId());
            ResultSet cityResultSet = Query.executeQuery("SELECT * FROM city where cityId = ?", cityMap);
            cityResultSet.next();
            City city = new City(cityResultSet.getString("city"),
                    cityResultSet.getInt("countryId"));
            city.setId(cityResultSet.getInt("cityId"));

            Map<Integer, Object> countryMap = new HashMap<>();

            countryMap.put(1, city.getCountryId());
            ResultSet countryResultSet = Query.executeQuery("SELECT * FROM country where countryId = ?", countryMap);
            countryResultSet.next();
            Country country = new Country(countryResultSet.getString("country"));
            country.setId(countryResultSet.getInt("countryId"));

            c.setCountry(country);
            c.setAddress(address);
            c.setCity(city);

            JDBCEntries.addCustomer(c);
        }
        Connect.closeConnection();
    }

    private void openStage(String stagePath, Parent parent) {
        Parent root = null;
        try {
            if (stagePath != null) root = FXMLLoader.load(getClass().getResource(stagePath));
            Stage newStage = new Stage();
            if (parent != null) {
                newStage.setScene(new Scene(parent));
            } else if (root != null) {
                newStage.setScene(new Scene(root));
            }
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
