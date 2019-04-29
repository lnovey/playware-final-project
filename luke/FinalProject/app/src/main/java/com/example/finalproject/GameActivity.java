package com.example.finalproject;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.OnAntEventListener;

import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.MotoConnection;

public class GameActivity extends AppCompatActivity implements OnAntEventListener
{
    MotoConnection connection = MotoConnection.getInstance();

    TZP_Game tzp_object = new TZP_Game(); // Creating the game object

    Point character_location = new Point(tzp_object.character_start_x, tzp_object.character_start_y); // Stores the location of our character

    //the state of the game loop
    //0 = in main overworld, walking around
    //1 = encountered battle
    // 2 = in battle
    private int currState;

    //boolean to run game loop
    private boolean isGameRunning;

    // Initial values for the character's vital signs
    int xp = 5;
    int health = 5;
    int attack = 5;
    int defence = 5;

    // Boosts for character vitals
    int xp_boost = 0;
    int health_boost = 0;

    String axis = "\0";

    // Textview variables for showing the x and y locations of our character
    TextView update_position_x;// = findViewById(R.id.x_position);
    TextView update_position_y;// = findViewById(R.id.y_position);

    //creating buttons for simulation use
    public Button leftButton, rightButton, upButton, downButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Connecting to the Moto tiles
        connection.startMotoConnection(this);
        connection.setDeviceId(4);
        connection.registerListener(this);

        // Starting our game type
        tzp_object.setSelectedGameType(0);
        tzp_object.startGame();

        //assignning textviews
        update_position_x = findViewById(R.id.x_position);
        update_position_y = findViewById(R.id.y_position);

        // Textview variables for the character's vital signs
        final TextView display_xp = findViewById(R.id.xp_value);
        TextView display_health = findViewById(R.id.health_value);
        TextView display_attack = findViewById(R.id.attack_value);
        TextView display_defence= findViewById(R.id.defence_value);

        // Button variables for moving the character when no tiles are available
        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);

        // Showing the initial location of our character on the screen
        update_position_x.setText(toString().valueOf(character_location.x));
        update_position_y.setText(toString().valueOf(character_location.y));

        // Showing the initial vital signs
        display_xp.setText(toString().valueOf(xp));
        display_health.setText(toString().valueOf(health));
        display_attack.setText(toString().valueOf(attack));
        display_defence.setText(toString().valueOf(defence));


        leftButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
             nx();
            }

        } );

        rightButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                px();
            }

        } );

        upButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                py();
            }

        } );

        downButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ny();
            }

        } );

        tzp_object.setOnGameEventListener(new Game.OnGameEventListener()
        {
            @Override
            public void onGameTimerEvent(int i)
            {

            }

            // Used to update the location of the character on the screen
            @Override
            public void onGameScoreEvent(int step, int player_index)
            {
                switch (axis)
                {
                    case "px": // Movement along positive X axis
                        px();
                        break;

                    case "nx": // Movement along negative X axis
                        nx(); //moved this method to it's own thing for modularity
                        break;

                    case "py": // Movement along positive Y axis
                        py();
                        break;

                    case "ny": // Movement along negative Y axis
                        ny();
                        break;
                }
            }

            @Override
            public void onGameStopEvent()
            {

            }

            @Override
            public void onSetupMessage(String s)
            {

            }

            @Override
            public void onGameMessage(String s)
            {
                axis = s;
            }

            @Override
            public void onSetupEnd()
            {

            }
        });

        // Displaying each colour for a certain period of time (default - 3000 ms)
        Thread my_thread = new Thread()
        {
            int i = 5; // Loop variable
            @Override
            public void run()
            {
                try
                {
                    while (i >= 0)
                    {
                        Thread.sleep(3000);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (i >= 0)
                                {
                                    connection.setAllTilesIdle(AntData.LED_COLOR_OFF);
                                    tzp_object.tile_controller();
                                }
                                i--;
                            }
                        });
                    }
                }
                catch (InterruptedException e)
                {

                }
            }
        };


        Thread mainGameLoop = new Thread(){

            @Override
            public void run()
            {
                try {
                    while (isGameRunning){

                        switch (currState) {
                            case 0: // walking in overworld
                                overworldUpdate();
                                System.out.println("curr pos x:" +character_location.x +
                                        "y:" + character_location.y);

                            break;
                            case 1: // found a fight
                                isGameRunning = false;
                            break;
                            case 2:
                            break;
                            case 3:
                            break;
                            default:
                            break;
                        }

                        Thread.sleep(1500); //wait 1.5 seconds each loop
                    }

                }
                catch (InterruptedException e) {

                }
            }
        };
        currState = 0;
        isGameRunning = true;

        my_thread.start();
        mainGameLoop.start();

        System.out.println("out of loop");


        //tzp_object.tile_controller();
    } //end onCreate

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        connection.stopMotoConnection();
        connection.unregisterListener(this);
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        connection.registerListener(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public void onMessageReceived(byte[] our_message, long l)
    {
        tzp_object.addEvent(our_message);
    }

    @Override
    public void onAntServiceConnected()
    {
        connection.setAllTilesToInit();
    }

    @Override
    public void onNumbersOfTilesConnected(final int i)
    {
        
    }

    private void overworldUpdate(){ // gets called everytick while in currState = 0
        if (character_location.x == 2 && character_location.y == 3){ // if we find a boss
            enterBattle();
        }
        System.out.println("loop");
    }

    private void enterBattle(){
        currState = 1; //currstate1 = battle, this might not be necessary
        Intent start_game_intent = new Intent(GameActivity.this, BattleActivity.class);
        startActivity(start_game_intent);

        System.out.println("back from battle, need to update user xp or death\nand return to game loop");

    }


    private void nx(){ //updates user location neg x axis
        character_location.x -= 1;
        if (character_location.x < tzp_object.lower_bound) // Condition to keep the character in the bounds of our world
        {
            character_location.x++;
        }
        GameActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                update_position_x.setText(toString().valueOf(character_location.x));
            }
        });
    }

    private void px(){ //updates user location pos x axis
        character_location.x += 1;
        if (character_location.x > tzp_object.upper_bound) // Condition to keep the character in the bounds of our world
        {
            character_location.x--;
        }
        GameActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                update_position_x.setText(toString().valueOf(character_location.x));
            }
        });
    }

    private void ny(){//updates user location neg y axis
        character_location.y -= 1;
        if (character_location.y < tzp_object.lower_bound) // Condition to keep the character in the bounds of our world
        {
            character_location.y++;
        }
        GameActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                update_position_y.setText(toString().valueOf(character_location.y));
            }
        });
    }

    private void py() {//updates user location pos y axis
        character_location.y += 1;
        if (character_location.y > tzp_object.upper_bound) // Condition to keep the character in the bounds of our world
        {
            character_location.y--;
        }
        GameActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                update_position_y.setText(toString().valueOf(character_location.y));
            }
        });
    }

}