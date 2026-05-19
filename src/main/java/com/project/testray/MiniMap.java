package com.project.testray;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static com.project.testray.MainController.PLAYER_RADIUS;

public class MiniMap {
    Player player;

    final double width;
    final double height;

    Canvas canvas;
    GraphicsContext gc;

    private final int[][] map;

    private final ArrayList<double[]> sectionsPlayer = new ArrayList<>();
    private final ArrayList<double[]> dotsPlayerCollision = new ArrayList<>();

    private final ArrayList<double[]> sectionsEnemy = new ArrayList<>();
    private final ArrayList<double[]> dotsEnemyCollision = new ArrayList<>();

    public MiniMap(Canvas canvas, double width, double height, Player player, int[][] map){
        this.map = map;

        this.player = player;

        this.width = width;
        this.height = height;

        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
    }

    private void drawWallsAndRays(){
        gc.clearRect(0, 0, width + 5, height + 5);
        gc.setLineWidth(3.0);
        for(int[] i : map){
            gc.strokeLine(i[0],i[1],i[2],i[3]);
        }

        for(double[] section : sectionsPlayer){
            gc.strokeLine(section[0], section[1], section[2], section[3]);
        }
        sectionsPlayer.clear();

        gc.setFill(Color.RED);
        for(double[] dot : dotsPlayerCollision){
            gc.fillOval(dot[0], dot[1], dot[2], dot[3]);
        }
        gc.setFill(Color.BLACK);
        dotsPlayerCollision.clear();
    }

    private void drawEnemiesAndItsRays(){
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.GREEN);
        for(double[] section : sectionsEnemy){
            gc.strokeLine(section[0], section[1], section[2], section[3]);
        }
        sectionsEnemy.clear();

        for(double[] dot : dotsEnemyCollision){
            gc.fillOval(dot[0], dot[1], dot[2], dot[3]);
        }
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        dotsEnemyCollision.clear();
    }

    public void drawMap(){
        drawWallsAndRays();
        drawEnemiesAndItsRays();
    }

    ArrayList<double[]> rays = new ArrayList<>();

    public ArrayList<double[]> drawMiniMap(double playerAngle, ArrayList<Enemy> enemies) {
        rays.clear();

        double currentPlayerX = player.getCurrentX();
        double currentPlayerY = player.getCurrentY();

        double fovAngleDeg = 90;
        int rayCount = (int)(fovAngleDeg * 3);

        double fovAngleRad = Math.toRadians(fovAngleDeg);
        double halfFovRad = fovAngleRad / 2.0;

        for (int i = 0; i < rayCount; i++) {
            double t = (double) i / (rayCount - 1);

            double screenOffset = (t * 2.0 - 1.0) * Math.tan(halfFovRad);
            double angle = playerAngle + Math.atan(screenOffset);

            double dirX = Math.cos(angle);
            double dirY = Math.sin(angle);

            double[] edgePointPlayer = findEdgeIntersection(currentPlayerX, currentPlayerY, dirX, dirY,
                    width, height);

            double[] collisionPointPlayer = findClosestWallCollision(currentPlayerX, currentPlayerY, edgePointPlayer[0], edgePointPlayer[1]);

            double rayX = collisionPointPlayer != null ? collisionPointPlayer[0] : edgePointPlayer[0];
            double rayY = collisionPointPlayer != null ? collisionPointPlayer[1] : edgePointPlayer[1];

            sectionsPlayer.add(new double[]{currentPlayerX,currentPlayerY,rayX,rayY});

            if(collisionPointPlayer != null){
                double distance = Math.hypot(collisionPointPlayer[0] - currentPlayerX, collisionPointPlayer[1] - currentPlayerY);

                int wallIdx = (int) collisionPointPlayer[2];
                int[] wall = map[wallIdx];
                double wdx = wall[2] - wall[0];
                double wdy = wall[3] - wall[1];
                int textureIndex = map[wallIdx][4];

                double hitCord = Math.abs(wdx) > Math.abs(wdy)
                        ? collisionPointPlayer[0]
                        : collisionPointPlayer[1];

                double texU = (((hitCord % 64) + 64) % 64) / 64.0;

                rays.add(new double[]{distance, angle, texU, wallIdx, textureIndex});
                dotsPlayerCollision.add(new double[]{collisionPointPlayer[0] - 4, collisionPointPlayer[1] - 4, 8, 8});
            }
        }

        for(Enemy enemy : enemies) {
            if(enemy == null) continue;

            double currentEnemyX = enemy.getCurrentX();
            double currentEnemyY = enemy.getCurrentY();

            dotsEnemyCollision.add(new double[]{currentEnemyX - 4, currentEnemyY - 4, 8, 8});

            double deltaX = currentPlayerX - currentEnemyX;
            double deltaY = currentPlayerY - currentEnemyY;

            double betweenAngle = Math.atan2(deltaY, deltaX);

            double dirX = Math.cos(betweenAngle);
            double dirY = Math.sin(betweenAngle);

            double[] edgePointEnemy = findEdgeIntersection(currentEnemyX, currentEnemyY, dirX, dirY,
                    width, height);

            double distanceEnemyPlayer = Math.hypot(currentEnemyX - currentPlayerX, currentEnemyY - currentPlayerY);
            double distanceEnemyWall   = Math.hypot(currentEnemyX - edgePointEnemy[0], currentEnemyY - edgePointEnemy[1]);

            double[] collisionPointEnemy = null;
            double finalRayX, finalRayY;

            if (distanceEnemyPlayer < distanceEnemyWall) {
                collisionPointEnemy = findClosestWallCollision(currentEnemyX, currentEnemyY, currentPlayerX, currentPlayerY);

                if (collisionPointEnemy != null) {
                    finalRayX = collisionPointEnemy[0];
                    finalRayY = collisionPointEnemy[1];
                    dotsEnemyCollision.add(new double[]{finalRayX - 4, finalRayY - 4, 8, 8});
                } else {
                    finalRayX = currentPlayerX;
                    finalRayY = currentPlayerY;
                    enemy.followToPlayer(distanceEnemyPlayer, betweenAngle);
                }
            } else {
                collisionPointEnemy = findClosestWallCollision(currentEnemyX, currentEnemyY, edgePointEnemy[0], edgePointEnemy[1]);

                if (collisionPointEnemy != null) {
                    finalRayX = collisionPointEnemy[0];
                    finalRayY = collisionPointEnemy[1];
                    dotsEnemyCollision.add(new double[]{finalRayX - 4, finalRayY - 4, 8, 8});
                } else {
                    finalRayX = edgePointEnemy[0];
                    finalRayY = edgePointEnemy[1];
                    enemy.followToPlayer(distanceEnemyPlayer, betweenAngle);
                }
            }

            sectionsEnemy.add(new double[]{currentEnemyX, currentEnemyY, finalRayX, finalRayY});
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
        for(int idx = 0; idx < map.length; idx++){
            int[] i = map[idx];
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

                if(s >= 0 && s <= 1 && r >= 0 && r <= 1){
                    double[] P = new double[2];
                    P[0] = s * (B[0] - A[0]) + A[0];
                    P[1] = s * (B[1] - A[1]) + A[1];

                    double dist = Math.hypot(startX - P[0], startY - P[1]);
                    if(dist < minDist){
                        minDist = dist;
                        minRay = new double[]{P[0], P[1], idx};
                    }
                }
            }
        }

        return minRay;
    }

    private double[] closestPointOnSegment(double ax, double ay, double bx, double by,
                                           double px, double py) {
        double abx = bx - ax, aby = by - ay;
        double len2 = abx * abx + aby * aby;
        if (len2 == 0) return new double[]{ax, ay};
        double t = ((px - ax) * abx + (py - ay) * aby) / len2;
        t = Math.max(0, Math.min(1, t));
        return new double[]{ax + t * abx, ay + t * aby};
    }

    public double[] resolveCollision(double newX, double newY) {
        for (int[] wall : map) {
            double[] closest = closestPointOnSegment(
                    wall[0], wall[1], wall[2], wall[3], newX, newY
            );
            double dx = newX - closest[0];
            double dy = newY - closest[1];
            double dist = Math.hypot(dx, dy);

            if (dist < PLAYER_RADIUS && dist > 0) {
                double overlap = PLAYER_RADIUS - dist;
                newX += (dx / dist) * overlap;
                newY += (dy / dist) * overlap;
            }
        }
        return new double[]{newX, newY};
    }
}