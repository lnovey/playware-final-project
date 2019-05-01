package com.example.tzp;

import android.graphics.Point;
import android.util.Log;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.MotoConnection;

import java.util.Random;

import static com.livelife.motolibrary.AntData.EVENT_PRESS;
import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;
import static com.livelife.motolibrary.AntData.LED_COLOR_BLUE;
import static com.livelife.motolibrary.AntData.LED_COLOR_GREEN;
import static com.livelife.motolibrary.AntData.LED_COLOR_ORANGE;
import static com.livelife.motolibrary.AntData.LED_COLOR_VIOLET;
import static com.livelife.motolibrary.AntData.LED_COLOR_WHITE;

public class TZP_Game extends Game
{
    MotoConnection connection = MotoConnection.getInstance();

    // Stores the location of our character
    int character_location_x = 0;
    int character_location_y = 0;
    Point character_location = new Point(character_location_x, character_location_y);

    // Holds the location where the power-ups will be generated
    int power_up_location_x = 1729;
    int power_up_location_y = 1729;
    Point power_up_location = new Point(power_up_location_x, power_up_location_y);

    // Bounds of the world in our game
    int upper_bound = 20;
    int lower_bound = 0;

    int current_level = 1; // Stores the level number that is character is currently on

    String[] character_movement_log = new String[7]; // Array to log the movement of the character
    int movement_log_index = 0; // Index for the above array

    // Array that holds the details about the character's movement
    // Index 0 -> ASCII code for the axis it is moving along
    // Index 1 -> Units moved along that axis
    // Index 2 -> Units moved along the positive direction of that axis
    // Index 3 -> Units moved along the negative direction of that axis
    int[] moves_count_array = new int[4];

    // Setting up the Game Type using the constructor
    public TZP_Game()
    {
        setName("TZP Game");
        setMaxPlayers(1);
        GameType gt = new GameType(0, GameType.GAME_TYPE_TIME,1200,"1 Player 1 min",1);
        addGameType(gt);
    }

    @Override
    public void onGameStart()
    {
        super.onGameStart();
        connection.setAllTilesIdle(LED_COLOR_OFF);
    }

    @Override
    public void onGameEnd()
    {
        super.onGameEnd();
        connection.setAllTilesBlink(1, LED_COLOR_ORANGE);
    }

    @Override
    public void onGameUpdate(byte[] message)
    {
        super.onGameUpdate(message);
        int event = AntData.getCommand(message);
        int colour = AntData.getColorFromPress(message);

        if (event == EVENT_PRESS)
        {
            if (colour == LED_COLOR_GREEN) // Right (Positive X axis)
            {
                this.getOnGameEventListener().onGameMessage("px");
                incrementPlayerScore(1, 1);

                character_movement_log[movement_log_index] = "px";
                movement_log_index++;

                if (movement_log_index == 7)
                {
                    this.getOnGameEventListener().onGameMessage("true");

                    // After every 7 moves we check which axis (positive x, negative x, positive y, negative y)
                    // the user has moved along more
                    // Then a power-up is generated accordingly
                    moves_count_array = count_moves(character_movement_log);
                    power_up_location = power_up_location_generator(moves_count_array);

                    // Resetting the index and the array after every 7 moves
                    // We need to keep overwriting the character_movement_log for every 7 moves
                    movement_log_index = 0;
                    character_movement_log = new String[7];
                }
            }
            else if (colour == LED_COLOR_BLUE) // Left (Negative X axis)
            {
                this.getOnGameEventListener().onGameMessage("nx");
                incrementPlayerScore(0, 1);

                character_movement_log[movement_log_index] = "nx";
                movement_log_index++;

                // Resetting the log when the final index is reached
                if (movement_log_index == 7)
                {
                    this.getOnGameEventListener().onGameMessage("true");

                    // After every 7 moves we check which axis (positive x, negative x, positive y, negative y)
                    // the user has moved along more
                    // Then a power-up is generated accordingly
                    moves_count_array = count_moves(character_movement_log);
                    power_up_location = power_up_location_generator(moves_count_array);

                    // Resetting the index and the array after every 7 moves
                    // We need to keep overwriting the character_movement_log for every 7 moves
                    movement_log_index = 0;
                    character_movement_log = new String[7];
                }
            }
            else if (colour == LED_COLOR_VIOLET) // Up (Positive Y axis)
            {
                this.getOnGameEventListener().onGameMessage("py");
                incrementPlayerScore(1, 1);

                character_movement_log[movement_log_index] = "py";
                movement_log_index++;

                // Resetting the log when the final index is reached
                if (movement_log_index == 7)
                {
                    this.getOnGameEventListener().onGameMessage("true");

                    // After every 7 moves we check which axis (positive x, negative x, positive y, negative y)
                    // the user has moved along more
                    // Then a power-up is generated accordingly
                    moves_count_array = count_moves(character_movement_log);
                    power_up_location = power_up_location_generator(moves_count_array);

                    // Resetting the index and the array after every 7 moves
                    // We need to keep overwriting the character_movement_log for every 7 moves
                    movement_log_index = 0;
                    character_movement_log = new String[7];
                }
            }
            else if (colour == LED_COLOR_WHITE) // Down (Negative Y axis)
            {
                this.getOnGameEventListener().onGameMessage("ny");
                incrementPlayerScore(0, 1);

                character_movement_log[movement_log_index] = "ny";
                movement_log_index++;

                // Resetting the log when the final index is reached
                if (movement_log_index == 7)
                {
                    this.getOnGameEventListener().onGameMessage("true");

                    // After every 7 moves we check which axis (positive x, negative x, positive y, negative y)
                    // the user has moved along more
                    // Then a power-up is generated accordingly
                    moves_count_array = count_moves(character_movement_log);
                    power_up_location = power_up_location_generator(moves_count_array);

                    // Resetting the index and the array after every 7 moves
                    // We need to keep overwriting the character_movement_log for every 7 moves
                    movement_log_index = 0;
                    character_movement_log = new String[7];
                }
            }
        }
    }

    // This function sets up the tiles as a game controller and assigns a certain colour to a tile
    public void tile_controller()
    {
        // Looping through the set of connected tiles and setting them to their corresponding colours using the tile IDs
        for(int t:connection.connectedTiles)
        {
            if (t == 1)
            {
                connection.setTileColor(AntData.LED_COLOR_BLUE, t); // Tile ID 1 is set to BLUE
            }
            else if (t == 2)
            {
                connection.setTileColor(AntData.LED_COLOR_GREEN, t); // Tile ID 2 is set to GREEN
            }
            else if (t == 3)
            {
                connection.setTileColor(AntData.LED_COLOR_VIOLET, t); // Tile ID 3 is set to VIOLET
            }
            else if (t == 4)
            {
                connection.setTileColor(AntData.LED_COLOR_WHITE, t); // Tile ID 4 is set to WHITE
            }
        }
    }

    // This function sets up the initial vital signs of the character -> XP, Health, Attack, Defence
    public int[] set_up_vital_signs(int[] vital_signs_array)
    {
        vital_signs_array[0] = 5;
        vital_signs_array[1] = 5;
        vital_signs_array[2] = 5;
        vital_signs_array[3] = 5;

        return vital_signs_array;
    }

    // This function provides the magical objects one at a time randomly
    public String[] power_up_generator()
    {
        // We store the details about our weapons in an array of objects
        // Index 0 -> Weapon name
        // Index 1 -> XP points
        // Index 2 -> Health
        // Index 3 -> Attack
        // Index 4 -> Defence
        String[] weapon_details = new String[5];

        int power_ups_level_one = 7; // Stores the number of power-ups offered in Level 1
        int power_ups_level_two = 6; // Stores the number of power-ups offered in Level 2
        int power_ups_level_three = 5; // Stores the number of power-ups offered in Level 3

        Random random_object = new Random(); // Object of the Random() class

        switch(current_level)
        {
            case 1: // Level 1 power-ups

                int random_number_level_one = random_object.nextInt(power_ups_level_one) + 1; // Generating a random number that corresponds to one of the objects
                switch (random_number_level_one)
                {
                    case 1:
                        weapon_details[0] = "The Sorting Hat"; // Power-up name
                        weapon_details[1] = "1"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "0"; // Attack
                        weapon_details[4] = "0"; // Defence
                        break;

                    case 2:
                        weapon_details[0] = "Deadpool's Sword"; // Power-up name
                        weapon_details[1] = "1"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "3"; // Attack
                        weapon_details[4] = "1"; // Defence
                        break;

                    case 3:
                        weapon_details[0] = "Web Cartridges and Shooters"; // Power-up name
                        weapon_details[1] = "2"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "4"; // Attack
                        weapon_details[4] = "2"; // Defence
                        break;

                    case 4:
                        weapon_details[0] = "Iron Man's Arc Reactor"; // Power-up name
                        weapon_details[1] = "5"; // XP
                        weapon_details[2] = "3"; // Health
                        weapon_details[3] = "7"; // Attack
                        weapon_details[4] = "1"; // Defence
                        break;

                    case 5:
                        weapon_details[0] = "Captain America's Shield"; // Power-up name
                        weapon_details[1] = "5"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "5"; // Attack
                        weapon_details[4] = "4"; // Defence
                        break;

                    case 6:
                        weapon_details[0] = "Adamantium Claws"; // Power-up name
                        weapon_details[1] = "5"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "5"; // Attack
                        weapon_details[4] = "3"; // Defence
                        break;

                    case 7:
                        weapon_details[0] = "Hawkeye's Bow and Arrow"; // Power-up name
                        weapon_details[1] = "5"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "5"; // Attack
                        weapon_details[4] = "3"; // Defence
                        break;
                }
                break;

            case 2: // Level 2 power-ups

                int random_number_level_two = random_object.nextInt(power_ups_level_two) + 1; // Generating a random number that corresponds to one of the objects

                switch (random_number_level_two)
                {
                    case 1:
                        weapon_details[0] = "The Symbiote Suit"; // Power-up name
                        weapon_details[1] = "5"; // XP
                        weapon_details[2] = "2"; // Health
                        weapon_details[3] = "10"; // Attack
                        weapon_details[4] = "5"; // Defence
                        break;

                    case 2:
                        weapon_details[0] = "The Pym Particle"; // Power-up name
                        weapon_details[1] = "5"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "10"; // Attack
                        weapon_details[4] = "5"; // Defence
                        break;

                    case 3:
                        weapon_details[0] = "The War Machine Suit"; // Power-up name
                        weapon_details[1] = "7"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "8"; // Attack
                        weapon_details[4] = "5"; // Defence
                        break;

                    case 4:
                        weapon_details[0] = "The Hulkbuster Suit"; // Power-up name
                        weapon_details[1] = "10"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "15"; // Attack
                        weapon_details[4] = "10"; // Defence
                        break;

                    case 5:
                        weapon_details[0] = "Thor's Mjölnir"; // Power-up name
                        weapon_details[1] = "10"; // XP
                        weapon_details[2] = "3"; // Health
                        weapon_details[3] = "10"; // Attack
                        weapon_details[4] = "5"; // Defence
                        break;

                    case 6:
                        weapon_details[0] = "Mar-Vell's Nega-Bands"; // Power-up name
                        weapon_details[1] = "12"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "10"; // Attack
                        weapon_details[4] = "5"; // Defence
                        break;
                }
                break;

            case 3: // Level 3 power-ups

                int random_number_level_three = random_object.nextInt(power_ups_level_three) + 1; // Generating a random number that corresponds to one of the objects
                switch (random_number_level_three)
                {
                    case 1:
                        weapon_details[0] = "The Sword of Gryffindor"; // Power-up name
                        weapon_details[1] = "10"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "7"; // Attack
                        weapon_details[4] = "3"; // Defence
                        break;

                    case 2:
                        weapon_details[0] = "The Bleeding Edge Suit"; // Power-up name
                        weapon_details[1] = "10"; // XP
                        weapon_details[2] = "5"; // Health
                        weapon_details[3] = "12"; // Attack
                        weapon_details[4] = "10"; // Defence
                        break;

                    case 3:
                        weapon_details[0] = "The Eye of Agamotto"; // Power-up name
                        weapon_details[1] = "12"; // XP
                        weapon_details[2] = "0"; // Health
                        weapon_details[3] = "15"; // Attack
                        weapon_details[4] = "10"; // Defence
                        break;

                    case 4:
                        weapon_details[0] = "The Stormbreaker"; // Power-up name
                        weapon_details[1] = "15"; // XP
                        weapon_details[2] = "5"; // Health
                        weapon_details[3] = "12"; // Attack
                        weapon_details[4] = "5"; // Defence
                        break;

                    case 5:
                        weapon_details[0] = "Fawke's the Pheonix"; // Power-up name
                        weapon_details[1] = "15"; // XP
                        weapon_details[2] = "30"; // Health
                        weapon_details[3] = "5"; // Attack
                        weapon_details[4] = "3"; // Defence
                        break;
                }
                break;
        }
        return weapon_details;
    }

    // This function counts the number of moves made by the character along each axis
    public int[] count_moves(String[] character_movement_log)
    {
        // Variables to store the number of moves in x and y direction respectively
        int moves_x = 0;
        int moves_y = 0;

        // Variables to store the number of moves in positive and negative x direction respectively
        int moves_positive_x = 0;
        int moves_negative_x = 0;

        // Variables to store the number of moves in positive and negative y direction respectively
        int moves_positive_y = 0;
        int moves_negative_y = 0;

        // Array that holds the details about the character's movement
        // Index 0 -> ASCII code for the axis it is moving along
        // Index 1 -> Units moved along that axis
        // Index 2 -> Units moved along the positive direction of that axis
        // Index 3 -> Units moved along the negative direction of that axis
        int[] moves_count_array = new int[4];

        for (int i = 0; i < character_movement_log.length; i++)
        {
            if (character_movement_log[i].equals("py") ||  character_movement_log[i].equals("ny")) // Movement along y axis
            {
                moves_y++;
                if (character_movement_log[i].equals("py")) // Movement along positive y axis
                {
                    moves_positive_y++;
                }
                else if (character_movement_log[i].equals("ny")) // Movement along negative y axis
                {
                    moves_negative_y++;
                }
            }
            else if (character_movement_log[i].equals("px") || character_movement_log[i].equals("nx")) // Movement along x axis
            {
                moves_x++;
                if (character_movement_log[i].equals("px")) // Movement along positive x axis
                {
                    moves_positive_x++;
                }
                else if (character_movement_log[i].equals("nx")) // Movement along negative x axis
                {
                    moves_negative_x++;
                }
            }
        }

        if (moves_x > moves_y) // If the character has made more moves along the x axis
        {
            moves_count_array[0] = 120; // ASCII code for x
            moves_count_array[1] = moves_x;
            moves_count_array[2] = moves_positive_x;
            moves_count_array[3] = moves_negative_x;
        }
        else // If the character has made more moves along the y axis
        {
            moves_count_array[0] = 121; // ASCII code for y
            moves_count_array[1] = moves_y;
            moves_count_array[2] = moves_positive_y;
            moves_count_array[3] = moves_negative_y;
        }

        return moves_count_array;
    }

    public Point power_up_location_generator(int[] moves_count_array)
    {
        // Holds the location where the power-ups will be generated
        int power_up_location_x = 0;
        int power_up_location_y = 0;
        Point power_up_location = new Point(power_up_location_x, power_up_location_y);

        int positive_steps = moves_count_array[2]; // Storing the number of positive steps taken from our input argument array
        int negative_steps = moves_count_array[3]; // Storing the number of negative steps taken from our input argument array

        Random random_object = new Random(); // Object of the Random() class

        int quarter_probability = random_object.nextInt(4) + 1; // Generating a box with a probability of 25%

        System.out.println(quarter_probability);

        if (moves_count_array[0] == 120) // The power-up will be generated along the x axis
        {
            power_up_location.y = character_location.y; // The y location of the power-up is the same as that of the character

            if (positive_steps > negative_steps) // The user has moved in the positive direction more
            {
                power_up_location.x = character_location.x + quarter_probability; // Generating the x location of our power-up
                if (power_up_location.x > upper_bound) // To make sure that the power-up is generated within our world
                {
                    power_up_location.x = upper_bound; // Placed at the edge of our world
                }
            }

            else if (negative_steps > positive_steps) // The user has moved in the negative direction more
            {
                power_up_location.x = character_location.x - quarter_probability; // Generating the x location of our power-up
                if (power_up_location.x < lower_bound) // To make sure that the power-up is generated within our world
                {
                    power_up_location.x = lower_bound; // Placed at the edge of our world
                }
            }
            else if (positive_steps == negative_steps) // The user has moved in both directions by the same amount
            {
                int direction_probability = random_object.nextInt(2); // Choosing between the positive or negative directions with an equal probability

                if (direction_probability == 0) // Positive direction
                {
                   power_up_location.x = character_location.x + quarter_probability; // Generating the x location of our power-up
                     if (power_up_location.x > upper_bound) // To make sure that the power-up is generated within our world
                         {
                        power_up_location.x = upper_bound; // Placed at the edge of our world
                    }
                }
                else // Negative direction
                {
                    power_up_location.x = character_location.x - quarter_probability; // Generating the x location of our power-up
                    if (power_up_location.x < lower_bound) // To make sure that the power-up is generated within our world
                    {
                        power_up_location.x = lower_bound; // Placed at the edge of our world
                    }
                }
            }
        }
        else // The power-up will be generated along the y axis
        {
            power_up_location.x = character_location.x; // The x location of the power-up is the same as that of the character

            if (positive_steps > negative_steps) // The user has moved in the positive direction more
            {
                power_up_location.y = character_location.y + quarter_probability; // Generating the y location of our power-up
                if (power_up_location.y > upper_bound) // To make sure that the power-up is generated within our world
                {
                    power_up_location.y = upper_bound; // Placed at the edge of our world
                }
            }
            else if (negative_steps > positive_steps) // The user has moved in the negative direction more
            {
                power_up_location.y = character_location.y - quarter_probability; // Generating the y location of our power-up
                if (power_up_location.y < lower_bound) // To make sure that the power-up is generated within our world
                {
                    power_up_location.y = lower_bound; // Placed at the edge of our world
                }
            }
            else if (positive_steps == negative_steps) // The user has moved in both directions by the same amount
            {
                System.out.println("Equal steps");

                int direction_probability = random_object.nextInt(2); // Choosing between the positive or negative directions with an equal probability

                if (direction_probability == 0) // Positive direction
                {
                    power_up_location.y = character_location.y + quarter_probability; // Generating the y location of our power-up
                    if (power_up_location.y > upper_bound) // To make sure that the power-up is generated within our world
                    {
                        power_up_location.y = upper_bound; // Placed at the edge of our world
                    }
                }
                else // Negative direction
                {
                    power_up_location.y = character_location.y - quarter_probability; // Generating the y location of our power-up
                    if (power_up_location.y < lower_bound) // To make sure that the power-up is generated within our world
                    {
                        power_up_location.y = lower_bound; // Placed at the edge of our world
                    }
                }
            }
        }

        return power_up_location;
    }

    public void reset_power_up()
    {
         power_up_location.x = 1729;
         power_up_location.y = 1729;
    }
}