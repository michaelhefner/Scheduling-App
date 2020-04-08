
package com.michaelhefner;

import com.michaelhefner.Model.DB.Connect;
import com.michaelhefner.Model.DB.Query;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("View/login.fxml"))));
        primaryStage.show();
    }


    public static void main(String[] args) {
        Connection connection = Connect.getConnection();
        try {
            Query.setStatement(connection);
            Statement statement = Query.getStatement();

            String insert = "INSERT INTO country(country, createDate, createdBy, lastUpdateBy) values('US', '2020-04-08 00:00:00.0', 'Michael', 'Michael')";
            statement.execute(insert);
            if (statement.getUpdateCount() > 0)
                System.out.println("Insert executed successfully. With " + statement.getUpdateCount() + " rows effected.");
            else
                System.out.println("No change detected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        launch(args);
        Connect.closeConnection();
    }
}
