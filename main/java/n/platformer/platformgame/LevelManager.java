/*
    Bereitet die Daten der Platform-Level so auf, dass sie vom Programm während des Spiels
    gut ausgewertet werden können.
    Erzeugt dazu insbesondere die Level-spezifischen Variablen
        "gameObjects" (ArrayList mit sämtlichen Gameobjekten des Levels) und
        "bitmapsArray" (Array mit dem Bitmaps der Gameobjekte des Levels) und
        "levelData" (ArrayList mit Strings, um die Anordnung der Gameobjekte des Levels als char-Zeichen zu speichern).
 */

package n.platformer.platformgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;


public class LevelManager {

    // Variablen:
    // ------------------------------------------------------------------------
    private String level;
    int mapWidth;
    int mapHeight;

    Player player; // Gameobjekt-Platzhalter für die Spielerfigur
    int playerIndex; // Wird genutzt, um das Gameobjekte in der ArrayList gameobjects innerhalb der Methode loadMapData() wiederzufinden.

    private boolean playing; // Läuft das Spiel gerade?
    float gravity; // Für spätere Spielerweiterungen, wird derzeit noch nicht genutzt.

    LevelData levelData; // ArrayList mit Strings, um die Anordnung der Gameobjekte des Levels als char-Zeichen zu speichern
    ArrayList<GameObject> gameObjects; // ArrayList mit sämtlichen Gameobjekten des Levels

    ArrayList<Rect> currentButtons; // Für die Player controll buttons
    Bitmap[] bitmapsArray; // Für die Bitmaps der Gameobjekte, die für den aktuellen Level gebraucht werden.


    // Konstruktor: --------------------------------------------------------------
    // Der 2. Parameter (pixelPerMetreX wird als Parameter für prepareBitmap() zur Skalierung genutzt.
    // Sinnvoller wäre eine Unterscheidung in pixelPerMetreX und pixelPerMetreY,
    // also jeweils ein zusätzlicher Parameter mehr (wird aber im Buch nicht gemacht):
    public LevelManager(Context context, int pixelsPerMetre, int screenWidth, InputController ic,
                        String level, float px, float py) {
        this.level = level;

        // Die Daten des aktuellen Levels laden:
        switch (level) {
            case "LevelCave":
                levelData = new LevelCave(); // Erzeugt die ArrayList "Tiles", welche die Leveldaten zeilenweise in Strings speichert.
                break;

                // Später kommen weitere Cases für weitere Levels hinzu.
        }

        gameObjects = new ArrayList<>(); // To hold all our Gameobjects
        bitmapsArray = new Bitmap[25]; // To hold one of every Bitmap
        loadMapData(context, pixelsPerMetre, px, py); // Load all the GameObjects and Bitmaps
        setWaypoints(); // Set waypoints für our guards
    }


    // Methoden:
    // --------------------------------------------------------------------------

    public  Bitmap getBitmap(char blockType) {
        // Gibt die zum Tile (blockType) passende Bitmap zurück, die in "bitmapsArray" gespeichert
        // ist.
        // Benutzt einen int-Wert als Index, um so das entsprechende Bitmap-Objekt zurückzugeben.
        // Jeder Index korrespondiert mit einer Bitmap.

        int index;
        switch (blockType) {
            case '.':
                index = 0;
                break;

            case '1': // Ziffer "1"
                index = 1;
                break;

            case 'p':
                index = 2;
                break;

            case 'c':
                index = 3;
                break;

            case 'u':
                index = 4;
                break;

            case 'e':
                index = 5;
                break;

            case 'd':
                index = 6;
                break;

            case 'g':
                index = 7;
                break;

            default:
                index = 0;
                break;
        }

        return bitmapsArray[index];
    }


    public int getBitmapIndex(char blockType) {
        // This method allows each Gameobject, which 'knows' its blockType to get and return
        // the correct index to its Bitmap in the Bitmap array:

        int index;
        switch (blockType) {
            case '.':
                index = 0;
                break;

            case '1': // Ziffer "1"
                index = 1;
                break;

            case 'p':
                index = 2;
                break;

            case 'c':
                index = 3;
                break;

            case 'u':
                index = 4;
                break;

            case 'e':
                index = 5;
                break;

            case 'd':
                index = 6;
                break;

            case 'g':
                index = 7;
                break;

            default:
                index = 0;
                break;
        }

        return index;
    }


    private void loadMapData(Context context, int pixelPerMetre, float px, float py) {
        // Parameter:
        //          pixelPerMetre:  Siehe unten und siehe Kommentar in der Klasse Viewport bei den Variablen pixelPerMetreX bzw. -Y.
        //          px und py:      X- bzw. Y-Position des Spielers
        // Der 2. Parameter (pixelPerMetreX wird als Parameter für prepareBitmap() zur Skalierung genutzt.
        // Sinnvoller wäre eine Unterscheidung in pixelPerMetreX und pixelPerMetreY,
        // also jeweils ein zusätzlicher Parameter mehr (wird aber im Buch nicht gemacht).

        // Speichert alle Gameobjekte eines Levels (aus der ArrayList "tiles") in die ArrayList "gameObjects"
        // einschließlich deren Positionen im Level. Nur die Leerfelder (Zeichen ".") eines Levels
        // werden dabei nicht berücksichtigt.
        // Load the level.

        char c;
        int currentIndex = -1; // Keep track of where we load our game objects

        // How wide and high is the map? The Viewport needs to know:
        mapHeight = levelData.tiles.size(); // Anzahl der Stringelemente in der Arraylist "tiles"
        mapWidth = levelData.tiles.get(0).length(); // Anzahl der Zeichen in einem Stringelement (alle Strings sind dort gleich groß) von "tiles"

        // In einer Doppelschleife werden alle Zeichen der ArrayList "tiles" nacheinander
        // ausgewertet, angefangen mit dem ersten Zeichen des ersten Strings.
        // Sofern es ein anderes Zeichen als das "."-Zeichen ist (leerer Raum), wird das
        // entsprechende Gameobjekt mit der angegebenen Position erzeugt und in "gameObjects"
        // (der ArrayList für die verwendeten Gameobjekte) gespeichert:
        for (int i = 0; i < levelData.tiles.size(); i++) { // Zeilenindex: arbeitet die Strings nacheinander ab, beginnt oben und endet unten -> Koordinatenursprung in der linken oberen Ecke.
            for (int j = 0; j < levelData.tiles.get(i).length(); j++) { // Zeichenindex: Arbeitet die Zeichen innnerhalb eines Strings (= Zeile) von links nach rechts ab.
                c = levelData.tiles.get(i).charAt(j);

                if (c != '.') { // Don't want to load the empty spaces
                    currentIndex++;
                    switch (c) {
                        case '1': // Add grass to the gameObjects
                            gameObjects.add(new Grass(j, i, c));
                            if (PlatformView.getDebugging()) { // Debug-Infos?
                                System.out.println("Grass: currentIndex: " + currentIndex
                                        + ", worldStartX: " + j + ", worldStartY: " + i);
                            }
                            break;

                        case 'p': // Add a player to the gameObjects
                            gameObjects.add(new Player(context, px, py, pixelPerMetre));
                            playerIndex = currentIndex; // Index of the player
                            player = (Player) gameObjects.get(playerIndex); // Reference to the player
                            if (PlatformView.getDebugging()) { // Debug-Infos?
                                System.out.println("Player: currentIndex: " + currentIndex
                                        + ", worldStartX: " + j + ", worldStartY: " + i
                                        + ", pixelPerMetre: " + pixelPerMetre);
                            }
                            break;

                        case 'c':
                            // Add a coin to the gameObjects:
                            gameObjects.add(new Coin(j, i, c));
                            break;

                        case 'u':
                            // Add a machine gun upgrade to the gameObjects:
                            gameObjects.add(new MachineGunUpgrade(j, i, c));
                            break;

                        case 'e':
                            // Add a extra life to the gameObjects:
                            gameObjects.add(new ExtraLife(j, i, c));
                            break;

                        case 'd':
                            // Add a drone to the gameObjects:
                            gameObjects.add(new Drone(j, i, c));
                            break;

                        case 'g':
                            // Add a guard to the gameObjects:
                            gameObjects.add(new Guard(context, j, i, c, pixelPerMetre));
                            break;
                    }

                    // If a new object has been added to "gameObjects" ArrayList, we need to check
                    // if the corresponding bitmap has been added to the "bitmapsArray".
                    // If it hasn't, we add one using the "prepareBitmap()" method of the
                    // current "GameObject" class.
                    // If the bitmap isn't prepared yet, we do it now:
                    if (bitmapsArray[getBitmapIndex(c)] == null) {
                        // Prepare it now and put it in the ArrayList "bitmapsArray":
                        bitmapsArray[getBitmapIndex(c)] = gameObjects.get(currentIndex)
                                .prepareBitmap( // Gibt die mittels pixelPerMetre skalierte Bitmap zurück.
                                        context,
                                        gameObjects.get(currentIndex).getBitmapName(),
                                        pixelPerMetre);
                    }
                }
            }
        }

    }


    public void  setWaypoints() {
        // Berechnet und speichert die beiden Wegpunkte, zwischen denen jeder Guard ständig
        // hin und her laufen soll.

        final int HALF_WAYLENGTH = 5; // halbe Weglänge des Patrouilen-Weges. Im Buch nicht über eine Konstante geregelt, sondern immer direkt als Zahlenwert programmiert.

        // Loop through all game objects looking for Guards:
        for (GameObject guard : this.gameObjects) {
            if (guard.getType() == 'g') { // gameObject ist ein Guard

                // Set waypoints for this guard, find the tile beneath the guard.
                // It relies on the designer putting the guard in sensible location
                // Offenbar berücksichtigt das Programm aber nicht die Situation, wenn der Guard
                // in der LevelMap dicht am Rand einer Tile-Zeichen-Zeile platziert wird.

                int startTileIndex = -1;
                float waypointX1 = -1;
                float waypointX2 = -1;

                for (GameObject tile : this.gameObjects) {
                    startTileIndex++;
                    // Ermittlung des Tiles, auf dem der Guard anfänglich steht:
                    if (tile.getWorldLocation().y == guard.getWorldLocation().y + 2) { // + 2, weil der Guard eine Höhe von 2 hat
                        // Tile gefunden, das sich in der Tile-Zeile, die sich 2 Tile-Einheiten unter dem Guard befindet (richtige Zeile).
                        // Nun muss aus der Zeile noch das genau das Tile gefunden werden, auf dem der Guard steht (x-Koordinaten mssen übereinstimmen).
                        // Tile is two spaces below current guard. Now see if it has the same x coordinate:
                        if (tile.getWorldLocation().x == guard.getWorldLocation().x) {

                            // Found the tile the guard is "standing" on. Now go left
                            // as far as possible before a non traversable tile is found.
                            // Either on guards row or tile row upto a maximum of "HALF_WAYLENGTH":
                            for (int i = 0; i < HALF_WAYLENGTH; i++) { // left for loop
                                if (!gameObjects.get(startTileIndex - i).isTraversable()) {
                                    // Tile is not traversible. Set left waypoint:
                                    waypointX1 = gameObjects.get(startTileIndex - (i + 1)).getWorldLocation().x;
                                    if (PlatformView.getDebugging()) {
                                        Log.w("DebuggingWaypoint", "Levelmanager, setWaypoint(), Zweig not traversible, waypointX1: " + waypointX1);
                                    }
                                    break; // Leave left for loop
                                } else {// Tile is traversible
                                    // Set to maximal HALF_WAYLENGTH tiles as no non traversible tile found:
                                    waypointX1 = gameObjects.get(startTileIndex - HALF_WAYLENGTH).getWorldLocation().x;
                                    if (PlatformView.getDebugging()) {
                                        Log.w("DebuggingWaypoint", "Levelmanager, setWaypoint(), Zweig max. Tiles, waypointX1: " + waypointX1);
                                    }
                                }
                            } // End get left waypoint

                            for (int i = 0; i < HALF_WAYLENGTH; i++) { // right for loop
                                if (!gameObjects.get(startTileIndex + i).isTraversable()) {
                                    // Tile is not traversible. Set the right waypoint:
                                    waypointX2 = gameObjects.get(startTileIndex + (i - 1)).getWorldLocation().x;
                                    if (PlatformView.getDebugging()) {
                                        Log.w("DebuggingWaypoint", "Levelmanager, setWaypoint(), Zweig not traversible, waypointX2: " + waypointX2);
                                    }
                                    break; // Leave right for loop

                                } else { // Tile is traversible
                                    // Set to maximal HALF_WAYLENGTH tiles away:
                                    waypointX2 = gameObjects.get(startTileIndex + HALF_WAYLENGTH).getWorldLocation().x;
                                    if (PlatformView.getDebugging()) {
                                        Log.w("DebuggingWaypoint", "Levelmanager, setWaypoint(), Zweig max. Tiles, waypointX2: " + waypointX2);
                                    }
                                }
                            } // End get right waypoint

                            Guard g = (Guard) guard;
                            g.setWaypoints(waypointX1, waypointX2);
                        }
                    }
                }
            } // End setWaypoints()
        }
    }


    public void switchPlayingStatus() {
        // Schaltet jeweils die boolsche Variable "playing" ein bzw. aus:
        playing = !playing;
        if (playing) {
            gravity = 6;
        } else {
            gravity = 0;
        }
    }


    // Getter und Setter: ---------------------------------------------------------------

    public boolean isPlaying() {
        return playing;
    }

}
