package com.andresrodr.todocloud;

import com.andresrodr.todocloud.classes.User;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private Label welcomeText;

    @FXML
    protected TextField userEmailField;
    @FXML
    protected TextField userPasswordField;


    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onExitButtonClick() {
        System.exit(0);
    }

    @FXML
    protected void onMinimizeButtonClick(Event event) {
        // Minimize the window
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    protected void onLoginButtonClick() {
        // Login the user
        System.out.println("Login the user");
        String email = userEmailField.getText();
        String password = userPasswordField.getText();
        User user = User.login(email, password);
        if (user != null) {
            System.out.println("User logged in: " + user.getEmail());
            // Cambiar a la pantalla de tareas
            try {
                Parent root = FXMLLoader.load(getClass().getResource("tasks-view.fxml"));
                Stage stage = (Stage) userEmailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();



                //grab your root here
                root.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        xOffset = event.getSceneX();
                        yOffset = event.getSceneY();
                    }
                });

                //move around here
                root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        stage.setX(event.getScreenX() - xOffset);
                        stage.setY(event.getScreenY() - yOffset);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("User not found");
        }
    }

    @FXML
    protected void onRegisterButtonClick() {
        // Register a new user
        System.out.println("Register a new user");
        String email = userEmailField.getText();
        String password = userPasswordField.getText();
        boolean registered = User.register(email, password);

        if (registered) {
            System.out.println("User registered: " + email);
        } else {
            System.out.println("User not registered");
        }
    }


}
