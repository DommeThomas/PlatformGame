/*
    Dienen der Collision-Detection. Beim Player werden gleich 4 Stück verwendet: oben, unten, links und rechts.
 */

package n.platformer.platformgame;

public class RectHitbox {

    // Variablen: -------------------------------------------------------------

    float top;
    float left;
    float bottom;
    float right;
    float height;


    // Methoden: --------------------------------------------------------------

    boolean intersects(RectHitbox rectHitbox) {
        // Testet, ob eine Überschneidung des Objekts mit dem Parameter-Object vorliegt.

        boolean hit = false;

        if (this.right > rectHitbox.left && this.left < rectHitbox.right) {
            // Intersecting on x axis

            if (this.top < rectHitbox.bottom && this.bottom > rectHitbox.top) {
                // Intersecting on y as well -> collision
                hit = true;
            }
        }
        return hit;
    }


    // Getter & Setter: -----------------------------------------------------------

    public void setTop(float top) {
        this.top = top;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
