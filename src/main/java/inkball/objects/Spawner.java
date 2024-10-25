package inkball.objects;
/**
 * Represents a spawner in the game that defines a rectangular area
 * from which objects can be spawned. The spawner is defined by its 
 * corners specified by coordinates (x1, y1) and (x2, y2).
 */
public class Spawner {

    public float x1, x2, y1, y2;
 /**
     * Constructs a Spawner object with specified coordinates.
     *
     * @param x1 The x-coordinate of the first corner of the spawner.
     * @param y1 The y-coordinate of the first corner of the spawner.
     * @param x2 The x-coordinate of the opposite corner of the spawner.
     * @param y2 The y-coordinate of the opposite corner of the spawner.
     */
    public Spawner(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }


}
