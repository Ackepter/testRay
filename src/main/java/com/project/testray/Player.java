package com.project.testray;

public class Player {
    private final double width;
    private final double height;

    private final double startX;
    public double getStartX(){
        return startX;
    }

    private final double startY;
    public double getstartY(){
        return startY;
    }

    private double currentX;
    public double getCurrentX(){
        return currentX;
    }
    public void setCurrentX(double value){
        if(value >= 0 && value <= width){
            currentX = value;
        }
    }

    private double currentY;
    public double getCurrentY(){
        return currentY;
    }
    public void setCurrentY(double value){
        if(value >= 0 && value <= height){
            currentY = value;
        }
    }

    public Player(double width, double height){
        this.width = width;
        this.height = height;

        startY = 300;
        startX = 100;

        currentX = startX;
        currentY = startY;
    }


}
