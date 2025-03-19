
<<<<<<< /usr/src/app/output/shhs-coding-club/drugwars/f07c4b5d24a5a246a9de55940bb80151684c64ea/src/main/java/com/tufsd/drugwars/Game.java/left.java
import java.util.Random;
import java.util.EnumMap;

/**
 * A class to represent a game session.
 *
 * @author SHHS Coding Club
 * @version 20141105
 */
public class Game {
  /**
     * Constructor for objects of class Game
     */
  public Player player;

  public Game(String name) {
    EnumMap<Drug, Integer> inventory = new EnumMap<Drug, Integer>(Drug.class);
    player = new Player(name);
    player.inventory = inventory;
    player.inventory.put(Drug.POT, 3);
  }

  public void run() {
    UI.playerInfo(player);
  }

  public boolean policeEncounter() {
    double random = Math.random() * 10 + 1;
    if (random == 1) {
      return true;
    } else {
      return false;
    }
  }
}
=======
package com.tufsd.drugwars;
import java.util.EnumMap;

/**
 * A class to represent a game session.
 *
 * @author SHHS Coding Club
 * @version 20141105
 */
public class Game {
  /**
     * Constructor for objects of class Game
     */
  public Player player;

  public final double RATE = .1;

  public int turns;

  public void turn() {
    addInterest();
  }

  public Game(String name) {
    EnumMap<Drug, Integer> inventory = new EnumMap<Drug, Integer>(Drug.class);
    player = new Player(name);
    player.inventory = inventory;
    player.inventory.put(Drug.POT, 3);
    turns = 0;
  }

  public double addInterest() {
    player.debt = (player.debt * Math.pow(Math.E, (RATE * turns)));
    return player.debt;
  }

  public void run() {
    UI.playerInfo(player);
  }
}
>>>>>>> /usr/src/app/output/shhs-coding-club/drugwars/f07c4b5d24a5a246a9de55940bb80151684c64ea/src/main/java/com/tufsd/drugwars/Game.java/right.java
