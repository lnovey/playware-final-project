package com.example.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.livelife.motolibrary.MotoConnection;
import com.livelife.motolibrary.OnAntEventListener;

public class MainActivity_fight_sim extends AppCompatActivity implements OnAntEventListener {

    MotoConnection connection;
    Button paringButton;
    Button startGameButton;
    Button testFight;
    boolean isParing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fight_sim_activity_main);

        connection=MotoConnection.getInstance();
        connection.startMotoConnection(MainActivity_fight_sim.this);
        connection.saveRfFrequency(46); //(Group No.)*10+6
        connection.setDeviceId(4); //Your group number
        connection.registerListener(MainActivity_fight_sim.this);

        startGameButton = findViewById(R.id.startGameButton);
        paringButton = findViewById(R.id.paringButton);
        testFight = findViewById(R.id.testFight);

        testFight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //connection.setTileColor(LED_COLOR_RED, 1);
                connection.unregisterListener(MainActivity_fight_sim.this);
                Intent i = new Intent(MainActivity_fight_sim.this, BattleActivity.class);
                startActivity(i);
            }
        });

        paringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isParing){
                    connection.pairTilesStart();
                    paringButton.setText("Stop Paring");
                } else {
                    connection.pairTilesStop();
                    paringButton.setText("Start Paring");
                }
                isParing = !isParing;
            }
        });


        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //connection.setTileColor(LED_COLOR_RED, 1);
                connection.unregisterListener(MainActivity_fight_sim.this);
                Intent i = new Intent(MainActivity_fight_sim.this, PathAlgorithmActivity.class);
                startActivity(i);
            }
        });
    }




    @Override
    public void onMessageReceived(byte[] bytes, long l) {
    }
    @Override
    public void onAntServiceConnected() {
        connection.setAllTilesToInit();
    }
    @Override
    public void onNumbersOfTilesConnected(int i) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        connection.stopMotoConnection();
        connection.unregisterListener(MainActivity_fight_sim.this);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        connection.startMotoConnection(MainActivity_fight_sim.this);
        connection.registerListener(MainActivity_fight_sim.this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.stopMotoConnection();
        connection.unregisterListener(MainActivity_fight_sim.this);
    }

}



