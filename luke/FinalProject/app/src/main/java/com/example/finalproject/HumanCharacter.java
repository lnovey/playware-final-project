package com.example.finalproject;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.util.ArrayList;

public class HumanCharacter extends Character implements Parcelable{
    boolean isUserTurn;
    int maxHealth;


    public HumanCharacter(int health, int level, int attack, int defense, int experience){
        super(health, level, attack, defense, experience);
        //this.maxHealth = health;
    }

    @Override
    BattleAction battleTurn() {
        return null;
    }


    //PARCEL THINGS

    public HumanCharacter(Parcel in) {
        super(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt());
        maxHealth = in.readInt();
        weaponAttack = in.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(health);
        dest.writeInt(level);
        dest.writeInt(attack);
        dest.writeInt(defense);
        dest.writeInt(experience);
        dest.writeInt(maxHealth);
        dest.writeInt(weaponAttack);


    }

    public static final Parcelable.Creator<HumanCharacter> CREATOR
            = new Parcelable.Creator<HumanCharacter>() {
        public HumanCharacter createFromParcel(Parcel in) {
            return new HumanCharacter(in);
        }

        @Override
        public HumanCharacter[] newArray(int size) {
            return new HumanCharacter[size];
        }
    };

}
