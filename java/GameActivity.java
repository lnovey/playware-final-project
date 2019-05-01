package com.example.finalproject;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.OnAntEventListener;

import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.MotoConnection;

import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;

public class GameActivity extends AppCompatActivity implements OnAntEventListener
{
    MotoConnection connection = MotoConnection.getInstance();

    TZP_Game tzp_object = new TZP_Game(); // Creating the game object

    //Point character_location = new Point(tzp_object.character_start_x, tzp_object.character_start_y); // Stores the location of our character
    Point character_location = tzp_object.character_location; // Holds the location of the character

    //the state of the game loop
    //0 = in main overworld, walking around
    //1 = encountered battle (and still in it)
    // 2 = finished battle
    private int currState;

    //boolean to run game loop
    private boolean isGameRunning, isBossDead;

    // Array that holds the details about the character's vital signs
    // Index 0 -> XP points
    // Index 1 -> Health
    // Index 2 -> Attack
    // Index 3 -> Defence
    int[] vital_signs_array = new int[4];

    // Boosts for character vitals
    int xp_boost = 0;
    int health_boost = 0;

    int numFought = 0;

    String axis = "\0";

    // Textview variables for showing the x and y locations of our character
    TextView update_position_x;// = findViewById(R.id.x_position);
    TextView update_position_y;// = findViewById(R.id.y_position);

    // Textview variable for the game level
    TextView game_level;// = findViewById(R.id.level_number);
    TextView game_over_message;// = findViewById(R.id.level);

    TextView power_up_message;// = findViewById(R.id.power_up_received);
    TextView new_power_up;// = findViewById(R.id.power_up_name);

    // Textview variables for the character's vital signs
    TextView power_up_x;// = findViewById(R.id.power_up_x);
    TextView power_up_y;// = findViewById(R.id.power_up_y);

    //the users character
    HumanCharacter player; // = new HumanCharacter()

    //the enemies
    AICharacter[] enemies;

    //creating buttons for simulation use
    public Button leftButton, rightButton, upButton, downButton;

    int current_level = 1; // Stores the level number that is character is currently on

    boolean game_end = false; // Boolean that is checked when setting up the tiles as a game controller

    String seven_moves = "\0"; // We use this string to check when seven moves are over

    String power_up_name = "\0"; // Stores the name of the power up

    Thread mainGameLoop = new Thread(){

        @Override
        public void run()
        {
            try {

                Thread.sleep(5000);
                setupTiles();
                Thread.sleep(2000);

                //worked with 10,1
                //failed 1, 1
                //works with 5,2
                while (isGameRunning){
                    setupTiles();


                    switch (currState) {
                        case 0: // walking in overworld
                            overworldUpdate();
                            //System.out.println("curr pos x:" +character_location.x +
                            //        "y:" + character_location.y);

                            break;
                        case 1: // found a fight
                            //isGameRunning = false;
                            Thread.sleep(10000);
                            System.out.println("please wait, fight is processing");
                            break;
                        case 2: // battle is over, lets update and get back to main loop
                            System.out.println("back from battle lets go back to updating");
                            currState = 0;
                            nx();
                            break;
                        case 3:
                            break;
                        case 17:
                            Thread.sleep(4000);
                            System.out.println("case 17!");
                            break;
                        default:
                            break;
                    }

                    Thread.sleep(300); //wait 1.5 seconds each loop
                }

            }
            catch (InterruptedException e) {

            }
        }
    };


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


        vital_signs_array = tzp_object.set_up_vital_signs(vital_signs_array);

        vital_signs_array = tzp_object.set_up_vital_signs(vital_signs_array);

        // Showing the initial vital signs on the screen
        display_xp.setText(toString().valueOf(vital_signs_array[0]));
        display_health.setText(toString().valueOf(vital_signs_array[1]));
        display_attack.setText(toString().valueOf(vital_signs_array[2]));
        display_defence.setText(toString().valueOf(vital_signs_array[3]));

         game_level = findViewById(R.id.level_number);
         game_over_message = findViewById(R.id.level);

         power_up_message = findViewById(R.id.power_up_received);
         new_power_up = findViewById(R.id.power_up_name);

        // Textview variables for the character's vital signs
         power_up_x = findViewById(R.id.power_up_x);
         power_up_y = findViewById(R.id.power_up_y);


        // Displaying the initial level on the screen
        game_level.setText(toString().valueOf(current_level));

        // Showing the initial location of our character on the screen
        update_position_x.setText(toString().valueOf(character_location.x));
        update_position_y.setText(toString().valueOf(character_location.y));


        //bulky boy or destructive dude or genuine guy or sketchy specimen?
        //Todo
        //create user char for real
        //finalize attack options
        //make multiple ai characters
        //come up with some creative names and sayings
        //user level up after battle



        //here is the main setup method!
        setupEverything();
        //characterSetup();


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

        // Displaying the initial level on the screen
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
                seven_moves = s;
            }

            @Override
            public void onSetupEnd()
            {

            }
        });

        // Displaying each colour for a certain period of time (default - 3000 ms)
        Thread my_thread = new Thread()
        {
            @Override
            public void run()
            {
                //try
                //{
                    while (true)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                //connection.setAllTilesIdle(AntData.LED_COLOR_OFF);
                                //tzp_object.tile_controller();

                                if (seven_moves.equals("true")) // This block is executed once every 7 moves of the character
                                {
                                    // Updating the location of the power-up on the screen
                                    power_up_x.setText(toString().valueOf(tzp_object.power_up_location.x));
                                    power_up_y.setText(toString().valueOf(tzp_object.power_up_location.y));

                                    power_up_name = tzp_object.power_up_generator(); // Generating a power-up randomly
                                }

                                Log.v("Game","x character:" + character_location.x);
                                Log.v("Game","y character:" + character_location.y);

                                Log.v("Game","x power up:" + tzp_object.power_up_location.x);
                                Log.v("Game","y power up:" + tzp_object.power_up_location.y);

                                if (character_location.x == tzp_object.power_up_location.x && character_location.y == tzp_object.power_up_location.y)
                                {
                                    System.out.println("You are at the power-up location");
                                    power_up_message.setText("You received:");

                                    new_power_up.setText(power_up_name);
                                }

                                // Condition for switching to the next level
                                // We proceed to the next level when the character is at the top right corner of the world
                                if (character_location.x == tzp_object.upper_bound && character_location.y == tzp_object.upper_bound)
                                {
                                    // The character location is reset back to the origin at the start of each level
                                    character_location.x = 0;
                                    character_location.y = 0;

                                    // Incrementing the level variable to move to the next one
                                    current_level++;
                                    isBossDead = false;

                                    // Since we have a maximum of 3 levels, the game execution stops at the end of the third
                                    if (current_level <= 3) // Executes for anything less than or equal to 3 levels
                                    {
                                        game_level.setText(toString().valueOf(current_level)); // Updating the level on the screen
                                        // Updating the locations after resetting them for the new level on the screen
                                        update_position_x.setText(toString().valueOf(character_location.x));
                                        update_position_y.setText(toString().valueOf(character_location.y));
                                    }
                                    else // Game is over after the end of the third level is reached
                                    {
                                        game_end = true;
                                        game_over_message.setText("Game Over"); // Endgame message
                                        game_level.setText("");

                                        connection.setAllTilesIdle(LED_COLOR_OFF);

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                // The following lines are executed after a delay of 5 seconds
                                                // Going back to the MainActivity (The game's homepage) once the user is done playing
                                                Intent main_activity_intent = new Intent(GameActivity.this, MainActivity.class);
                                                startActivity(main_activity_intent);
                                            }
                                        }, 5000);
                                    }
                                }
                            }
                        });
                    }
                //}
                //catch (InterruptedException e)
                //{

                //}
            }
        };



        currState = 0;
        isGameRunning = true;

        //my_thread.start();


        mainGameLoop.start();

        System.out.println("FLAG A: OUT OF LOOP");


        //tzp_object.tile_controller();
    } //end onCreate

    //this method sets up everything that needs to be done once
    private void setupEverything(){
        setupTiles();
        characterSetup();
    }

    //sets up tile colors
    private void setupTiles(){
        connection.setAllTilesIdle(LED_COLOR_OFF); // setTileColor() requires the tiles to be set off before being used
        tzp_object.tile_controller(); // Setting up the tiles as a controller


    }


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

        System.out.println("******UPDATING******");
        if (character_location.x == 2 && character_location.y == 3){ // if we find a boss
            enterBattle();
        }

        if (seven_moves.equals("true")) // This block is executed once every 7 moves of the character
        {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // Updating the location of the power-up on the screen
                    power_up_x.setText(toString().valueOf(tzp_object.power_up_location.x));
                    power_up_y.setText(toString().valueOf(tzp_object.power_up_location.y));
                }
            });

            power_up_name = tzp_object.power_up_generator(); // Generating a power-up randomly
        }

        //Log.v("Game","x character:" + character_location.x);
        //Log.v("Game","y character:" + character_location.y);

        Log.v("Game","x power up:" + tzp_object.power_up_location.x);
        Log.v("Game","y power up:" + tzp_object.power_up_location.y);

        if (character_location.x == tzp_object.power_up_location.x && character_location.y == tzp_object.power_up_location.y)
        {
            System.out.println("You are at the power-up location");
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    power_up_message.setText("You received:");
                    new_power_up.setText(power_up_name);
                }
            });

        }

        // Condition for switching to the next level
        // We proceed to the next level when the character is at the top right corner of the world
        if (character_location.x == tzp_object.upper_bound && character_location.y == tzp_object.upper_bound)
        {
            if (isBossDead){
                endLevel();
            } else {
                //character at 20,20 but did not defeat boss
                System.out.println("at teleporter, need to defeat boss to turn it on!");
                nx();
            }

        }

        System.out.println("****** DONE UPDATE ******* \n\n");
        System.out.println("\n");
    }

    //we've been told the chracter has reached the end of the level so let's update
    private void endLevel(){

        // The character location is reset back to the origin at the start of each level
        character_location.x = 0;
        character_location.y = 0;

        // Incrementing the level variable to move to the next one
        current_level++;

        // Since we have a maximum of 3 levels, the game execution stops at the end of the third
        if (current_level <= 3) // Executes for anything less than or equal to 3 levels
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    game_level.setText(toString().valueOf(current_level)); // Updating the level on the screen
                    // Updating the locations after resetting them for the new level on the screen
                    update_position_x.setText(toString().valueOf(character_location.x));
                    update_position_y.setText(toString().valueOf(character_location.y));
                }
            });

        }
        else // Game is over after the end of the third level is reached
        {
            game_end = true;

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    game_over_message.setText("Game Over"); // Endgame message
                    game_level.setText("");
                }
            });


            connection.setAllTilesIdle(LED_COLOR_OFF);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    // The following lines are executed after a delay of 5 seconds
                    // Going back to the MainActivity (The game's homepage) once the user is done playing
                    Intent main_activity_intent = new Intent(GameActivity.this, MainActivity.class);
                    startActivity(main_activity_intent);
                }
            }, 5000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("return from activity with result");

        if (requestCode == 17){

            if (data.getIntExtra("winner", 0) == 1){
                //user won
                player.health = data.getIntExtra("health", 10);

                System.out.println("user won in returned method: " + player.health);
                isBossDead = true;

                //TODO
                // update health to screen here?
                // and xp and stuff maybe call a method
            } else {
                //user lost

                System.out.println("user lost in returned method");
            }

            //if result is 7 continue
            if(resultCode == 7){
                System.out.println("made it back with finish");
                currState = 0;

            } else {
                System.out.println("back wrong");
            }
            battleOver();
        }

    }

    private void battleOver(){ //do some xp gaining, update some we won battle stuff,
        // and get loop going again
        numFought++;
        currState = 2;
        //System.out.println("back from battle");
        System.out.println("battle over and hopefully restarting thread ");

    }

    private void enterBattle(){
        currState = 1; //currstate1 = battle, this might not be necessary
        Intent start_game_intent = new Intent(GameActivity.this, BattleActivity.class);
        start_game_intent.putExtra("enemyFighter", enemies[numFought]);
        start_game_intent.putExtra("playerFighter", player);
        startActivityForResult(start_game_intent,17);
        System.out.println("just sent activity to battle, lets keep this thread going");


        //System.out.println("back from battle, need to update user xp or death\nand return to game loop");
        //currState = 0;

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

        tzp_object.character_movement_log[tzp_object.movement_log_index] = "nx";
        tzp_object.movement_log_index++;

        if (tzp_object.movement_log_index == 7)
        {
            seven_moves = "true";
            // After every 7 moves we check which axis (positive x, negative x, positive y, negative y)
            // the user has moved along more
            // Then a power-up is generated accordingly
            tzp_object.moves_count_array = tzp_object.count_moves(tzp_object.character_movement_log);
            tzp_object.power_up_location = tzp_object.power_up_location_generator(tzp_object.moves_count_array);


            // Resetting the index and the array after every 7 moves
            // We need to keep overwriting the character_movement_log for every 7 moves
            tzp_object.movement_log_index = 0;
            tzp_object.character_movement_log = new String[7];
        }
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

        tzp_object.character_movement_log[tzp_object.movement_log_index] = "px";
        tzp_object.movement_log_index++;

        if (tzp_object.movement_log_index == 7)
        {
            seven_moves = "true";
            // After every 7 moves we check which axis (positive x, negative x, positive y, negative y)
            // the user has moved along more
            // Then a power-up is generated accordingly
            tzp_object.moves_count_array = tzp_object.count_moves(tzp_object.character_movement_log);
            tzp_object.power_up_location = tzp_object.power_up_location_generator(tzp_object.moves_count_array);


            // Resetting the index and the array after every 7 moves
            // We need to keep overwriting the character_movement_log for every 7 moves
            tzp_object.movement_log_index = 0;
            tzp_object.character_movement_log = new String[7];
        }
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

        tzp_object.character_movement_log[tzp_object.movement_log_index] = "ny";
        tzp_object.movement_log_index++;

        if (tzp_object.movement_log_index == 7)
        {
            seven_moves = "true";
            // After every 7 moves we check which axis (positive x, negative x, positive y, negative y)
            // the user has moved along more
            // Then a power-up is generated accordingly
            tzp_object.moves_count_array = tzp_object.count_moves(tzp_object.character_movement_log);
            tzp_object.power_up_location = tzp_object.power_up_location_generator(tzp_object.moves_count_array);


            // Resetting the index and the array after every 7 moves
            // We need to keep overwriting the character_movement_log for every 7 moves
            tzp_object.movement_log_index = 0;
            tzp_object.character_movement_log = new String[7];
        }
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

        tzp_object.character_movement_log[tzp_object.movement_log_index] = "py";
        tzp_object.movement_log_index++;

        if (tzp_object.movement_log_index == 7)
        {
            seven_moves = "true";
            // After every 7 moves we check which axis (positive x, negative x, positive y, negative y)
            // the user has moved along more
            // Then a power-up is generated accordingly
            tzp_object.moves_count_array = tzp_object.count_moves(tzp_object.character_movement_log);
            tzp_object.power_up_location = tzp_object.power_up_location_generator(tzp_object.moves_count_array);


            // Resetting the index and the array after every 7 moves
            // We need to keep overwriting the character_movement_log for every 7 moves
            tzp_object.movement_log_index = 0;
            tzp_object.character_movement_log = new String[7];
        }
    }

    private void characterSetup(){
        //this should change based on users choice of char
        player = new HumanCharacter(20,1,4,3,0);
        player.weaponAttack = 1;

        //this should change based on difficulty selected
        enemies = new AICharacter[3];
        //first enemy has 8-12 health, 2 atk, 4 def, lvl 1
        enemies[0] = new AICharacter((int) (Math.random() * 5) + 8, 1,
                2,4,0);
        //second enemy has 16-21 health, 9-11 atk, 6 def, lvl 3
        enemies[1] = new AICharacter((int) (Math.random() * 6) + 16, 3,
                (int) (Math.random() * 3) + 9, 6, 0);
        //third enemy has 20-35 health, 14-15 atk, 15-19 def, lvl 5
        enemies[2] = new AICharacter((int) (Math.random() * 16) + 20, 6,
                (int) (Math.random() * 2) + 14, (int) (Math.random() * 5) + 15,
                0);

        enemies[0].weaponAttack = 1;
        enemies[1].weaponAttack = 3;
        enemies[2].weaponAttack = 5;

        enemies[0].intelligence = 0;
        enemies[1].intelligence=1;
        enemies[2].intelligence=2;

        enemies[1].numHeals = 1;
        enemies[2].numHeals = 3;

        enemies[0].maxHealth = enemies[0].health;
        enemies[1].maxHealth = enemies[1].health;
        enemies[2].maxHealth = enemies[2].health;


    }

}