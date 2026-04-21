package com.project.testray;

import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Canvas mainCanvas;

    public MiniMap workWithMiniMap;
    public PlayerView workWithPlayerView;

    public Player player;

    final double miniMapWidth = 400.0;
    final double miniMapHeight = 400.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player = new Player(miniMapWidth, miniMapHeight);

        workWithMiniMap = new MiniMap(mainCanvas, miniMapWidth, miniMapHeight, player);
        workWithPlayerView = new PlayerView(mainCanvas);

        mainCanvas.setOnMouseMoved(event -> {
            double xNode   = event.getX();
            double yNode   = event.getY();

            ArrayList<double[]> rays = workWithMiniMap.drawMiniMap(xNode, yNode);
            workWithPlayerView.drawObjects(rays);
            workWithMiniMap.drawMiniMap(xNode, yNode);
        });
    }

    public void keyPressed(String key) {
        if (key == null || key.isEmpty()) return;

        switch (key) {
            case "W", "S":
                double stepY = player.getCurrentY();
                stepY = key.compareTo("W") == 0 ? stepY - 20 : stepY + 20;
                player.setCurrentY(stepY);
                ArrayList<double[]> rays = workWithMiniMap.drawMiniMap();
                workWithPlayerView.drawObjects(rays);
                workWithMiniMap.drawMiniMap();
                break;
            case "A", "D":
                double stepX = player.getCurrentX();
                stepX = key.compareTo("D") == 0 ? stepX + 20 : stepX - 20;
                player.setCurrentX(stepX);
                ArrayList<double[]> rays1 = workWithMiniMap.drawMiniMap();
                workWithPlayerView.drawObjects(rays1);
                workWithMiniMap.drawMiniMap();
                break;
            default:
                break;
        }
    }
}
