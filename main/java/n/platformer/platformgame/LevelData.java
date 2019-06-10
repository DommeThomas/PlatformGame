/*
    Elternklasse für die Level-Klassen, welche die Leveldaten enthalten.
    Die Gameobjekte werden als char-Typen jeweils zeilenweise in Strings gespeichert.
    Die String selbst werden in der ArrayList "tiles" gespeichert.
 */

package n.platformer.platformgame;

import java.util.ArrayList;


public class LevelData {
    ArrayList<String> tiles; // Zur Speicherung der Anordnung der Gameobjekte im Level

    // Tile types:
    // . = no tile;
    // 1 = Grass;
    // 2 = Snow
    // 3 = Brick
    // 4 = Coal
    // 5 = Concrete
    // 6 = Scorched
    // 7 = Stone

    //Active objects
    // g = guard
    // d = drone
    // t = teleport
    // c = coin
    // u = upgrade
    // f = fire
    // e  = extra life

    //Inactive objects
    // w = tree
    // x = tree2 (snowy)
    // l = lampost
    // r = stalactite
    // s = stalacmite
    // m = mine cart
    // z = boulders

}
