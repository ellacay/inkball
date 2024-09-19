package inkball;
import processing.core.PApplet;
import processing.core.PImage;

public class Ball {
    private PApplet app; 
    private PImage image;
    private float x, y;
    private float velocityX, velocityY;

    public Ball(PApplet app, PImage image, float velocityX, float velocityY, float width, float height) {
        this.app = app; 
        this.image = image;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.x = app.random(width);
        this.y = app.random(height);
    }

    public void update() {
        x += velocityX;
        y += velocityY;

    }

    public void display() {
        app.image(image, x, y); 
    }
}
