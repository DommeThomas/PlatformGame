/*
    Basisklasse für die Game-Objekte.
 */

package n.platformer.platformgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


public abstract class GameObject {

    // Variables:
    // ------------------------------------------------------------------------

    private RectHitbox rectHitbox = new RectHitbox();

    // Lokalisierung:
    // X-, Y-Position in Tiles-Einheiten (X entspricht Zeichenposition im String
    // und Y der Stringposition in der ArrayList "tiles" der Levelmap (z.B. "LevelCave").
    // Zudem noch Z-Layer als Folienlage: kleinstes Z -> unterste Folie:
    private Vector2Point5D worldLocation;

    private float width; // in Tiles-Grundeinheiten (z.B. bei Grass ist width = 1)
    private float height; // in Tiles-Grundeinheiten (z.B. bei Grass ist height = 1, beim Player = 2)

    private boolean traversable = false; // Können der Player oder Guard über das Gameobjekt gehen (passierbar)?
    private boolean active = true;
    private boolean visible = true;
    private int animFrameCount = 1;
    private char type;

    private String bitmapName;

    private float xVelocitiy;
    private float yVelocitiy;
    final int LEFT = -1;
    final int RIGHT = 1;
    private int facing; // In welche Richtung blickt das Gameobjekt (links oder rechts)?
    private boolean moves = false; // Kann sich das Gameobjekt bewegen?

    // Most objects only have 1 frame and don't need to bother with animation and these atrributes:
    private Animation anim = null;
    private boolean animated;
    private int animFps = 1;



    // Methods:
    // -----------------------------------------------------------------------
    public abstract void update(long fps, float gravity);


    public Bitmap prepareBitmap(Context context, String bitmapName, int pixelsPerMetre) {
        // Erzeugt aus der Resource-Datei eine skalierte Bitmap, die dann später in BitmapsArray
        // gespeichert werden kann, um darauf während des Spiels leicht zugreifen zu können.
        // Der 3. Parameter (pixelPerMetreX wird als Parameter für prepareBitmap() zur Skalierung genutzt.
        // Sinnvoller wäre eine Unterscheidung in pixelPerMetreX und pixelPerMetreY,
        // also jeweils ein zusätzlicher Parameter mehr (wird aber im Buch nicht gemacht).
        // Ein 'Metre' entspricht der Höhe und der Breite der kleinsten Tile-Einheit (Height = 1 und
        // Width = 1), wie sie z.B. das Tile 'Grass' darstellt. Je nach Display-Auflösung werden
        // unterschiedliche Pixelanzahlen pro Metre benötigt, was sich in der Variablen "pixelsPerMetre"
        // widerspiegelt.

        // Make a resource ID from the bitmapName:
        int resID = context.getResources().getIdentifier(bitmapName, "drawable", context.getPackageName());

        // Create the bitmap:
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID);

        // Scale the bitmap based on the number of pixels per metre.
        // Multiply by the number of frames in the image. Default: 1 frame.
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * animFrameCount * pixelsPerMetre),
                (int) (height * pixelsPerMetre), false);

        return bitmap;
    }


    void move(long fps) {
        // Bewegt das Gameobjekt durch Änderung seiner worldLocation-Attribute, sofern
        // dessen Geschwindigkeit ungleich Null ist.
        // Damit die Bewegung auf allen Endgeräten gleich schnell ist, wird die Geschwindigkeit
        // durch fps dividiert, da in jedem neuen Frame die update-Methode aufgerufen wird, welche
        // auch die Bewegung updatet:

        if (xVelocitiy != 0) {
            this.worldLocation.x += xVelocitiy / fps;
        }
        if (yVelocitiy != 0) {
            this.worldLocation.y += yVelocitiy / fps;
        }
    }


    public void setAnimated(Context context, int pixelsPerMetre, boolean animated) {
        // Parametriert und erzeugt das komplette "animation object" für ein (animierbares) Gameobjekt:
        this.animated = animated;
        this.anim = new Animation(context, bitmapName, height, width, animFps, animFrameCount, pixelsPerMetre);
    }


    public Rect getRectToDraw(long deltaTime) {
        // Parameter deltaTime: Systemzeit beim Aufruf (für die Zeitmessung zum Framewechsel)

        // Diese Methode wird von der draw()-Methode aus der Klasse PlatformView aufgerufen,
        // um das aktuell zu zeichnende Frame als Rückgabewert zu bekommen
        // (mittels der getCurrentFrame()-Methode aus der Animation Klasse):
        return anim.getCurrentFrame(deltaTime, xVelocitiy, isMoves());
    }



    // Methods to check the Status:
    // ----------------------------------------------------------------------
    public boolean isActive() {
        return active;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isMoves() {
        return moves;
    }

    public boolean isAnimated() {
        return animated;
    }

    public boolean isTraversable() {
        return traversable;
    }


    // Getter and Setter:
    // ----------------------------------------------------------------------

    public String getBitmapName() {
        return bitmapName;
    }

    public Vector2Point5D getWorldLocation() {
        return worldLocation;
    }

    public void setWorldLocation(float x, float y, int z) {
        this.worldLocation = new Vector2Point5D();
        this.worldLocation.x = x; // s. Kommentar in der Klasse Vector2Point5D
        this.worldLocation.y = y; // s. Kommentar in der Klasse Vector2Point5D
        this.worldLocation.z = z; // s. Kommentar in der Klasse Vector2Point5D
    }

    public void setWorldLocationX(float x) {
        this.worldLocation.x = x;
    }

    public void setWorldLocationY(float y) {
        this.worldLocation.y = y;
    }

    public void setBitmapName(String bitmapName) {
        this.bitmapName = bitmapName;
    }

    public float getWidth() {
        return width;
    }

    public void  setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public float getxVelocitiy() {
        return xVelocitiy;
    }

    public void setxVelocitiy(float xVelocitiy) {
        // Only allow for objects that can move
        if (moves) {
            this.xVelocitiy = xVelocitiy;
        }
    }

    public float getyVelocitiy() {
        return yVelocitiy;
    }

    public void setyVelocitiy(float yVelocitiy) {
        // Only allow for objects that can move
        if (moves) {
            this.yVelocitiy = yVelocitiy;
        }
    }

    public void setMoves(boolean moves) {
        this.moves = moves;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRectHitbox() {
        rectHitbox.setTop(worldLocation.y);
        rectHitbox.setLeft(worldLocation.x);
        rectHitbox.setBottom(worldLocation.y + height);
        rectHitbox.setRight(worldLocation.x + width);
    }

    public RectHitbox getHitbox() {
        return rectHitbox;
    }

    public void setAnimFps (int animFps) {
        this.animFps = animFps;
    }

    public void setAnimFrameCount(int animFrameCount) {
        this.animFrameCount = animFrameCount;
    }

    public void setTraversable() {
        traversable = true;
    }

}
