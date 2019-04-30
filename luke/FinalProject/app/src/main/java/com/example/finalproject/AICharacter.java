package com.example.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

public class AICharacter extends Character implements Parcelable {

    public int intelligence, numHeals, maxHealth;

    public AICharacter(int health, int level, int attack, int defense, int experience) {
        super(health, level, attack, defense, experience);
    }


    @Override
    BattleAction battleTurn() {
        BattleAction returner;// = new BattleAction();
        switch (intelligence) {
            case 0:
                returner = dumbTurn();
                break;
            case 1:
                returner = mediumTurn();
                break;
            case 2:
                returner = hardTurn();
                break;
            default:
                returner = dumbTurn();
                break;

        }


        return returner;

    }

    BattleAction hardTurn() {
        BattleAction returner = new BattleAction();

        return returner;
    }

    BattleAction mediumTurn() {
        BattleAction returner = new BattleAction();
        if (this.health <= 5) {
            returner.type = 3;
            returner.info[0] = 10;
        } else {
            returner.type = 1;
        }
        return returner;
    }

    BattleAction dumbTurn() {
        BattleAction returner = new BattleAction();
        returner.type = 1;
        return returner;
    }


    //PARCEL THINGS

    public AICharacter(Parcel in) {
        super(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt());
        intelligence = in.readInt();
        numHeals = in.readInt();
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
        dest.writeInt(intelligence);
        dest.writeInt(numHeals);
        dest.writeInt(maxHealth);
        dest.writeInt(weaponAttack);


    }

    public static final Parcelable.Creator<AICharacter> CREATOR
            = new Parcelable.Creator<AICharacter>() {
        public AICharacter createFromParcel(Parcel in) {
            return new AICharacter(in);
        }

        @Override
        public AICharacter[] newArray(int size) {
            return new AICharacter[size];
        }
    };
}
