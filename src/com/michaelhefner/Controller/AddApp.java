package com.michaelhefner.Controller;

import com.michaelhefner.Model.Appointment;
import com.michaelhefner.Model.Customer;
import com.michaelhefner.Model.DB.Connect;
import com.michaelhefner.Model.DB.Query;
import com.michaelhefner.Model.JDBCEntries;
import com.michaelhefner.Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddApp implements Initializable {
    private ObservableList<String> startHour = FXCollections.observableArrayList();
    private ObservableList<String> startMin = FXCollections.observableArrayList();
    private ObservableList<String> startAMPM = FXCollections.observableArrayList();
    private ObservableList<String> endHour = FXCollections.observableArrayList();
    private ObservableList<String> endMin = FXCollections.observableArrayList();
    private ObservableList<String> endAMPM = FXCollections.observableArrayList();
    private FilteredList<Customer> customerFilteredList;

    @FXML
    private ComboBox<String> cbStartHour;
    @FXML
    private ComboBox<String> cbStartMin;
    @FXML
    private ComboBox<String> cbEndHour;
    @FXML
    private ComboBox<String> cbEndMin;
    @FXML
    private ComboBox<String> cbEnd;
    @FXML
    private ComboBox<String> cbStart;
    @FXML
    private Button btnCancel;
    @FXML
    private Text txtHeading;
    @FXML
    private TextField txtTitle;
    @FXML
    private TextArea txtDescription;
    @FXML
    private TextField txtLocation;
    @FXML
    private TextField txtType;
    @FXML
    private TextField txtUrl;
    @FXML
    private DatePicker dpStart;
    @FXML
    private DatePicker dpEnd;
    @FXML
    private TableView<Customer> tblCustomer;
    @FXML
    private TableColumn<Customer, String> clmCustID;
    @FXML
    private TableColumn<Customer, String> clmCustName;

    private final String NO_ERROR = "-fx-border-color: rgba(25, 205, 25, 1);";

    private final String ERROR = " -fx-border-color: rgba(255,0,0,1);";

    final private Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    private boolean isModifyAppointment = false;
    private Appointment appointmentToModify;
    private Customer customerToAddToAppointment = null;
    private LocalDateTime startAppointmentDateTime;
    private LocalDateTime endAppointmentDateTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        clmCustID.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmCustName.setCellValueFactory(new PropertyValueFactory<>("name"));

        customerFilteredList = new FilteredList<>(JDBCEntries.getAllCustomers(), customer -> true);
        tblCustomer.setItems(customerFilteredList);
        tblCustomer.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, customer, t1) -> customerToAddToAppointment = t1);

        prepareDateTimeSelectors();
    }

    @FXML
    public void onSaveClicked() throws SQLException {
        boolean isValid = checkForEmptyField(new TextField[]{
                        txtLocation, txtTitle, txtType, txtUrl},
                txtDescription,
                new DatePicker[]{dpStart, dpEnd});


        int startHourConverted = (Integer.parseInt(cbStartHour.getValue()) + 12) == 24 ? 12
                : (Integer.parseInt(cbStartHour.getValue()) + 12);
        int endHourConverted = (Integer.parseInt(cbEndHour.getValue()) + 12) == 24 ? 12
                : (Integer.parseInt(cbEndHour.getValue()) + 12);

        startAppointmentDateTime = (dpStart.getValue() != null) ? LocalDateTime.of(dpStart.getValue().getYear(),
                dpStart.getValue().getMonth(),
                dpStart.getValue().getDayOfMonth(),
                (cbStart.getValue() == "PM") ? startHourConverted
                        : Integer.parseInt(cbStartHour.getValue()),
                Integer.parseInt(cbStartMin.getValue())) : null;
        endAppointmentDateTime = (dpEnd.getValue() != null) ? LocalDateTime.of(dpEnd.getValue().getYear(),
                dpEnd.getValue().getMonth(),
                dpEnd.getValue().getDayOfMonth(),
                (cbEnd.getValue() == "PM") ? endHourConverted
                        : Integer.parseInt(cbEndHour.getValue()),
                Integer.parseInt(cbEndMin.getValue())) : null;

        if (dpStart.getValue() != null && dpEnd.getValue() != null) {
            if(isStartBeforeEndTime(startAppointmentDateTime, endAppointmentDateTime)){
                dpStart.setStyle(NO_ERROR);
                dpEnd.setStyle(NO_ERROR);
                System.out.println(startAppointmentDateTime + " :: " + endAppointmentDateTime);
            } else {
                dpStart.setStyle(ERROR);
                dpEnd.setStyle(ERROR);
                showAlert("Error", "Start date and time is before end time.", "Press 'OK' to continue.");
            }
        } else {
            dpStart.setStyle(ERROR);
            dpEnd.setStyle(ERROR);
        }


        if (isValid && !isModifyAppointment) {
            addAppointment();
        } else if (isValid) {
//            if (updateCustomerInfo()) {
//                Connect.closeConnection();
//                closeWindow();
//            } else {
//                showAlert("Error", "There was an error uploading information to database",
//                        "Select OK to retry");
//            }
        }
    }


    private void addAppointment() throws SQLException {
        Appointment appointment = null;
        if (customerToAddToAppointment != null) {
            appointment = new Appointment(customerToAddToAppointment.getId(),
                    User.getId(), txtTitle.getText(), txtDescription.getText(), txtLocation.getText(), customerToAddToAppointment.getName(),
                    txtType.getText(), txtUrl.getText(), startAppointmentDateTime, endAppointmentDateTime);
        }

        Map<Integer, Object> hashMap = new HashMap<>();
        hashMap.put(1, appointment.getCustomerId());
        hashMap.put(2, appointment.getUserId());
        hashMap.put(3, appointment.getTitle());
        hashMap.put(4, appointment.getDescription());
        hashMap.put(5, appointment.getLocation());
        hashMap.put(6, appointment.getContact());
        hashMap.put(7, appointment.getType());
        hashMap.put(8, appointment.getUrl());
        hashMap.put(9, appointment.getStart().toLocalDate() + " " + appointment.getStart().toLocalTime());
        hashMap.put(10, appointment.getEnd().toLocalDate() + " " + appointment.getEnd().toLocalTime());
        hashMap.put(11, User.getName());
        hashMap.put(12, User.getName());


        int appointmentResult = Query.executeUpdate("INSERT INTO appointment(customerId, userId, title, " +
                "description, location, contact, type, url, start, end, createDate, createdBy, lastUpdateBy)" +
                " values(?,?,?,?,?,?,?,?,?,?,NOW(),?,?)", hashMap);
//        ResultSet resultSet = Query.executeQuery("SELECT * FROM appointment", null);
        if (appointmentResult == 0) {
            System.out.println("country result set failed to insert");
            showAlert("Failed", "Appointment failed to save in database.", "Select 'OK' to retry");
        } else {
            JDBCEntries.addAppointment(appointment);
            closeWindow();
        }
        Connect.closeConnection();
    }

//    public boolean updateCustomerInfo() throws SQLException {
//        int indexOfCustomerToModify = JDBCEntries.getAllCustomers().indexOf(customerToModify);
//        customerToModify.setName(txtName.getText());
//        customerToModify.getCountry().setCountry(txtCountry.getText());
//        customerToModify.getAddress().setAddress(txtAddress.getText());
//        customerToModify.getAddress().setAddress2(txtAddress2.getText());
//        customerToModify.getAddress().setPostalCode(txtPostalCode.getText());
//        customerToModify.getAddress().setPhone(txtPhone.getText());
//        customerToModify.getCity().setCity(txtCity.getText());
//
//        Map<Integer, Object> hashMap = new HashMap<>();
//        hashMap.put(1, txtCountry.getText());
//        hashMap.put(2, User.getName());
//        hashMap.put(3, customerToModify.getCountry().getId());
//
//        int resultSet = Query.executeUpdate("UPDATE country set country = ?, lastUpdateBy = ?, lastUpdate = NOW() " +
//                "WHERE countryId = ?", hashMap);
//
//        if (resultSet == 0)
//            return false;
//
//        hashMap = new HashMap<>();
//        hashMap.put(1, txtCity.getText());
//        hashMap.put(2, User.getName());
//        hashMap.put(3, customerToModify.getCity().getId());
//
//        resultSet = Query.executeUpdate("UPDATE city SET city = ?, lastUpdateBy = ?, lastUpdate = NOW() " +
//                "WHERE cityId = ?", hashMap);
//
//        if (resultSet == 0)
//            return false;
//
//        hashMap = new HashMap<>();
//        hashMap.put(1, txtAddress.getText());
//        hashMap.put(2, txtAddress2.getText());
//        hashMap.put(3, txtPhone.getText());
//        hashMap.put(4, txtPostalCode.getText());
//        hashMap.put(5, User.getName());
//        hashMap.put(6, customerToModify.getAddress().getId());
//
//        resultSet = Query.executeUpdate("UPDATE address SET address = ?, address2 = ?, phone = ?, postalCode = ?, " +
//                "lastUpdateBy = ?, lastUpdate = NOW() WHERE addressId = ?", hashMap);
//
//        if (resultSet == 0)
//            return false;
//
//        hashMap = new HashMap<>();
//        hashMap.put(1, txtName.getText());
//        hashMap.put(2, User.getName());
//        hashMap.put(3, customerToModify.getId());
//        resultSet = Query.executeUpdate("UPDATE customer SET customerName = ?, lastUpdateBy = ?, " +
//                "lastUpdate = NOW() WHERE customerId = ?", hashMap);
//
//        if (resultSet == 0)
//            return false;
//
//        JDBCEntries.updateCustomer(indexOfCustomerToModify, customerToModify);
//        return true;
//    }

    public void isModify(Appointment appointment) {
        txtHeading.setText("Modify Appointment");
//        txtAddress.setText(customer.getAddress().getAddress());
//        txtAddress2.setText(customer.getAddress().getAddress2());
//        txtName.setText(customer.getName());
//        txtCity.setText(customer.getCity().getCity());
//        txtCountry.setText(customer.getCountry().getCountry());
//        txtPostalCode.setText(customer.getAddress().getPostalCode());
//        txtPhone.setText(customer.getAddress().getPhone());
        isModifyAppointment = true;
        appointmentToModify = appointment;
    }


    @FXML
    public void closeWindowWithAlert() {
        if (showAlert("Cancel", "You are about to close this window", "Select OK to proceed")) {
            closeWindow();
        }
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

    private String getHour2DigitFormat(int hour) {
        if (hour == 0)
            return "12";
        else if (hour < 10)
            return "0" + hour;
        else if (hour < 13)
            return Integer.toString(hour);
        else
            return Integer.toString(hour - 12);
    }

    private void prepareDateTimeSelectors() {
        LocalDateTime localDateTime = LocalDateTime.now();

        for (int i = 1; i < 13; i++) {
            String b = (i < 10) ? "0" + i : Integer.toString(i);
            startHour.add(b);
            endHour.add(b);
        }
        for (int i = 1; i < 61; i++) {
            String b = (i < 10) ? "0" + i : Integer.toString(i);
            startMin.add(b);
            endMin.add(b);
        }
        startAMPM.add("AM");
        startAMPM.add("PM");
        endAMPM.add("AM");
        endAMPM.add("PM");

        cbStart.setItems(startAMPM);
        cbEnd.setItems(endAMPM);
        cbStartHour.setItems(startHour);
        cbStartMin.setItems(startMin);
        cbEndHour.setItems(endHour);
        cbEndMin.setItems(endMin);
        cbStartMin.setValue(Integer.toString(localDateTime.getMinute()));
        cbEndMin.setValue(Integer.toString(localDateTime.getMinute()));

        cbStartHour.setValue(getHour2DigitFormat(localDateTime.getHour()));
        cbEndHour.setValue(getHour2DigitFormat(localDateTime.getHour() + 1));
        if (localDateTime.getHour() > 11) {
            cbStart.setValue("PM");
            cbEnd.setValue("PM");
        } else {
            cbStart.setValue("AM");
            cbEnd.setValue((localDateTime.getHour() + 1 > 11) ? "PM" : "AM");
        }
    }

    private boolean checkForEmptyField(TextField[] textFields, TextArea textArea, DatePicker[] datePicker) {
        boolean isValid = true;

        for (TextField field : textFields) {
            if (field.getText().isEmpty()) {
                field.setStyle(ERROR);
                isValid = false;
            } else {
                field.setStyle(NO_ERROR);
            }
        }
        for (DatePicker field : datePicker) {
            if (field.getValue() != null) {
                field.setStyle(NO_ERROR);
            } else {
                field.setStyle(ERROR);
                isValid = false;
            }
        }

        if (textArea.getText().isEmpty()) {
            textArea.setStyle(ERROR);
            isValid = false;
        } else {
            textArea.setStyle(NO_ERROR);
        }
        if (!isValid)
            showAlert("Error", "Empty fields detected", "Please fill in empty fields");
        return isValid;
    }

    public boolean isStartBeforeEndTime(LocalDateTime start, LocalDateTime end){
        return (end.isAfter(start));
    }

}
