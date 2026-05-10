package com.project.testray;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class PlayerView {
    Canvas canvas;

    GraphicsContext gc;

    public ArrayList<double[]> currentRay;

    public PlayerView(Canvas canvas){
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();

    }

    public void drawObjects(ArrayList<double[]> rays, double playerAngle) {
        currentRay = rays;
        gc.setLineWidth(1.0);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int screenWidth = 1600;
        int screenHeight = 900;

        double wallHeightConstant = screenHeight * 45;

        double stripWidth = (double) screenWidth / rays.size() + 1.0;

        for (int i = 0; i < rays.size(); i++) {
            double[] ray = rays.get(i);
            double rawDistance = ray[0];
            double rayAngle = ray[1];

            double angleDiff = rayAngle - playerAngle;
            while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
            while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

            double correctedDistance = rawDistance * Math.cos(angleDiff);
            if (correctedDistance < 1.0) correctedDistance = 1.0;

            int wallHeight = (int) (wallHeightConstant / correctedDistance);
            if (wallHeight > screenHeight * 2) wallHeight = screenHeight * 2;

            double xRect = (double) i / rays.size() * screenWidth;
            double yTop = (screenHeight - wallHeight) / 2.0;
            gc.setFill(Color.GRAY);
            gc.fillRect(xRect, yTop, stripWidth, wallHeight);
        }
    }
}
