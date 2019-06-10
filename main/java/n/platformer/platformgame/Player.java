/*
    Für Spieler-Objekte.
 */

package n.platformer.platformgame;

import android.content.Context;


public class Player extends GameObject {

    // Variablen: -------------------------------------------------------------------
    final float MAX_X_VELOCITY = 10;
    boolean isPressingRight = false;
    boolean isPressingLeft = false;
    public boolean isFalling;
    private boolean isJumping;
    private long jumpTime;
    private long maxJumpTime = 700; // 700 ms
    public MachineGun bfg; // Maschinengewehr für Player

    // Hitboxen:
    RectHitbox rectHitboxFeet;
    RectHitbox rectHitboxHead;
    RectHitbox rectHitboxLeft;
    RectHitbox rectHitboxRight;


    // Konstruktor: -----------------------------------------------------------------
    Player(Context context, float worldStartX, float worldStartY, int pixelsPerMetre) {
        final float HEIGHT = 2;
        final float WIDTH = 1;

        setHeight(HEIGHT); // 2 metre tall
        setWidth(WIDTH); // 1 metre wide

        setxVelocitiy(0); // Standing still at start
        setyVelocitiy(0); // Standing still at start
        setFacing(LEFT);
        isFalling = false;

        setMoves(true); // Bewegbares Gameobjekt
        setActive(true);
        setVisible(true);

        setType('p');
        bfg = new MachineGun(); // Maschinengewehr erzeugen

        // Choose a Bitmap.
        // This is a sprite sheet with multiple frames of animation:
        setBitmapName("player");

        final int ANIMATION_FPS = 16;
        final int ANIMATION_FRAME_COUNT = 5; // 5 Animationsbilder bzw. -phasen

        // Set this object up to be animated:
        setAnimFps(ANIMATION_FPS);
        setAnimFrameCount(ANIMATION_FRAME_COUNT);
        setAnimated(context, pixelsPerMetre, true);

        setWorldLocation(worldStartX, worldStartY, 0); // X and Y locations from constructor parameters

        // 4 rechteckige Hitboxen erzeugen:
        rectHitboxFeet = new RectHitbox();
        rectHitboxHead = new RectHitbox();
        rectHitboxLeft = new RectHitbox();
        rectHitboxRight = new RectHitbox();
    }


    // Methods: ------------------------------------------------------------------------

    public void update(long fps, float gravity) {
        // horizontale Bewegung:
        if (isPressingRight) {
            this.setxVelocitiy(MAX_X_VELOCITY);
        }
        else if (isPressingLeft) {
            this.setxVelocitiy(-MAX_X_VELOCITY);
        }
        else {
            this.setxVelocitiy(0);
        }

        // Which way is player facing?
        if (this.getxVelocitiy() > 0) {
            setFacing(RIGHT);
        }
        else if (this.getxVelocitiy() <0) {
            setFacing(LEFT);
        } // If xVelocity = 0 then unchanged

        // Jumping and gravity
        if (isJumping) {
            long timeJumping = System.currentTimeMillis() - jumpTime; // jumpTime wird in der Methode startJump() initialisiert
            if (timeJumping < maxJumpTime) {
                if (timeJumping < maxJumpTime / 2) {
                    this.setyVelocitiy(-gravity);  // on the way up
                }
                else if (timeJumping > maxJumpTime / 2) {
                    this.setyVelocitiy(gravity); // going down
                }
            }
            else {
                isJumping = false;
            }
        }
        else {
            this.setyVelocitiy(gravity); // going down
            // Remove the next line to make the game easier. It means the long jumps are less
            // punishing because the player can take off just after the platform.
            // They will also be able to cheat by jumping in thin air.
            isFalling = true;
        }

        bfg.update(fps, gravity); // Maschinengewehr aktualsieren

        this.move(fps); // Let's go (Methode der Elternklasse "Gameobject")

        // Update all 4 hitboxes to the new location (use the current worldlocation)
        // of the player and save them as local variables.
        // Beim Update wird der leere Raum zwischen Shape-Rechteck der Grafik
        // und der Hitbox möglichst klein gehalten (vgl. Buch S. 153ff):

        // lokale Hilfsvariablen für die Hitbox-Updates:
        Vector2Point5D location = getWorldLocation();
        float lx = location.x;
        float ly = location.y;

        // Update the player feet hitbox:
        rectHitboxFeet.top = ly + getHeight() * 0.95f;
        rectHitboxFeet.left = lx + getWidth() * 0.2f;
        rectHitboxFeet.bottom = ly + getHeight() * 0.98f;
        rectHitboxFeet.right = lx + getWidth() * 0.8f;

        // Update the player head hitbox:
        rectHitboxHead.top = ly;
        rectHitboxHead.left = lx + getWidth() * 0.4f;
        rectHitboxHead.bottom = ly + getHeight() * 0.2f;
        rectHitboxHead.right = lx +getWidth() * 0.6f;

        // Update the player left hitbox:
        rectHitboxLeft.top = ly + getHeight() * 0.2f;
        rectHitboxLeft.left = lx + getWidth() * 0.2f;
        rectHitboxLeft.bottom = ly + getHeight() * 0.8f;
        rectHitboxLeft.right = lx +getWidth() * 0.3f;

        // Update the player right hitbox:
        rectHitboxRight.top = ly + getHeight() * 0.2f;
        rectHitboxRight.left = lx + getWidth() * 0.7f; // Im Buch sind die float-Werte für .left und .right vermutlich vertauscht (vgl. S. 154)
        rectHitboxRight.bottom = ly + getHeight() * 0.8f;
        rectHitboxRight.right = lx +getWidth() * 0.8f;
    }


    public int checkCollisions(RectHitbox rectHitbox) {
        // Prüft, ob sich eine der 4 Player-Hitboxen sich mit der Parameter-Hitbox überschneidet
        // und gibt einen entsprechenden int-Wert zurück:
        // 0 : keine Kollision;  1 : Kollision mit linker oder rechter Playerseite
        // 2 : Kollision mit Player-Füßen;  3 : Kollision mit Player-Kopf

        int collided = 0; // No collision

        // The left Player-Hitbox:
        if (this.rectHitboxLeft.intersects(rectHitbox)) {
            // Left has collided. Move the player just to right of the current hitbox.
            // Der Subtrahend berücksichtigt den Abstand der linken Seite der linken Player-Hitbox
            // zur Player-X-Position (bzw. zur linken Seite des Player-Shape-Rechtecks):
            this.setWorldLocationX(rectHitbox.right - getWidth() * 0.2f);
            collided = 1;
        }

        // The right Player-Hitbox:
        if (this.rectHitboxRight.intersects(rectHitbox)) {
            // Right has collided. Move the player just to left of the current hitbox.
            // Hier muss die Player-Breite subtrahiert werden, weil für die Pixelkoordinaten
            // immer die linke obere Ecke den Bezugspunkt darstellt.
            // Die verbleibende Differenz (Width * (1 - 0.8)) berücksichtigt den Abstand der rechten
            // Seite der rechten Player-Hitbox zur Player-X-Position + Playerbreite (Width):
            this.setWorldLocationX(rectHitbox.left - getWidth() * 0.8f);
            collided = 1;
        }

        // The Feet Player-Hitbox:
        if (this.rectHitboxFeet.intersects(rectHitbox)) {
            // Feet have collided. Move the player just above current hitbox.
            // Der Abstand zwischen Feet-Hitbox und Player-Shape-Unterseite ist so minimal,
            // dass er nicht mit einem Faktor berücksichtigt werden braucht:
            this.setWorldLocationY(rectHitbox.top - getHeight());
            collided = 2;
        }

        // The Head Player-Hitbox:
        if (this.rectHitboxHead.intersects(rectHitbox)) {
            // Head has collided. Move the player just below current hitbox bottom.
            // Der Abstand zwischen Head-Hitbox und Player-Shape-Oberseite ist so minimal,
            // dass er nicht mit einem Faktor berücksichtigt werden braucht:
            this.setWorldLocationY(rectHitbox.bottom);
            collided = 3;
        }

        return collided;
    }


    public void restorePreviousVelocity() {
        // When the player collides with another object, the default action in the player class's
        // "checkCollisions"-method is to stop the character moving.
        // We don't want this happen for collisions with a pickup because it will be irritating
        // for the player.
        // This method restores the x-velocity, the player has have before the collision:
        if (!isJumping && !isFalling) {
            if (getFacing() == LEFT) {
                isPressingLeft = true;
                setxVelocitiy(- MAX_X_VELOCITY);
            }
            else {
                isPressingRight = true;
                setxVelocitiy(MAX_X_VELOCITY);
            }
        }
    }


    public void startJump(SoundManager sm) {
        if (!isFalling) { // Can't jump if falling
            if (!isJumping) { // not already jumping
                isJumping = true;
                jumpTime = System.currentTimeMillis();
                sm.playSound("jump");
            }
        }
    }


    public boolean pullTrigger() {
        // Try and fire a shot:
        return bfg.shoot(this.getWorldLocation().x, this.getWorldLocation().y, getFacing(), getHeight());
    }



    // Getter & Setter: --------------------------------------------------------------------

    public void setPressingRight(boolean isPressingRight) {
        this.isPressingRight = isPressingRight;
    }

    public void setPressingLeft(boolean isPressingLeft) {
        this.isPressingLeft = isPressingLeft;
    }

}
