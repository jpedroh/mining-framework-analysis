package com.tufsd.drugwars;

/**
 * Write a description of class Player here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Player {
  private int y;

  /**
     * Constructor for objects of class Player
     */
  public int[] inventory;

  public String name;

  /**
     * Constructor for objects of class Player
     */
  public Player(String nameIn, int[] inventoryIn) {
    y = 0;
    name = nameIn;
    inventory = inventoryIn;
    int health = 100;
    int dunkness = 0;
    int highness = 0;
    int stamina = 100;
    int tiredness = 10;
    int days = 31;
    int bounty = 0;
  }

  /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
  public int sampleMethod(int y) {
    return y + y;
  }
}