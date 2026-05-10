package com.project.testray;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    boolean isWPressed = false;
    boolean isAPressed = false;
    boolean isSPressed = false;
    boolean isDPressed = false;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("RayCast");
        stage.setScene(scene);
        stage.show();

        MainController controller = fxmlLoader.getController();

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.W && !isWPressed) {
                controller.keyPressed("W");
                isWPressed = true;
            }
            else if (event.getCode() == KeyCode.A && !isAPressed){
                controller.keyPressed("A");
                isAPressed = true;
            }
            else if (event.getCode() == KeyCode.S && !isSPressed){
                controller.keyPressed("S");
                isSPressed = true;
            }
            else if (event.getCode() == KeyCode.D && !isDPressed){
                controller.keyPressed("D");
                isDPressed = true;
            }
            else if(event.getCode() == KeyCode.ESCAPE){
                stage.hide();
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.W) {
                isWPressed = false;
            }
            else if (event.getCode() == KeyCode.A) {
                isAPressed = false;
            }
            else if (event.getCode() == KeyCode.S) {
                isSPressed = false;
            }
            else if (event.getCode() == KeyCode.D) {
                isDPressed = false;
            }
        });
    }
}
