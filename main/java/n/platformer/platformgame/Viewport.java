/*
    It creates the area of the game that is to be shown to the player.
    Typically, it will enter on Bob.
 */

package n.platformer.platformgame;

import android.graphics.Rect;



public class Viewport {

    // Variables:
    // ----------------------------------------------------------------------------------
    private Vector2Point5D currentViewportWorldCentre; // In Tile-Einheiten (vgl. Klasse Vector2Point5D). Wird in der Methode "setWorldCentre()" mit Werten gefüllt.
    private Rect convertedRect;

    // Ein 'Metre' entspricht der Höhe und der Breite der kleinsten Tile-Einheit (Height = 1 und
    // Width = 1), wie sie z.B. das Tile 'Grass' darstellt. Je nach Display-Auflösung werden
    // unterschiedliche Pixelanzahlen pro Metre benötigt, was sich in der Variablen "pixelsPerMetre"
    // widerspiegelt. Im Konstruktor wird für eine Screen-Breite 32 'Tile'-Einheiten ('Metre') veranschlagt
    // und für eine Screen-Höhe 18 'Tile'-Einheiten ('Metre'),
    // woraus sich dann die Pixelzahl pro Metre für X bzw. Y berechnent.
    private int pixelsPerMetreX;
    private int pixelsPerMetreY;

    private int screenXResolution; // in Pixel
    private int screenYResolution; // in Pixel
    private int screenCentreX; // in Pixel
    private int screenCentreY; // in Pixel
    private int metresToShowX; // Wie viele Tile-Einheiten eine Screenbreite darstellen soll
    private int metresToShowY; // Wie viele Tile-Einheiten eine Screenhöhe darstellen soll
    private int numClipped; // Debug


    // Konstruktor:
    // ------------------------------------------------------------------------------------
    Viewport(int x, int y) {
        // Parameter:
        //          x: screenWidth
        //          y: screeHeight
        screenXResolution = x;
        screenYResolution = y;

        screenCentreX = screenXResolution / 2;
        screenCentreY = screenYResolution / 2;

        pixelsPerMetreX = screenXResolution / 32; // Siehe Kommentar bei der Deklaration von "pixelsPerMetreX".
        pixelsPerMetreY = screenYResolution / 18; // Siehe Kommentar bei der Deklaration von "pixelsPerMetreY".

        metresToShowX = 34; // Overscan
        metresToShowY = 20; // Overscan

        convertedRect = new Rect();
        currentViewportWorldCentre = new Vector2Point5D();

        if (PlatformView.getDebugging()) { // Debug-Infos?
            System.out.println("\nViewport-Variablen: ----------------------------------------------");
            System.out.println("screenXResolution: " + screenXResolution + ", screenYResolution: " + screenYResolution
                    + ", pixelsPerMetreX: " + pixelsPerMetreX + ", pixelsPerMetreY: " + pixelsPerMetreY + "\n");
        }
    }


    // Methoden:
    // -------------------------------------------------------------------------------------
    public Rect worldToScreen(float objectX, float objectY, float objectWidth, float objectHeight) {
        // Parameter:
        //          objectX:        worldLocation.x (in Tile-Einheiten) des Gameobjekts
        //          objectY:        worldLocation.y (in Tile-Einheiten) des Gameobjekts
        //          objedtWidth:    width des Gameobjekts (in Tile-Einheiten für width = 1 oder in Untereinheiten davon für width > 1), bei dessen Definition mithilfe des Konstruktors festgelegt
        //          objedtHeight:   height des Gameobjekts (in Tile-Einheiten für height = 1 oder in Untereinheiten davon für height > 1), bei dessen Definition mithilfe des Konstruktors festgelegt

        // Converts the locations of a Gameobject currently in the visible viewport from
        // world coordinates (in Tile-Einheiten) to pixel coordinates that can actually be drawn to the screen.
        // The values are then packed into the left, top, right and bottom values of "convertedRect" and returned
        // to the draw()-Method of PlatformView, which has called the wordToScreen()-Method. The draw()-Method
        // calls the worldToScreen-Method only for the Gameobjects with the attribut "visible" = true.
        // In "currentViewportWorldCentre" ist (aus einem PlatformView-Objekt) heraus die aktuelle Player-Position
        // (in Tile-Einheiten) geladen worden.
        // In "screenCentreX" bzw. "screenCentreY" sind jeweils die Hälfte der Screen-Auflösung (in Pixel)
        // geladen, d.h. sie stellen immer genau die Screenmitte dar.

        int left = (int) (screenCentreX - ((currentViewportWorldCentre.x - objectX) * pixelsPerMetreX));
        int top = (int) (screenCentreY - ((currentViewportWorldCentre.y - objectY) * pixelsPerMetreY));
        int right = (int) (left + (objectWidth * pixelsPerMetreX));
        int bottom = (int) (top + (objectHeight * pixelsPerMetreY));

        convertedRect.set(left, top, right, bottom);
        return convertedRect;
    }


    public boolean clipObjects(float objectX, float objectY, float objectWidth, float objectHeight) {
        // Parameter:
        //          objectX:        worldLocation.x (in Tile-Einheiten) des Gameobjekts
        //          objectY:        worldLocation.y (in Tile-Einheiten) des Gameobjekts
        //          objedtWidth:    width des Gameobjekts (in Tile-Einheiten für width = 1 oder in Untereinheiten davon für width > 1), bei dessen Definition mithilfe des Konstruktors festgelegt
        //          objedtHeight:   height des Gameobjekts (in Tile-Einheiten für height = 1 oder in Untereinheiten davon für height > 1), bei dessen Definition mithilfe des Konstruktors festgelegt

        // Removes objects that are currently of no interest:

        boolean clipped = true;

        if ((objectX - objectWidth < currentViewportWorldCentre.x + (metresToShowX / 2)) // linke Seite innerhalb des Screenbereichs?
                && (objectX + objectWidth > currentViewportWorldCentre.x - (metresToShowX / 2)) // rechte Seite innerhalb des Screenbereichs?
                && (objectY - objectHeight < currentViewportWorldCentre.y + (metresToShowY / 2)) // obere Seite innerhalb des Screenbereichs?
                && (objectY + objectHeight > currentViewportWorldCentre.y - (metresToShowY / 2))) // untere Seite innerhalb des Screenbereichs?
        {
            clipped = false; // alle Seiten innerhalb des Screenbereichs!
        }

        // For debugging:
        if (clipped) {
            numClipped++;
        }

        return clipped;
    }




    // Getter, Setter and Reset:
    // -------------------------------------------------------------------------------------
    void setWorldCentre(float x, float y) {
        // in Tile-Einheiten (vgl. Klasse Vector2Point5D)
        // Wird von der Klasse PlatformView aufgerufen, um die Player-Position als currentViewportWordCentre
        // (Mittelpunkt des aktuell darzustellenden Screens) zu setzen:
        currentViewportWorldCentre.x = x;
        currentViewportWorldCentre.y = y;
    }

    public int getScreenWidth() {
        return  screenXResolution;
    }

    public int getScreenHeight() {
        return screenYResolution;
    }

    public int getPixelsPerMetreX() {
        return pixelsPerMetreX;
    }

    public int getNumClipped() {
        return numClipped;
    }

    public void resetNumClipped() {
        numClipped = 0;
    }

}
