/*
    Repräsentiert die "world location" der jeweiligen Gameobjekte. Was genau sie darstellen, wird
    aus dem Buchtext (vgl. S. 110) leider nicht deutlich.
    Vermutlich entsprechen sie den Pixel-Koordinaten (bezogen auf den gesamten Level oder nur dem
    jeweiligen Ausschnitt (Viewport), dass das Display anzeigt?).
    Die z-Achse ist als Layer-Nummer interpretierbar. Die kleinere Nummer wird
    zuerst gewählt und dargestellt.
 */

package n.platformer.platformgame;

public class Vector2Point5D {
    // x bzw. y entsprechen 'Tile-Einheiten': Jedes Tile (= Zeichen in der Levelmap wie z.B. "LevelCave"
    // entspricht der x-Einheit "1", jede Zeile (= String in der Levelmap) der y-Einheit "1".
    float x; // X-Position (s.o.)
    float y; // Y-Position (s.o.)
    int z; // Z-Layer (kleinstes Z -> unterste Folie)
}
