package inkball.objects;

import processing.core.PVector;

public class Hole {
    private PVector position;
    private float radius;
    private int color; // Assuming color is stored as an int (ARGB)

    public Hole(float x, float y, float radius, char color) {
        this.position = new PVector(x+16, y+16);
        this.radius = radius;
        this.color = color;
    }

    public PVector getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }
    public int getColour() {
        return color;
    }

}
