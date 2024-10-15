package inkball.objects;

import processing.core.PVector;

public class Hole {
    private PVector position;
    private float radius;
    private int colour;

    public Hole(float x, float y, float radius, char colour) {
        this.position = new PVector(x + 16, y + 16);
        this.radius = radius;
        this.colour = colour;
    }

    public PVector getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

}
