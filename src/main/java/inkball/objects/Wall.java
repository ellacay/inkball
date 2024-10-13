package inkball.objects;

import inkball.loaders.ImageLoader;
import processing.core.PApplet;

public class Wall {
    private PApplet app;
    public float x1, y1, x2, y2; // Coordinates of the wall
    private int hitCount = 0; // Tracks hits
    public char color; // Wall color
    private ImageLoader imageLoader;
    public boolean collided = false;

    public Wall(PApplet app, float x1, float y1, float x2, float y2, char color, ImageLoader imageLoader) {
        this.app = app;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.imageLoader = imageLoader; // Make sure this is properly initialized
    }
    public void hit() {
            this.hitCount++;
    }

    public boolean canBeDamagedBy(char ballColor) {
        return this.color == ballColor || this.color == '0'; // Grey can be hit by any color
    }

    public boolean isRemoved() {
        return hitCount >= 3; // Wall is removed after 3 hits
    }


    // public void display() {
    //     app.stroke(255, 0, 0); // Set color to red for visibility
    //     app.strokeWeight(3); // Increase stroke weight for better visibility

    //     // Draw horizontal lines
    //     app.line(x1, y1, x2, y1); // Top edge
    //     app.line(x1, y2, x2, y2); // Bottom edge

    //     // Draw vertical lines
    //     app.line(x1, y1, x1, y2); // Left edge
    //     app.line(x2, y1, x2, y2); // Right edge
    // }
    public void display() {

        switch(this.color){
    
            case '0':
            if(this.hitCount == 0){
                app.image(imageLoader.wall0, this.x1, this.y1);
                break;
            }
            else{
                app.image(imageLoader.smashedWall0, this.x1, this.y1);
                break;
            }
                
            case '1':
            if(this.hitCount == 0){
                app.image(imageLoader.wall1, this.x1, this.y1);
                break;
            }
            else{
                app.image(imageLoader.smashedWall1, this.x1, this.y1);
                break;
            }
               
            case '2':
            if(this.hitCount == 0){
                app.image(imageLoader.wall2, this.x1, this.y1);
                break;
            }
            else{
                app.image(imageLoader.smashedWall2, this.x1, this.y1);
                break;
            }
               
            case '3':
            if(this.hitCount == 0){
                app.image(imageLoader.wall3, this.x1, this.y1);
                break;
            }
            else{
                app.image(imageLoader.smashedWall3, this.x1, this.y1);
                break;
            }
            case '4':
            if(this.hitCount == 0){
                app.image(imageLoader.wall4, this.x1, this.y1);
                break;
            }
            else{
                app.image(imageLoader.smashedWall4, this.x1, this.y1);
                break;
            }
    }
    }
}