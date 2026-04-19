package com.project.testray;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MyCanvas {
    Canvas canvas;

    GraphicsContext gc;

    private final int[][] borders = new int[][]{
            {50, 50, 50, 200},
            {50, 50, 300, 50},
            {300, 50, 300, 100},
            {300, 100, 100, 100},
            {100, 100, 100, 200},
            {100, 200, 50, 200},
    };

    public MyCanvas(Canvas canvas){
        this.canvas = canvas;
    }
    public void initialize(){
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(3.0);
    }
    public void drawBorders(){
        for(int[] i : borders){
            gc.strokeLine(i[0],i[1],i[2],i[3]);
        }
    }

    public void drawRay(double mouseX, double mouseY) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawBorders();

        double startX = canvas.getWidth() / 2;
        double startY = canvas.getHeight() / 2;

        double fovAngle = 60;
        int rayCount = 40;

        double centerDirX = mouseX - startX;
        double centerDirY = mouseY - startY;
        double centerDist = Math.hypot(centerDirX, centerDirY);

        if (centerDist == 0) return;

        centerDirX /= centerDist;
        centerDirY /= centerDist;

        double baseAngle = Math.atan2(centerDirY, centerDirX);
        double halfFovRad = Math.toRadians(fovAngle / 2.0);

        for (int i = 0; i < rayCount; i++) {

            double t = (double) i / (rayCount - 1);
            double angle = baseAngle - halfFovRad + t * fovAngle * Math.PI / 180.0;

            double dirX = Math.cos(angle);
            double dirY = Math.sin(angle);

            double[] edgePoint = findEdgeIntersection(startX, startY, dirX, dirY,
                    canvas.getWidth(), canvas.getHeight());

            gc.strokeLine(startX, startY, edgePoint[0], edgePoint[1]);

            drawCollideForRay(startX, startY, edgePoint[0], edgePoint[1]);
        }
    }

    private double[] findEdgeIntersection(double startX, double startY,
                                          double dirX, double dirY,
                                          double width, double height) {
        double minT = Double.MAX_VALUE;
        double resultX = startX, resultY = startY;

        if (dirX != 0) {
            double t = (0 - startX) / dirX;
            if (t > 0) {
                double y = startY + t * dirY;
                if (y >= 0 && y <= height && t < minT) {
                    minT = t;
                    resultX = 0;
                    resultY = y;
                }
            }
        }

        if (dirX != 0) {
            double t = (width - startX) / dirX;
            if (t > 0) {
                double y = startY + t * dirY;
                if (y >= 0 && y <= height && t < minT) {
                    minT = t;
                    resultX = width;
                    resultY = y;
                }
            }
        }

        if (dirY != 0) {
            double t = (0 - startY) / dirY;
            if (t > 0) {
                double x = startX + t * dirX;
                if (x >= 0 && x <= width && t < minT) {
                    minT = t;
                    resultX = x;
                    resultY = 0;
                }
            }
        }

        if (dirY != 0) {
            double t = (height - startY) / dirY;
            if (t > 0) {
                double x = startX + t * dirX;
                if (x >= 0 && x <= width && t < minT) {
                    minT = t;
                    resultX = x;
                    resultY = height;
                }
            }
        }

        return new double[]{resultX, resultY};
    }

    private void drawCollideForRay(double startX, double startY,
                                   double edgePointX, double edgePointY){
        for(int[] i : borders){
            //отрезок
            double[] A = new double[]{i[0],i[1]};
            double[] B = new double[]{i[2],i[3]};

            //луч
            double[] C = new double[]{startX, startY};
            double[] D = new double[]{edgePointX, edgePointY};


            double rNumerator = (B[0] - A[0]) * (C[1] - A[1]) - (C[0] - A[0]) * (B[1] - A[1]);
            double rDenominator = (D[0] - C[0]) * (B[1] - A[1]) - (B[0] - A[0]) * (D[1] - C[1]);

            double sNumerator = (A[0] - C[0]) * (D[1] - C[1]) - (D[0] - C[0]) * (A[1] - C[1]);
            double sDenominator = (D[0] - C[0]) * (B[1] - A[1]) - (B[0] - A[0]) * (D[1] - C[1]);

            if(sDenominator != 0 && rDenominator != 0){
                double r = rNumerator / rDenominator;
                double s = sNumerator / sDenominator;

                if(s >= 0 && s <= 1 && r >= 0){
                    double[] P = new double[2];
                    P[0] = s * (B[0] - A[0]) + A[0];
                    P[1] = s * (B[1] - A[1]) + A[1];

                    gc.setFill(Color.RED);
                    gc.fillOval(P[0] - 4, P[1] - 4, 8, 8);
                    gc.setFill(Color.BLACK);
                }
            }
        }
    }
}
