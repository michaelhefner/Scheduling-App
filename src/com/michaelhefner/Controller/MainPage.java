package com.michaelhefner.Controller;

import com.michaelhefner.Model.*;
import com.michaelhefner.Model.DB.Connect;
import com.michaelhefner.Model.DB.Query;
import com.michaelhefner.Model.Time.TimeSlot;
import com.michaelhefner.Model.Time.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MainPage implements Initializable {

    private FilteredList<Appointment> appointmentFilteredList;
    private Appointment appointmentToBeModified;
    private FilteredList<Customer> customerFilteredList;
    private Customer customerIdToBeModified;
    private final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

    @FXML
    private TableView<Customer> tblCustomer;
    @FXML
    private TextField txtSearchCust;
    @FXML
    private TextField txtSearchApp;
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
    @FXML
    private Label messageLabel;
    @FXML
    private TableView<Appointment> tblCalendarMonth;
    @FXML
    private TableColumn<Appointment, String> clmTitle;
    @FXML
    private TableColumn<Appointment, String> clmStartDate;
    @FXML
    private TableColumn<Appointment, String> clmStartTime;
    @FXML
    private TableColumn<Appointment, String> clmEndDate;
    @FXML
    private TableColumn<Appointment, String> clmEndTime;
    @FXML
    private TableColumn<Appointment, String> clmContact;
    @FXML
    private TableColumn<Appointment, String> clmDescription;
    @FXML
    private TableColumn<Appointment, String> clmLocation;
    @FXML
    private Label lblMonth;
    @FXML
    private ComboBox<String> cbDurationFilter;
    @FXML
    private ComboBox<String> cbContactFilter;
    private final ObservableList<String> durationList = FXCollections.observableArrayList();
    private final ObservableList<String> customerList = FXCollections.observableArrayList();
    @FXML
    private Button btnClearFilters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            populateCustomerDataFromDB();
            populateAppointmentDataFromDB();
            populateTimeline();
            setTblCalendarMonth();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        clmAppID.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmAppTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        clmAppStartDate.setCellValueFactory(new PropertyValueFactory<>("start"));
        clmAppEndDate.setCellValueFactory(new PropertyValueFactory<>("end"));

        appointmentFilteredList = new FilteredList<>(JDBCEntries.getAllAppointments(), appointment -> true);
        tblAppointments.setItems(appointmentFilteredList);
        tblAppointments.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, appointment, t1) -> appointmentToBeModified = t1);
        clmCustID.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmCustName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmCustAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        clmCustCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        clmCustCountry.setCellValueFactory(new PropertyValueFactory<>("country"));

        customerFilteredList = new FilteredList<>(JDBCEntries.getAllCustomers(), customer -> true);
        tblCustomer.setItems(customerFilteredList);
        tblCustomer.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, customer, t1) -> customerIdToBeModified = t1);
    }

    private void setTblCalendarMonth() {
        durationList.add(null);
        durationList.add("Day");
        durationList.add("Week");
        durationList.add("Month");
        cbDurationFilter.setItems(durationList);
        SortedList<Appointment> sortedList = new SortedList<>(JDBCEntries.getAllAppointments(),
                (Appointment appointment, Appointment appointment2) -> {
                    if (appointment.getStart().isBefore(appointment2.getStart()))
                        return -1;
                    else if (appointment.getStart().isAfter(appointment2.getStart()))
                        return 1;
                    return 0;
                });
        FilteredList<Appointment> thisMonthsAppointments = sortedList.filtered(appointment ->
                appointment.getStart().getMonth().equals(LocalDate.now().getMonth()));

        cbDurationFilter.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) ->
                thisMonthsAppointments.setPredicate(appointment -> {
                    if (t1 != (null))
                        if (t1.equals("Day"))
                            return (appointment.getStart().getDayOfMonth() - LocalDateTime.now().getDayOfMonth() < 2);
                        else if (t1.equals("Week"))
                            return (appointment.getStart().getDayOfMonth() - LocalDateTime.now().getDayOfMonth() < 8);
                    return true;
                }));

        customerList.add(null);
        for (Customer customer : JDBCEntries.getAllCustomers())
            customerList.add(customer.getName());
        cbContactFilter.setItems(customerList);

        cbContactFilter.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) ->
                thisMonthsAppointments.setPredicate(appointment -> {
                    if (t1 != (null))
                        return (appointment.getContact().equals(t1));
                    return true;
                }));
        lblMonth.setText(LocalDate.now().getMonth().toString());
        clmTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        clmStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        clmStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        clmEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        clmEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        clmContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        clmDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        clmLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        tblCalendarMonth.setItems(thisMonthsAppointments);
        btnClearFilters.setOnAction(e -> {
            cbContactFilter.getSelectionModel().clearSelection();
            cbDurationFilter.getSelectionModel().clearSelection();
            thisMonthsAppointments.setPredicate(appointment -> true);
        });
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
    public void openModifyApp() throws IOException {
        if (appointmentToBeModified != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../View/AddApp.fxml"));
            AnchorPane root = loader.load();
            AddApp addApp = loader.getController();
            addApp.isModify(appointmentToBeModified);
            openStage(null, root);
        }
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
    public void openModifyCust() {
        try {
            if (customerIdToBeModified != null) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../View/AddCust.fxml"));
                AnchorPane root;
                root = loader.load();
                AddCust addCust = loader.getController();
                addCust.isModify(customerIdToBeModified);
                openStage(null, root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void deleteApp() throws SQLException {
        if (appointmentToBeModified != null) {
            alert.setTitle("Delete");
            alert.setHeaderText("You are about to delete " + appointmentToBeModified.getTitle());
            alert.setContentText("Are you sure you would like to proceed?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                Map<Integer, Object> appointmentMap = new HashMap<>();
                appointmentMap.put(1, appointmentToBeModified.getId());
                int updateCount = Query.executeUpdate("DELETE FROM appointment WHERE appointmentId =?", appointmentMap);
                System.out.println("DB update count: " + updateCount);
                JDBCEntries.deleteAppointment(appointmentToBeModified);
                Connect.closeConnection();
            }
        }
    }

    @FXML
    public void handleSearchCust() {
//        customerFilteredList.setPredicate(new Predicate<>() {
//            @Override
//            public boolean test(Customer customer) {
//                if (txtSearchCust.getText().isEmpty())
//                    return true;
//                return (customer.getName().toLowerCase().equals(txtSearchCust.getText().toLowerCase()));
//            }
//        });
        customerFilteredList.setPredicate(customer -> {     // As you can see the commented out code above is the equivalent
            if (txtSearchCust.getText().isEmpty())          // code to this lambda expression.  The code is shorter and
                return true;                                // implements the required test method.
            return (customer.getName().toLowerCase().equals(txtSearchCust.getText().toLowerCase()));
        });
    }

    @FXML
    public void handleSearchApp() {
        appointmentFilteredList.setPredicate(appointment -> {     // As you can see the commented out code above is the equivalent
            if (txtSearchApp.getText().isEmpty())          // code to this lambda expression.  The code is shorter and
                return true;                                // implements the required test method.
            return (appointment.getTitle().toLowerCase()
                    .equals(txtSearchApp.getText().toLowerCase()));
        });
    }

    private void populateTimeline() {
        for (Appointment appointment : JDBCEntries.getAllAppointments())
            Timeline.addTimeSlot(
                    new TimeSlot(appointment.getStart(), appointment.getEnd()));
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
            ResultSet addressResultSet =
                    Query.executeQuery("SELECT * FROM address where addressId = ?", map);
            addressResultSet.next();
            Address address = new Address(addressResultSet.getString("address"),
                    addressResultSet.getString("address2"),
                    addressResultSet.getInt("cityId"),
                    addressResultSet.getString("postalCode"),
                    addressResultSet.getString("phone"));
            address.setId(addressResultSet.getInt("addressId"));

            Map<Integer, Object> cityMap = new HashMap<>();
            cityMap.put(1, c.getAddressId());
            ResultSet cityResultSet =
                    Query.executeQuery("SELECT * FROM city where cityId = ?", cityMap);
            cityResultSet.next();
            City city = new City(cityResultSet.getString("city"),
                    cityResultSet.getInt("countryId"));
            city.setId(cityResultSet.getInt("cityId"));

            Map<Integer, Object> countryMap = new HashMap<>();

            countryMap.put(1, city.getCountryId());
            ResultSet countryResultSet =
                    Query.executeQuery("SELECT * FROM country where countryId = ?",
                            countryMap);
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

    private void populateAppointmentDataFromDB() throws SQLException {
        ResultSet appointmentResultSet = Query.executeQuery("SELECT * FROM appointment");
        while (appointmentResultSet.next()) {
            Appointment appointment = new Appointment(
                    appointmentResultSet.getInt("customerId"),
                    appointmentResultSet.getString("userId"),
                    appointmentResultSet.getString("title"),
                    appointmentResultSet.getString("description"),
                    appointmentResultSet.getString("location"),
                    appointmentResultSet.getString("contact"),
                    appointmentResultSet.getString("type"),
                    appointmentResultSet.getString("url"),
                    appointmentResultSet.getTimestamp("start").toLocalDateTime(),
                    appointmentResultSet.getTimestamp("end").toLocalDateTime());
            appointment.setId(appointmentResultSet.getInt("appointmentId"));
            JDBCEntries.addAppointment(appointment);
            checkAppTimeEminent(appointment.getStart(), appointment.getTitle());
        }
        Connect.closeConnection();
    }

    private void checkAppTimeEminent(LocalDateTime start, String title) {
        if (LocalDateTime.now().isBefore(start) && LocalDateTime.now().plusMinutes(15).isAfter(start)) {
            showAlert(
                    "You have an upcoming event in " +
                            ChronoUnit.MINUTES.between(LocalDateTime.now(), start) + " minutes"
            );
            messageLabel.setText("Your event " + title + " in "
                    + ChronoUnit.MINUTES.between(LocalDateTime.now(), start)
                    + " minutes"
            );
        }
    }

    private void showAlert(String header) {
        alert.setTitle("Upcoming appointment");
        alert.setHeaderText(header);
        alert.setContentText("Click 'OK' to dismiss.");
        alert.showAndWait();
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
