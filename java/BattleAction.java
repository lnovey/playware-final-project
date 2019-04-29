package com.example.finalproject;

public class BattleAction {
    int type; //1=attack, 2=item use, 3 = heal

    int[] info = new int[5]; //provides info related to the type of action taken
    //ie if type is attack, info is: character attacking, character attacked, damage,
    //if item info is: character using, inventory slot

}
