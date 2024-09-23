package inkball.objects;

import processing.core.PVector;

public class Hole {
    private PVector position;
    private float radius;

    public Hole(float x, float y, float radius) {
        this.position = new PVector(x, y);
        this.radius = radius;
    }

    public PVector getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }
}
