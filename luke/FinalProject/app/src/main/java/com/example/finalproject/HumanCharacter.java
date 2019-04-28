package com.example.finalproject;

import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.util.ArrayList;

public class HumanCharacter extends Character {
    boolean isUserTurn;
    ArrayList<String> battleChoices = new ArrayList<String>();
    ListView listView;
    ArrayAdapter<String> adapter;
    public LinearLayout layout;
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//ll.addView(myButton, lp);

    public HumanCharacter(int health, int level, int attack, int defense, int experience){
        super(health, level, attack, defense, experience);
    }

    @Override
    BattleAction battleTurn() {
        isUserTurn = true;
        layout.addView(listView, lp);


        battleChoices.add("attack!");
        battleChoices.add("item!");
//
//        adapter = new ArrayAdapter<String>(layout,android.R.layout.simple_list_item_1, battleChoices);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(messageClickedHandler);

        while (isUserTurn){


        }

        return null;
    }
}
