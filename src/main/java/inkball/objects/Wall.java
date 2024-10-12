package inkball.objects;

import inkball.loaders.ImageLoader;
import processing.core.PApplet;

public class Wall {
    private PApplet app;
    public float x1, y1, x2, y2; // Coordinates of the wall
    private int hitCount = 0; // Tracks hits
    public char color; // Wall color
    private ImageLoader imageLoader;

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


    }}