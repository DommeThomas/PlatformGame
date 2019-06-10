/*
    This class holds the state of the current player. It monitors the money collected, power of
    machine gun, respawn location and lives remaining.
 */

package n.platformer.platformgame;

import android.graphics.PointF;

public class PlayerState {

    // Variables: ----------------------------------------------------------------------

    private int numCredits; // Geldeinheiten
    private int mgFireRate; // Maschinengewehr: Schüsse pro Sekunde
    private int lives;
    private float restartX; // Respawn-PositionX
    private float restartY; // Respawn-PositionY



    // Constructor: ----------------------------------------------------------------------

    PlayerState() {
        lives = 3;
        mgFireRate = 1;
        numCredits = 0;
    }



    // Methods: --------------------------------------------------------------------------

    public void saveLocation(PointF location) {
        // Initialize the respawn location. It will be called each time the player uses a teleport:
        restartX = location.x;
        restartY = location.y;
    }


    public PointF loadLocation() {
        // Used every time the player loses a life:
        return new PointF(restartX, restartY);
    }



    // Getter & Setter: --------------------------------------------------------------------

    public int getLives() {
        return lives;
    }

    public int getMgFireRate() {
        return mgFireRate;
    }

    public void increaseFireRate() {
        mgFireRate += 2;
    }

    public void gotCredit() {
        numCredits++;
    }

    public int getCredits() {
        return numCredits;
    }

    public void  loseLife() {
        lives--;
    }

    public void addLife() {
        lives++;
    }

    public void resetLives() {
        lives = 3;
    }

    public void resetCredits() {
        numCredits = 0;
    }

}
