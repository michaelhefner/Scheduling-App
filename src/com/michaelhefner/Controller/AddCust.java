package com.michaelhefner.Controller;

import com.michaelhefner.Model.*;
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
    public void onSaveClicked() {
        boolean isValid = checkForEmptyField(new TextField[]{
                txtAddress, txtAddress2, txtCity, txtCountry, txtName, txtPhone, txtPhone, txtPostalCode});
        if (isValid){
            Country country = new Country(txtCountry.getText());
            City city = new City(txtCity.getText(), country.getId());
            Address address = new Address(txtAddress.getText(), txtAddress2.getText(), city.getId(), txtPostalCode.getText(), txtPhone.getText());
            Customer customer = new Customer(txtName.getText(), address.getId(), 1);

            JDBCEntries.addCustomer(customer);

            closeWindow(true);
        }
    }

    @FXML
    public void closeWindow(boolean bypass) {
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
        if (result.get() == ButtonType.OK) {
            return true;
        }
        return false;
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

    private boolean addCustomerInDB(Country country, City city, Address address, Customer customer){

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://3.227.166.251/U05yp2", "U05yp2", "53688646210");
            Statement statement = connection.createStatement();
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
            String dateTime = formatter.format(localDateTime);

            statement.executeUpdate(
                    "insert into country values(0, '" + country.getCountry() + "', '" + dateTime + "', '" + User.getName() + "', CONVERT(NOW(), CHAR), 'bobo')");

            ResultSet rs = statement.executeQuery("select * from country");
            while (rs.next()) {
                System.out.println(rs.getString("createDate"));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
}
