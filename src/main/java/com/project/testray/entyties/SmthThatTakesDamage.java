package com.project.testray.entyties;

public abstract class SmthThatTakesDamage {
    public enum AliveStates { ALIVE, DEAD }

    protected AliveStates aliveState = AliveStates.ALIVE;
    public AliveStates getCurrentState(){ return aliveState; }

    private final double maxHp;
    private double hp;
    public double getHp() { return hp; }
    public void getDamage(double damage){
        if(damage < 0) damage *= -1.0;

        if(hp - damage >= 0.0) hp = hp - damage;
        else{
            hp = 0;
            aliveState = AliveStates.DEAD;
        }
    }
    public void getHeal(double heal){
        if(heal < 0) heal *= -1.0;

        hp = Math.min(hp + heal, maxHp);
    }

    public SmthThatTakesDamage(double maxHp) {
        this.maxHp = maxHp;
        this.hp = maxHp;
    }
}
