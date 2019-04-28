package com.example.finalproject;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class BattleActivity extends AppCompatActivity {

    Button startFight;
    boolean isFight;
    LinearLayout layout;
    Battle testBattle;
    ArrayList<String> battleChoices = new ArrayList<String>();
    HumanCharacter user = new HumanCharacter(110, 1,
            10, 10, 0);
    HumanCharacter enemy = new HumanCharacter(100, 1,
            10, 10, 0);
    AICharacter ai = new AICharacter(100, 1,
            10, 10, 0);
    Character[] fighters = new Character[2];
    ListView listView;
    ArrayAdapter<String> adapter;
    TextView userHealth;
    TextView enemyHealth;
    //= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, temp);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        startFight = findViewById(R.id.startFight);
        layout = (LinearLayout)findViewById(R.id.layout);
        userHealth = findViewById(R.id.userHealth);
        enemyHealth = findViewById(R.id.enemyHealth);

        ai.weaponAttack = 10;
        user.weaponAttack = 10;
        //testBattle = new Battle();
        //testBattle.layout = this.layout;
        //user.layout = this.layout;



        startFight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BattleActivity.this.setupBattle();
                //BattleActivity.this.startBattle();

            }
        });


    }

    private void setupBattle(){

        startBattle(user, enemy);
    }


    //for now we use this start battle which creates a list of options for the user
    //there is no flow, everything is based on user clicking an option
    public void startBattle(Character user, Character enemy){



        battleChoices.add("attack");
        battleChoices.add("item");
        battleChoices.add("heal");


        //these are the options for the user

        //maybe everytime this is clicked it calls a method that goes:
        //userturn;compute;enemyturn;compute;
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, battleChoices);
        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                switch (position){
                    case 0: // attack
                        ai.health = ai.health - (BattleActivity.this.user.attack +
                                BattleActivity.this.user.weaponAttack - ai.defense);
                    break;
                    case 1: //item
                        //need to make item menu
                        ai.health -= 40;
                    break;
                    case 2://heal
                        BattleActivity.this.user.health += 50;
                    break;
                    default:
                    break;
                }
                //update from user move
                BattleAction action = ai.battleTurn();
                switch (action.type){
                    case 1://ai attack
                        BattleActivity.this.user.health = BattleActivity.this.user.health
                                - (ai.attack + ai.weaponAttack - BattleActivity.this.user.defense);
                    break;
                    case 2:
                    break;
                    case 3:
                        ai.health += 50;
                    break;
                    default:
                    break;
                }
                //enemy turn
                //update from enemy move
                //repeat
                userHealth.setText("User health" + BattleActivity.this.user.health);
                enemyHealth.setText("Enemy health" + ai.health);
                if (BattleActivity.this.user.health <= 0 ){
                    System.out.println("you lose");
                }
                if (ai.health <= 0 ){
                    System.out.println("you win");
                }

                System.out.println(battleChoices.get(position));
            }
        });

    }




    public void startBattle(Character[] fighters){
        BattleAction action;
        Battle battle = new Battle(fighters);


        while (isFight){
            for (Character each : fighters){
                action = each.battleTurn();
                battle.update(action);
            }
        }

//        isFight = true;
//
//        BattleAction userTurn;
//        BattleAction enemyTurn;
    }
}
