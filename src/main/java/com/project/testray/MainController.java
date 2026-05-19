package com.project.testray;

import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    //ОСНОВНЫЕ КОНСТАНТЫ ИГРЫ
    private boolean DO_DRAW_MAP = false;
    public static final double PLAYER_RADIUS = 15.0;

    public Canvas mainCanvas;

    public MiniMap workWithMiniMap;
    public PlayerView workWithPlayerView;

    public Player player;

    final double miniMapWidth = 1000.0;
    final double miniMapHeight = 1000.0;

    private final int[][] map = new int[][]{
            //границы
            {0, 0, (int)miniMapWidth, 0,   0},
            {(int)miniMapHeight, 0, (int)miniMapHeight, (int)miniMapHeight, 0},
            {(int)miniMapWidth, (int)miniMapWidth, 0, (int)miniMapWidth, 0},
            {0, (int)miniMapHeight, 0, 0,   0},

            //первая комната
            {50, 50, 50, 250,      1},
            {50, 50, 250, 50,      1},
            {250, 50, 250, 75,     1},
            {50, 250, 250, 250,    1},
            {250, 250, 250, 125,   1},

            //коридор из первой комнаты
            {250, 75, 650, 75,     1},
            {250, 125, 425, 125,   1},
            {475, 125, 650, 125,   1},

            //большая г-образная комната
            {650, 75, 650, 50,     1},
            {650, 50, 950, 50,     1},
            {950, 50, 950, 750,    1},
            {950, 750, 50, 750,    1},
            {50, 750, 50, 600,     1},
            {50, 600, 100, 600,    1},
            {150, 600, 650, 600,   1},
            {650, 600, 650, 450,   1},
            {650, 250, 650, 125,   1},

            //верхний вход в центральную комнату
            {650, 250, 600, 250,   1},
            {600, 250, 600, 175,   1},
            {600, 175, 475, 175,   1},
            {475, 175, 475, 125,   1},

            //нижний вход в центральную комнату
            {650, 450, 600, 450,   1},
            {600, 450, 600, 550,   1},
            {600, 550, 300, 550,   1},
            {300, 550, 300, 175,   1},
            {300, 175, 425, 175,   1},
            {425, 175, 425, 125,   1},

            //колона в проходе справа
            {650, 400, 650, 300,   2},
            {650, 400, 600, 400,   2},
            {600, 400, 600, 300,   2},
            {600, 300, 650, 300,   2},

            //последняя комната
            {100, 600, 100, 550,   1},
            {100, 550, 50, 550,    1},
            {50, 550, 50, 300,     1},
            {50, 300, 250, 300,    1},
            {250, 300, 250, 550,   1},
            {250, 550, 150, 550,   1},
            {150, 550, 150, 600,   1},

    };

    public ArrayList<Enemy> enemies = new ArrayList<>();

    private double playerAngle = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player = new Player(miniMapWidth, miniMapHeight);

        workWithMiniMap = new MiniMap(mainCanvas, miniMapWidth, miniMapHeight, player, map);
        workWithPlayerView = new PlayerView(mainCanvas);

        mainCanvas.setCursor(Cursor.NONE);

        enemies.add(
                new Enemy(miniMapWidth, miniMapHeight, 150, 75)
        );
    }


    public void drawAll(long now){
        ArrayList<double[]> rays = workWithMiniMap.drawMiniMap(playerAngle, enemies);
        workWithPlayerView.drawObjects(rays, playerAngle,
                player.getCurrentX(), player.getCurrentY(),
                enemies, now);

        if(DO_DRAW_MAP) workWithMiniMap.drawMap();
    }

    public void keyPressedControl(String key, double percent, double deltaTime) {
        if (key == null || key.isEmpty()) return;
        double playerSpeed = player.isRunning() ? player.getMaxPlayerRunSpeed() : player.getMaxPlayerWalkSpeed();

        double trueSpeedByX = Math.cos(playerAngle) * playerSpeed * percent * deltaTime;
        double trueSpeedByY = Math.sin(playerAngle) * playerSpeed * percent * deltaTime;

        double newX = player.getCurrentX();
        double newY = player.getCurrentY();

        switch (key) {
            case "W" -> {
                newX += trueSpeedByX;
                newY += trueSpeedByY;
            }
            case "S" -> {
                newX -= trueSpeedByX;
                newY -= trueSpeedByY;
            }
            default  -> { return; }
        }

        double[] resolved = workWithMiniMap.resolveCollision(newX, newY);

        player.setCurrentX(resolved[0]);
        player.setCurrentY(resolved[1]);
    }

    public void keyPressedRotate(String key, double deltaTime) {
        if (key == null || key.isEmpty()) return;

        double rotSpeed = Math.PI / 2 * deltaTime;

        switch (key) {
            case "A" -> playerAngle -= rotSpeed;
            case "D" -> playerAngle += rotSpeed;
        }
    }

    public void keyRunning(){
        player.run();
    }

    public void keyWalk(){
        player.walk();
    }

    public void switchMap(){
        DO_DRAW_MAP = !DO_DRAW_MAP;
    }
}