package com.project.testray;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class PlayerView {
    Canvas canvas;

    GraphicsContext gc;

    public PlayerView(Canvas canvas){
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();

    }

    public void drawObjects(ArrayList<double[]> rays){
        gc.setLineWidth(1.0);

        gc.clearRect(400,0,1600,900);
        gc.clearRect(0,400,400,900);

        for(double[] ray : rays){
            double xRect = 1600 * (1 + ray[2]) / 2;
            int r = 50;
            if(xRect < 1600 * 0.2) r += 20;
            else if(xRect < 1600 * 0.3) r += 40;
            else if(xRect < 1600 * 0.4) r += 60;
            else if(xRect < 1600 * 0.5) r += 80;

            gc.fillRect(xRect, r, 5, 900 - r);
        }
    }
}
