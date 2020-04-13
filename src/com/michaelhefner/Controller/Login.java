package com.michaelhefner.Controller;

import com.michaelhefner.Model.DB.Query;
import com.michaelhefner.Model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class Login implements Initializable {
    private String noUserFound;
    private String noPassword;
    private String noUsername;
    private String correctAuth;
    private String incorrectAuth;

    private StringBuilder writeBuilder;

    @FXML
    private Label lblError;
    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    private void addMessage(String message) {
        writeBuilder.append(" message=").append(message).append(" ");
    }

    @FXML
    private void onLoginClicked() throws SQLException {
        writeBuilder = new StringBuilder();
        LocalDateTime dateTime = LocalDateTime.now();
        writeBuilder.append(dateTime);
        if (txtUsername.getText().isEmpty()) {
            addMessage(noUsername);
            lblError.setText(noUsername);
        } else {
            if (txtPassword.getText().isEmpty()) {
                addMessage(noPassword);
                lblError.setText(noPassword);
            } else {
                if (verifyLogin()) {
                    logToUserFile();
                    openMainStage();
                } else {
                    logToUserFile();
                }
            }
        }
    }

    public void logToUserFile() {
        writeBuilder.append("username=").append(txtUsername.getText());
        ArrayList<String> readList = new ArrayList<>();

        String USER_LOGIN_PATH = "user_login_log.txt";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(USER_LOGIN_PATH))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                readList.add(line);
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(USER_LOGIN_PATH))) {
            for (String line : readList) {
                bufferedWriter.append(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.append(writeBuilder);
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void openMainStage() {
        Stage stage = (Stage) lblError.getScene().getWindow();
        stage.close();

        try {
            Parent mainPage = FXMLLoader.load(getClass().getResource("../View/MainPage.fxml"));
            Stage mainStage = new Stage();
            mainStage.setScene(new Scene(mainPage));
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean verifyLogin() throws SQLException {
        boolean isValid = false;
        User.setName(txtUsername.getText().toLowerCase());
        System.out.println("username entered = " + User.getName());

        Map<Integer, Object> mapList = new HashMap<>();
        mapList.put(1, User.getName());
        ResultSet resultSet = Query.executeQuery("SELECT * FROM user where userName = ?", mapList);

        if (resultSet.next()) {
            System.out.println("User found");
            User.setPassword(resultSet.getString("password"));
            if (txtPassword.getText().compareTo(User.getPassword()) == 0) {
                addMessage(correctAuth);
                lblError.setText(correctAuth);
                String NO_ERROR = "-fx-text-fill: rgba(25, 205, 25, 1);";
                lblError.setStyle(NO_ERROR);
                isValid = true;
                User.setId(resultSet.getString("userId"));
            } else {
                addMessage(incorrectAuth);
                lblError.setText(incorrectAuth);
            }
        } else {
            System.out.println(noUserFound);
            addMessage(noUserFound);
            lblError.setText(noUserFound);
        }

        return isValid;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        resourceBundle = ResourceBundle.getBundle("com.michaelhefner/Nat", Locale.getDefault());
        if (Locale.getDefault().getLanguage().equals("es")
                || Locale.getDefault().getLanguage().equals("fr")
                || Locale.getDefault().getLanguage().equals("en")) {
            noUserFound = resourceBundle.getString("NO_USER_FOUND");
            noPassword = resourceBundle.getString("NO_PASSWORD");
            noUsername = resourceBundle.getString("NO_USERNAME");
            correctAuth = resourceBundle.getString("CORRECT_AUTH");
            incorrectAuth = resourceBundle.getString("INCORRECT_AUTH");
        }
    }
}
