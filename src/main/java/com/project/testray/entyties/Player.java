package com.project.testray.entyties;

public class Player extends SmthThatTakesDamage {
    public static final double START_X = 75;
    public static final double START_Y = 75;


    private int killsAmount = 0;
    public void setKillsAmount(int newKills){
        if(newKills != killsAmount) killsAmount = newKills;
    }
    public int getKillsAmount() {return killsAmount; }

    private static final double MAX_PLAYER_WALK_SPEED = 150;
    public double getMaxPlayerWalkSpeed(){
        return MAX_PLAYER_WALK_SPEED;
    }

    private static final double MAX_PLAYER_RUN_SPEED = 200;
    public double getMaxPlayerRunSpeed(){
        return MAX_PLAYER_RUN_SPEED;
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
        super(100);
        this.width = width;
        this.height = height;

        currentX = START_X;
        currentY = START_Y;
    }

}
