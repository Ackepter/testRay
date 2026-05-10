package com.project.testray;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    private boolean isWPressed = false;
    private boolean isSPressed = false;

    private boolean isAPressed = false;
    private boolean isDPressed = false;

    private double percent = 0.0;
    private final double acceleration = 0.015;

    MainController controller;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("RayCast");
        stage.setScene(scene);
        stage.show();


        controller = fxmlLoader.getController();
        AnimationTimer gameControlLoop = getAnimationTimerControl(controller);
        gameControlLoop.start();

        AnimationTimer gameRotateLoop = getAnimationTimerRotate(controller);
        gameRotateLoop.start();


        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.W) {
                isWPressed = true;
            }
            else if (event.getCode() == KeyCode.S) {
                isSPressed = true;
            }

            if (event.getCode() == KeyCode.A) {
                isAPressed = true;
            }
            else if (event.getCode() == KeyCode.D) {
                isDPressed = true;
            }

            if(event.getCode() == KeyCode.SHIFT){
                controller.keyRunning();
            }

            if(event.getCode() == KeyCode.ESCAPE){
                stage.close();
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.W) {
                isWPressed = false;
                percent = 0.0;
            }
            else if (event.getCode() == KeyCode.S){
                isSPressed = false;
                percent = 0.0;
            }

            if (event.getCode() == KeyCode.A) {
                isAPressed = false;
                percent = 0.6;
            }
            else if (event.getCode() == KeyCode.D){
                isDPressed = false;
                percent = 0.6;
            }

            if(event.getCode() == KeyCode.SHIFT){
                controller.keyWalk();
            }
        });
    }

    private AnimationTimer getAnimationTimerControl(MainController controller) {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                percent = Math.min(percent + acceleration, 1.0);

                if (isWPressed) {
                    controller.keyPressedControl("W", percent);
                }
                else if (isSPressed) {
                    controller.keyPressedControl("S", percent);
                }
            }
        };
    }


    private AnimationTimer getAnimationTimerRotate(MainController controller) {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isAPressed) {
                    controller.keyPressedRotate("A");
                }
                else if (isDPressed) {
                    controller.keyPressedRotate("D");
                }
            }
        };
    }
}
