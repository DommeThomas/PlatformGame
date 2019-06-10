/*
    Zentrale Controll-Methode für die App. Wird von PlatformActivity aus aufgerufen
    und übernimmt dann die Kontrolle.
 */

package n.platformer.platformgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;


public class PlatformView extends SurfaceView implements Runnable {

    // Variablen: -----------------------------------------------------------

    private static boolean debugging = true; // "static" habe ich hinzugefügt, damit (meine) Methode getDebugging auch statisch sein kann und so von allen Klassen (ohne extra Reference) aufrufbar ist.
    private volatile boolean running;
    private Thread gameThread = null;

    // For drawing:
    private Paint paint;
    private Canvas canvas; // Canvas could initially be local but later we will use it outside of draw()
    private SurfaceHolder ourHolder; // Zur späteren Verriegelung der Canvas-Speichers bei Datenzugriffen (Threadsicherheit)

    Context context;
    long startFrameTime;
    long timeThisFrame;
    long fps;

    // Our new engine classes:
    private LevelManager lm;
    private Viewport vp;
    InputController ic;
    SoundManager sm;
    private PlayerState ps;


    // Konstruktor: -----------------------------------------------------------
    public PlatformView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.context = context;

        // Initialize our drawing objects:
        ourHolder = getHolder();
        paint = new Paint();

        // Initialize the viewport:
        vp = new Viewport(screenWidth, screenHeight);

        // Initialize the SoundManager;
        sm = new SoundManager();
        sm.loadSound(context);

        // Create and initialize a PlayerState object:
        ps = new PlayerState();

        // Load the first level:
        loadLevel("LevelCave", 15, 2);
    }


    // Methoden:
    // --------------------------------------------------------------------------------------

    @Override
    public void run() {
        while (running) {
            startFrameTime = System.currentTimeMillis();

            update();
            draw();

            // Calculate the fps this frame.
            // We can then use the result to time animations and movement.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }


    private void update() {
        // Nur die (zur Zeit) aktiven Gameobjekte, die sich zudem innerhalb des darzustellenden
        // Bildschirmbereichs befinden, sollen sichtbar (visible) sein. Die nicht sichtbaren
        // können später von der draw()-Methode ignoriert werden.
        // Zumindest in Buchkapitel 5 haben alle Gameobjekte die Objektvariable "active" = true, da
        // "active" bei der Deklaration den Wert true erhält und die Objektvariable danach
        // (zumindest in Kapitel 5) nicht mehr ihren Wert ändert.
        for (GameObject go: lm.gameObjects) {
            if (go.isActive()) {
                // Clip anything off-screen (Gameobjekte außerhalb des Bildschirmbereichs wegschneiden).
                // Die Gameobjekte, bei denen die Methode clipObjects() den Wert false zurückgibt,
                // werden mit der Objektvariablen "visible" als sichtbar gekennzeichnet,
                // die anderen entsprechend mit visible = false.
                if (! vp.clipObjects(go.getWorldLocation().x, go.getWorldLocation().y,
                        go.getWidth(), go.getHeight())) {
                    go.setVisible(true); // Set visible flag to true

                    // Check collision with player:
                    int hit = lm.player.checkCollisions(go.getHitbox()); // Kollision mit der Hitbox des Gameobjekts?
                    if (hit > 0) {
                        // Collision! Now deal with the different types:
                        switch (go.getType()) {

                            case 'c': // pickup Coin
                                sm.playSound("coin_pickup");
                                go.setActive(false);
                                go.setVisible(false);
                                ps.gotCredit();

                                // Now restore the state (x-velocity) of the player that was
                                // removed by collision detection:
                                if (hit != 2) { // Any hit except feet
                                    lm.player.restorePreviousVelocity();
                                }
                                break;

                            case 'u': // pickup MachineGunUpgrade
                                sm.playSound("gun_upgrade");
                                go.setActive(false);
                                go.setVisible(false);
                                lm.player.bfg.upgradeRateOfFire();
                                ps.increaseFireRate();

                                // Now restore the state (x-velocity) of the player that was
                                // removed by collision detection:
                                if (hit != 2) { // Any hit except feet
                                    lm.player.restorePreviousVelocity();
                                }
                                break;

                            case 'e': // pickup ExtraLife
                                sm.playSound("extra_life");
                                go.setActive(false);
                                go.setVisible(false);
                                ps.addLife();

                                // Now restore the state (x-velocity) of the player that was
                                // removed by collision detection:
                                if (hit != 2) { // Any hit except feet
                                    lm.player.restorePreviousVelocity();
                                }
                                break;

                            case 'd': // Hit by drone
                                PointF location;
                                sm.playSound("player_burn");
                                ps.loseLife();
                                // Letzte in der Klasse PlayerState gespeicherte Player Position:
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y); // Respawn-Position
                                // Player auf Respawn-Position setzen:
                                lm.player.setWorldLocationX(location.x);
                                lm.player.setWorldLocationY(location.y);
                                lm.player.setyVelocitiy(0);
                                break;

                            case 'g':  // Hit by guard
                                sm.playSound("player_burn");
                                ps.loseLife();
                                // Letzte in der Klasse PlayerState gespeicherte Player Position:
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y); // Respawn-Position
                                // Player auf Respawn-Position setzen:
                                lm.player.setWorldLocationX(location.x);
                                lm.player.setWorldLocationY(location.y);
                                lm.player.setyVelocitiy(0);
                                break;

                            default: // Probably a regular tile (z.B. Grass)
                                if (hit == 1) { // Left or right
                                    lm.player.setxVelocitiy(0);
                                    lm.player.isPressingRight = false;
                                    lm.player.isPressingLeft = false;
                                }

                                if (hit == 2) { // Feet
                                    lm.player.isFalling = false;
                                }
                                break;
                        }
                    }

                    if (lm.isPlaying()) {
                        go.update(fps, lm.gravity); // Run any un-clipped updates

                        if (go.getType() == 'd') {
                            // Let any near by drones know where the player is:
                            Drone d = (Drone) go;
                            d.setWaypoint(lm.player.getWorldLocation());
                        }
                    }
                }
                else {
                    go.setVisible(false); // Now draw() can ignore them.
                }
            }
        }

        if (lm.isPlaying()) {
            // Setze das Zentrum des viewports auf die aktuelle Player-Position:
            vp.setWorldCentre(lm.gameObjects.get(lm.playerIndex).getWorldLocation().x,
                    lm.gameObjects.get(lm.playerIndex).getWorldLocation().y);
        }
    }


    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            // First we lock the area of memory we will be drawing to:
            canvas = ourHolder.lockCanvas();

            // Rub out the last frame with arbitrary color:
            paint.setColor(Color.argb(255, 0, 0, 255));
            canvas.drawColor(Color.argb(255, 0, 0, 255));

            // Draw all the visible GameObjects: -------------------------------

            Rect toScreen2d = new Rect();

            // Loop through the gameObjects ArrayList once for each layer (z-Komponente)
            // starting with the lowest layer. Draw a layer at a time:
            for (int layer = -1; layer <= 1; layer++) {
                for (GameObject go : lm.gameObjects) {
                    // Only draw if visible and this layer:
                    if (go.isVisible() && go.getWorldLocation().z == layer) {
                        // Rechteck mit den Koordinaten des Gameobjekts erzeugen:
                        toScreen2d.set(vp.worldToScreen( // worldToScreen gibt die Screen-Pixelwerte für left, top, right, bottom als Rect-Objekt zurück.
                                go.getWorldLocation().x, go.getWorldLocation().y,
                                go.getWidth(), go.getHeight()));

                        if (go.isAnimated()) {
                            // Get the next frame of the bitmap. Rotate if necessary (z.B. für facing links bzw. rechts):
                            if (go.getFacing() == 1) {
                                // Rotate
                                Matrix flipper = new Matrix();
                                // Erzeugung einer Matrix für horizontale Spiegelung durch Skalierung von -1 für die x-Achse.
                                // Ein vertikaler Skalierungsfaktor von 1 ergibt sonderbarerweise eine kleine Stauchung der Playergrafik.
                                // Mit sy = 1.1 ist die Playergröße wieder gleich:
                                flipper.preScale(-1, 1.1f);
                                Rect r = go.getRectToDraw(System.currentTimeMillis()); // Rechteck mit den passenden Pixeldaten für den aus der Bitmap auszuwählenden Frame
                                Bitmap b = Bitmap.createBitmap(
                                        lm.bitmapsArray[lm.getBitmapIndex(go.getType())], // Bitmap
                                        r.left, // The x coordinate of the first pixel in source
                                        r.top, // The y coordinate of the first pixel in source
                                        r.width(), // The number of pixels in each row
                                        r.height(), // The number of rows
                                        flipper, // Matrix to be applied to the pixels
                                        true); // True if the source should be filtered. Only applies if the matrix contains more than just translation.
                                canvas.drawBitmap(b, toScreen2d.left, toScreen2d.top, paint);
                            }
                            else {
                                // No Rotation, draw it the regular way:
                                canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                        go.getRectToDraw(System.currentTimeMillis()),
                                        toScreen2d,
                                        paint);
                            }
                        }
                        else {
                            // Just draw the whole bitmap (no animation):
                            canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                    toScreen2d.left, toScreen2d.top, paint);
                        }
                    }
                }
            }

            // Draw the bullets:
            paint.setColor(Color.argb(255, 255, 255,255)); // Farbe weiß
            for (int i = 0; i < lm.player.bfg.getNumBullets(); i++) {
                // Pass i the x and y coordinates as usual, then .25 and .05 for the bullet width and height
                // Die Methode worldToScreen errechnet aus den Angaben (in Tile-Einheiten) x, y, width und height
                // die erforderlichen Parameter für die Rect.set()-Methode (in Pixel-Koordinaten)
                // und gibt dieses neu erzeugte Rect-Objekt zurück. Dieses kann dann mit drawRect()
                // der Klasse Canvas sofort auf der Canvas gezeichnet werden.
                toScreen2d.set(vp.worldToScreen(lm.player.bfg.getBulletX(i), // worldLocation.x (in Tile-Einheiten) -> left Pixelkoordinate
                        lm.player.bfg.getBulletY(i), // worldLocation.y (in Tile-Einheiten) -> top Pixelkoordinate
                        0.25f, // width (in Tile-Einheiten) -> right Pixelkoordinate
                        0.05f)); // height (in Tile-Einheiten) -> bottom Pixelkoordinate
                canvas.drawRect(toScreen2d, paint);
            }

            // Screentext for debugging:
            if (debugging) {
                paint.setTextSize(16);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255)); // weiß
                canvas.drawText("fps: " + fps, 10, 60, paint);
                canvas.drawText("num objects: " + lm.gameObjects.size(), 10, 80, paint);
                canvas.drawText("num clipped: " + vp.getNumClipped(), 10, 100, paint);
                canvas.drawText("playerX: " + lm.gameObjects.get(lm.playerIndex).getWorldLocation().x,
                        10, 120, paint);
                canvas.drawText("playerY: " + lm.gameObjects.get(lm.playerIndex).getWorldLocation().y,
                        10, 140, paint);
                canvas.drawText("Gravity: " + lm.gravity, 10, 160, paint);
                canvas.drawText("X velocity: " + lm.gameObjects.get(lm.playerIndex).getxVelocitiy(), 10, 180, paint);
                canvas.drawText("Y velocity: " + lm.gameObjects.get(lm.playerIndex).getyVelocitiy(), 10, 200, paint);
                vp.resetNumClipped(); // Reset the number of clipped objects each frame

                canvas.drawText("touch left: " + lm.player.isPressingLeft, 10, 220, paint); // DEBUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111
                canvas.drawText("touch right: " + lm.player.isPressingRight, 10, 240, paint); // DEBUG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111
            }

            // Draw buttons:
            paint.setColor(Color.argb(80, 255, 255, 255));
            ArrayList<Rect> buttonsToDraw;
            buttonsToDraw = ic.getButtons();

            for (Rect rect: buttonsToDraw) {
                // RectF holds four float coordinates for a rectangle. The rectangle is represented
                // by the coordinates of its 4 edges (left, top, right, bottom).
                RectF rf = new RectF(rect.left, rect.top, rect.right, rect.bottom);
                canvas.drawRoundRect(rf, 15f, 15f, paint);
            }

            // Draw paused text:
            if (!this.lm.isPlaying()) {
                paint.setTextAlign(Paint.Align.CENTER); // Text mittig zu (x,y) zentrieren
                paint.setColor(Color.argb(255, 255, 255, 225));
                paint.setTextSize(120);
                canvas.drawText("Paused", vp.getScreenWidth() / 2, vp.getScreenHeight() / 2, paint);
            }

            // Unlock and draw the scene:
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }


    public void loadLevel(String level, float px, float py) {

        lm = null; // LevelManager-Object initialisieren

        // Create a new LevelManager. Pass in a Context, screen details, level namer and player location.
        // Der 2. Parameter (pixelPerMetreX wird als Parameter für prepareBitmap() zur Skalierung genutzt.
        // Sinnvoller wäre eine Unterscheidung in pixelPerMetreX und pixelPerMetreY,
        // also jeweils ein zusätzlicher Parameter mehr (wird aber im Buch nicht gemacht):
        lm = new LevelManager(context, vp.getPixelsPerMetreX(), vp.getScreenWidth(), ic, level, px, py);

        ic = new InputController(vp.getScreenWidth(), vp.getScreenHeight());

        // Store the players starting location and pass it in to the PlayerState-Object ps
        // for safe keeping. Each time the player dies, he can be respawned using this location:
        PointF location = new PointF(px, py);
        ps.saveLocation(location);

        // Set the players location as the world centre (in Tile-Einheiten):
        vp.setWorldCentre(lm.gameObjects.get(lm.playerIndex).getWorldLocation().x,
                lm.gameObjects.get(lm.playerIndex).getWorldLocation().y);
    }



    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // Die Verarbeitung des "onTouchEvents" soll vom ImputController-Objekt durchgeführt werden.

        // Die Vorbedingung "lm != null" ist notwendig, weil die onTouchEvent-Methode von dem
        // Android UI Thread getriggert wird. Auf den Zeitpunkt haben wir aber keinen Einfluss.
        // Wenn wir "lm" als Parameter übergeben und "lm" aber noch nicht initialisiert ist,
        // bevor Android "onTouchEvent" aufruft, wird das Spiel crashen.
        if (lm != null) {
            ic.handleInput(motionEvent, lm, sm, vp);
        }
        return true;
    }


    // Clean up our thread if the game is interrupted:
    public void pause() {
        running = false;
        try {
            gameThread.join(); // Waits for this thread to die
        }
        catch (InterruptedException e) {
            Log.e("error", "failed to pause thread");
        }
    }


    // Make a new thread and start it if the game is resumed.
    // Execution moves to our run method
    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    public static boolean getDebugging() {
        // Von mir hinzugefügt, um auf den debugging-Schalter auch von anderen Klassen aus
        // Zugriff zu haben:
        return debugging;
    }

}
