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

import java.util.Random;

import static java.lang.Integer.parseInt;
import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;

public class GameActivity extends AppCompatActivity implements OnAntEventListener
{
    MotoConnection connection = MotoConnection.getInstance();

    TZP_Game tzp_object = new TZP_Game(); // Creating the game object

    //Point character_location = new Point(tzp_object.character_start_x, tzp_object.character_start_y); // Stores the location of our character
    Point character_location = tzp_object.character_location; // Holds the location of the character

    // point for the enemy location to be generated to
    Point enemyLocation;

    //the state of the game loop
    //0 = in main overworld, walking around
    //1 = encountered battle (and still in it)
    // 2 = finished battle
    private int currState;

    //boolean to run game loop
    private boolean isGameRunning, isBossDead, isBossGen, isSkipGenFlag;

    // Array that holds the details about the character's vital signs
    // Index 0 -> XP points
    // Index 1 -> Health
    // Index 2 -> Attack
    // Index 3 -> Defence
    int[] vital_signs_array = new int[4];

    // Boosts for character vitals
    int xp_boost = 0;
    int health_boost = 0;
    int attack_boost = 0;
    int defence_boost = 0;
    int initial_xp = 0; // Stores the initial XP points at the start of every level

    int health_cap = 30; // Maximum limit for health

    //integer value of enemies fought, differs from current_level
    int numFought = 0;

    // XP threshold points for each level
    int level_one_xp_threshold = 10;
    int level_two_xp_threshold = 20;
    int level_three_xp_threshold = 40;

    String axis = "\0"; // Stores which axis we are moving on

    // Textview to tell the user not to act after a battle
    TextView processing;

    // Textview variables for showing the x and y locations of our character
    TextView update_position_x;// = findViewById(R.id.x_position);
    TextView update_position_y;// = findViewById(R.id.y_position);

    // Textview variable for the game level
    TextView game_level;// = findViewById(R.id.level_number);
    TextView game_over_message;// = findViewById(R.id.level);

    TextView power_up_message;// = findViewById(R.id.power_up_received);
    TextView new_power_up;// = findViewById(R.id.power_up_name);

    // Textview variable for displaying the power-up comments
    TextView power_up_tagline;// = findViewById(R.id.power_up_comment);

    // Textview variable for displaying the full health message
    TextView full_health_text;// = findViewById(R.id.full_health_message);

    // Textview variables for the boss location
     TextView boss;// = findViewById(R.id.fight_the_boss);
     TextView boss_message;// = findViewById(R.id.go_to_the_boss);
     TextView boss_x;// = findViewById(R.id.boss_location_x);
     TextView boss_y;// = findViewById(R.id.boss_location_y);

    //Textview variables for powerup coordinates
    TextView power_up_x;// = findViewById(R.id.power_up_x);
    TextView power_up_y;// = findViewById(R.id.power_up_y);

    // Textview variables for the character's vital signs
    TextView display_xp;
    TextView display_health;// = findViewById(R.id.health_value);
    TextView display_attack;// = findViewById(R.id.attack_value);
    TextView display_defence;// = findViewById(R.id.defence_value);

    //the users character
    HumanCharacter player; // = new HumanCharacter()

    //the enemies
    AICharacter[] enemies;

    //creating buttons for simulation use
    public Button leftButton, rightButton, upButton, downButton;

    boolean game_end = false; // Boolean that is checked when setting up the tiles as a game controller

    String seven_moves = "\0"; // We use this string to check when seven moves are over

    String power_up_name = "\0"; // Stores the name of the power up
    String power_up_comments = "\0"; // Holds comments for corresponding power-ups


    // Array that holds the details about the power-ups
    // Index 0 -> Name of the power-up
    // Index 1 -> XP points gained
    // Index 2 -> Health points gained
    // Index 3 -> Attack points gained
    // Index 4 -> Attack points gained
    String[] power_up_details = new String[5];


    //this is the main game loop that houses all of the logic for our project
    //much of the code has been exported to other methods but some bulky pieces remain directly in the loop
    // the thread.sleep method called at the end is how long the thread sleeps in between updates
    Thread mainGameLoop = new Thread(){

        @Override
        public void run()
        {
            try {

                //setting up the tiles, this time pause seems to work consistently
                // without it the only conistent method is to call setupTiles in the while loop
                Thread.sleep(5000);
                setupTiles();
                Thread.sleep(2000);

                //the main section of the loop that is run by far the most times
                //tried to keep it efficient as possible
                while (isGameRunning){
                    //if the tile setup isnt working outside the while loop this can be uncommented
                    //setupTiles();

                    //switching on current state to allow better interfacing with the UI, esp for battles
                    switch (currState) {
                        case 0: // walking in overworld
                            overworldUpdate();
                            //System.out.println("curr pos x:" +character_location.x +
                            //        "y:" + character_location.y);

                            break;
                        case 1: // found a fight
                            //isGameRunning = false;
                            Thread.sleep(8000);
                            System.out.println("please wait, fight is processing");
                            break;
                        case 2: // battle is over, lets update and get back to main loop
                            System.out.println("back from battle lets go back to updating");
                            currState = 0;
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    processing.setVisibility(View.GONE);
                                }
                            });

                            //nx();
                            break;
                        case 3: //game is over, finish not necessary for winning, required for losing
                            isGameRunning = false;
                            if (!isBossDead){
                                finish();
                            }
                            break;
                        case 17:
                            Thread.sleep(4000);
                            System.out.println("case 17!");
                            break;
                        default:
                            break;
                    }

                    //*****This is how long the loop sleeps in between updates****//
                    Thread.sleep(300); //wait 1.5 seconds each loop
                }

            }
            catch (InterruptedException e) {

            }
        }
    };

    //onCreate initializes a lot of variables that were declared in the class
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        enemyLocation = new Point(1729,1729);

        display_xp = findViewById(R.id.xp_value);
        display_health = findViewById(R.id.health_value);
        display_attack = findViewById(R.id.attack_value);
        display_defence = findViewById(R.id.defence_value);

        power_up_tagline = findViewById(R.id.power_up_comment);
        full_health_text = findViewById(R.id.full_health_message);
        boss = findViewById(R.id.fight_the_boss);
        boss_message = findViewById(R.id.go_to_the_boss);
        boss_x = findViewById(R.id.boss_location_x);
        boss_y = findViewById(R.id.boss_location_y);

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
        processing = findViewById(R.id.processing_text);
        processing.setVisibility(View.GONE);

        // Textview variables for the character's vital signs


        // Button variables for moving the character when no tiles are available
        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);

        //main setup function, need to do this before assigning vitals
        setupEverything();

        //vital_signs_array = tzp_object.set_up_vital_signs(vital_signs_array);

        //vital_signs_array = tzp_object.set_up_vital_signs(vital_signs_array);
        vital_signs_array[0] = player.experience;
        vital_signs_array[1] = player.health;
        vital_signs_array[2] = player.attack;
        vital_signs_array[3] = player.defense;

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
        game_level.setText(toString().valueOf(tzp_object.current_level));

        // Showing the initial location of our character on the screen
        update_position_x.setText(toString().valueOf(character_location.x));
        update_position_y.setText(toString().valueOf(character_location.y));

        initial_xp = vital_signs_array[0]; // Updating the initial XP for the first level

        //here is the main setup method!
        setupEverything();
        //characterSetup();


        //unused buttons
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
                switch (currState){
                    case 0://character is moving around

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

                    break;
                    case 1:
                    break;
                    case 2:
                    break;
                    default:
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

    private boolean shouldBossGenerate(){
        //System.out.println("numf:"+numFought+"cL:"+tzp_object.current_level);
        if (numFought + 1 == tzp_object.current_level){
            //System.out.println("numf=cL");
            if (!isBossGen){
                System.out.println("settingtrue");
                isBossGen = true;
                return true;
            }
        }
        return false;
    }

    private void printFightLocation(){

        final String tempx = ""+enemyLocation.x;
        final String tempy = ""+enemyLocation.y;

/*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                boss.setText("You will fight you nemesis for this level");
                boss_message.setText("Go to:");
                boss_x.setText(".x");
                boss_x.setText(tempx);
                boss_y.setText(tempy);

            }
        });
    }

    private void overworldUpdate(){ // gets called everytick while in currState = 0

        //System.out.println("******UPDATING******");

        //this if statement checks if we should generate boss
        //moved to shouldBossGen method as seen later
//        if (character_location.x == 10 && character_location.y == 10 &&
//                (numFought + 1 == tzp_object.current_level)){
//            if (!isBossGen){
//                generateFightLocataion();
//                System.out.println("generated boss at" + enemyLocation.x + "," +
//                        enemyLocation.y);
//                isBossGen = true;
//            }
//
//            // if we find a boss and need to fight a boss then lets fight
//
//            runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    processing.setVisibility(View.VISIBLE);
//                }
//            });
//            enterBattle();
//
//        }

        if (character_location.equals(enemyLocation)){
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    processing.setVisibility(View.VISIBLE);
                }
            });
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

            int delta_xp = vital_signs_array[0] - initial_xp; // Keeps track of the change in XP points gained in each level

            switch (tzp_object.current_level)
            {
                case 1: // Generating the power-ups for level 1
                    if (delta_xp <= level_one_xp_threshold )
                    {
                        power_up_details = tzp_object.power_up_generator(); // Generating a power-up randomly
                    }
                    else
                    {
                        if (shouldBossGenerate()){
                            generateFightLocataion();
                            printFightLocation();
                            System.out.println("generated boss at" + enemyLocation.x + "," +
                                enemyLocation.y);
                            isBossGen = true;
                            isSkipGenFlag = true;
                        }

                    }
                    break;

                case 2: // Generating the power-ups for level 2
                    if (vital_signs_array[0] - initial_xp <= level_two_xp_threshold )
                    {
                        power_up_details = tzp_object.power_up_generator(); // Generating a power-up randomly
                    }
                    else
                    {
                        if (shouldBossGenerate()){
                            generateFightLocataion();
                            printFightLocation();
                            System.out.println("generated boss at" + enemyLocation.x + "," +
                                    enemyLocation.y);
                            isBossGen = true;
                            isSkipGenFlag = true;

                        }
                    }
                    break;

                case 3: // Generating the power-ups for level 3
                    if (vital_signs_array[0] - initial_xp <= level_three_xp_threshold )
                    {
                        power_up_details = tzp_object.power_up_generator(); // Generating a power-up randomly
                    }
                    else
                    {

                        if (shouldBossGenerate()){
                            generateFightLocataion();
                            printFightLocation();
                            System.out.println("generated boss at" + enemyLocation.x + "," +
                                    enemyLocation.y);
                            isBossGen = true;
                            isSkipGenFlag = true;

                        }
                    }
                    break;
            }

        }


        if (character_location.x == tzp_object.power_up_location.x && character_location.y == tzp_object.power_up_location.y)
        {
            System.out.println("You are at the power-up location");

            // Getting all the details about our weapon
            power_up_name = power_up_details[0];
            xp_boost = parseInt(power_up_details[1]);
            health_boost = parseInt(power_up_details[2]);
            attack_boost = parseInt(power_up_details[3]);
            defence_boost = parseInt(power_up_details[4]);

            if (power_up_name.equals("Fawke's the Pheonix"))
            {
                full_health_text.setText("FULL HEALTH BOOST!!!"); // Displaying the full health message
            }

            power_up_comments = weapon_comments(power_up_name); // Generating a comment for the power-up received

            // Updating each element of the vital signs array (XP, Health, Attack, Defence)
            vital_signs_array[0] = vital_signs_array[0] + xp_boost;
            vital_signs_array[1] = vital_signs_array[1] + health_boost;
            if (vital_signs_array[1] > health_cap) // Making sure that the health of the character does not exceed the limit
            {
                vital_signs_array[1] = 30;
            }
            vital_signs_array[2] = vital_signs_array[2] + attack_boost;
            vital_signs_array[3] = vital_signs_array[3] + defence_boost;

            player.health += health_boost;
            player.experience += xp_boost;
            player.attack += attack_boost;
            player.defense += defence_boost;

            // Updating the vital signs on the screen

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    power_up_message.setText("You received:");
                    new_power_up.setText(power_up_name);

                    display_xp.setText(toString().valueOf(vital_signs_array[0]));
                    display_health.setText(toString().valueOf(vital_signs_array[1]));
                    display_attack.setText(toString().valueOf(vital_signs_array[2]));
                    display_defence.setText(toString().valueOf(vital_signs_array[3]));

                    power_up_tagline.setText(power_up_comments); // Updating the comment for the power-up on the screen

                }
            });

            tzp_object.reset_power_up();

        }

        // Condition for switching to the next level
        // We proceed to the next level when the character is at the top right corner of the world
        if (character_location.x == tzp_object.upper_bound && character_location.y == tzp_object.upper_bound)
        {
            if (numFought == tzp_object.current_level){
                endLevel();
            } else {
                //character at 20,20 but did not defeat boss
                System.out.println("at teleporter, need to defeat boss to turn it on!");

            }

        }



        //System.out.println("****** DONE UPDATE ******* \n\n");
        //System.out.println("\n");
    }

    //we've been told the chracter has reached the end of the level so let's update
    private void endLevel(){

        // The character location is reset back to the origin at the start of each level
        character_location.x = 0;
        character_location.y = 0;

        // Incrementing the level variable to move to the next one
        tzp_object.current_level++;

        // Since we have a maximum of 3 levels, the game execution stops at the end of the third
        if (tzp_object.current_level <= 3) // Executes for anything less than or equal to 3 levels
        {

            // Resetting the power up name and the vital sign boosts
            power_up_name = "";
            xp_boost = 0;
            health_boost = 0;
            attack_boost = 0;
            defence_boost = 0;

            // Resetting the movement log its index at the beginning of every level
            tzp_object.movement_log_index = 0;
            tzp_object.character_movement_log = new String[7];
            Log.v("Game","Movement index:"+tzp_object.movement_log_index);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    game_level.setText(toString().valueOf(tzp_object.current_level)); // Updating the level on the screen
                    // Updating the locations after resetting them for the new level on the screen
                    update_position_x.setText(toString().valueOf(character_location.x));
                    update_position_y.setText(toString().valueOf(character_location.y));

                    // Resetting the power-up name on the screen
                    power_up_x.setText("-");
                    power_up_y.setText("-");
                    power_up_message.setText("");
                    new_power_up.setText("");
                    power_up_tagline.setText("");

                    // Resetting the boss details at the beginning of every level
                    boss.setText("");
                    boss_message.setText("");
                    boss_x.setText("");
                    boss_y.setText("");


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

            // Going back to the MainActivity (The game's homepage) once the user is done playing
            Intent main_activity_intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(main_activity_intent);
            currState = 3;


            /*
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

            */
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
                isBossDead = false;
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

        if (!isBossDead){ //user died we need to clean up
            currState = 3;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    game_over_message.setText("Game Over"); // Endgame message
                    game_level.setText("");
                }
            });
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            numFought++;
            isBossGen = false;
            currState = 2;
            enemyLocation.set(1729,1729);
            //System.out.println("back from battle");
            System.out.println("battle over and hopefully restarting thread ");
        }


    }

    private void enterBattle(){
        currState = 1; //currstate1 = battle, this might not be necessary
        player.health=1;
        player.defense=1;
        player.attack=1;
        Intent start_game_intent = new Intent(GameActivity.this, BattleActivity.class);
        start_game_intent.putExtra("enemyFighter", enemies[numFought]);
        start_game_intent.putExtra("playerFighter", player);
        startActivityForResult(start_game_intent,17);
        System.out.println("just sent activity to battle, lets keep this thread going");



        //System.out.println("back from battle, need to update user xp or death\nand return to game loop");
        //currState = 0;

    }

    // This function generates tag lines for every weapon
    public String weapon_comments(String weapon_name)
    {
        String comment = "\0"; // Holds the comment that will be made for that power-up
        switch (weapon_name)
        {
            case "The Sorting Hat":
                comment = "Which house should I put you in?";
                break;

            case "Deadpool's Sword":
                comment = "Maximum effort!!!";
                break;

            case "Web Cartridges and Shooters":
                comment = "I am Spider-Man!";
                break;

            case "Iron Man's Arc Reactor":
                comment = "I am Iron-Man";
                break;

            case "Captain America's Shield":
                comment = "I could do this all day";
                break;

            case "Adamantium Claws":
                comment = "I'll cut you bub";
                break;

            case "Hawkeye's Bow and Arrow":
                comment = "I don't seem to miss";
                break;

            case "The Symbiote Suit":
                comment = "We are Venom!!!";
                break;

            case "The Pym Particle":
                comment = "Now where's Anthony??!";
                break;

            case "The War Machine Suit":
                comment = "Second Iron-Man??!";
                break;

            case "The Hulkbuster Suit":
                comment = "Is Hulk Iron-Man now?!";
                break;

            case "Thor's Mjölnir":
                comment = "Odin deems you worthy";
                break;

            case "Mar-Vell's Nega-Bands":
                comment = "You are now cosmically aware";
                break;

            case "The Sword of Gryffindor":
                comment = "You are a worthy Gryffindor";
                break;

            case "The Bleeding Edge Suit":
                comment = "It's Nanotech. Do you like it?";
                break;

            case "The Eye of Agamotto":
                comment = "I now possess the time stone";
                break;

            case "The Stormbreaker":
                comment = "This is Thanos killing kind of a weapon";
                break;

            case "Fawke's the Pheonix":
                comment = "Rise from the ashes";
                break;
        }

        return comment;
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

    //method to generate fight some distance from user
    private void generateFightLocataion(){

        Random rand = new Random();
        int xdist = rand.nextInt(7) + 3;
                //(int) (Math.random() * 7) + 3;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int ydist = rand.nextInt(7) + 3;
                //(int) (Math.random() * 7) + 3;

        if (Math.random() < .5){
            if (character_location.x - xdist < 0){
                enemyLocation.x = character_location.x + xdist;
            } else {
                enemyLocation.x = character_location.x - xdist;
            }
        } else {
            if (character_location.x + xdist > 20){
                enemyLocation.x = character_location.x - xdist;
            } else {
                enemyLocation.x = character_location.x + xdist;
            }
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Math.random() < .5){
            if (character_location.y - ydist < 0){
                enemyLocation.y = character_location.y + xdist;
            } else {
                enemyLocation.y = character_location.y - xdist;
            }
        } else {
            if (character_location.y + ydist > 20){
                enemyLocation.y = character_location.y - xdist;
            } else {
                enemyLocation.y = character_location.y + xdist;
            }
        }
    }

    private void characterSetup(){
        //this should change based on users choice of char
        player = new HumanCharacter(1,1,6,1,0);
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