package com.project.testray;

public class Player extends SmthThatTakesDamage {
    private final double maxPlayerWalkSpeed = 100;
    public double getMaxPlayerWalkSpeed(){
        return maxPlayerWalkSpeed;
    }

    private final double maxPlayerRunSpeed = 150;
    public double getMaxPlayerRunSpeed(){
        return maxPlayerRunSpeed;
    }


    private boolean isRunning = false;
    public boolean isRunning(){
        return isRunning;
    }
    public void walk(){
        isRunning = false;
    }
    public void run(){
        isRunning = true;
    }

    private final double width;
    private final double height;

    private final double startX;
    public double getStartX(){
        return startX;
    }

    private final double startY;
    public double getStartY(){
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

        startY = 75;
        startX = 75;

        currentX = startX;
        currentY = startY;
    }

}
