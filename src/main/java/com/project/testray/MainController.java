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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        workWithMiniMap = new MiniMap(mainCanvas);
        workWithPlayerView = new PlayerView(mainCanvas);

        mainCanvas.setOnMouseMoved(event -> {
            double xNode   = event.getX();
            double yNode   = event.getY();

            ArrayList<double[]> rays = workWithMiniMap.drawMiniMap(xNode, yNode);
            workWithPlayerView.drawObjects(rays);
            workWithMiniMap.drawMiniMap(xNode, yNode);
        });
    }
}
