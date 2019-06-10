/*
    Steuert einen Guard: Ein Wächter, der immer eine bestimmte Wegstrecke hin- und herläuft.
    Diese Wegstrecke (genauer: die Endpunkte) ermittelt die Methode setWaypoints() automatisch.
 */


package n.platformer.platformgame;

import android.content.Context;
import android.util.Log;

public class Guard extends GameObject {

    // Guards just move on x axis between 2 waypoints

    // Variables: ------------------------------------------------------------------------

    private float waypointX1; // linkes Wegende
    private float waypointX2; // rechtes Wegende
    private int currentWaypoint; // Zu welchem Wegpunkt (waypointX1 oder waypointX2) soll der Guard gerade hinlaufen
    private float MAX_X_VELOCITY = 3;


    // Constructor: ------------------------------------------------------------------------

    Guard(Context context, float worldStartX, float worldStartY, char type, int pixelsPerMetre) {
        final int ANIMATION_FPS = 8;
        final int ANIMATION_FRAME_COUNT = 5;
        final String BITMAP_NAME = "guard";
        final float HEIGHT = 2f;
        final float WIDTH = 1;

        setHeight(HEIGHT);
        setWidth(WIDTH);

        setType(type);

        setMoves(true);
        setActive(true);
        setVisible(true);

        // Set this object up to be animated:
        setAnimFps(ANIMATION_FPS);
        setAnimFrameCount(ANIMATION_FRAME_COUNT);
        setBitmapName(BITMAP_NAME);
        setAnimated(context, pixelsPerMetre, true);

        // Where does the tile start. X an Y Locations from constructor parameters:
        setWorldLocation(worldStartX, worldStartY, 0);
        setxVelocitiy(-MAX_X_VELOCITY);
        currentWaypoint = 1;
    }


    // Methods: ------------------------------------------------------------------------------

    public void setWaypoints(float x1, float x2) {
        waypointX1 = x1;
        if (PlatformView.getDebugging()) {
            Log.w("DebuggingWaypoint", "Guard, setWaypoints, waypointX1: " + waypointX1);
        }
        waypointX2 = x1;
        if (PlatformView.getDebugging()) {
            Log.w("DebuggingWaypoint", "Guard, setWaypoints, waypointX2: " + waypointX2);
        }
    }


    @Override
    public void update(long fps, float gravity) {
        if (currentWaypoint == 1) { // Heading left
            if (getWorldLocation().x <= waypointX1) {
                // Arrived at waypoint 1
                currentWaypoint = 2; // Der andere Wegpunkt ist nun das neue Ziel
                setxVelocitiy(MAX_X_VELOCITY);
                setFacing(RIGHT);
            }
        }

        if (currentWaypoint == 2) { // Heading right
            if (getWorldLocation().x >= waypointX2) {
                // Arrived at waypoint 2
                currentWaypoint = 1; // Der andere Wegpunkt ist nun das neue Ziel
                setxVelocitiy(-MAX_X_VELOCITY);
                setFacing(LEFT);
            }
        }

        move(fps);
        setRectHitbox(); // Update the guards hitbox
    }

}
