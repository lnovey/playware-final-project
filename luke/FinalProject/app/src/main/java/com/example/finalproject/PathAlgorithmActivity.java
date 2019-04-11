package com.example.finalproject;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.livelife.motolibrary.MotoConnection;

import java.util.Arrays;


//class has a 2d array of buttons, methods that return easy, medium, hard paths
//paths are 1d ordered arrays indicating what order buttons will be lit
public class PathAlgorithmActivity extends AppCompatActivity {
    final int numButtons = 9;
    final int easyPathLength = 3;
    final int medPathLength = 5;
    final int hardPathLength = 7;

    boolean[][] seen = new boolean[3][3];

    MotoConnection connection = MotoConnection.getInstance();
    Button[] buttons = new Button[numButtons];
    Button[][] bA = new Button[3][3];
    Button b0;
    Button b1;
    Button b2;
    Button b3;
    Button b4;
    Button b5;
    Button b6;
    Button b7;
    Button b8;

    Button easyPathGen;
    Button medPathGen;
    Button hardPathGen;
    Button clearColors;


    //btn.setBackgroundColor(Color.WHITE);
    //btn.setTextColor(Color.BLACK);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_algorithm);


        Button[] medPath = new Button[medPathLength];
        Button[] hardPath = new Button[hardPathLength];

        clearColors = findViewById(R.id.clearColors);

        b0= findViewById(R.id.b0);
        b1= findViewById(R.id.b1);
        b2= findViewById(R.id.b2);
        b3= findViewById(R.id.b3);
        b4= findViewById(R.id.b4);
        b5= findViewById(R.id.b5);
        b6= findViewById(R.id.b6);
        b7= findViewById(R.id.b7);
        b8= findViewById(R.id.b8);
        //b0.setBackgroundColor(Color.MAGENTA);

        buttons[0] = b0;
        buttons[1] = b1;
        buttons[2] = b2;
        buttons[3] = b3;
        buttons[4] = b4;
        buttons[5] = b5;
        buttons[6] = b6;
        buttons[7] = b7;
        buttons[8] = b8;


        int count = 0;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j <3; j++){
                bA[i][j] = buttons[count];
                //buttonArray[i][j].setBackgroundColor
                 //       (Color.argb(count*15, count*15,
                  //              count*15, 0));
                count ++;
            }
        }


        clearColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Button[] eachRow : PathAlgorithmActivity.this.bA){
                    for (Button each : eachRow){
                        each.setBackgroundColor(Color.GRAY);
                    }

                }

            }
        });


        easyPathGen = findViewById(R.id.easyPathButton);
        medPathGen = findViewById(R.id.medPathButton);
        hardPathGen = findViewById(R.id.hardPathButton);

        medPathGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pathCount = 1;
                int intensity = 40;

                Button[] medPath = new Button[medPathLength];

                PathAlgorithmActivity.this.medPathGen(medPath);

                for (Button each : medPath){
                    if (each == null){
                        System.out.println("we have a null reference: "+ pathCount);
                    } else {
                        each.setBackgroundColor(Color.argb(
                                pathCount * intensity, pathCount*intensity/3,
                                3*intensity, 3*intensity
                        ));

                    }

                    pathCount++;

                }
                medPath[0].setBackgroundColor(Color.MAGENTA);

            }
        });


        easyPathGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pathCount = 1;
                int intensity = 60;

                Button[] easyPath = new Button[easyPathLength];

                PathAlgorithmActivity.this.easyPathGen(easyPath);

                for (Button each : easyPath){

                    each.setBackgroundColor(Color.argb(
                            pathCount * intensity, pathCount*intensity,
                            pathCount*intensity, 0
                    ));
                    pathCount++;

                }
                easyPath[0].setBackgroundColor(Color.MAGENTA);
            }
        });

        //this.easyPathGen(easyPath);

/*
        easyPath[0].setBackgroundColor(Color.MAGENTA);

        pathCount = 1;
        intensity = 30;

        this.medPathGen(medPath);
        for (Button each : medPath){
            each.setBackgroundColor(Color.argb(
                    pathCount * intensity, pathCount*intensity,
                    pathCount*intensity, 0
            ));
            pathCount++;
        }
        */


    }

    private  boolean isDirGood(int x, int y){
        if (x < 0 || x > 2 || y < 0 || y > 2){
            return false;
        }
        if (seen[x][y]) {
            return false;
        }

        return true;
    }

    private void medPathGen(Button[] returner){
        int x=0, y=0;
        Arrays.fill(seen[0], false);
        int rand;
        Button curr = bA[x][y];
        returner[0] = curr;


        int currPathLength = 1;
        int dirTried = 0;
        boolean newPath = false;

        for (boolean[] each : seen){
            Arrays.fill(each, false);
        }
        seen[x][y] = true;



        while (currPathLength < medPathLength){
            rand = (int) (Math.random() * 88);
            rand = rand % 8;


            while (dirTried < 8 && !newPath){
                System.out.print("trying a new direction: " + rand);
                System.out.println(" weve tried: " + dirTried + "directions");
                switch (rand){//each case checks the direction, if available adds that button
                              //to the seen array, and to the path, increments path length
                              //indicates new path found,
                    case 0: //left
                        if (isDirGood(x-1,y)){
                            x = x-1;
                            curr = bA[x][y];
                            seen[x][y] = true;
                            returner[currPathLength] = curr;
                            newPath = true;
                            currPathLength++;
                        } else {
                            dirTried++;
                        }

                        break;
                    case 1://leftup
                        if (isDirGood(x-1,y+1)){
                            x = x-1;
                            y=y+1;
                            curr = bA[x][y];
                            seen[x][y] = true;
                            returner[currPathLength] = curr;
                            newPath = true;
                            currPathLength++;

                        } else {
                            dirTried++;
                        }
                        break;
                    case 2://up
                        if (isDirGood(x,y+1)){
                            y=y+1;
                            curr = bA[x][y];
                            seen[x][y] = true;
                            returner[currPathLength] = curr;
                            newPath = true;
                            currPathLength++;

                        } else {
                            dirTried++;
                        }
                        break;
                    case 3://rightup
                        if (isDirGood(x+1,y+1)){
                            x = x+1;
                            y=y+1;
                            curr = bA[x][y];
                            seen[x][y] = true;
                            returner[currPathLength] = curr;
                            newPath = true;
                            currPathLength++;

                        } else {
                            dirTried++;
                        }
                        break;
                    case 4://right
                        if (isDirGood(x+1,y)){
                            x = x+1;
                            curr = bA[x][y];
                            seen[x][y] = true;
                            returner[currPathLength] = curr;
                            newPath = true;
                            currPathLength++;

                        } else {
                            dirTried++;
                        }
                        break;
                    case 5://rightdown
                        if (isDirGood(x+1,y-1)){
                            x = x+1;
                            y=y-1;
                            curr = bA[x][y];
                            seen[x][y] = true;
                            returner[currPathLength] = curr;
                            newPath = true;
                            currPathLength++;

                        } else {
                            dirTried++;
                        }
                        break;
                    case 6://down
                        if (isDirGood(x,y-1)){
                            y=y-1;
                            curr = bA[x][y];
                            seen[x][y] = true;
                            returner[currPathLength] = curr;
                            newPath = true;
                            currPathLength++;

                        } else {
                            dirTried++;
                        }
                        break;
                    case 7://leftdown
                        if (isDirGood(x-1,y-1)){
                            x = x-1;
                            y=y-1;
                            curr = bA[x][y];
                            seen[x][y] = true;
                            returner[currPathLength] = curr;
                            newPath = true;
                            currPathLength++;

                        } else {
                            dirTried++;
                        }
                        break;
                    default://something is wrong!
                        System.out.println("something is wrong, default dir loop");
                        return;


                }
                //dirTried = dirTried + 1;
                rand = (rand+1)%8;
            }
            dirTried = 0;
            System.out.println("out of while loop, went: " + (rand-1) +
                    "newpath was a " + newPath);
            if (!newPath){ //we tried all directions and none worked, end the path
                return;
            }
            newPath = false;


        }



    }




    private void easyPathGen(Button[] returner){
        double rand;
        int pos = 00;
        Button start = bA[0][0];
        returner[0] = start;

        for (int count = 1; count < returner.length; count ++){
            rand = Math.random();

            if (rand < .33) { //dir=up
                pos = pos + 10;
                returner[count] = bA [pos/10][pos%10];
                //bA [pos/10][pos%10].setBackgroundColor(Color.argb(
                 //       count*20, count*20,0, 0));

            } else if (rand < .66) { //dir = diag
                pos = pos + 11;
                returner[count] = bA [pos/10][pos%10];
                //bA [pos/10][pos%10].setBackgroundColor(Color.argb(
                 //       count*20, count*20,0, 0));

            } else { // dir = right
                pos = pos + 1;
                returner[count] = bA [pos/10][pos%10];
                //bA [pos/10][pos%10].setBackgroundColor(Color.argb(
                 //       count*20, count*20,0, 0));
            }
        }




    }


}
