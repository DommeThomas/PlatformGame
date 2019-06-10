/*
    Start-Activity des Spiels Platform Game.
    Vgl. Buch Android Game Programming by Example (John Horton), S. 99 ff).
    Wird beim Starten der App aufgerufen, fragt die aktuelle Displaygröße ab, erzeugt eine
    Instanz der Klasse PlatformView (Kindklasse von SurfaceView), übergibt ihr die Displaygröße
    und setzt das erzeugte Objekt als ContenView.

    Der aufgerufene Konstruktor der Klasse PlatformView übernimmt dann die weitere Programmsteuerung.

    Sofern es zu den App-Lebenszyklen "onPause()" oder "onResume()" kommt, verweisen diese auf
    entsprechende Methoden ("pause()" und "resume()") der Klasse PlatformView.
 */

package n.platformer.platformgame;


import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class PlatformActivity extends Activity {

    // Our Object to handle the View:
    private PlatformView platformView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details:
        Display display = getWindowManager().getDefaultDisplay();

        //Load the resolution into a Point object:
        Point resolution = new Point();
        display.getSize(resolution);

        // Finally set the view for our game and also passing in the screen resolution:
        platformView = new PlatformView(this, resolution.x, resolution.y);

        // Make our platformView the view for the Activity:
        setContentView(platformView);
    }


    // If the Activity is paused make sure to pause our thread:
    @Override
    protected void onPause() {
        super.onPause();
        platformView.pause();
    }


    // If the Activity is resumed make sure to resume our thread:
    @Override
    protected void onResume() {
        super.onResume();
        platformView.resume();
    }
}
