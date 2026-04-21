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

    public void drawObjects(ArrayList<double[]> rays){
        currentRay = rays;
        gc.setLineWidth(1.0);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // очистка всего холста

        int screenWidth = 1600;
        int screenHeight = 900;
        double fov = Math.toRadians(130); // должно совпадать с MiniMap
        double wallHeightConstant = screenHeight * 50; // подберите под нужный масштаб

        // Рисуем каждый луч как вертикальную полоску
        for (int i = 0; i < rays.size(); i++) {
            double[] ray = rays.get(i);
            double distance = ray[3]; // расстояние до стены
            double dirX = ray[2];     // направление луча

            // Фиш-ай коррекция: компенсируем искажение по краям FOV
            double rayAngle = Math.acos(dirX); // угол между лучом и осью X
            double centerAngle = Math.acos(Math.cos(rayAngle)); // упрощённо
            // Более точный способ: храните угол луча относительно центра при генерации
            // Но для начала можно использовать приближение:
            double correctedDistance = distance; // пока без коррекции, можно добавить позже

            // Высота стены обратно пропорциональна расстоянию
            int wallHeight = (int)(wallHeightConstant / correctedDistance);
            wallHeight = Math.min(wallHeight, screenHeight); // ограничиваем

            // Позиция по X: просто распределяем лучи равномерно по ширине экрана
            double xRect = (double) i / rays.size() * screenWidth;

            // Центрируем стену по вертикали
            double yTop = (screenHeight - wallHeight) / 2.0;

            // Рисуем полоску стены
            gc.setFill(Color.GRAY);
            gc.fillRect(xRect, yTop, screenWidth / rays.size() + 1, wallHeight);
        }
    }
}
