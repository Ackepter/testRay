package com.project.testray;

import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.robot.Robot;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private final double mouseSensitivity = 0.003;

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
    private double previousMouseX = 0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player = new Player(miniMapWidth, miniMapHeight);

        workWithMiniMap = new MiniMap(mainCanvas, miniMapWidth, miniMapHeight, player, map);
        workWithPlayerView = new PlayerView(mainCanvas);

        mainCanvas.setCursor(Cursor.NONE);

        mainCanvas.setOnMouseMoved(event -> {
            double deltaX = event.getX() - previousMouseX;

            double relativeDeltaX = event.getX() - (mainCanvas.getWidth() / 2);
            if (Math.abs(relativeDeltaX) > 1) {
                playerAngle += relativeDeltaX * mouseSensitivity;
                try {
                    Robot robot = new Robot();
                    robot.mouseMove((int) (event.getScreenX() - relativeDeltaX), (int) event.getScreenY());
                } catch (Exception _) {

                }
            }

            playerAngle += deltaX * mouseSensitivity;
            previousMouseX = event.getX();

            motionByMouse(playerAngle);
            System.out.println(playerAngle);
        });
    }
    public void motionByMouse(double playerAngle){

        ArrayList<double[]> rays = workWithMiniMap.drawMiniMap(playerAngle);
        workWithPlayerView.drawObjects(rays);
        workWithMiniMap.drawMiniMap(playerAngle);
    }

    public void keyPressed(String key) {
        if (key == null || key.isEmpty()) return;

        switch (key) {
            case "W", "S":
                double stepY = player.getCurrentY();
                stepY = key.compareTo("W") == 0 ? stepY - 20 : stepY + 20;
                player.setCurrentY(stepY);

                ArrayList<double[]> rays = workWithMiniMap.drawMiniMap(playerAngle);
                workWithPlayerView.drawObjects(rays);
                workWithMiniMap.drawMiniMap(playerAngle);
                break;
            case "A", "D":
                double stepX = player.getCurrentX();
                stepX = key.compareTo("D") == 0 ? stepX + 20 : stepX - 20;
                player.setCurrentX(stepX);

                ArrayList<double[]> rays1 = workWithMiniMap.drawMiniMap(playerAngle);
                workWithPlayerView.drawObjects(rays1);
                workWithMiniMap.drawMiniMap(playerAngle);
                break;
            default:
                break;
        }
    }
}
