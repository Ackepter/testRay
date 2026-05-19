package com.project.testray;

public abstract class SmthThatTakesDamage {
    public enum AliveStates { ALIVE, DEAD }

    private AliveStates state = AliveStates.ALIVE;
    public AliveStates getCurrentState(){ return state; }

    private double hp = 0;
    public double getHp() { return hp; }
    public void getDamage(double damage){
        if(damage < 0) damage *= -1.0;

        if(hp - damage >= 0.0) hp = hp - damage;
        else{
            hp = 0;
            state = AliveStates.DEAD;
        }
    };
}
