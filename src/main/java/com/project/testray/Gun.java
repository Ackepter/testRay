package com.project.testray;

public class Gun {
    private static final double DAMAGE = 50;
    public double getDamage() { return DAMAGE; }

    public enum GunAnimationState { PEACE, SHOOT }

    private GunAnimationState state;
    private long lastFrameTime = 0;
    private int currentFrame = 0;

    private static final int FRAME_COUNTS = 4;
    private static final long FRAME_DURATION_NS = 120_000_000L;

    public GunAnimationState getState() { return state; }
    public void setState(GunAnimationState s) {
        if (this.state != s) {
            this.state = s;
            currentFrame = 0;
        }
    }
    public int getCurrentFrame() { return currentFrame; }

    public void updateAnimation(long now) {
        if (now - lastFrameTime > FRAME_DURATION_NS) {
            lastFrameTime = now;
            if (state == GunAnimationState.PEACE) {
                currentFrame = Math.min(currentFrame + 1, FRAME_COUNTS - 1);
            }
            else currentFrame = (currentFrame + 1) % FRAME_COUNTS;
        }
    }
}
