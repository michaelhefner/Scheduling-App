package com.michaelhefner.Controller;

import com.michaelhefner.Model.*;
import com.michaelhefner.Model.DB.Connect;
import com.michaelhefner.Model.DB.Query;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddCust implements Initializable {

    final private String EMPTY_ERROR =
            "-fx-background-color: rgba(255, 0, 0, 0.1);" +
                    " -fx-border-color: rgba(255,0,0,1);";

    final private String NO_ERROR =
            "-fx-background-color: rgba(255, 255, 255, 1);";

    final private Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtAddress2;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtPostalCode;
    @FXML
    private TextField txtCountry;
    @FXML
    private TextField txtPhone;


    @FXML
    private Button btnCancel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void onSaveClicked() throws SQLException {
        if (txtAddress2.getText().isEmpty())
            txtAddress2.setText("none");
        boolean isValid = checkForEmptyField(new TextField[]{
                txtAddress, txtAddress2, txtCity, txtCountry, txtName, txtPhone, txtPhone, txtPostalCode});
        if (isValid){
            Country country = addCountryToDB();
            City city = (country.getId() > 0 ? addCityToDB(country.getId()) : null);
            Address address = (city.getId() > 0 ? addAddressToDB(city.getId()) : null);
            Customer customer = (address.getId() > 0 ? addCustomerToDB(address.getId()) : null);

            if (customer.getId() > 0) {
                Connect.closeConnection();
                JDBCEntries.addCustomer(customer);
                closeWindow();
            } else {
                showAlert("Error", "There was an error uploading information to database",
                        "Select OK to retry");
            }

        }
    }

    public Customer addCustomerToDB(int addressId) throws SQLException {
        Customer customer = new Customer(txtName.getText(), addressId, 1);
        Statement statement = Query.getStatement();
        statement.executeUpdate("INSERT INTO customer(customerName, addressId, active, " +
                "createDate, createdBy, lastUpdateBy) values('" + customer.getName() + "', " +
                addressId + ", " + customer.getActive()  + ", NOW(), '" + User.getName() +
                "', '" + User.getName() + "')");

        ResultSet resultSet = statement.executeQuery("SELECT * FROM customer");
        int customerId = 0;
        while (resultSet.next())
            if(resultSet.last())
                customerId = Integer.parseInt(resultSet.getString("customerId"));
        if (customerId > 0)
            customer.setId(customerId);
        else
            Connect.closeConnection();
        return customer;
    }

    public Address addAddressToDB(int cityId) throws SQLException {
        Address address = new Address(txtAddress.getText(), txtAddress2.getText(), cityId, txtPostalCode.getText(), txtPhone.getText());
        Statement statement = Query.getStatement();
        statement.executeUpdate("INSERT INTO address(address, address2, cityId, postalCode, phone, " +
                "createDate, createdBy, lastUpdateBy) values('" + address.getAddress() + "', '" +
                address.getAddress2() + "', " + cityId + ", '" + address.getPostalCode() + "', '" +
                address.getPhone() + "', NOW(), '" + User.getName() + "', '" + User.getName() + "')");

        ResultSet resultSet = statement.executeQuery("SELECT * FROM address");
        int addressId = 0;
        while (resultSet.next())
            if(resultSet.last())
                addressId = Integer.parseInt(resultSet.getString("addressId"));
        if (addressId > 0)
            address.setId(addressId);
        else
            Connect.closeConnection();
        return address;
    }

    public City addCityToDB(int countryId) throws SQLException {
        City city = new City(txtCity.getText(), countryId);
        Statement statement = Query.getStatement();
        statement.executeUpdate("INSERT INTO city(city, countryId, createDate, createdBy, lastUpdateBy)" +
                        " values('" + txtCity.getText() + "', " + countryId + ", NOW(), '"
                + User.getName() + "', '" + User.getName() + "')");

        ResultSet resultSet = statement.executeQuery("SELECT * FROM city");
        int cityId = 0;
        while (resultSet.next())
            if(resultSet.last())
                cityId = Integer.parseInt(resultSet.getString("cityId"));
        if (cityId > 0)
            city.setId(cityId);
        else
            Connect.closeConnection();
        return city;
    }

    public Country addCountryToDB() throws SQLException {
        Country country = new Country(txtCountry.getText());
        Query.setStatement(Connect.getConnection());
        Statement statement = Query.getStatement();
        statement.executeUpdate("INSERT INTO country(country, createDate, createdBy, lastUpdateBy)" +
                        " values('" + txtCountry.getText() + "', NOW(), '" + User.getName() + "', '" + User.getName() + "')");
        ResultSet resultSet = statement.executeQuery("SELECT * FROM country");
        int countryId = 0;
        while (resultSet.next())
            if(resultSet.last())
                countryId = Integer.parseInt(resultSet.getString("countryId"));
        if (countryId > 0)
            country.setId(countryId);
        else
            Connect.closeConnection();
        return country;
    }

    @FXML
    public void closeWindow() {
        if (showAlert("Cancel","You are about to close this window","Select OK to proceed")) {
            close();
        }
    }

    private void close(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private Boolean showAlert(String title, String header, String context){
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private boolean checkForEmptyField(TextField[] textFields) {
        boolean isValid = true;
        for (TextField field : textFields) {
            if (field.getText().isEmpty()) {
                field.setStyle(EMPTY_ERROR);
                isValid = false;
            } else {
                field.setStyle(NO_ERROR);
            }
        }
        if (!isValid)
            showAlert("Error", "Empty fields detected", "Please fill in empty fields");
        return isValid;
    }

}
