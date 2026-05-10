package com.project.testray;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class MiniMap {
    Player player;

    final double width;
    final double height;

    Canvas canvas;
    GraphicsContext gc;

    private final int[][] map;

    private ArrayList<double[]> sections = new ArrayList<>();
    private ArrayList<double[]> dots = new ArrayList<>();

    public MiniMap(Canvas canvas, double width, double height, Player player, int[][] map){
        this.map = map;

        this.player = player;

        this.width = width;
        this.height = height;

        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
    }

    public void drawMap(){
        gc.clearRect(0, 0, width + 5, height + 5);
        gc.setLineWidth(3.0);
        for(int[] i : map){
            gc.strokeLine(i[0],i[1],i[2],i[3]);
        }

        for(double[] section : sections){
            gc.strokeLine(section[0], section[1], section[2], section[3]);
        }
        sections.clear();

        gc.setFill(Color.RED);
        for(double[] dot : dots){
            gc.fillOval(dot[0], dot[1], dot[2], dot[3]);
        }
        gc.setFill(Color.BLACK);
        dots.clear();
    }

    public ArrayList<double[]> drawMiniMap(double playerAngle) {
        ArrayList<double[]> rays = new ArrayList<>();

        double currentX = player.getCurrentX();
        double currentY = player.getCurrentY();

        double fovAngleDeg = 90;
        int rayCount = (int)(fovAngleDeg * 5);

        double fovAngleRad = Math.toRadians(fovAngleDeg);
        double halfFovRad = fovAngleRad / 2.0;

        for (int i = 0; i < rayCount; i++) {
            double t = (double) i / (rayCount - 1);

            double screenOffset = (t * 2.0 - 1.0) * Math.tan(halfFovRad);
            double angle = playerAngle + Math.atan(screenOffset);

            double dirX = Math.cos(angle);
            double dirY = Math.sin(angle);

            double[] edgePoint = findEdgeIntersection(currentX, currentY, dirX, dirY,
                    width, height);

            double[] collisionPoint = findClosestWallCollision(currentX, currentY, edgePoint[0], edgePoint[1]);

            double rayX = collisionPoint != null ? collisionPoint[0] : edgePoint[0];
            double rayY = collisionPoint != null ? collisionPoint[1] : edgePoint[1];

            sections.add(new double[]{currentX,currentY,rayX,rayY});

            if(collisionPoint != null){
                double distance = Math.hypot(collisionPoint[0] - currentX, collisionPoint[1] - currentY);
                rays.add(new double[]{distance, angle});
                dots.add(new double[]{collisionPoint[0] - 4, collisionPoint[1] - 4, 8, 8});
            }
        }

        return rays;
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

    private double[] findClosestWallCollision(double startX, double startY,
                                              double edgePointX, double edgePointY){
        double minDist = 1e9;
        double[] minRay = null;
        for(int[] i : map){
            //отрезок
            double[] A = new double[]{i[0],i[1]};
            double[] B = new double[]{i[2],i[3]};

            //луч
            double[] C = new double[]{startX, startY};
            double[] D = new double[]{edgePointX, edgePointY};


            double rNumerator = (B[0] - A[0]) * (C[1] - A[1]) - (C[0] - A[0]) * (B[1] - A[1]);
            double sNumerator = (A[0] - C[0]) * (D[1] - C[1]) - (D[0] - C[0]) * (A[1] - C[1]);

            double denominator = (D[0] - C[0]) * (B[1] - A[1]) - (B[0] - A[0]) * (D[1] - C[1]);

            if(denominator != 0){
                double r = rNumerator / denominator;
                double s = sNumerator / denominator;

                if(s >= 0 && s <= 1 && r >= 0){
                    double[] P = new double[2];
                    P[0] = s * (B[0] - A[0]) + A[0];
                    P[1] = s * (B[1] - A[1]) + A[1];

                    double dist = Math.hypot(startX - P[0], startY - P[1]);
                    if(dist < minDist){
                        minDist = dist;
                        minRay = new double[]{P[0], P[1]};
                    }
                }
            }
        }

        return minRay;
    }
}