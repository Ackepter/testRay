package com.project.testray;

public class Enemy extends SmthThatTakesDamage{
    public enum EnemyAnimationState { IDLE, WALK, ATTACK, DEATH }

    private EnemyAnimationState state;
    private long lastFrameTime = 0;
    private int currentFrame = 0;

    private static final int[] FRAME_COUNTS = {1, 4, 2, 5};
    private static final long FRAME_DURATION_NS = 120_000_000L;

    public EnemyAnimationState getState() { return state; }
    public void setState(EnemyAnimationState s) {
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
            if (state == EnemyAnimationState.DEATH) {
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

    private static final double SEE_BORDER = 300;
    private static final double FOLLOWING_BORDER = 80.0;
    private static final double STEP = 0.9;
    public void followToPlayer(double distance, double angle){
        if(distance >= SEE_BORDER) state = EnemyAnimationState.IDLE;
        else if(distance > FOLLOWING_BORDER){
            state = EnemyAnimationState.WALK;

            double dirX = Math.cos(angle) * STEP;
            double dirY = Math.sin(angle) * STEP;

            setCurrentX(getCurrentX() + dirX);
            setCurrentY(getCurrentY() + dirY);
        }
        else{
            state = EnemyAnimationState.ATTACK;
        }
    }

    public Enemy(double width, double height, double startX, double startY){
        super(150);
        this.width = width;
        this.height = height;

        this.startY = startY;
        this.startX = startX;

        currentX = startX;
        currentY = startY;

        state = EnemyAnimationState.IDLE;
    }
}
