package com.project.testray.entyties;

public class Enemy extends SmthThatTakesDamage {
    private static int enemyAmount = 0;
    public static int getEnemyAmount() {return enemyAmount; }

    private final Player player;

    public enum EnemyAnimationState { IDLE, WALK, ATTACK, DEATH, HURT }

    private EnemyAnimationState animationState;
    private long lastFrameTime = 0;
    private int currentFrame = 0;

    private static final int[] FRAME_COUNTS = {1, 4, 4, 5, 3};
    private static final long FRAME_DURATION_NS = 120_000_000L;

    public EnemyAnimationState getAnimationState() { return animationState; }
    public void setState(EnemyAnimationState s) {
        if (this.animationState != s) {
            this.animationState = s;
            currentFrame = 0;
        }
    }

    public int getCurrentFrame() { return currentFrame; }

    public void updateAnimation(long now) {
        if (now - lastFrameTime > FRAME_DURATION_NS) {
            if(aliveState == AliveStates.DEAD) animationState = EnemyAnimationState.DEATH;
            lastFrameTime = now;

            int maxFrames = FRAME_COUNTS[animationState.ordinal()];

            if (animationState == EnemyAnimationState.DEATH) {
                currentFrame = Math.min(currentFrame + 1, maxFrames - 1);
            }else if (animationState == EnemyAnimationState.HURT) {
                currentFrame++;
                if (currentFrame >= maxFrames) {
                    currentFrame = 0;
                    animationState = EnemyAnimationState.IDLE;
                }
            } else {
                currentFrame = (currentFrame + 1) % maxFrames;
            }
        }
    }

    private static final double MAX_ENEMY_WALK_SPEED = 100;
    public double getMaxEnemyWalkSpeed(){
        return MAX_ENEMY_WALK_SPEED;
    }

    private static final double MAX_ENEMY_RUN_SPEED = 150;
    public double getMaxEnemyRunSpeed(){
        return MAX_ENEMY_RUN_SPEED;
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

    private static final double SEE_BORDER = 300;
    private static final double FOLLOWING_BORDER = 130.0;
    private static final double STEP = 0.9;

    private static final long ATTACK_COOLDOWN_NS = 600_000_000L;
    private long lastAttackTime = 0;

    public void followToPlayer(double distance, double angle, double now){
        if(aliveState == AliveStates.ALIVE){
            if(animationState == EnemyAnimationState.HURT) return;

            if(distance >= SEE_BORDER){
                animationState = EnemyAnimationState.IDLE;
            }
            else if(distance > FOLLOWING_BORDER){
                animationState = EnemyAnimationState.WALK;

                double dirX = Math.cos(angle) * STEP;
                double dirY = Math.sin(angle) * STEP;

                setCurrentX(getCurrentX() + dirX);
                setCurrentY(getCurrentY() + dirY);
            }
            else{
                animationState = EnemyAnimationState.ATTACK;
                if (now - lastAttackTime >= ATTACK_COOLDOWN_NS) {
                    player.getDamage(10);
                    lastAttackTime = (long) now;
                }
            }
        }
    }

    public Enemy(double width, double height, double startX, double startY, Player player){
        super(150);
        this.width = width;
        this.height = height;

        currentX = startX;
        currentY = startY;

        animationState = EnemyAnimationState.IDLE;

        this.player = player;

        enemyAmount++;
    }

    @Override
    public void getDamage(double damage){
        super.getDamage(damage);
        if(aliveState == AliveStates.ALIVE){
            animationState = EnemyAnimationState.HURT;
            currentFrame = 0;
            lastFrameTime = 0;
        }
    }
}
