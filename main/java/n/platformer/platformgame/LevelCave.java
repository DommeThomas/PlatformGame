/*
    Enthält die Tiles und deren Anordnung für den Level "Cave" (1. Level).
 */

package n.platformer.platformgame;

import java.util.ArrayList;


public class LevelCave extends LevelData {

    // Konstruktor:
    LevelCave() {
        // Erzeugung der LevelDaten:
        tiles = new ArrayList<String>();
        this.tiles.add("p.............................................");
        this.tiles.add("..............................................");
        this.tiles.add("..............................................");
        this.tiles.add("..............................................");
        this.tiles.add("....................c.........................");
        this.tiles.add("....................1........u................");
        this.tiles.add(".................c..........u1................");
        this.tiles.add(".................1.........u1.................");
        this.tiles.add("..............c...........u1..................");
        this.tiles.add("........g.....1..........u1...................");
        this.tiles.add("......................e..1....e.....e.........");
        this.tiles.add("....11111111111111111111111111111111111111....");
    }
}

