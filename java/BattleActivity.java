package com.example.finalproject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
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
    //0 means waiting for user input
    //1 means continuing in "game loop"
    //2 means fight has finished
    int battleState = 0;
    boolean isThreadOn = true;
    boolean isFight, isUserDone;
    LinearLayout layout;
    Battle testBattle;
    ArrayList<String> battleChoices = new ArrayList<String>();
    //HumanCharacter user = new HumanCharacter(110, 1,
     //       10, 10, 0);
    //HumanCharacter enemy = new HumanCharacter(100, 1,
     //       10, 10, 0);
    //AICharacter ai = new AICharacter(100, 1,
     //       10, 10, 0);
    AICharacter enemy;
    HumanCharacter player;
    ListView listView;
    ArrayAdapter<String> adapter;
    TextView userHealth;
    TextView enemyHealth;
    //= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, temp);

    public Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        //tells our old activity to wait until result is 7


        startFight = findViewById(R.id.startFight);
        layout = (LinearLayout) findViewById(R.id.layout);
        userHealth = findViewById(R.id.userHealth);
        enemyHealth = findViewById(R.id.enemyHealth);

        //ai.weaponAttack = 10;
        //user.weaponAttack = 10;
        enemy = getIntent().getParcelableExtra("enemyFighter");
        player = getIntent().getParcelableExtra("playerFighter");
        //testBattle = new Battle();
        //testBattle.layout = this.layout;
        //user.layout = this.layout;


        System.out.println("inside the battle activity: " + player.health);
        startFight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BattleActivity.this.setupBattleTest();
                //BattleActivity.this.startBattle();

            }
        });

        // we just run this so that the activity keeps running until fight is over
        /*Thread fightLoop = new Thread() {

            @Override
            public void run() {

                while (isThreadOn){
                    System.out.println("in the thread loop");

                    if (player.health <= 0 || enemy.health <= 0){
                        isThreadOn = false;
                    }

                    try {
                        currentThread().sleep(5000);
                    } catch (InterruptedException e) {

                    }
                }
                System.out.println("battle over in thread");

            }

        };

        fightLoop.start();
        */

    }

    private void setupBattleTest() {
        battleChoices.add("attack");
        battleChoices.add("item");
        battleChoices.add("heal");
        isFight = true;

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, battleChoices);
        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                switch (position) {
                    case 0: // attack
                        enemy.health = enemy.health - (BattleActivity.this.player.attack +
                                BattleActivity.this.player.weaponAttack - enemy.defense);
                        break;
                    case 1: //item
                        //need to make item menu
                        enemy.health -= player.attack + 10;
                        break;
                    case 2://heal
                        BattleActivity.this.player.health += 10;
                        break;
                    default:
                        break;
                }
                isUserDone = true;
                battleState = 1;

                System.out.println(battleChoices.get(position));
                startBattleTest(enemy);
            }
        });

        System.out.println("about to start battle");
        startBattleTest(enemy);

    }

    private void startBattleTest(AICharacter enemy) {

        switch (battleState) {
            case 0:
                //do nothing, we are waiting for using input
                break;
            case 1:
                //we have user input so lets do some processing
                // need to check if fight is over and move on, or continue fight
                BattleAction action;
                battleState = 0;

                if (enemy.health <= 0) {
                    System.out.println("you win");
                    isFight = false;
                    battleState = 2;
                }
                if (isFight) {
                    action = enemy.battleTurn();
                    switch (action.type) {
                        case 1://ai attack
                            BattleActivity.this.player.health = BattleActivity.this.player.health
                                    - (enemy.attack + enemy.weaponAttack - BattleActivity.this.player.defense);
                            break;
                        case 2:
                            break;
                        case 3: //heal
                            enemy.health += action.info[0];
                            break;
                        default:
                            break;
                    }
                    isUserDone = false;

                }


                userHealth.setText("User health" + BattleActivity.this.player.health);
                enemyHealth.setText("Enemy health" + enemy.health);
                if (BattleActivity.this.player.health <= 0) {
                    System.out.println("you lose");
                    isFight = false;
                    battleState = 2;
                }

                startBattleTest(enemy);

                break;
            case 2:
                //someone is dead so fight is over

                endBattle();


                break;
            default:
                // this should not happen
                System.out.println("weird case statement");
                break;
        }

        //System.out.println("after case statement!");

    }

    private void endBattle() {
        // do something here to return to rishav loop

        //need to remove my gui stuff so user can not click on it more


        if (player.health <= 0) {
            System.out.println("game over, returning home");
        } else {
            System.out.println("you won the fight,\n gaining xp\n go to next level!");
        }

        this.finish();
        return;
    }

}
