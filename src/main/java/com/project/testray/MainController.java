package com.project.testray;

import javafx.animation.AnimationTimer;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    //ОСНОВНЫЕ КОНСТАНТЫ ИГРЫ
    private final boolean doDrawingMinimap = false;

    public Canvas mainCanvas;

    public MiniMap workWithMiniMap;
    public PlayerView workWithPlayerView;

    public Player player;

    final double miniMapWidth = 400.0;
    final double miniMapHeight = 400.0;

    private final int[][] map = new int[][]{
            //границы
            {0, 0, 400, 0},
            {400, 0, 400, 400},
            {400, 400, 0, 400},
            {0, 400, 0, 0},

            //колонна
            {250, 250, 300, 250},
            {300, 250, 300, 300},
            {300, 300, 250, 300},
            {250, 300, 250, 250},

            //стена
            {50, 50, 50, 100},
            {50, 50, 300, 50},
            {300, 50, 300, 100},
            {300, 100, 50, 100},
    };

    private double playerAngle = 0;

    private long lastUpdateTime = 0;
    int targetFPS = 60;
    private final long frameIntervalNs = 1_000_000_000L / targetFPS;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player = new Player(miniMapWidth, miniMapHeight);

        workWithMiniMap = new MiniMap(mainCanvas, miniMapWidth, miniMapHeight, player, map);
        workWithPlayerView = new PlayerView(mainCanvas);

        mainCanvas.setCursor(Cursor.NONE);

        startGameLoop();
    }

    private void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdateTime >= frameIntervalNs) {
                    update();
                    lastUpdateTime = now;
                }
            }
        };
        gameLoop.start();
    }

    private void update() {
        drawAll();
    }

    private void drawAll(){
        ArrayList<double[]> rays = workWithMiniMap.drawMiniMap(playerAngle);
        workWithPlayerView.drawObjects(rays, playerAngle);

        if(doDrawingMinimap) workWithMiniMap.drawMap();
    }

    public void keyPressedControl(String key, double percent) {
        if (key == null || key.isEmpty()) return;
        double playerSpeed = player.isRunning() ? player.getMaxPlayerRunSpeed() : player.getMaxPlayerWalkSpeed();

        double trueSpeedByX = Math.cos(playerAngle) * playerSpeed * percent;
        double trueSpeedByY = Math.sin(playerAngle) * playerSpeed * percent;
        switch (key) {
            case "W":
                player.move(trueSpeedByX,trueSpeedByY);
                break;
            case "S":
                player.move(-trueSpeedByX,-trueSpeedByY);
                break;
            default:
                break;
        }
    }

    public void keyPressedRotate(String key) {
        if (key == null || key.isEmpty()) return;

        switch (key) {
            case "A":
                playerAngle -= Math.PI / 200;
                break;
            case "D":
                playerAngle += Math.PI / 200;
                break;
            default:
                break;
        }
    }

    public void keyRunning(){
        player.run();
    }

    public void keyWalk(){
        player.walk();
    }
}