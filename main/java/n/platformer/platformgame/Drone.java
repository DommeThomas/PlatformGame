/*
    Erzeugt und verwaltet eine Drone, die sich immer in Richtung Player bewegt und ihn bei Berührung tötet.
 */

package n.platformer.platformgame;

import android.graphics.PointF;


public class Drone extends GameObject {

    // Variables: ------------------------------------------------------------------
    long lastWaypointSetTime; // Systemzeit, bei der das letzte Mal die Player-Position geladen worden ist
    PointF currentWayPoint; // Letzte registrierte X- und Y-Position des Players in Tile-Einheiten

    final float MAX_X_VELOCITY = 3;
    final float MAX_Y_VELOCITY = 3;

    // Zeitabstand in ms, in denen die currentWayPoint-Daten aktualisiert werden.
    // Umso kürzer der Zeitabstand, umso gefährlicher ist die Drone:
    final int SMARTNESS = 2000; // Im Buch gibt es diese Variable nicht, stattdessen wird der Wert direkt in der Methode setWayPoint eingesetzt


    // Construktor: ------------------------------------------------------------------
    Drone(float worldStartX, float worldStartY, char type) {
        final float HEIGHT = 1;
        final float WIDTH = 1;
        setHeight(HEIGHT);
        setWidth(WIDTH);

        setType(type);

        setBitmapName("drone");
        setMoves(true);
        setActive(true);
        setVisible(true);

        currentWayPoint = new PointF();

        setWorldLocation(worldStartX, worldStartY, 0); // Startpunkt der Drone (X- und Y-Position)
        setRectHitbox();
        setFacing(RIGHT);

    }


    // Methods: -----------------------------------------------------------------------

    @Override
    public void update(long fps, float gravity) {

        // Horizontale Dronenbewegung in Abhängigkeit von der Position zum Player steuern:
        if (currentWayPoint.x > getWorldLocation().x) {
            setxVelocitiy(MAX_X_VELOCITY);
        }
        else if (currentWayPoint.x < getWorldLocation().x) {
            setxVelocitiy(-MAX_X_VELOCITY);
        }
        else {
            setxVelocitiy(0); // Drone ist schon am Playerstandort (horizontal)
        }

        // Vertikale Dronenbewegung in Abhängigkeit von der Position zum Player steuern:
        if (currentWayPoint.y > getWorldLocation().y) {
            setyVelocitiy(MAX_Y_VELOCITY);
        }
        else if (currentWayPoint.y < getWorldLocation().y) {
            setyVelocitiy(-MAX_Y_VELOCITY);
        }
        else {
            setyVelocitiy(0); // Drone ist schon am Playerstandort (vertikal)
        }

        move(fps);

        //  Update the drone hitbox:
        setRectHitbox();
    }


    public void setWaypoint(Vector2Point5D playerLocation) {
        // Player-Position abrufen und in currentWayPoint speichern:
        if (System.currentTimeMillis() > lastWaypointSetTime + SMARTNESS) {
            // Zeitabstand ist ausreichend:
            lastWaypointSetTime = System.currentTimeMillis();
            currentWayPoint.x = playerLocation.x;
            currentWayPoint.y = playerLocation.y;
        }
    }
}
