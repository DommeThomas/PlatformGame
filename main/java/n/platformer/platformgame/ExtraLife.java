/*
    Pickup: ExtraLife.
    Set a bitmap, get a hitbox and a location in the world.
 */

package n.platformer.platformgame;

public class ExtraLife extends GameObject {

    ExtraLife(float worldStartX, float worldStartY, char type) {
        final float HEIGHT = 0.8f; // Etwas kleiner als eine Tile-Einheit
        final float WIDTH = 0.65f; // Etwas kleiner als eine Tile-Einheit

        setHeight(HEIGHT);
        setWidth(WIDTH);

        setType(type);

        // Choose a bitmap
        setBitmapName("life");

        // Where does the tile start: X and Y locations from constructor parameters:
        setWorldLocation(worldStartX, worldStartY, 0);

        // Set hitbox:
        setRectHitbox();
    }


    @Override
    public void update(long fps, float gravity) {
        // Kein Update notwendig.
    }
}
