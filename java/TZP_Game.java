package com.example.finalproject;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.livelife.motolibrary.GameType;

import com.livelife.motolibrary.AntData;
import com.livelife.motolibrary.Game;
import com.livelife.motolibrary.GameType;
import com.livelife.motolibrary.MotoConnection;

import java.util.Random;

import static com.livelife.motolibrary.AntData.EVENT_PRESS;
import static com.livelife.motolibrary.AntData.LED_COLOR_BLUE;
import static com.livelife.motolibrary.AntData.LED_COLOR_GREEN;
import static com.livelife.motolibrary.AntData.LED_COLOR_OFF;
import static com.livelife.motolibrary.AntData.LED_COLOR_ORANGE;
import static com.livelife.motolibrary.AntData.LED_COLOR_VIOLET;
import static com.livelife.motolibrary.AntData.LED_COLOR_WHITE;

public class TZP_Game extends Game
{
    MotoConnection connection = MotoConnection.getInstance();

    // Initial X and Y locations of our character
    int character_start_x = 0;
    int character_start_y = 0;

    // Bounds of the world in our game
    int upper_bound = 20;
    int lower_bound = 0;


    public TZP_Game()
    {
        setName("TZP Game");
        setMaxPlayers(1);
        GameType gt = new GameType(0, GameType.GAME_TYPE_TIME,60,"1 Player 1 min",1);
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
            }
            else if (colour == LED_COLOR_BLUE) // Left (Negative X axis)
            {
                this.getOnGameEventListener().onGameMessage("nx");
                incrementPlayerScore(0, 1);
            }
            else if (colour == LED_COLOR_VIOLET) // Up (Positive Y axis)
            {
                this.getOnGameEventListener().onGameMessage("py");
                incrementPlayerScore(1, 1);
            }
            else if (colour == LED_COLOR_WHITE) // Down (Negative Y axis)
            {
                this.getOnGameEventListener().onGameMessage("ny");
                incrementPlayerScore(0, 1);
            }
        }
    }

    // This function sets up the tiles as a game controller and assigns a certain colour to a tile
    public void tile_controller()
    {
        for(int t:connection.connectedTiles)
        {
            if (t == 1)
            {
                connection.setTileColor(AntData.LED_COLOR_BLUE, t);
            }
            else if (t == 2)
            {
                connection.setTileColor(AntData.LED_COLOR_GREEN, t);
            }
            else if (t == 3)
            {
                connection.setTileColor(AntData.LED_COLOR_VIOLET, t);
            }
            else if (t == 4)
            {
                connection.setTileColor(AntData.LED_COLOR_WHITE, t);
            }
        }
    }

    // This function provides the magical objects one at a time randomly
    public String magical_objects()
    {
        int magical_objects = 11; // Stores the number of magical objects

        Random random_object = new Random(); // Object of the Random() class
        int random_number = random_object.nextInt(magical_objects) + 1;

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
}