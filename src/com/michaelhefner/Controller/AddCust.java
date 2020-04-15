package com.michaelhefner.Controller;

import com.michaelhefner.Model.*;
import com.michaelhefner.Model.DB.Connect;
import com.michaelhefner.Model.DB.Query;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddCust implements Initializable {

    @FXML
    private Text txtHeading;
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

    final private Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    private boolean isModifyCustomer = false;
    private Customer customerToModify;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void closeWindowWithAlert() {
        if (showAlert("Cancel", "You are about to close this window",
                "Select OK to proceed")) {
            closeWindow();
        }
    }

    @FXML
    public void onSaveClicked() throws SQLException {
        if (txtAddress2.getText().isEmpty())
            txtAddress2.setText("none");
        boolean isValid = checkForEmptyField(new TextField[]{
                txtAddress, txtAddress2, txtCity, txtCountry, txtName, txtPhone, txtPhone, txtPostalCode});
        if (isValid && !isModifyCustomer) {
            Country country = addCountryToDB();
            City city = null;
            Address address = null;
            Customer customer = null;
            if (country != null) city = (country.getId() > 0 ? addCityToDB(country.getId()) : null);
            if (city != null) address = (city.getId() > 0 ? addAddressToDB(city.getId()) : null);
            if (address != null) customer = (address.getId() > 0 ? addCustomerToDB(address.getId()) : null);
            if (customer != null) {
                customer.setCountry(country);
                customer.setCity(city);
                customer.setAddress(address);
                if (customer.getId() > 0) {
                    Connect.closeConnection();
                    JDBCEntries.addCustomer(customer);
                    closeWindow();
                } else {
                    showAlert("Error", "There was an error uploading information to database",
                            "Select OK to retry");
                }
            }
        } else if (isValid) {
            if (updateCustomerInfo()) {
                Connect.closeConnection();
                closeWindow();
            } else {
                showAlert("Error", "There was an error uploading information to database",
                        "Select OK to retry");
            }
        }
    }

    public Customer addCustomerToDB(int addressId) throws SQLException {
        Customer customer = new Customer(txtName.getText(), addressId, 1);

        Map<Integer, Object> hashMap = new HashMap<>();
        hashMap.put(1, customer.getName());
        hashMap.put(2, addressId);
        hashMap.put(3, customer.getActive());
        hashMap.put(4, User.getName());
        hashMap.put(5, User.getName());

        int customerResultSet = Query.executeUpdate("INSERT INTO customer(customerName, " +
                "addressId, active, createDate, createdBy, lastUpdateBy) values(?,?,?,NOW(),?,?)", hashMap);

        ResultSet resultSet = Query.executeQuery("SELECT * FROM customer", null);
        if (customerResultSet == 0) {
            System.out.println("country result set failed to insert");
            return null;
        }
        int customerId = 0;
        while (resultSet.next())
            if (resultSet.last())
                customerId = Integer.parseInt(resultSet.getString("customerId"));
        if (customerId > 0)
            customer.setId(customerId);
        else
            Connect.closeConnection();
        return customer;
    }

    public Address addAddressToDB(int cityId) throws SQLException {
        Address address = new Address(
                txtAddress.getText(),
                txtAddress2.getText(),
                cityId,
                txtPostalCode.getText(),
                txtPhone.getText());

        Map<Integer, Object> hashMap = new HashMap<>();
        hashMap.put(1, address.getAddress());
        hashMap.put(2, address.getAddress2());
        hashMap.put(3, cityId);
        hashMap.put(4, address.getPostalCode());
        hashMap.put(5, address.getPhone());
        hashMap.put(6, User.getName());
        hashMap.put(7, User.getName());
        int addressResultSet = Query.executeUpdate(
                "INSERT INTO address(address, address2, cityId, postalCode, phone, " +
                        "createDate, createdBy, lastUpdateBy) values(?,?,?,?,?,NOW(),?,?)", hashMap);

        ResultSet resultSet = Query.executeQuery("SELECT * FROM address", null);
        if (addressResultSet == 0) {
            System.out.println("address result set failed to insert");
            return null;
        }
        int addressId = 0;
        while (resultSet.next())
            if (resultSet.last())
                addressId = Integer.parseInt(resultSet.getString("addressId"));
        if (addressId > 0)
            address.setId(addressId);
        else
            Connect.closeConnection();
        return address;
    }

    public City addCityToDB(int countryId) throws SQLException {
        City city = new City(txtCity.getText(), countryId);

        Map<Integer, Object> hashMap = new HashMap<>();
        hashMap.put(1, txtCity.getText());
        hashMap.put(2, countryId);
        hashMap.put(3, User.getName());
        hashMap.put(4, User.getName());

        int countryResultSet = Query.executeUpdate("INSERT INTO city(city, countryId, createDate, " +
                "createdBy, lastUpdateBy) values(?,?,NOW(),?,?)", hashMap);

        ResultSet resultSet = Query.executeQuery("SELECT * FROM city", null);
        if (countryResultSet == 0) {
            System.out.println("country result set failed to insert");
            return null;
        }
        int cityId = 0;
        while (resultSet.next())
            if (resultSet.last())
                cityId = Integer.parseInt(resultSet.getString("cityId"));
        if (cityId > 0)
            city.setId(cityId);
        else
            Connect.closeConnection();
        return city;
    }

    public Country addCountryToDB() throws SQLException {
        Country country = new Country(txtCountry.getText());

        Map<Integer, Object> countryMap = new HashMap<>();
        countryMap.put(1, txtCountry.getText());
        countryMap.put(2, User.getName());
        countryMap.put(3, User.getName());
        int countryResultSet = Query.executeUpdate("INSERT INTO country(country, createDate, " +
                "createdBy, lastUpdateBy) values(?,NOW(),?,?)", countryMap);

        ResultSet resultSet = Query.executeQuery("SELECT * FROM country", null);
        if (countryResultSet == 0) {
            System.out.println("country result set failed to insert");
            return null;
        }
        int countryId = 0;
        while (resultSet.next())
            if (resultSet.last())
                countryId = Integer.parseInt(resultSet.getString("countryId"));
        if (countryId > 0)
            country.setId(countryId);
        else
            Connect.closeConnection();
        return country;
    }

    public boolean updateCustomerInfo() throws SQLException {
        int indexOfCustomerToModify = JDBCEntries.getAllCustomers().indexOf(customerToModify);
        customerToModify.setName(txtName.getText());
        customerToModify.getCountry().setCountry(txtCountry.getText());
        customerToModify.getAddress().setAddress(txtAddress.getText());
        customerToModify.getAddress().setAddress2(txtAddress2.getText());
        customerToModify.getAddress().setPostalCode(txtPostalCode.getText());
        customerToModify.getAddress().setPhone(txtPhone.getText());
        customerToModify.getCity().setCity(txtCity.getText());

        Map<Integer, Object> hashMap = new HashMap<>();
        hashMap.put(1, txtCountry.getText());
        hashMap.put(2, User.getName());
        hashMap.put(3, customerToModify.getCountry().getId());

        int resultSet = Query.executeUpdate("UPDATE country set country = ?, lastUpdateBy = ?, " +
                "lastUpdate = NOW() WHERE countryId = ?", hashMap);

        if (resultSet == 0)
            return false;

        hashMap = new HashMap<>();
        hashMap.put(1, txtCity.getText());
        hashMap.put(2, User.getName());
        hashMap.put(3, customerToModify.getCity().getId());

        resultSet = Query.executeUpdate("UPDATE city SET city = ?, lastUpdateBy = ?, lastUpdate " +
                "= NOW() WHERE cityId = ?", hashMap);

        if (resultSet == 0)
            return false;

        hashMap = new HashMap<>();
        hashMap.put(1, txtAddress.getText());
        hashMap.put(2, txtAddress2.getText());
        hashMap.put(3, txtPhone.getText());
        hashMap.put(4, txtPostalCode.getText());
        hashMap.put(5, User.getName());
        hashMap.put(6, customerToModify.getAddress().getId());

        resultSet = Query.executeUpdate("UPDATE address SET address = ?, address2 = ?, phone = ?" +
                ", postalCode = ?, lastUpdateBy = ?, lastUpdate = NOW() WHERE addressId = ?", hashMap);

        if (resultSet == 0)
            return false;

        hashMap = new HashMap<>();
        hashMap.put(1, txtName.getText());
        hashMap.put(2, User.getName());
        hashMap.put(3, customerToModify.getId());
        resultSet = Query.executeUpdate("UPDATE customer SET customerName = ?, lastUpdateBy = ?, " +
                "lastUpdate = NOW() WHERE customerId = ?", hashMap);

        if (resultSet == 0)
            return false;

        JDBCEntries.updateCustomer(indexOfCustomerToModify, customerToModify);
        return true;
    }

    public void isModify(Customer customer) {
        txtHeading.setText("Modify Customer");
        txtAddress.setText(customer.getAddress().getAddress());
        txtAddress2.setText(customer.getAddress().getAddress2());
        txtName.setText(customer.getName());
        txtCity.setText(customer.getCity().getCity());
        txtCountry.setText(customer.getCountry().getCountry());
        txtPostalCode.setText(customer.getAddress().getPostalCode());
        txtPhone.setText(customer.getAddress().getPhone());
        isModifyCustomer = true;
        customerToModify = customer;
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private Boolean showAlert(String title, String header, String context) {
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
                String EMPTY_ERROR = "-fx-background-color: rgba(255, 0, 0, 0.1);" +
                        " -fx-border-color: rgba(255,0,0,1);";
                field.setStyle(EMPTY_ERROR);
                isValid = false;
            } else {
                String NO_ERROR = "-fx-background-color: rgba(255, 255, 255, 1);";
                field.setStyle(NO_ERROR);
            }
        }
        if (!isValid)
            showAlert("Error", "Empty fields detected", "Please fill in empty fields");
        return isValid;
    }

}
