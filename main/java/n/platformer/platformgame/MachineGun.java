/*
    Maschinengewehr des Spielers. Erweiterung der Klasse GameObject.
 */

package n.platformer.platformgame;

import java.util.concurrent.CopyOnWriteArrayList;


public class MachineGun extends GameObject {

    // Variables: ----------------------------------------------------------------------

    private int maxBullets = 10; // Maximale Ladung pro Machinegun
    private int numBullets; // Anzahl der geschossenen Bullets
    private int nextBullet; // Index für aktuellen Bullet
    private int rateOfFire = 1; // Bullets pro second
    private long lastShotTime; // In Systemzeit-Millisekunden

    private CopyOnWriteArrayList<Bullet> bullets; // Wie ArrayList aber threadsicher

    int speed = 25;


    // Constructor: -------------------------------------------------------------------

    MachineGun() {
        bullets = new CopyOnWriteArrayList<Bullet>();
        lastShotTime = -1;
        nextBullet = -1;
    }



    // Methods: ------------------------------------------------------------------------

    public void update(long fps, float gravity) {
        // Update all the bullets
        for (Bullet bullet: bullets) {
            bullet.update(fps, gravity);
        }
    }


    public boolean shoot(float ownerX, float ownerY, int ownerFacing, float ownerHeight) {
        boolean shotFired = false;
        if (System.currentTimeMillis() - lastShotTime > 1000 / rateOfFire) { // Umrechnung mit Faktor 1000, weil lastShortTime in ms und rateOfFire in 1/s gemessen wird
            // Spawn another bullet:
            nextBullet++;
            if (numBullets >= maxBullets) {
                numBullets = maxBullets;
            }

            if (nextBullet == maxBullets) {
                nextBullet = 0;
            }

            lastShotTime = System.currentTimeMillis();
            bullets.add(nextBullet, new Bullet(ownerX, (ownerY + ownerHeight / 3), speed, ownerFacing));

            shotFired = true;
            numBullets++;
        }
        return shotFired;
    }


    public void  hideBullet(int index) {
        bullets.get(index).hideBullet();
    }


    public void upgradeRateOfFire() {
        rateOfFire += 2;
    }



    // Getter & Setter: -----------------------------------------------------------------

    public int getRateOfFire() {
        return rateOfFire;
    }

    public void setFireRate(int rate) {
        rateOfFire = rate;
    }

    public int getNumBullets() {
        // Tell the view how many bullets there are
        return numBullets;
    }

    public float getBulletX(int bulletIndex) {
        if (bullets != null && bulletIndex < numBullets) {
            return bullets.get(bulletIndex).getX();
        }
        return -1f;
    }

    public float getBulletY(int bulletIndex) {
        if (bullets != null) {
            return bullets.get(bulletIndex).getY();
        }
        return -1f;
    }

    public int getDirection(int index) {
        return bullets.get(index).getDirection();
    }
}
