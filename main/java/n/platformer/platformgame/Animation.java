
/*
    Führt die Animation von geeigneten Gameobjekten durch.
 */
package n.platformer.platformgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;


public class Animation {

    // Variablen: -----------------------------------------------------------------

    Bitmap bitmapSheet; // Bitmap mit den Frames des Gameobjekts
    String bitmapName;
    private Rect sourceRect; // Nimtt später die Rect-Werte eines Frames im umgerechneten Screen-Pixel-Maßstab auf
    private int frameCount; // Anzahl der Frames
    private int currentFrame;
    private long frameTicker; // Zur Zeitmessung für den Framewechsel; wird (anfänglich) mit der Systemzeit parametriert
    private int framePeriod;
    private int frameWidth;
    private int frameHeight;
    int pixelsPerMetre; // zur Umrechnung von Tile-Einheiten in Pixelwerte


    // Konstruktor: ----------------------------------------------------------------
    Animation(Context context, String bitmapName, float frameHeight, float frameWidth, int animFps,
              int frameCount, int pixelsPerMetre) {
        this.currentFrame = 0; // mit dem 1. Frame (Position 0) beginnen
        this.frameCount = frameCount;
        this.frameWidth = (int) frameWidth * pixelsPerMetre;
        this.frameHeight = (int) frameHeight * pixelsPerMetre;
        sourceRect = new Rect(0, 0, this.frameWidth, this.frameHeight);
        framePeriod = 1000 / animFps;
        frameTicker = 0l;
        this.bitmapName = "" + bitmapName;
        this.pixelsPerMetre = pixelsPerMetre;
    }


    // Methoden: ---------------------------------------------------------------------

    public Rect getCurrentFrame(
            long time, // Systemzeit beim Aufruf
            float xVelocity, // aktuelle X-Geschwindigkeit des Gameobjekts
            boolean moves) { // Kann sich das Gameobjekt grundsätzlich bewegen (auch wenn es sich derzeit nicht bewegt)?
        // Gibt den Teil der Bitmap zurück, der den aktkuellen Frame enthält.
        // Misst und vergleicht selbständig die Zeit, um zum rechten Zeitpunkt den Folgeframe auszuwählen.

        // Only animate if the Gameobject is actual moving
        // or it is an object which doesn't move but is still animated (like fire):
        if (xVelocity != 0 || moves == false) {
            // Gameobject is animated! Time to change the frame?
            if (time > frameTicker + framePeriod) {
                frameTicker = time;
                currentFrame++;
                if (currentFrame >= frameCount) { // Höchste Framenummer überschritten?
                    currentFrame = 0; // Weiter mit dem ersten Frame
                }
            }
        }

        // Update the left and right values of the source of the next frame on the spritesheet.
        // (Damit der korrekte nächste Abschnitt (=Frame) im Spritesheet ausgewählt wird):
        this.sourceRect.left = currentFrame * frameWidth;
        this.sourceRect.right = this.sourceRect.left + frameWidth;

        return sourceRect;
    }
}
