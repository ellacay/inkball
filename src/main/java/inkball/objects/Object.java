package inkball.objects;

import processing.core.PVector;

public abstract class Object {
    protected PVector position;
    protected char colour;
    protected float radius;

    public Object(float x, float y, char colour, float radius) {
        this.position = new PVector(x, y);
        this.colour = colour;
        this.radius = radius;
    }

    public char getColour() {
        return colour;
    }

    public String getColourAsString() {
        return Character.toString(colour);
    }

    public PVector getPosition() {
        return position.copy();
    }

    public float getY() {
        return position.y;
    }

    public float getX() {
        return position.x;
    }

    public void setY(float newY) {
        position.y = newY;
    }

    public void setX(float newX) {
        position.x = newX;
    }

    public char getCharColour() {
        return colour;
    }

    public void setColour(char colour) {
        this.colour = colour;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getColourString() {
        switch ((this.colour)) {
            case '0':
                return "grey";
            case '1':
                return "orange";
            case '2':
                return "blue";
            case '3':
                return "green";
            case '4':
                return "yellow";
            default:
                return null;
        }
    }

    public abstract void display();
}
