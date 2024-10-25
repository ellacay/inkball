package inkball.objects;

import inkball.loaders.ImageLoader;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Represents a wall in the game that can be hit by balls.
 * The wall has properties such as position, color, and hit count,
 * which determine its behavior when interacted with by balls.
 */

public class Wall {
    private PApplet app;
    public float x1, y1, x2, y2;
    public int hitCount = 0;
    public char colour;
    private ImageLoader imageLoader;
    public boolean collided = false;
    public boolean tintApplied = false;
    public boolean isRemoved = false;
    public PImage newImage;

    /**
     * Constructs a Wall object with specified position, color, and associated image
     * loader.
     *
     * @param app         The PApplet instance used for rendering.
     * @param x1          The x-coordinate of the starting point of the wall.
     * @param y1          The y-coordinate of the starting point of the wall.
     * @param x2          The x-coordinate of the ending point of the wall.
     * @param y2          The y-coordinate of the ending point of the wall.
     * @param colour      The character representing the color of the wall.
     * @param imageLoader The ImageLoader instance used to load wall images.
     */
    public Wall(PApplet app, float x1, float y1, float x2, float y2, char colour, ImageLoader imageLoader) {
        this.app = app;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.colour = colour;
        this.imageLoader = imageLoader;
    }

    /**
     * Increments the hit count of the wall when it is hit by a ball.
     */
    public void hit() {
        this.hitCount++;
    }

    /**
     * Sets the color of the wall to the specified new color.
     *
     * @param newColour The new color character to assign to the wall.
     */
    public void setColour(char newColour) {
        this.colour = newColour;
    }

    /**
     * Determines if the wall can be damaged by a ball of a specific color.
     * A wall can be damaged if the ball's color matches the wall's color
     * or if the wall's color is neutral ('0').
     *
     * @param ballColour The color character of the ball.
     * @return true if the wall can be damaged by the ball; false otherwise.
     */
    public boolean canBeDamagedBy(char ballColour) {
        return this.colour == ballColour || this.colour == '0';
    }

    /**
     * Checks if the wall has been removed based on its hit count.
     * A wall is considered removed if it has been hit 3 or more times.
     *
     * @return true if the wall is removed; false otherwise.
     */
    public boolean isRemoved() {
        return hitCount >= 3;
    }

    /**
     * Displays the wall on the screen based on its hit count and color.
     * If the wall has been hit 3 or more times, it is rendered as removed.
     * Otherwise, it displays the wall according to its current hit state.
     */
    public void display() {
        if (this.hitCount >= 3) {
            app.tint(255, 255, 255, 128);
            app.image(imageLoader.tile, this.x1, this.y1);
            app.noTint();
            this.isRemoved = true;
            return;
        }
        if (hitCount == 1) {
            app.tint(255, 255, 255, 128);
            tintApplied = true;
        } else {
            app.noTint();
        }
        switch (this.colour) {
            case '0':
                app.image(hitCount < 2 ? imageLoader.wall0 : imageLoader.smashedWall0, this.x1, this.y1);
                break;
            case '1':
                newImage = (hitCount < 2) ? imageLoader.wall1 : imageLoader.smashedWall1;
                app.image(newImage, this.x1, this.y1);
                break;
            case '2':
                newImage = hitCount < 2 ? imageLoader.wall2 : imageLoader.smashedWall2;
                app.image(newImage, this.x1, this.y1);
                break;
            case '3':
                newImage = hitCount < 2 ? imageLoader.wall3 : imageLoader.smashedWall3;
                app.image(newImage, this.x1, this.y1);
                break;
            case '4':
                newImage = hitCount < 2 ? imageLoader.wall4 : imageLoader.smashedWall4;
                app.image(newImage, this.x1, this.y1);
                break;
        }
        app.noTint();
    }

}