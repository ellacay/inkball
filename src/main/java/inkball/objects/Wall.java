package inkball.objects;


public class Wall {
    public float x1, y1, x2, y2;

    public Wall(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean isVertical() {
        return x1 == x2;
    }

    public boolean isHorizontal() {
        return y1 == y2;
    }
}
