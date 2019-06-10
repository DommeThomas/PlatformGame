/*
    Kugeln (Patronen) für das Maschinengewehr.
 */

package n.platformer.platformgame;


public class Bullet {

    // Variables: -------------------------------------------------------------------
    private float x; // X-Position
    private float y; // Y-Position
    private float xVelocity; // horizontale Geschwindigkeit
    private int direction; // direction of travel



    // Konstruktor: ------------------------------------------------------------------

    Bullet(float x, float y, int speed, int direction) {
        this.direction = direction;
        this.x = x;
        this.y = y;
        this.xVelocity = speed * direction;
    }



    // Methods: -----------------------------------------------------------------------

    public void update(long fps, float gravity) {
        x += xVelocity / fps;
    }


    public void hideBullet() {
        this.x = -100; // außerhalb des Bildbereichs
        this.xVelocity = 0;
    }



    // Getter & Setter: ---------------------------------------------------------------

    public int getDirection() {
        return direction;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
