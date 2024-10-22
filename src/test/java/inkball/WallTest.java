package inkball;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import inkball.objects.Wall;
import inkball.loaders.ImageLoader;
import processing.core.PApplet;

class WallTest {

    private Wall wall;
    private PApplet app;
    private ImageLoader imageLoader;

    @BeforeEach
    void setUp() {
        app = new PApplet();
        PApplet.runSketch(new String[] {"inkball.App"}, app);
        imageLoader = new ImageLoader(app);  // Assuming ImageLoader has a constructor that accepts PApplet
        wall = new Wall(app, 0, 0, 10, 10, '1', imageLoader);
    }
 


    
   
    @Test
    void testHitIncrementsHitCount() {
        wall.hit();
        wall.hit();
        
        // Before 3 hits, the wall should not be removed
        assertFalse(wall.isRemoved(), "Wall should not be removed after two hits.");

        wall.hit();
        
        // After 3 hits, the wall should be removed
        assertTrue(wall.isRemoved(), "Wall should be removed after three hits.");
    }

    @Test
    void testCanBeDamagedByMatchingColour() {
        assertTrue(wall.canBeDamagedBy('1'), "Wall should be damaged by the same color.");
        assertFalse(wall.canBeDamagedBy('2'), "Wall should not be damaged by a different color.");

        Wall universalWall = new Wall(app, 0, 0, 10, 10, '0', imageLoader);
        assertTrue(universalWall.canBeDamagedBy('1'), "Universal wall should be damaged by any color.");
    }

    @Test
    void testIsRemovedAfterHits() {
        assertFalse(wall.isRemoved(), "Initially, wall should not be removed.");
        
        wall.hit();
        assertFalse(wall.isRemoved(), "Wall should not be removed after one hit.");
        
        wall.hit();
        assertFalse(wall.isRemoved(), "Wall should not be removed after two hits.");
        
        wall.hit();
        assertTrue(wall.isRemoved(), "Wall should be removed after three hits.");
    }

    
 




    
}
