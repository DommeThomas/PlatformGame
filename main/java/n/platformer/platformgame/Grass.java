/*
    Basic tile type that the player can walk about.
 */

package n.platformer.platformgame;

public class Grass extends GameObject {

    // Konstruktor:
    Grass(float worldStartX, float worldStartY, char type) {
        final float HEIGHT = 1;
        final float WIDTH = 1;

        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);

        setTraversable(); // passierbar

        // Choose a Bitmap:
        setBitmapName("turf");

        // Where does the tile start:
        setWorldLocation(worldStartX, worldStartY, 0);

        setRectHitbox(); // Passende RectHitbox erzeugen
    }


    public void update(long fps, float gravity) {
        // Empty body
    }
}
