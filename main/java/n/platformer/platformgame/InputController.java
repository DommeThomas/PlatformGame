/*
    Verarbeitet die User-Eingaben.
 */

package n.platformer.platformgame;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;



public class InputController {

    // Variablen: -----------------------------------------------------------------------

    // Maße und Positionen der Touchfelder ("Buttons":
    Rect left;
    Rect right;
    Rect jump;
    Rect shoot;
    Rect pause;

    String debug = "Debugging"; // Für gezielte LocCat-Ausgaben


    // Konstruktor: ----------------------------------------------------------------------
    InputController(int screenWidth, int screenHeight) {
        // Configure the player buttons. Die Buttons werden proportional zur Screen-Größe und
        // -Auflösung konfiguriert:
        int buttonWidth = screenWidth / 8;
        int buttonHeight = screenWidth / 7;
        int buttonPadding = screenWidth / 80;

        left = new Rect(buttonPadding,                              // Parameter left-coordinate
                screenHeight - buttonHeight - buttonPadding,   // Parameter top-coordinate
                buttonWidth,                                        // Parameter right-coordinate
                screenHeight - buttonPadding);              // Parameter bottom-coordinate

        right = new Rect(buttonWidth + buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                buttonWidth + buttonPadding + buttonWidth,
                screenHeight - buttonPadding);

        jump = new Rect(screenWidth - buttonWidth - buttonPadding,
                screenHeight - buttonHeight - buttonPadding - buttonHeight - buttonPadding,
                screenWidth - buttonPadding,
                screenHeight - buttonPadding - buttonHeight - buttonPadding);

        shoot = new Rect(screenWidth - buttonWidth - buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                screenWidth - buttonPadding,
                screenHeight - buttonPadding);

        pause = new Rect(screenWidth - buttonPadding - buttonWidth,
                buttonPadding,
                screenWidth - buttonPadding,
                buttonPadding + buttonHeight);
    }


    public ArrayList getButtons() {
        // Create an array of buttons for the draw method:
        ArrayList<Rect> currentButtonList = new ArrayList<>();
        currentButtonList.add(left);
        currentButtonList.add(right);
        currentButtonList.add(jump);
        currentButtonList.add(shoot);
        currentButtonList.add(pause);
        return currentButtonList;
    }


    public void handleInput(MotionEvent motionEvent, LevelManager l, SoundManager sound, Viewport vp) {
        // Diese Methode wird von der überschriebenen Eventmethode "onTouchEvent()" aus der
        // Klasse PlatformView aufgerufen und zwar immer dann, wenn der Screen durch den User
        // berührt oder losgelassen wird (auch bei einzelnen Änderungen im Multitouch).

        // In order to record and pass on the details of multiple fingers, touching, leaving and
        // moving on the screen, the MotionEvent class stores them all in an array.
        // When the first finger touches the screen, the details, coordinates, and so on,
        // are stored at position zero. Subsequent actions are stored later in the array.
        // The posiotion in the array related to any such finger's activity is not consistent.
        // Eine 2. (3., 4., usw.) zeitgleiche Berührung erzeugt statt eines ACTION_DOWN Events
        // ein ACTION_POINTER_DOWN Event. Wenn eine dieser (zeitparallelen) Berührungen endet,
        // wird ein ACTION_POINTER_UP Event erzeugt.
        // Erst wenn die letzte noch verbliebene Berührung endet, wird ein ACTION_UP Event erzeugt.

        // Im Buch S. 161 versucht der Autor die Änderungen dadurch auszuwerten,
        // dass er in einer Schleife das gesamte Array durchläuft und in jedem Schleifendurchgang
        // jeden Button überprüft, ob seine Koordinaten zutreffen.
        // Abgesehen davon, dass die Koordinatenermittlung fehlerhaft programmiert ist
        // ("motionEvent.get(x)" anstelle von "motionEvent.getActionIndex()") und dies dazu führt, dass
        // jeder Finger in seinem zugehörigen Schleifendurchgang immer wieder erneut das neu
        // hinzugekommene Ereignis (z.B. ACTION_POINTER_UP) fälschlicherweise auswertet, (weil mit
        // "motionEvent.get(x)" immer die Koordinaten des betroffenen Fingers ausgewertet werden),
        // reicht es, wenn immer jeweil nur die neu hinzugekommene Änderung ausgewertet wird!
        // Das neue aufgetretene (triggernde) Ereignis lässt sich mit der MotionEvent-Methode "getActionIndex()"
        // leicht herausfinden:

        int triggerEventIndex = motionEvent.getActionIndex(); // Der Index der auslösenden Berührung bzw. des Loslassen (Wichtig by Multitouch!)

        int x = (int) motionEvent.getX(motionEvent.getActionIndex()); // Touch-Koordinate (X) des aktuellen Array-Elements
        int y = (int) motionEvent.getY(motionEvent.getActionIndex()); // Touch-Koordinate (Y) des aktuellen Array-Elements

        if (l.isPlaying()) {
            switch (motionEvent.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    if (right.contains(x, y)) { // Returns true if (x,y) is inside the rectangle.
                        l.player.setPressingRight(true);
                        l.player.setPressingLeft(false);
                        Log.w(debug, "Button right: ACTION_DOWN" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (left.contains(x, y)) {
                        l.player.setPressingLeft(true);
                        l.player.setPressingRight(false);
                        Log.w(debug, "Button left: ACTION_DOWN" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (jump.contains(x, y)) {
                        l.player.startJump(sound);
                        Log.w(debug, "Button jump: ACTION_DOWN" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (shoot.contains(x, y)) {
                        if (l.player.pullTrigger()) {
                            Log.w(debug, "Button shoot: ACTION_DOWN" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                            sound.playSound("shoot");
                        }
                    } else if (pause.contains(x, y)) {
                        l.switchPlayingStatus();
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    if (right.contains(x, y)) {
                        l.player.setPressingRight(false);
                        Log.w(debug, "Button right: ACTION_UP" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (left.contains(x, y)) {
                        l.player.setPressingLeft(false);
                        Log.w(debug, "Button left: ACTION_UP" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (jump.contains(x, y)) {
                        Log.w(debug, "Button jump: ACTION_UP" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (shoot.contains(x, y)) {
                        Log.w(debug, "Button shoot: ACTION_UP" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    }
                    break;

                case MotionEvent.ACTION_POINTER_DOWN: // Weitere Finger-Touches nach dem 1. Touch?
                    if (right.contains(x, y)) { // Returns true if (x,y) is inside the rectangle.
                        l.player.setPressingRight(true);
                        l.player.setPressingLeft(false);
                        Log.w(debug, "Button right: ACTION_POINTER_DOWN" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (left.contains(x, y)) {
                        l.player.setPressingLeft(true);
                        l.player.setPressingRight(false);
                        Log.w(debug, "Button left: ACTION_POINTER_DOWN" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (jump.contains(x, y)) {
                        l.player.startJump(sound);
                        Log.w(debug, "Button jump: ACTION_POINTER_DOWN" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (shoot.contains(x, y)) {
                        if (l.player.pullTrigger()) {
                            Log.w(debug, "Button shoot: ACTION_POINTER_DOWN" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                            sound.playSound("shoot");
                        }
                    } else if (pause.contains(x, y)) {
                        l.switchPlayingStatus();
                    }

                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    if (right.contains(x, y)) {
                        l.player.setPressingRight(false); // Änderung!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        Log.w(debug, "Button right: ACTION_POINTER_UP" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (left.contains(x, y)) {
                        l.player.setPressingLeft(false); // Änderung!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        Log.w(debug, "Button left: ACTION_POINTER_UP" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                    } else if (shoot.contains(x, y)) {
                        Log.w(debug, "Button shoot: ACTION_POINTER_UP" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                        // Handle shooting here
                    } else if (jump.contains(x, y)) {
                        Log.w(debug, "Button jump: ACTION_POINTER_UP" + "   x: " + x + "   y: " + y + "    Pointer-ID: " + motionEvent.getPointerId(triggerEventIndex));
                        // Handle more jumping stuff here later
                    }

                    break;
            } // End switch
        } // End if(l.playing)

        else { // Not playing
            // Move the viewport around to explore the map:   Offenbar noch nicht implementiert!!!!!!!!!!
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (pause.contains(x, y)) {
                        l.switchPlayingStatus();
                        // Log.w("pause: ", "DOWN");
                    }

                    break;
            }
        }
    }
}
