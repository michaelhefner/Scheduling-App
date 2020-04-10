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
import java.sql.Statement;
import java.util.ArrayList;
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
    private FilteredList<Customer> customerFilteredList;
    private FilteredList<Appointment> appointmentFilteredList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            populateCustomerDataFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
        clmCustAddress.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
        clmCustCity.setCellValueFactory(new PropertyValueFactory<Customer, String>("city"));
        clmCustCountry.setCellValueFactory(new PropertyValueFactory<Customer, String>("country"));

        customerFilteredList = new FilteredList<Customer>(JDBCEntries.getAllCustomers(), customer -> true);
        tblCustomer.setItems(customerFilteredList);
        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, customer, t1) -> {
            if (t1 != null)
                customerIdToBeModified = (Customer) t1;
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
        openStage("../View/AddCust.fxml", null);
    }

    @FXML
    public void openAddApp() {
        openStage("../View/AddApp.fxml", null);
    }

    @FXML
    public void deleteCust() {

    }

    @FXML
    public void openModifyCust() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../View/AddCust.fxml"));
        AnchorPane root = loader.load();
        AddCust addCust = loader.getController();
        addCust.isModify(customerIdToBeModified);
        openStage(null, root);

    }

    @FXML
    public void handleSearchCust() {

    }

    @FXML
    public void handleSearchApp() {
    }

    private void populateCustomerDataFromDB() throws SQLException {
        ArrayList<Customer> customers = new ArrayList<>();

        Query.setStatement(Connect.getConnection());
        Statement statement = Query.getStatement();

        ResultSet customerResultSet = statement.executeQuery("SELECT * FROM customer");
        while (customerResultSet.next()) {

            Customer customer = new Customer(customerResultSet.getString("customerName"),
                    customerResultSet.getInt("addressId"),
                    customerResultSet.getInt("active"));
            customer.setId(customerResultSet.getInt("customerId"));
            customers.add(customer);
        }
        for (Customer c : customers) {
            Statement addressStatement = Query.getStatement();

            ResultSet addressResultSet =
                    addressStatement.executeQuery("SELECT * FROM address where addressId = "
                            + c.getAddressId());
            addressResultSet.next();
            Address address = new Address(addressResultSet.getString("address"),
                    addressResultSet.getString("address2"),
                    addressResultSet.getInt("cityId"),
                    addressResultSet.getString("postalCode"),
                    addressResultSet.getString("phone"));
            address.setId(addressResultSet.getInt("addressId"));

            Statement cityStatement = Query.getStatement();
            ResultSet cityResultSet =
                    cityStatement.executeQuery("SELECT * FROM city where cityId = "
                            + address.getCityId());
            cityResultSet.next();
            City city = new City(cityResultSet.getString("city"),
                    cityResultSet.getInt("countryId"));
            city.setId(cityResultSet.getInt("cityId"));

            Statement countryStatement = Query.getStatement();
            ResultSet countryResultSet =
                    countryStatement.executeQuery("SELECT * FROM country where countryId = "
                            + city.getCountryId());
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
            } else {
                newStage.setScene(new Scene(root));
            }
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
