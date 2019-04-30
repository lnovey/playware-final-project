package com.example.tzp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.OnAntEventListener;

public class MainActivity extends AppCompatActivity  implements OnAntEventListener
{
    // Defining a MotoConnection instance
    MotoConnection connection;

    // Setting the behaviour of our button
    // Defining a button instance
    // Boolean to monitor the pairing status
    Button pairing_button;
    boolean is_pairing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialising moto connection in the onCreate() method
        connection = MotoConnection.getInstance();
        connection.startMotoConnection(MainActivity.this);
        connection.saveRfFrequency(46); // (Group number*10) + 6
        connection.setDeviceId(4);
        connection.registerListener(MainActivity.this);

        // Configuring the button in onCreate()
        pairing_button = findViewById(R.id.pairing_button);
        pairing_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!is_pairing)
                {
                    // pairTilesStart() - Starts the pairing process,
                    // the tiles in this state are spinning in red color.
                    // Press all the spinning tiles to pair the tiles to the Android device
                    // and then call pairTilesStop to end pairing.
                    // Note that every time you start the pairing the id index starts at 1.
                    connection.pairTilesStart();
                    pairing_button.setText("Stop Pairing");
                }
                else
                {
                    // pairTilesStop() - Stops the pairing procedure.
                    connection.pairTilesStop();
                    pairing_button.setText("Start Pairing");
                }
                is_pairing = !is_pairing;
            }
        });
    }

    @Override
    // onMessageReceived() is called when an ANT message in received from the tile
    public void onMessageReceived(byte[] message, long time)
    {

    }

    @Override
    // on AntServiveConnected() is called when the moto tile is ready to send and receive messages
    public void onAntServiceConnected()
    {
        // setAllTilesToInit() - called to put the tiles in the init mode
        // In this mode the Android device will check the active tiles
        // populate the connectedTiles array
        // connectedTiles array - An ArrayList containing all the connected tiles ids
        connection.setAllTilesToInit();
    }

    @Override
    // onNumbersOfTilesConnected() - called when the number of tiles connected changes
    public void onNumbersOfTilesConnected(int connected)
    {

    }

    //super - keyword in java is a reference variable that is
    // used to refer to immediate parent class object

    // Once we disconnect with the tile(s), we need to quit the application
    @Override
    protected void onPause()
    {
        super.onPause();
        // stopMotoConnection() - Stops the ANT connection and clear the connection service
        connection.stopMotoConnection();
        // unregisterListener() - Remove the listener class
        // from the list of classes to be notified in case of new ANT events
        connection.unregisterListener(MainActivity.this);
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        // startMotoConnection() - Starts a connection service between the Android device and the tiles
        connection.startMotoConnection(MainActivity.this);
        // registerListener() - Add the listener class to the
        // list of classes to be notified in case of new ANT events
        connection.registerListener(MainActivity.this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // stopMotoConnection() - Stops the ANT connection and clear the connection service
        connection.stopMotoConnection();
        // unregisterListener() - Remove the listener class
        // from the list of classes to be notified in case of new ANT events
        connection.unregisterListener(MainActivity.this);
    }

    // This function is called when the Play Game button is pressed
    public void play_game_button(View view)
    {
        Intent start_game_intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(start_game_intent);
    }

    // This function is called when the About Game button is pressed
    public void about_game_button(View view)
    {
        Intent about_game_intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(about_game_intent);
    }

    // This function is called when the Instructions button is pressed
    public void see_instructions_button(View view)
    {
        Intent instructions_intent = new Intent(MainActivity.this, InstructionActivity.class);
        startActivity(instructions_intent);
    }
}