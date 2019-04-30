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
    int power_up_location_x = 0;
    int power_up_location_y = 0;
    Point power_up_location = new Point(power_up_location_x, power_up_location_y);

    // Bounds of the world in our game
    int upper_bound = 20;
    int lower_bound = 0;

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
        GameType gt = new GameType(0, GameType.GAME_TYPE_TIME,300,"1 Player 1 min",1);
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

        //Log.v("Game","Tile ID:"+AntData.getId(message));

        if (event == EVENT_PRESS)
        {
            if (colour == LED_COLOR_GREEN) // Right (Positive X axis)
            {
                this.getOnGameEventListener().onGameMessage("px");
                incrementPlayerScore(1, 1);
                character_movement_log[movement_log_index] = "px";
                //Log.v("Game","Log:"+character_movement_log[movement_log_index]);
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
                //Log.v("Game","Log:"+character_movement_log[movement_log_index]);
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
                //Log.v("Game","Log:"+character_movement_log[movement_log_index]);
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
                //Log.v("Game","Log:"+character_movement_log[movement_log_index]);
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

    // This function sets up the intial vital signs of the character -> XP, Health, Attack, Defence
    public int[] set_up_vital_signs(int[] vital_signs_array)
    {
        vital_signs_array[0] = 5;
        vital_signs_array[1] = 5;
        vital_signs_array[2] = 5;
        vital_signs_array[3] = 5;

        return vital_signs_array;
    }

    // This function provides the magical objects one at a time randomly
    public String power_up_generator()
    {
        int magical_objects = 11; // Stores the number of magical objects

        Random random_object = new Random(); // Object of the Random() class
        int random_number = random_object.nextInt(magical_objects) + 1; // Generating a random number that corresponds to one of the objects

        String random_magical_object = "\0"; // Stores the name of the magical object

        switch(random_number)
        {
            case 1:
                random_magical_object = "Sorting hat";
                break;
            case 2:
                random_magical_object = "Deadpool's sword";
                break;
            case 3:
                random_magical_object = "Webs";
                break;
            case 4:
                random_magical_object = "Arc reactor";
                break;
            case 5:
                random_magical_object = "Captain America's shield";
                break;
            case 6:
                random_magical_object = "Mjölnir";
                break;
            case 7:
                random_magical_object = "Symbiote";
                break;
            case 8:
                random_magical_object = "Adamantium Claws";
                break;
            case 9:
                random_magical_object = "Hulkbuster";
                break;
            case 10:
                random_magical_object = "Stormbreaker";
                break;
            case 11:
                random_magical_object = "The sword of Gryffindor";
                break;
            case 12:
                random_magical_object = "Fawke's the pheonix";
                break;
        }
        return random_magical_object;
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

        for (int i = 0; i < 4; i++)
        {
            Log.v("Game","Moves count:"+moves_count_array[i]);
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

        System.out.print("Which box will have the power-up: ");
        System.out.println(quarter_probability);

        if (moves_count_array[0] == 120) // The power-up will be generated along the x axis
        {
            System.out.println("Since the character has moved more along the x axis, the power-up will be generated on the x axis");

            System.out.print("Movement along positive x axis: ");
            System.out.println(positive_steps);

            System.out.print("Movement along negative x axis: ");
            System.out.println(negative_steps);

            power_up_location.y = character_location.y; // The y location of the power-up is the same as that of the character

            if (positive_steps > negative_steps) // The user has moved in the positive direction more
            {
                System.out.println("Since movement is more along positive x axis, the power up will be generated on the positive x axis");

                power_up_location.x = character_location.x + quarter_probability; // Generating the x location of our power-up
                System.out.print("X location of the character: ");

                System.out.println(character_location.x);

                if (power_up_location.x > upper_bound) // To make sure that the power-up is generated within our world
                {
                    power_up_location.x = upper_bound; // Placed at the edge of our world
                }
            }

            else if (negative_steps > positive_steps) // The user has moved in the negative direction more
            {
                System.out.println("Since movement is more along negative x axis, the power up will be generated on the negative x axis");

                System.out.print("X location of the character: ");
                System.out.println(character_location.x);

                power_up_location.x = character_location.x - quarter_probability; // Generating the x location of our power-up
                if (power_up_location.x < lower_bound) // To make sure that the power-up is generated within our world
                {
                    power_up_location.x = lower_bound; // Placed at the edge of our world
                }
            }
            else if (positive_steps == negative_steps) // The user has moved in both directions by the same amount
            {
                System.out.println("Equal steps");

                int direction_probability = random_object.nextInt(2); // Choosing between the positive or negative directions with an equal probability

                if (direction_probability == 0) // Positive direction
                {
                    System.out.println("Positive direction");

                    System.out.print("X location of the character: ");
                    System.out.println(character_location.x);

                    power_up_location.x = character_location.x + quarter_probability; // Generating the x location of our power-up
                    if (power_up_location.x > upper_bound) // To make sure that the power-up is generated within our world
                    {
                        power_up_location.x = upper_bound; // Placed at the edge of our world
                    }
                }
                else // Negative direction
                {
                    System.out.println("Negative direction");

                    System.out.print("X location of the character: ");
                    System.out.println(character_location.x);

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
            System.out.println("Since the character has moved more along the y axis, the power up will be generated on the y axis");

            System.out.print("Movement along positive y axis: ");
            System.out.println(positive_steps);

            System.out.print("Movement along negative y axis: ");
            System.out.println(negative_steps);

            power_up_location.x = character_location.x; // The x location of the power-up is the same as that of the character

            if (positive_steps > negative_steps) // The user has moved in the positive direction more
            {
                System.out.println("Since movement is more along positive y axis, the power up will be generated on the positive y axis");

                System.out.print("Y location of the character: ");
                System.out.println(character_location.y);

                power_up_location.y = character_location.y + quarter_probability; // Generating the y location of our power-up
                if (power_up_location.y > upper_bound) // To make sure that the power-up is generated within our world
                {
                    power_up_location.y = upper_bound; // Placed at the edge of our world
                }
            }
            else if (negative_steps > positive_steps) // The user has moved in the negative direction more
            {
                System.out.println("Since movement is more along negative y axis, the power up will be generated on the negative y axis");

                System.out.print("Y location of the character: ");
                System.out.println(character_location.y);

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
                    System.out.println("Positive direction");

                    System.out.print("Y location of the character: ");
                    System.out.println(character_location.y);

                    power_up_location.y = character_location.y + quarter_probability; // Generating the y location of our power-up
                    if (power_up_location.y > upper_bound) // To make sure that the power-up is generated within our world
                    {
                        power_up_location.y = upper_bound; // Placed at the edge of our world
                    }
                }
                else // Negative direction
                {
                    System.out.println("Negative direction");

                    System.out.print("Y location of the character: ");
                    System.out.println(character_location.y);

                    power_up_location.y = character_location.y - quarter_probability; // Generating the y location of our power-up
                    if (power_up_location.y < lower_bound) // To make sure that the power-up is generated within our world
                    {
                        power_up_location.y = lower_bound; // Placed at the edge of our world
                    }
                }
            }
        }

        System.out.print("X location of power up:");
        System.out.println(power_up_location.x);
        System.out.print("Y location of power up:");
        System.out.println(power_up_location.y);

        return power_up_location;
    }
}