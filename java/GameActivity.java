package com.example.tzp;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    Point character_location = tzp_object.character_location; // Holds the location of the character

    // Array that holds the details about the character's vital signs
    // Index 0 -> XP points
    // Index 1 -> Health
    // Index 2 -> Attack
    // Index 3 -> Defence
    int[] vital_signs_array = new int[4];

    // Boosts for character vitals
    int xp_boost = 0;
    int health_boost = 0;

    String axis = "\0"; // Stores which axis we are moving on

    int current_level = 1; // Stores the level number that is character is currently on

    boolean game_end = false; // Boolean that is checked when setting up the tiles as a game controller

    String seven_moves = "\0"; // We use this string to check when seven moves are over

    String power_up_name = "\0"; // Stores the name of the power up

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
        TextView display_xp = findViewById(R.id.xp_value);
        TextView display_health = findViewById(R.id.health_value);
        TextView display_attack = findViewById(R.id.attack_value);
        TextView display_defence= findViewById(R.id.defence_value);

        // Textview variables for the character's vital signs
        final TextView power_up_x = findViewById(R.id.power_up_x);
        final TextView power_up_y = findViewById(R.id.power_up_y);

        // Textview variable for the game level
        final TextView game_level = findViewById(R.id.level_number);
        final TextView game_over_message = findViewById(R.id.level);

        final TextView power_up_message = findViewById(R.id.power_up_received);
        final TextView new_power_up = findViewById(R.id.power_up_name);

        // Showing the initial location of our character on the screen
        update_position_x.setText(toString().valueOf(character_location.x));
        update_position_y.setText(toString().valueOf(character_location.y));

        vital_signs_array = tzp_object.set_up_vital_signs(vital_signs_array);

        // Showing the initial vital signs on the screen
        display_xp.setText(toString().valueOf(vital_signs_array[0]));
        display_health.setText(toString().valueOf(vital_signs_array[1]));
        display_attack.setText(toString().valueOf(vital_signs_array[2]));
        display_defence.setText(toString().valueOf(vital_signs_array[3]));

        // Displaying the initial level on the screen
        game_level.setText(toString().valueOf(current_level));

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

        Thread my_thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while (true)
                    {
                        Thread.sleep(2500); // Giving a delay of 2.5 seconds before switching to the next level
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
}