package com.project.testray;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;


public class PlayerView {
    Canvas canvas;
    GraphicsContext gc;

    public ArrayList<double[]> currentRay;

    private static final int TEX  = 64;
    private static final int SW   = 1600;
    private static final int SH   = 900;

    private final int[][][] wallTex;
    private final int[][]   floorTex;
    private final int[][]   ceilTex;

    private final WritableImage fb;
    private final PixelWriter pw;

    private final Textures textures = new Textures();

    public PlayerView(Canvas canvas){
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();

        fb = new WritableImage(SW, SH);
        pw = fb.getPixelWriter();


        wallTex  = new int[][][]{ makeBrick(), makeStone() };
        floorTex = makeFloor();
        ceilTex  = makeCeil();
    }

    private int[][] makeBrick() {
        Image img = textures.getBrickImage();
        PixelReader pr = img.getPixelReader();
        int[][] t = new int[TEX][TEX];
        for (int y = 0; y < TEX; y++)
            for (int x = 0; x < TEX; x++)
                t[y][x] = pr.getArgb(x, y);
        return t;
    }

    private int[][] makeStone() {
        Image img = textures.getStoneImage();
        PixelReader pr = img.getPixelReader();
        int[][] t = new int[TEX][TEX];
        for (int y = 0; y < TEX; y++)
            for (int x = 0; x < TEX; x++)
                t[y][x] = pr.getArgb(x, y);
        return t;
    }

    private int[][] makeFloor() {
        Image img = textures.getFloorImage();
        PixelReader pr = img.getPixelReader();
        int[][] t = new int[TEX][TEX];
        for (int y = 0; y < TEX; y++)
            for (int x = 0; x < TEX; x++)
                t[y][x] = pr.getArgb(x, y);
        return t;
    }

    private int[][] makeCeil() {
        Image img = textures.getCeilImage();
        PixelReader pr = img.getPixelReader();
        int[][] t = new int[TEX][TEX];
        for (int y = 0; y < TEX; y++)
            for (int x = 0; x < TEX; x++)
                t[y][x] = pr.getArgb(x, y);
        return t;
    }

    private int rgb(int r, int g, int b) {
        return 0xFF000000
                | (Math.max(0, Math.min(255, r)) << 16)
                | (Math.max(0, Math.min(255, g)) << 8)
                |  Math.max(0, Math.min(255, b));
    }

    private int darken(int c, double f) {
        return rgb((int)(((c>>16)&0xFF)*f),
                (int)(((c>> 8)&0xFF)*f),
                (int)(( c     &0xFF)*f));
    }

    public void drawObjects(ArrayList<double[]> rays,
                            double playerAngle, double playerX, double playerY) {
        currentRay = rays;

        double dirX   =  Math.cos(playerAngle);
        double dirY   =  Math.sin(playerAngle);
        double planeX = -Math.sin(playerAngle);
        double planeY =  Math.cos(playerAngle);

        for (int y = SH/2 + 1; y < SH; y++) {

            double rowDist = (SH * 45.0) / (2.0 * (y - SH / 2.0));
            double shade   = Math.max(0.25, Math.min(1.0, 120.0 / rowDist));

            double stepX = rowDist * 2.0 * planeX / SW;
            double stepY = rowDist * 2.0 * planeY / SW;
            double fx = playerX + rowDist * (dirX - planeX);
            double fy = playerY + rowDist * (dirY - planeY);

            for (int x = 0; x < SW; x++) {
                int tx = (int) Math.floor(fx) & (TEX - 1);
                int ty = (int) Math.floor(fy) & (TEX - 1);

                pw.setArgb(x, y,        darken(floorTex[ty][tx], shade));
                pw.setArgb(x, SH-y-1,   darken(ceilTex [ty][tx], shade * 0.55));

                fx += stepX;
                fy += stepY;
            }
        }

        int mid = darken(ceilTex[0][0], 0.55);
        for (int x = 0; x < SW; x++) pw.setArgb(x, SH/2, mid);

        if (!rays.isEmpty()) {
            double stripW          = (double) SW / rays.size() + 1.0;
            double wallHeightConst = SH * 45.0;

            for (int i = 0; i < rays.size(); i++) {
                double[] ray    = rays.get(i);
                double rawDist  = ray[0];
                double rayAngle = ray[1];
                double texU     = ray.length > 2 ? ray[2] : 0.0;
                int textureIndex = (int)ray[4];

                double diff = rayAngle - playerAngle;
                while (diff >  Math.PI) diff -= 2*Math.PI;
                while (diff < -Math.PI) diff += 2*Math.PI;

                double dist = Math.max(1.0, rawDist * Math.cos(diff));
                int wallH   = (int) Math.min(wallHeightConst / dist, SH * 2.0);

                int xStart = (int)((double) i / rays.size() * SW);
                int xEnd   = Math.min(SW, (int)(xStart + stripW));
                int yTop   = (SH - wallH) / 2;

                int[][] tex  = wallTex[textureIndex % wallTex.length];
                int     texX = (int)(texU * TEX) & (TEX - 1);
                double  shade = Math.max(0.15, Math.min(1.0, 250.0 / dist));

                for (int sx = xStart; sx < xEnd; sx++) {
                    for (int sy = Math.max(0, yTop); sy < Math.min(SH, yTop + wallH); sy++) {
                        int texY = (int)((double)(sy - yTop) / wallH * TEX);
                        texY = Math.max(0, Math.min(TEX-1, texY));
                        pw.setArgb(sx, sy, darken(tex[texY][texX], shade));
                    }
                }
            }
        }

        gc.drawImage(fb, 0, 0);
    }

}
