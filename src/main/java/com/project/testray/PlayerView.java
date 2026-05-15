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

    private int darken(int c, double f) {
        int r = (int)(((c >> 16) & 0xFF) * f);
        int g = (int)(((c >>  8) & 0xFF) * f);
        int b = (int)(( c        & 0xFF) * f);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private final int[] rowBuf = new int[SW];
    private final int[] columnBuf = new int[SH * 2];
    private final int[] stripBuf = new int[SH];
    private final int[] ceilBuf = new int[SW];
    private static final javafx.scene.image.PixelFormat<java.nio.IntBuffer> FMT =
            javafx.scene.image.PixelFormat.getIntArgbInstance();

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
                rowBuf[x]    = darken(floorTex[ty][tx], shade);
                ceilBuf[x]   = darken(ceilTex[ty][tx],  shade * 0.55); // ← одновременно
                fx += stepX;
                fy += stepY;
            }
            pw.setPixels(0, y,       SW, 1, FMT, rowBuf,  0, SW);
            pw.setPixels(0, SH-y-1, SW, 1, FMT, ceilBuf,  0, SW);
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

                int[] column = columnBuf;
                for (int row = 0; row < wallH; row++) {
                    int texY = row * TEX / wallH;
                    column[row] = darken(tex[texY][texX], shade);
                }

                int clampTop = Math.max(0, yTop);
                int clampBot = Math.min(SH, yTop + wallH);
                int drawH = clampBot - clampTop;

                for (int sy = clampTop; sy < clampBot; sy++) {
                    int r = sy - yTop;
                    stripBuf[sy] = column[r];
                }

                for (int sx = xStart; sx < xEnd; sx++) {
                    pw.setPixels(sx, clampTop, 1, drawH, FMT, stripBuf, clampTop, 1);
                }
            }
        }

        gc.drawImage(fb, 0, 0);
    }

}
