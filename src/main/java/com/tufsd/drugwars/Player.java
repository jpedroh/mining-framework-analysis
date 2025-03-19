
<<<<<<< /usr/src/app/output/shhs-coding-club/drugwars/f07c4b5d24a5a246a9de55940bb80151684c64ea/src/main/java/com/tufsd/drugwars/Player.java/left.java
import java.util.EnumMap;

/**
 * A class to represent players.
 *
 * @author SHHS Coding Club Game Logic Team
 * @version 20141105
 */
public class Player {
  /**
     * The player's inventory with drug names as keys and count as values.
     */
  public EnumMap<Drug, Integer> inventory;

  /**
     * The player's name.
     */
  public String name;

  /**
     * The player's health (initial value of 100).
     */
  public int health = 100;

  /**
     * Constructor for objects of class Player
     *
     */
  public Player(String name) {
    this.name = name;
    int drunkness = 0;
    int highness = 0;
    int stamina = 100;
    int tiredness = 10;
    int bounty = 0;
  }
}
=======
package com.tufsd.drugwars;
import java.util.EnumMap;

/**
 * A class to represent players.
 *
 * @author SHHS Coding Club Game Logic Team
 * @version 20141105
 */
public class Player {
  /**
     * The player's inventory with drug names as keys and count as values.
     */
  public EnumMap<Drug, Integer> inventory;

  /**
     * The player's name.
     */
  public String name;

  /**
     * The player's health (initial value of 100).
     */
  public int health = 100;

  public double debt;

  /**
     * Constructor for objects of class Player
     *
     */
  public Player(String name) {
    this.name = name;
    int drunkness = 0;
    int highness = 0;
    int stamina = 100;
    int tiredness = 10;
    int bounty = 0;
    debt = 10000;
  }
}
>>>>>>> /usr/src/app/output/shhs-coding-club/drugwars/f07c4b5d24a5a246a9de55940bb80151684c64ea/src/main/java/com/tufsd/drugwars/Player.java/right.java
