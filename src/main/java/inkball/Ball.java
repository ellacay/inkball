package inkball;

import java.util.Random;
import processing.core.PApplet;
import processing.core.PImage;

public class Ball {
    private PApplet app; // Reference to PApplet
    private PImage image;
    private float x, y;
    private float velocityX, velocityY;
    private float width;
    private float height;

    public Ball(PApplet app, PImage image, float velocityX, float velocityY, float width, float height) {
        this.app = app; // Initialize PApplet reference
        this.image = image;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.width = width;
        this.height = height;
        this.x = app.random(width); // Random initial position
        this.y = app.random(height);
    }

    public void update() {
        x += velocityX;
        y += velocityY;

        // Handle collisions with walls or boundaries here
    }

    public void display() {
        app.image(image, x, y); // Use PApplet instance to call image()
    }
}
