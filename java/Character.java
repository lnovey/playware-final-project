package com.example.finalproject;

import android.os.Parcelable;

import java.util.ArrayList;

public abstract class Character {

    public int health, level, attack, defense, experience;
    public boolean isAlive;

    private String weaponName;
    public int weaponAttack;


    protected ArrayList<String> inventory = new ArrayList<String>();

    public Character(int health, int level, int attack, int defense, int experience){
        this.health = health;
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.experience = experience;

    }

    public void setAllStats(int health, int level, int attack, int defense, int experience){
        this.health = health;
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.experience = experience;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getExperience() {
        return experience;
    }

    public int getLevel() {
        return level;
    }

    public int getHealth() {
        return health;
    }

    public boolean addToInventory(String item){
        return inventory.add(item);
    }

    public void setWeapon(String weaponName){
        this.weaponName = weaponName;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    abstract BattleAction battleTurn();

}
