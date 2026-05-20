package com.project.testray;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    private boolean isWPressed = false;
    private boolean isSPressed = false;

    private boolean isAPressed = false;
    private boolean isDPressed = false;

    private boolean isShiftPressed = false;
    private boolean isCtrlPressed = false;

    private boolean isRPressed = false;

    private double percent = 0.0;
    private final double acceleration = 0.015;

    MainController controller;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("RayCast");
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();


        controller = fxmlLoader.getController();

        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastTime = -1;

            @Override
            public void handle(long now) {
                if(controller.getGameState() == MainController.GAME_STATES.WIN
                || controller.getGameState() == MainController.GAME_STATES.LOSE){
                    if (isRPressed) {
                        controller.keyRestart();
                    }
                }
                else{
                    if (lastTime < 0) {
                        lastTime = now;
                        return;
                    }

                    double deltaTime = (now - lastTime) / 1_000_000_000.0;
                    lastTime = now;

                    deltaTime = Math.min(deltaTime, 0.1);

                    percent = Math.min(percent + acceleration, 1.0);

                    if (isWPressed) {
                        controller.keyPressedControl("W", percent, deltaTime);
                    } else if (isSPressed) {
                        controller.keyPressedControl("S", percent, deltaTime);
                    }

                    if (isAPressed) {
                        controller.keyPressedRotate("A", deltaTime);
                    } else if (isDPressed) {
                        controller.keyPressedRotate("D", deltaTime);
                    }

                    if(isCtrlPressed){
                        controller.keyShoot();
                    }
                }
                controller.drawAll(now);
            }
        };
        gameLoop.start();

        scene.setOnKeyPressed(event -> {
            controller.DO_DRAW_GUIDE = false;

            switch (event.getCode()) {
                case W -> isWPressed = true;
                case S -> isSPressed = true;
                case A -> isAPressed = true;
                case D -> isDPressed = true;
                case SHIFT -> {
                    if (!isShiftPressed) {
                        controller.keyRunning(); isShiftPressed = true;
                    }
                }
                case ESCAPE -> stage.close();
                case M -> controller.switchMap();
                case CONTROL -> isCtrlPressed = true;
                case R -> isRPressed = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W -> { isWPressed = false; percent = 0.0; }
                case S -> { isSPressed = false; percent = 0.0; }
                case A -> { isAPressed = false; percent = 0.6; }
                case D -> { isDPressed = false; percent = 0.6; }
                case SHIFT -> { controller.keyWalk(); isShiftPressed = false; }
                case CONTROL -> isCtrlPressed = false;
                case R -> isRPressed = false;
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
