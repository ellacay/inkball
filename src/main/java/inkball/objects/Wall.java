package inkball.objects;

import inkball.loaders.ImageLoader;
import inkball.managers.BoardManager;
import processing.core.PApplet;

public class Wall {
    private PApplet app;
    public float x1, y1, x2, y2; // Coordinates of the wall
    private int hitCount = 0; // Tracks hits
    public char colour; // Wall colour
    private ImageLoader imageLoader;
    public boolean collided = false;
    public boolean isRemoved = false;

    public Wall(PApplet app, float x1, float y1, float x2, float y2, char colour, ImageLoader imageLoader) {
        this.app = app;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.colour = colour;
        this.imageLoader = imageLoader; // Make sure this is properly initialized
    }

    public void hit() {
        this.hitCount++;
    }

    public boolean canBeDamagedBy(char ballColour) {
        return this.colour == ballColour || this.colour == '0'; // Grey can be hit by any colour
    }

    public boolean isRemoved() {
        return hitCount >= 3; // Wall is removed after 3 hits
    }

    public void display() {
        if (this.hitCount >= 3) {
            // Apply tint for the tile to make it look different
            app.tint(255, 255, 255, 128); // White tint with 50% transparency
            app.image(imageLoader.tile, this.x1, this.y1);
            app.noTint(); // Reset tint for other images
            this.isRemoved = true;
            return;
        }

        // Apply tint based on hit count
        if (hitCount == 1) {
            app.tint(255, 255, 255, 128); // White tint with 50% transparency for dimming effect
        } else if (hitCount == 3) {
            app.tint(255, 255, 255, 64); // Lower transparency for smashed wall
        } else {
            app.noTint(); // No tint for other cases
        }

        // Display wall image according to colour and hit count
        switch (this.colour) {
            case '0':
                app.image(hitCount < 2 ? imageLoader.wall0 : imageLoader.smashedWall0, this.x1, this.y1);
                break;
            case '1':
                app.image(hitCount < 2 ? imageLoader.wall1 : imageLoader.smashedWall1, this.x1, this.y1);
                break;
            case '2':
                app.image(hitCount < 2 ? imageLoader.wall2 : imageLoader.smashedWall2, this.x1, this.y1);
                break;
            case '3':
                app.image(hitCount < 2 ? imageLoader.wall3 : imageLoader.smashedWall3, this.x1, this.y1);
                break;
            case '4':
                app.image(hitCount < 2 ? imageLoader.wall4 : imageLoader.smashedWall4, this.x1, this.y1);
                break;
        }

        // Reset tint for any subsequent drawing
        app.noTint();
    }

}