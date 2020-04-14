package com.michaelhefner.Controller;

import com.michaelhefner.Model.*;
import com.michaelhefner.Model.DB.Connect;
import com.michaelhefner.Model.DB.Query;
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
import java.sql.ResultSet;
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
    private TextField txtDescription;
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

    private void setStartAndEndDateTime() {
        int startHourConverted = (Integer.parseInt(cbStartHour.getValue())) == 12 ? 12
                : (Integer.parseInt(cbStartHour.getValue()) + 12);
        int endHourConverted = (Integer.parseInt(cbEndHour.getValue())) == 12 ? 12
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
    }

    private boolean validateDate() {
        if (dpStart.getValue() != null && dpEnd.getValue() != null) {
            if (isStartBeforeEndTime(startAppointmentDateTime, endAppointmentDateTime)) {
                dpStart.setStyle(NO_ERROR);
                dpEnd.setStyle(NO_ERROR);
                return true;
            } else {
                dpStart.setStyle(ERROR);
                dpEnd.setStyle(ERROR);
                showAlert("Error", "Start date and time is before end time.", "Press 'OK' to continue.");
                return false;
            }
        } else {
            dpStart.setStyle(ERROR);
            dpEnd.setStyle(ERROR);
            return false;
        }
    }

    @FXML
    public void onSaveClicked() throws SQLException {
        boolean isValid = checkForEmptyField(new TextField[]{
                        txtLocation, txtTitle, txtType, txtUrl, txtDescription},
                new DatePicker[]{dpStart, dpEnd}, customerToAddToAppointment);
        setStartAndEndDateTime();

        if (!Timeline.addTimeSlot(
                new TimeSlot(startAppointmentDateTime, endAppointmentDateTime, "Save"))) {
            isValid = false;
            showAlert("Error", "Appointment time taken", "Select 'OK' to change");
        }

        if (validateDate() && isValid && !isModifyAppointment && customerToAddToAppointment != null) {
            addAppointment();
        } else if (validateDate() && isValid && customerToAddToAppointment != null) {
            if (updateAppointment()) {
                Connect.closeConnection();
                closeWindow();
            } else {
                showAlert("Error", "There was an error uploading information to database",
                        "Select OK to retry");
            }
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
        ResultSet resultSet = Query.executeQuery("SELECT * FROM appointment", null);
        while (resultSet.next())
            if (resultSet.last())
                appointment.setId(resultSet.getInt("appointmentId"));
        if (appointmentResult == 0) {
            System.out.println("country result set failed to insert");
            showAlert("Failed", "Appointment failed to save in database.", "Select 'OK' to retry");
        } else {
            JDBCEntries.addAppointment(appointment);
            closeWindow();
        }
        Connect.closeConnection();
    }

    public boolean updateAppointment() throws SQLException {
        int indexOfAppointment = JDBCEntries.getAllAppointments().indexOf(appointmentToModify);
        appointmentToModify.setTitle(txtTitle.getText());
        appointmentToModify.setType(txtType.getText());
        appointmentToModify.setUrl(txtUrl.getText());
        appointmentToModify.setLocation(txtLocation.getText());
        appointmentToModify.setDescription(txtDescription.getText());
        appointmentToModify.setContact(customerToAddToAppointment.getName());
        appointmentToModify.setStart(startAppointmentDateTime);
        appointmentToModify.setEnd(endAppointmentDateTime);

        Map<Integer, Object> hashMap = new HashMap<>();
        hashMap.put(1, txtTitle.getText());
        hashMap.put(2, txtType.getText());
        hashMap.put(3, txtUrl.getText());
        hashMap.put(4, txtLocation.getText());
        hashMap.put(5, txtDescription.getText());
        hashMap.put(6, customerToAddToAppointment.getName());
        hashMap.put(7, User.getName());
        hashMap.put(8, startAppointmentDateTime.toLocalDate() + " " + startAppointmentDateTime.toLocalTime());
        hashMap.put(9, endAppointmentDateTime.toLocalDate() + " " + endAppointmentDateTime.toLocalTime());
        hashMap.put(10, appointmentToModify.getId());

        int resultSet = Query.executeUpdate("UPDATE appointment set title = ?, type = ?, url = ?, " +
                "location = ?, description = ?, contact = ?, lastUpdateBy = ?, start = ?, end = ?, lastUpdate = NOW() " +
                "WHERE appointmentId = ?", hashMap);

        if (resultSet == 0)
            return false;

        JDBCEntries.updateAppointment(indexOfAppointment, appointmentToModify);
        return true;
    }

    public void isModify(Appointment appointment) {
        txtHeading.setText("Modify Appointment");
        txtUrl.setText(appointment.getUrl());
        txtType.setText(appointment.getType());
        txtTitle.setText(appointment.getTitle());
        txtLocation.setText(appointment.getLocation());
        txtDescription.setText(appointment.getDescription());
        dpStart.setValue(appointment.getStart().toLocalDate());
        dpEnd.setValue(appointment.getEnd().toLocalDate());
        cbStartHour.setValue(getHour2DigitFormat(appointment.getStart().getHour()));
        cbStartMin.setValue(Integer.toString(appointment.getStart().getMinute()));
        cbEndHour.setValue(getHour2DigitFormat(appointment.getEnd().getHour()));
        cbEndMin.setValue(Integer.toString(appointment.getStart().getMinute()));
        customerToAddToAppointment = (Customer) JDBCEntries.getAllCustomers().filtered(customer -> customer.getName().toLowerCase().equals(appointment.getContact().toLowerCase())).get(0);
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

    private boolean checkForEmptyField(TextField[] textFields, DatePicker[] datePicker, Customer customer) {
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
        if (customer == null) {
            isValid = false;
            tblCustomer.setStyle(ERROR);
        } else {
            tblCustomer.setStyle(NO_ERROR);
        }
        if (!isValid)
            showAlert("Error", "Empty fields detected", "Please fill in empty fields");
        return isValid;
    }

    public boolean isStartBeforeEndTime(LocalDateTime start, LocalDateTime end) {
        return (end.isAfter(start));
    }

}
