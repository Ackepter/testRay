package com.project.testray;

public class Enemy {
    public enum State { IDLE, WALK, ATTACK, DEATH }

    private State state = State.IDLE;
    private long lastFrameTime = 0;
    private int currentFrame = 0;

    private static final int[] FRAME_COUNTS = {1, 4, 2, 5};
    private static final long FRAME_DURATION_NS = 120_000_000L;

    public State getState() { return state; }
    public void setState(State s) {
        if (this.state != s) {
            this.state = s;
            currentFrame = 0;
        }
    }

    public int getCurrentFrame() { return currentFrame; }

    public void updateAnimation(long now) {
        if (now - lastFrameTime > FRAME_DURATION_NS) {
            lastFrameTime = now;
            int maxFrames = FRAME_COUNTS[state.ordinal()];
            if (state == State.DEATH) {
                currentFrame = Math.min(currentFrame + 1, maxFrames - 1);
            } else {
                currentFrame = (currentFrame + 1) % maxFrames;
            }
        }
    }

    private final double maxEnemyWalkSpeed = 100;
    public double getMaxEnemyWalkSpeed(){
        return maxEnemyWalkSpeed;
    }

    private final double maxEnemyRunSpeed = 150;
    public double getMaxEnemyRunSpeed(){
        return maxEnemyRunSpeed;
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

    public Enemy(double width, double height, double startX, double startY){
        this.width = width;
        this.height = height;

        this.startY = startY;
        this.startX = startX;

        currentX = startX;
        currentY = startY;
    }
}
