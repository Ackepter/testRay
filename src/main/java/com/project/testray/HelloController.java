package com.project.testray;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    public Canvas canvas;

    public MyCanvas workWithCanvas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        workWithCanvas = new MyCanvas(canvas);
        workWithCanvas.initialize();
        workWithCanvas.drawBorders();

        canvas.setOnMouseMoved(event -> {
            double xNode   = event.getX();
            double yNode   = event.getY();

            workWithCanvas.drawRay(xNode, yNode);
        });
    }
}
