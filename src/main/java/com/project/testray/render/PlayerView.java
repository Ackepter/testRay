package com.project.testray.render;

import com.project.testray.Textures;
import com.project.testray.entyties.Enemy;
import com.project.testray.entyties.Gun;
import com.project.testray.entyties.Player;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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

    private final Font hudFont = Font.font("Monospaced", FontWeight.BOLD, 22);

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
    private final int[] clearBuf = new int[SW];
    private final double[] zBuffer = new double[SW];
    public double getZBufferCenter() {
        return zBuffer[SW / 2];
    }
    private static final javafx.scene.image.PixelFormat<java.nio.IntBuffer> FMT =
            javafx.scene.image.PixelFormat.getIntArgbInstance();

    public void drawObjects(ArrayList<double[]> rays,
                            double playerAngle, double playerX, double playerY,
                            ArrayList<Enemy> enemies, long now, Player player,
                            Gun gun) {

        for (int y = 0; y < SH; y++) {
            pw.setPixels(0, y, SW, 1, FMT, clearBuf, 0, SW);
        }
        java.util.Arrays.fill(zBuffer, Double.MAX_VALUE);

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
                ceilBuf[x]   = darken(ceilTex[ty][tx],  shade * 0.55);
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

        for (int i = 0; i < rays.size(); i++) {
            double[] ray = rays.get(i);
            double rawDist = ray[0];
            double diff = ray[1] - playerAngle;
            while (diff >  Math.PI) diff -= 2 * Math.PI;
            while (diff < -Math.PI) diff += 2 * Math.PI;
            double correctedDist = rawDist * Math.cos(diff);

            int xStart = (int)((double) i / rays.size() * SW);
            int xEnd   = Math.min(SW, (int)(xStart + (double) SW / rays.size() + 1));
            for (int sx = xStart; sx < xEnd; sx++) {
                zBuffer[sx] = correctedDist;
            }
        }

        drawSprites(enemies, playerX, playerY, playerAngle, textures, now);

        gc.drawImage(fb, 0, 0);

        drawGun(gun);
        drawHud(player);
    }

    private void drawGun(Gun gun) {
        Image sprite = textures.getGunSprite(gun.getCurrentFrame());

        double spriteW = sprite.getWidth();
        double spriteH = sprite.getHeight();

        double scale = (SH * 0.45) / spriteH;
        double drawW = spriteW * scale;
        double drawH = spriteH * scale;

        double drawX = (SW - drawW) / 2.0;
        double drawY = SH - drawH;

        gc.setImageSmoothing(false);
        gc.drawImage(sprite, drawX, drawY, drawW, drawH);
        gc.setImageSmoothing(true);
    }

    private void drawHud(Player player) {
        int panelW = 370;
        int panelH = 44;
        int panelX = 44;
        int panelY = SH - panelH - 12;

        gc.setFill(Color.rgb(60, 60, 60));
        gc.fillRect(panelX, panelY, panelW, panelH);

        gc.setStroke(Color.rgb(90, 90, 90));
        gc.setLineWidth(2);
        gc.strokeRect(panelX, panelY, panelW, panelH);

        gc.setStroke(Color.rgb(40, 40, 40));
        gc.setLineWidth(1);
        gc.strokeRect(panelX + 2, panelY + 2, panelW - 4, panelH - 4);

        gc.setFill(Color.RED);
        gc.setFont(hudFont);

        int enemiesAmount = Enemy.getEnemyAmount() - player.getKillsAmount();
        gc.fillText("HP: " + (int)player.getHp() + " Осталось врагов: " + enemiesAmount, panelX + 12, panelY + (double)panelH / 2 + 8);
    }

    private void drawSprites(ArrayList<Enemy> enemies,
                             double playerX, double playerY, double playerAngle,
                             Textures textures, long now) {

        double dirX =  Math.cos(playerAngle);
        double dirY =  Math.sin(playerAngle);

        double planeX = -Math.sin(playerAngle);
        double planeY =  Math.cos(playerAngle);

        enemies.sort((a, b) -> {
            double da = Math.hypot(a.getCurrentX() - playerX, a.getCurrentY() - playerY);
            double db = Math.hypot(b.getCurrentX() - playerX, b.getCurrentY() - playerY);
            return Double.compare(db, da);
        });

        for (Enemy enemy : enemies) {
            enemy.updateAnimation(now);

            double spX = enemy.getCurrentX() - playerX;
            double spY = enemy.getCurrentY() - playerY;

            double invDet = 1.0 / (planeX * dirY - dirX * planeY);
            double tX = invDet * ( dirY * spX - dirX * spY);
            double tY = invDet * (-planeY * spX + planeX * spY);

            if (tY <= 0) continue;

            int spriteScreenX = (int)((double)(SW / 2) * (1 + tX / tY));

            int spriteH = Math.abs((int)((SH * 45.0) / tY));
            int spriteW = spriteH;

            int drawStartY = Math.max(0, (SH - spriteH) / 2);
            int drawEndY   = Math.min(SH, (SH + spriteH) / 2);
            int drawStartX = Math.max(0, spriteScreenX - spriteW / 2);
            int drawEndX   = Math.min(SW, spriteScreenX + spriteW / 2);

            Image sprite = textures.getEnemySprite(enemy.getAnimationState(), enemy.getCurrentFrame());
            PixelReader pr = sprite.getPixelReader();
            int texW = (int) sprite.getWidth();
            int texH = (int) sprite.getHeight();

            double shade = Math.max(0.15, Math.min(1.0, 250.0 / tY));

            for (int sx = drawStartX; sx < drawEndX; sx++) {

                double zAtPixel = sx < SW ? zBuffer[sx] : Double.MAX_VALUE;
                if (tY >= zAtPixel) continue;

                int texX = (int)((sx - (spriteScreenX - spriteW / 2.0))
                        * texW / spriteW);
                texX = Math.max(0, Math.min(texW - 1, texX));

                for (int sy = drawStartY; sy < drawEndY; sy++) {
                    int texY = (int)((sy - (SH - spriteH) / 2.0)
                            * texH / spriteH);
                    texY = Math.max(0, Math.min(texH - 1, texY));

                    int argb = pr.getArgb(texX, texY);

                    if (((argb >> 24) & 0xFF) < 128) continue;

                    pw.setArgb(sx, sy, darken(argb, shade));
                }
            }
        }
    }
}