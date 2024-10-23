package inkball;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import inkball.objects.Wall;
import inkball.loaders.ImageLoader;
import processing.core.PApplet;
import processing.core.PImage;

class WallTest {

    private Wall wall;
    private int tintAlpha;
    private PApplet app;
    private ImageLoader imageLoader;
    private boolean imageCalled = false; // Track if image method is called
    private boolean noTintCalled = false; // Track if noTint method is called
    private PImage expectedImage; // Track the expected image

    @BeforeEach
    void setUp() {
        app = new PApplet();
        PApplet.runSketch(new String[] { "inkball.App" }, app);
        imageLoader = new ImageLoader(app);


        // Initialize images (ensure these paths are correct)
        imageLoader.wall0 = app.loadImage("path/to/wall0.png");
        imageLoader.wall1 = app.loadImage("path/to/wall1.png");
        imageLoader.wall2 = app.loadImage("path/to/wall2.png");
        imageLoader.wall3 = app.loadImage("path/to/wall3.png");
        imageLoader.wall4 = app.loadImage("path/to/wall4.png");
        imageLoader.smashedWall0 = app.loadImage("path/to/smashedWall0.png");
        imageLoader.smashedWall1 = app.loadImage("path/to/smashedWall1.png");
        imageLoader.smashedWall2 = app.loadImage("path/to/smashedWall2.png");
        imageLoader.smashedWall3 = app.loadImage("path/to/smashedWall3.png");
        imageLoader.smashedWall4 = app.loadImage("path/to/smashedWall4.png");

        wall = new Wall(app, 0, 0, 10, 10, '1', imageLoader);
        wall.hitCount = 0; // Set initial hitCount
        expectedImage = null; // Reset expected image
    }

    public void image(PImage img, float x, float y) {
        this.imageCalled = true; // Mark that image was called
        this.expectedImage = img; // Track the expected image
    }

   

  

    @Test
    void testColoursWithHitDisplay() {
        imageLoader.loadImages();
        wall.hitCount = 1;
        wall.setColour('1');
        wall.display();
        assertEquals(imageLoader.wall1, wall.newImage);

        wall.setColour('2');
        wall.display();
        assertEquals(imageLoader.wall2, wall.newImage);

        wall.setColour('3');
        wall.display();
        assertEquals(imageLoader.wall3, wall.newImage);

        wall.setColour('4');
        wall.display();
        assertEquals(imageLoader.wall4, wall.newImage);

        wall.hitCount = 2;
        wall.setColour('1');
        wall.display();
        assertEquals(imageLoader.smashedWall1, wall.newImage);

        wall.setColour('2');
        wall.display();
        assertEquals(imageLoader.smashedWall2, wall.newImage);

        wall.setColour('3');
        wall.display();
        assertEquals(imageLoader.smashedWall3, wall.newImage);

        wall.setColour('4');
        wall.display();
        assertEquals(imageLoader.smashedWall4, wall.newImage);
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

    // Overriding PApplet methods for testing

}
