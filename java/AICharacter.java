package com.example.finalproject;

public class AICharacter extends Character {
    public AICharacter(int health, int level, int attack, int defense, int experience) {
        super(health, level, attack, defense, experience);
    }

    @Override
    BattleAction battleTurn() {
        BattleAction returner = new BattleAction();
        if (this.health <= 30){
            returner.type = 3;
        } else {
            returner.type = 1;
        }
        return returner;

    }

}
