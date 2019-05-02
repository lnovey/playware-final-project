package com.example.finalproject;

import android.widget.Button;
import android.widget.LinearLayout;



//Todo

//get numfought and current level working together, >>>>
// ^don't jump into new battle when old one is done, >>>>
// tell user they cant move on when they go to 20 20 >>>>
//last ai implement >>>>
//make attack-def not go negative >>>>
// check coordinate layout (is it x,y on both the you are and the power up gen?) >>>>
//while fight is processing have something on the screen telling the user to wait >>>>
//update enemy generation >>>>
//come up with some creative names and sayings>> add these to screen
//update battle activity xml
//update attack values on screen (same as human characters) >>>>
//modify powerup gen for final level(rishav)
//implement user death and \/ >>>>
//^implement more clear description of in between levels ^

//get leveling up working and test it!
//bulky boy or destructive dude or genuine guy or sketchy specimen?
//add tile input to the battle



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
