package inkball.objects;

import processing.core.PVector;

/**
 * Represents a generic object in the game with a position, color, 
 * and radius. This is an abstract class that provides basic 
 * properties and methods for objects that can be positioned in 
 * a 2D space.
 */
public abstract class Object {
    protected PVector position;
    protected char colour;
    protected float radius;

    /**
     * Constructs an Object with specified position, color, and radius.
     *
     * @param x      The x-coordinate of the object's position.
     * @param y      The y-coordinate of the object's position.
     * @param colour  The color of the object, represented as a character.
     * @param radius  The radius of the object.
     */
    public Object(float x, float y, char colour, float radius) {
        this.position = new PVector(x, y);
        this.colour = colour;
        this.radius = radius;
    }

    /**
     * Returns the color of the object.
     *
     * @return The color of the object as a character.
     */
    public char getColour() {
        return colour;
    }

    /**
     * Returns the color of the object as a String.
     *
     * @return The color of the object as a String.
     */
    public String getColourAsString() {
        return Character.toString(colour);
    }

    /**
     * Returns the position of the object as a copy of the PVector.
     *
     * @return A PVector representing the object's position.
     */
    public PVector getPosition() {
        return position.copy();
    }

    /**
     * Returns the y-coordinate of the object's position.
     *
     * @return The y-coordinate of the object.
     */
    public float getY() {
        return position.y;
    }

    /**
     * Returns the x-coordinate of the object's position.
     *
     * @return The x-coordinate of the object.
     */
    public float getX() {
        return position.x;
    }

    /**
     * Sets the y-coordinate of the object's position.
     *
     * @param newY The new y-coordinate to set.
     */
    public void setY(float newY) {
        position.y = newY;
    }

    /**
     * Sets the x-coordinate of the object's position.
     *
     * @param newX The new x-coordinate to set.
     */
    public void setX(float newX) {
        position.x = newX;
    }

    /**
     * Returns the color of the object as a character.
     *
     * @return The color of the object as a character.
     */
    public char getCharColour() {
        return colour;
    }

    /**
     * Sets the color of the object.
     *
     * @param colour The new color to set for the object.
     */
    public void setColour(char colour) {
        this.colour = colour;
    }

    /**
     * Returns the radius of the object.
     *
     * @return The radius of the object.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the object.
     *
     * @param radius The new radius to set for the object.
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * Returns a string representation of the object's color.
     *
     * @return A string representing the color (e.g., "grey", "orange").
     */
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

    
}
