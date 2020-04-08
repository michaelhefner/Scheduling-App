package com.michaelhefner.Controller;

import com.michaelhefner.Model.DB.Connect;
import com.michaelhefner.Model.DB.Query;
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
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Login implements Initializable {
    private final String USER_LOGIN_PATH = "user_login_log.txt";
    private final String NO_USER_FOUND = "No user found by that name";
    private final String NO_PASSWORD = "No password entered.";
    private final String NO_USERNAME = "No username entered.";
    private final String CORRECT_AUTH = "Success";
    private final String NO_ERROR = "-fx-text-fill: rgba(25, 205, 25, 1);";
    private final String INCORRECT_AUTH = "Incorrect username and password entered.";

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
            addMessage(NO_USERNAME);
            lblError.setText(NO_USERNAME);
        } else {
            if (txtPassword.getText().isEmpty()) {
                addMessage(NO_PASSWORD);
                lblError.setText(NO_PASSWORD);
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

        String username = txtUsername.getText().toLowerCase();
        System.out.println("username entered = " + username);
        Query.setStatement(Connect.getConnection());
        Statement statement = Query.getStatement();
        ResultSet resultSet = statement.executeQuery("SELECT password FROM user where userName = '"
                + username + "'");

        if (resultSet.next()) {
            System.out.println("User found");
            String password = resultSet.getString("password");
            if (txtPassword.getText().compareTo(password) == 0) {
                addMessage(CORRECT_AUTH);
                lblError.setText(CORRECT_AUTH);
                lblError.setStyle(NO_ERROR);
                isValid = true;
            } else {
                addMessage(INCORRECT_AUTH);
                lblError.setText(INCORRECT_AUTH);
            }
        } else {
            System.out.println(NO_USER_FOUND);
            addMessage(NO_USER_FOUND);
            lblError.setText(NO_USER_FOUND);
        }

        return isValid;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
