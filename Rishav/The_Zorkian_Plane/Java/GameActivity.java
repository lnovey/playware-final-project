package com.example.tzp;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.OnAntEventListener;

import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.MotoConnection;

import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;
import static java.lang.Integer.parseInt;

public class GameActivity extends AppCompatActivity implements OnAntEventListener
{
    MotoConnection connection = MotoConnection.getInstance();

    TZP_Game tzp_object = new TZP_Game(); // Creating the game object

    Point character_location = tzp_object.character_location; // Holds the location of the character

    // Array that holds the details about the character's vital signs
    // Index 0 -> XP points
    // Index 1 -> Health
    // Index 2 -> Attack
    // Index 3 -> Defence
    int[] vital_signs_array = new int[4];

    // Array that holds the details about the power-ups
    // Index 0 -> Name of the power-up
    // Index 1 -> XP points gained
    // Index 2 -> Health points gained
    // Index 3 -> Attack points gained
    // Index 4 -> Attack points gained
    String[] power_up_details = new String[5];

    // Boosts for character vitals
    int xp_boost = 0;
    int health_boost = 0;
    int attack_boost = 0;
    int defence_boost = 0;

    int health_cap = 30; // Maximum limit for health
    int initial_xp = 0; // Stores the initial XP points at the start of every level

    // XP threshold points for each level
    int level_one_xp_threshold = 10;
    int level_two_xp_threshold = 20;
    int level_three_xp_threshold = 40;

    String axis = "\0"; // Stores which axis we are moving on
    String seven_moves = "\0"; // We use this string to check when seven moves are over
    String power_up_name = "\0"; // Stores the name of the power up
    String power_up_comments = "\0"; // Holds comments for corresponding power-ups

    boolean game_end = false; // Boolean that is checked when setting up the tiles as a game controller

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

        // Textview variables for showing the x and y locations of our character
        final TextView update_position_x = findViewById(R.id.x_position);
        final TextView update_position_y = findViewById(R.id.y_position);

        // Textview variables for the character's vital signs
        final TextView display_xp = findViewById(R.id.xp_value);
        final TextView display_health = findViewById(R.id.health_value);
        final TextView display_attack = findViewById(R.id.attack_value);
        final TextView display_defence= findViewById(R.id.defence_value);

        // Textview variables for the character's vital signs
        final TextView power_up_x = findViewById(R.id.power_up_x);
        final TextView power_up_y = findViewById(R.id.power_up_y);

        // Textview variables for the game level
        final TextView game_level = findViewById(R.id.level_number);
        final TextView game_over_message = findViewById(R.id.level);

        // Textview variables for displaying the power-ups
        final TextView power_up_message = findViewById(R.id.power_up_received);
        final TextView new_power_up = findViewById(R.id.power_up_name);

        // Textview variable for displaying the power-up comments
        final TextView power_up_tagline = findViewById(R.id.power_up_comment);

        // Textview variable for displaying the full health message
        final TextView full_health_text = findViewById(R.id.full_health_message);

        // Textview variables for the boss location
        final TextView boss = findViewById(R.id.fight_the_boss);
        final TextView boss_message = findViewById(R.id.go_to_the_boss);
        final TextView boss_x = findViewById(R.id.boss_location_x);
        final TextView boss_y = findViewById(R.id.boss_location_y);

        // Showing the initial location of our character on the screen
        update_position_x.setText(toString().valueOf(character_location.x));
        update_position_y.setText(toString().valueOf(character_location.y));

        vital_signs_array = tzp_object.set_up_vital_signs(vital_signs_array); // Updating the initial vital signs on the screen

        // Showing the initial vital signs on the screen
        display_xp.setText(toString().valueOf(vital_signs_array[0]));
        display_health.setText(toString().valueOf(vital_signs_array[1]));
        display_attack.setText(toString().valueOf(vital_signs_array[2]));
        display_defence.setText(toString().valueOf(vital_signs_array[3]));

        // Displaying the initial level on the screen
        game_level.setText(toString().valueOf(tzp_object.current_level));

        // Used to exchange data between the tiles and GameActivity
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
                        character_location.x += 1; // Taking the character ahead by 1 unit
                        if (character_location.x > tzp_object.upper_bound) // Condition to keep the character in the bounds of our world
                        {
                            character_location.x--;
                        }
                        GameActivity.this.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                update_position_x.setText(toString().valueOf(character_location.x)); // Updating the character's new location on the screen
                            }
                        });
                        break;

                    case "nx": // Movement along negative X axis
                        character_location.x -= 1; // Taking the character behind by 1 unit
                        if (character_location.x < tzp_object.lower_bound) // Condition to keep the character in the bounds of our world
                        {
                            character_location.x++;
                        }
                        GameActivity.this.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                update_position_x.setText(toString().valueOf(character_location.x)); // Updating the character's new location on the screen
                            }
                        });
                        break;

                    case "py": // Movement along positive Y axis
                        character_location.y += 1; // Taking the character ahead by 1 unit
                        if (character_location.y > tzp_object.upper_bound) // Condition to keep the character in the bounds of our world
                        {
                            character_location.y--;
                        }
                        GameActivity.this.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                update_position_y.setText(toString().valueOf(character_location.y)); // Updating the character's new location on the screen
                            }
                        });
                        break;

                    case "ny": // Movement along negative Y axis
                        character_location.y -= 1; // Taking the character behind by 1 unit
                        if (character_location.y < tzp_object.lower_bound) // Condition to keep the character in the bounds of our world
                        {
                            character_location.y++;
                        }
                        GameActivity.this.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                update_position_y.setText(toString().valueOf(character_location.y)); // Updating the character's new location on the screen
                            }
                        });
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

            // Used to keep a track of which axis the character is moving along
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

        initial_xp = vital_signs_array[0]; // Updating the initial XP for the first level

        Thread my_thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while (true)
                    {
                        Thread.sleep(300);

                        connection.setAllTilesIdle(AntData.LED_COLOR_OFF); // setTileColor() requires the tiles to be set off before being used
                        if (game_end == false)
                        {
                            tzp_object.tile_controller(); // Setting up the tiles as a controller
                        }

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (seven_moves.equals("true")) // This block is executed once every 7 moves of the character
                                {
                                    // Updating the location of the power-up on the screen
                                    power_up_x.setText(toString().valueOf(tzp_object.power_up_location.x));
                                    power_up_y.setText(toString().valueOf(tzp_object.power_up_location.y));

                                    int delta_xp = vital_signs_array[0] - initial_xp; // Keeps track of the change in XP points gained in each level

                                    switch (tzp_object.current_level)
                                    {
                                        case 1: // Generating the power-ups for level 1
                                            if (delta_xp <= level_one_xp_threshold)
                                            {
                                                power_up_details = tzp_object.power_up_generator(); // Generating a power-up randomly
                                            }
                                            else
                                            {
                                                boss.setText("You will fight you nemesis for this level");
                                                boss_message.setText("Go to:");
                                                boss_x.setText(toString().valueOf(10));
                                                boss_y.setText(toString().valueOf(10));
                                            }
                                            break;

                                        case 2: // Generating the power-ups for level 2
                                            if (vital_signs_array[0] - initial_xp <= level_two_xp_threshold)
                                            {
                                                power_up_details = tzp_object.power_up_generator(); // Generating a power-up randomly
                                            }
                                            else
                                            {
                                                boss.setText("You will fight you nemesis for this level");
                                                boss_message.setText("Go to:");
                                                boss_x.setText(toString().valueOf(10));
                                                boss_y.setText(toString().valueOf(10));
                                            }
                                            break;

                                        case 3: // Generating the power-ups for level 3
                                            if (vital_signs_array[0] - initial_xp <= level_three_xp_threshold)
                                            {
                                                power_up_details = tzp_object.power_up_generator(); // Generating a power-up randomly
                                            }
                                            else
                                            {
                                                boss.setText("You will fight you nemesis for this level");
                                                boss_message.setText("Go to:");
                                                boss_x.setText(toString().valueOf(10));
                                                boss_y.setText(toString().valueOf(10));
                                            }
                                            break;
                                    }


                                }

                                if (character_location.x == tzp_object.power_up_location.x && character_location.y == tzp_object.power_up_location.y)
                                {
                                    // Updating the power-up messages on the screen
                                    System.out.println("You are at the power-up location");
                                    power_up_message.setText("You received:");

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
                                    vital_signs_array[2] = vital_signs_array[2] + attack_boost;
                                    vital_signs_array[3] = vital_signs_array[3] + defence_boost;

                                    if (vital_signs_array[1] > health_cap) // Making sure that the health of the character does not exceed the limit
                                    {
                                        vital_signs_array[1] = 30;
                                    }

                                    // Updating the vital signs on the screen
                                    new_power_up.setText(power_up_name);
                                    display_xp.setText(toString().valueOf(vital_signs_array[0]));
                                    display_health.setText(toString().valueOf(vital_signs_array[1]));
                                    display_attack.setText(toString().valueOf(vital_signs_array[2]));
                                    display_defence.setText(toString().valueOf(vital_signs_array[3]));

                                    power_up_tagline.setText(power_up_comments); // Updating the comment for the power-up on the screen

                                    tzp_object.reset_power_up();
                                }

                                // Condition for switching to the next level
                                // We proceed to the next level when the character is at the top right corner of the world
                                if (character_location.x == tzp_object.upper_bound && character_location.y == tzp_object.upper_bound)
                                {
                                    // The character location is reset back to the origin at the start of each level
                                    character_location.x = 0;
                                    character_location.y = 0;

                                    tzp_object.current_level++; // Incrementing the level variable to move to the next one

                                    // Since we have a maximum of 3 levels, the game execution stops at the end of the third
                                    if (tzp_object.current_level <= 3) // Executes for anything less than or equal to 3 levels
                                    {
                                        game_level.setText(toString().valueOf(tzp_object.current_level)); // Updating the level on the screen

                                        // Updating the locations after resetting them for the new level on the screen
                                        update_position_x.setText(toString().valueOf(character_location.x));
                                        update_position_y.setText(toString().valueOf(character_location.y));

                                        // Resetting the power up name and the vital sign boosts
                                        power_up_name = "";
                                        xp_boost = 0;
                                        health_boost = 0;
                                        attack_boost = 0;
                                        defence_boost = 0;

                                        initial_xp = vital_signs_array[0]; // Resetting the initial XP value at the beginning of every level to the XP points at the start of that level

                                        // Resetting the power-up name, location and comment on the screen
                                        power_up_x.setText("-");
                                        power_up_y.setText("-");
                                        power_up_message.setText("");
                                        new_power_up.setText("");
                                        power_up_tagline.setText("");

                                        // Resetting the movement log and its index at the beginning of every level
                                        tzp_object.movement_log_index = 0;
                                        tzp_object.character_movement_log = new String[7];

                                        // Resetting the boss details at the beginning of every level
                                        boss.setText("");
                                        boss_message.setText("");
                                        boss_x.setText("");
                                        boss_y.setText("");
                                    }
                                    else // Game is over after the end of the third level is reached
                                    {
                                        game_end = true; // To switch off the tiles as game controller
                                        game_over_message.setText("Game Over"); // Endgame message
                                        game_level.setText("");

                                        connection.setAllTilesIdle(LED_COLOR_OFF); // Switching off all the tiles

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
                }
                catch (InterruptedException e)
                {

                }
            }
        };

        my_thread.start();
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
}