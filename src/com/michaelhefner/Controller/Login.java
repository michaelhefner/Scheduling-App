package com.michaelhefner.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Login implements Initializable {
    /*

    Username = test
    Password = test

    A.   Create a log-in form that can determine the user’s location and translate
    log-in and error control messages (e.g., “The username and password did not match.”) into two languages.

     */
    final private String NO_ERROR =
            "-fx-text-fill: rgba(25, 205, 25, 1);";
    final private String USER_LOGIN_PATH = "user_login_log.txt";
    final private String NO_USERNAME = "No_Username_Entered";
    final private String NO_PASSWORD = "No_Password_Entered";
    final private String CORRECT_AUTH = "Correct_Password_Entered";
    final private String INCORRECT_AUTH = "Incorrect_Username_Password_Combination";
    final private String password = "test";
    final private String username = "test";

    private StringBuilder writeBuilder;
    private LocalDateTime dateTime;

    @FXML
    private Label lblError;
    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    private void addMessage(String message){
        writeBuilder.append(" message=" + message + " ");
    }
    @FXML
    private void onLoginClicked() {
        writeBuilder = new StringBuilder();
        dateTime = LocalDateTime.now();


        writeBuilder.append(dateTime);
        if (txtUsername.getText().isEmpty()) {
            addMessage(NO_USERNAME);
            lblError.setText("No username entered.");
        }
        if (txtPassword.getText().isEmpty()) {
            addMessage(NO_PASSWORD);
            lblError.setText("No password entered.");
        }
        if ((txtPassword.getText().compareTo(password) == 0)
                && (txtUsername.getText().compareTo(username) == 0)) {
            addMessage(CORRECT_AUTH);
            lblError.setText("Success");
            lblError.setStyle(NO_ERROR);
        } else {
            addMessage(INCORRECT_AUTH);
            lblError.setText("Incorrect username and password entered.");
        }
        writeBuilder.append("username=" + txtUsername.getText());

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
