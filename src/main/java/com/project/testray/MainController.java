package com.project.testray;

import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Canvas mainCanvas;

    public MiniMap workWithMiniMap;
    public PlayerView workWithPlayerView;

    public Player player;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        workWithMiniMap = new MiniMap(mainCanvas);
        workWithPlayerView = new PlayerView(mainCanvas);

        player = new Player(workWithMiniMap.width, workWithMiniMap.height);

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
                stepY = key.compareTo("W") == 0 ? stepY + 10 : stepY - 10;
                player.setCurrentY(stepY);
                break;
            case "A", "D":
                double stepX = player.getCurrentX();
                stepX = key.compareTo("D") == 0 ? stepX + 10 : stepX - 10;
                player.setCurrentX(stepX);
                break;
            default:
                break;
        }
    }
}
