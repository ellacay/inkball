package inkball.objects;

import processing.core.PVector;

public class Wall {
    public float x1, y1, x2, y2;

    public Wall(float d, float e, float f, float g) {
        this.x1 = d;
        this.y1 = e;
        this.x2 = f;
        this.y2 = g;
    }

    public boolean isVertical() {
        return x1 == x2;
    }
public PVector getCenter() {
    return new PVector((x1 + x2) / 2, (y1 + y2) / 2);
}

    public boolean isHorizontal() {
        return y1 == y2;
    }

    @Override
    public String toString() {
        return "Wall{" + 
               "x1=" + x1 + 
               ", y1=" + y1 + 
               ", x2=" + x2 + 
               ", y2=" + y2 + 
               '}';
    }
}
