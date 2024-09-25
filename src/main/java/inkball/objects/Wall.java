package inkball.objects;

import inkball.loaders.ImageLoader;
import processing.core.PApplet;

public class Wall {
    private PApplet app;
    public float x1, y1, x2, y2; // Coordinates of the wall
    private int hitCount = 0; // Tracks hits
    public char color; // Wall color

    public Wall(PApplet app, float x1, float y1, float x2, float y2, char color) {
        this.app = app;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.imageLoader = ImageLoader;
        
        
    }

    public void hit(char ballColor) {
        if (canBeDamagedBy(ballColor)) {
            hitCount++;
            if (hitCount >= 3) {
                // Mark the wall for removal or handle accordingly
            }
        }
    }

    public boolean canBeDamagedBy(char ballColor) {
        return this.color == ballColor || this.color == '0'; // Grey can be hit by any color
    }

    public boolean isRemoved() {
        return hitCount >= 3; // Wall is removed after 3 hits
    }

    public int display() {
        if (!isRemoved()) {
            
            if(hitCount==3){
            
                app.image(imageLoader.wall1, this.x1, this.y1);

            }
            else if(hitCount>0){
                return 2;
            }
        
      
            app.image(ImageLoader.tile, this.x1, this.y1);
    
    }
    return 0;
}
}