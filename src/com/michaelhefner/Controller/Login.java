package com.michaelhefner.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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
    @FXML
    private Label lblError;
    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    @FXML
    private void onLoginClicked() {
        StringBuilder writeBuilder = new StringBuilder();
        LocalDateTime date = LocalDateTime.now();
        String password = "test";
        String username = "test";
        writeBuilder.append(date);
        if (txtUsername.getText().isEmpty()) {
            writeBuilder.append(" No_Username_Entered ");
            lblError.setText("No username entered.");
        } else if (txtPassword.getText().isEmpty()) {
            writeBuilder.append(" No_Password_Entered ");
            lblError.setText("No password entered.");
        } else if ((txtPassword.getText().compareTo(password) != 0)
                && (txtUsername.getText().compareTo(username) != 0)) {
            writeBuilder.append(" Incorrect_Password_Entered ");
            lblError.setText("Incorrect username and password entered.");
        } else {
            writeBuilder.append(" Correct_Password_Entered");
        }
        writeBuilder.append(txtUsername.getText());

        ArrayList<String> readList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("user_log.txt"))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                readList.add(line);
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("user_log.txt"))) {
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
