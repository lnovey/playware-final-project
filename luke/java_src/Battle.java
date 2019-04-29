package com.example.finalproject;

import android.widget.Button;
import android.widget.LinearLayout;


public class Battle {
    //set up some gui
    public LinearLayout layout;
    //Button
    public Character[] fighters;

    public Battle(Character[] fighters){
        this.fighters = fighters;
    }

    public void update(BattleAction action){
        //this is where we do the math!
        switch (action.type) {
            case 1:
                //attack!
                fighters[action.info[1]].health = fighters[action.info[1]].health
                        - (fighters[action.info[0]].attack*fighters[action.info[0]].weaponAttack/
                        fighters[action.info[1]].defense);
            break;
            case 2:
            break;
            default:
            break;
        }

    }


    public void endBattle(Character p1, Character p2){ //either resets human or gives xp

    }
}
