package com.example.finalproject;

import android.app.Activity;
import android.content.Intent;
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
    Button attack_button, heal_button;
    TextView health_value, attack_value,
            defence_value, enemy_attack, enemy_health, trash, enemy_health_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        //tells our old activity to wait until result is 7

        attack_button = findViewById(R.id.attack_button);
        heal_button = findViewById(R.id.heal_button);
        health_value = findViewById(R.id.health_value);
        attack_value = findViewById(R.id.attack_value);
        defence_value = findViewById(R.id.defence_value);
        enemy_attack = findViewById(R.id.enemy_attack);
        enemy_health = findViewById(R.id.enemy_health);
        trash = findViewById(R.id.trash);
        enemy_health_value = findViewById(R.id.enemy_health_value);


        //ai.weaponAttack = 10;
        //user.weaponAttack = 10;
        enemy = getIntent().getParcelableExtra("enemyFighter");
        player = getIntent().getParcelableExtra("playerFighter");
        //testBattle = new Battle();
        //testBattle.layout = this.layout;
        //user.layout = this.layout;

        BattleActivity.this.setupBattleTest();

        System.out.println("health:" + player.health + "attack:" + player.attack +
                "defense:" + player.defense + "xp:" + player.experience);


        System.out.println("inside the battle activity: " + player.health);

    }

    private void setupBattleTest() {
        health_value.setText("User health" + BattleActivity.this.player.health);
        enemy_health_value.setText("Enemy health" + enemy.health);

        battleChoices.add("attack");
        battleChoices.add("item");
        battleChoices.add("heal");
        isFight = true;

        //adapter = new ArrayAdapter<String>(BattleActivity.this, android.R.layout.simple_list_item_1, battleChoices);
        //listView = (ListView) findViewById(R.id.listView);

        //listView.setAdapter(adapter);

        attack_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {                int tempAttack = 0;

                //1/5 of the time print something funny and gain extra 20% attack
                if (Math.random() < .2){
                    System.out.println("You really poked him hard");
                    tempAttack = 2;
                }

                if (enemy.defense < BattleActivity.this.player.attack + tempAttack +
                        BattleActivity.this.player.weaponAttack) {
                    // if an attack has enough power to deal damage

                    enemy.health = enemy.health - (BattleActivity.this.player.attack +
                            BattleActivity.this.player.weaponAttack + tempAttack - enemy.defense);
                } else {
                    //todo
                    // print that the user isn't strong enough to harm
                    System.out.println("you cant hurt this guy");
                }
                isUserDone = true;
                battleState = 1;
                startBattleTest(enemy);

            }

        } );

        heal_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int tempAttack = 0;
                if (Math.random() < .2){
                    System.out.println("You're a wizard Harry");
                    tempAttack = BattleActivity.this.player.health/10;
                }

                BattleActivity.this.player.health += 10 + tempAttack;
                isUserDone = true;
                battleState = 1;
                startBattleTest(enemy);

            }

        } );

        System.out.println("about to start battle");
        startBattleTest(enemy);

    }

    private void startBattleTest(AICharacter enemy) {
        int tempAttack = 0;

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

                            if (Math.random() < .11){ //the enemy gets a boosted attack
                                System.out.println("Ouch, someone's been eating their spinach");
                                tempAttack = 2;
                            }

                            if (BattleActivity.this.player.defense < enemy.attack + enemy.weaponAttack + tempAttack) {
                                //if the enemy is strong enough to deal damage

                                BattleActivity.this.player.health = BattleActivity.this.player.health
                                        - (enemy.attack + enemy.weaponAttack + tempAttack - BattleActivity.this.player.defense);
                            } else {
                                //todo
                                //print that the enemy can't even harm you
                            }

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


                health_value.setText("User health" + BattleActivity.this.player.health);
                enemy_health_value.setText("Enemy health" + enemy.health);
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

        //returning info about who won the fight!
        Intent resultIntent = new Intent();

        if (player.health <= 0) {
            System.out.println("game over, returning home");
            resultIntent.putExtra("winner", 2);
            resultIntent.putExtra("health", 0);
        } else {
            System.out.println("you won the fight,\n gaining xp\n go to next level!");
            resultIntent.putExtra("winner", 1);
            resultIntent.putExtra("health", player.health);
        }

        setResult(Activity.RESULT_OK, resultIntent);
        this.finish();
        return;
    }

}
