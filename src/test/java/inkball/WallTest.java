package inkball;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import inkball.objects.Wall;
import inkball.loaders.ImageLoader;
import processing.core.PApplet;
import processing.core.PImage;

class WallTest {

    private Wall wall; // Instance of the Wall being tested
    private PApplet app; // PApplet instance for rendering
    private ImageLoader imageLoader; // Loader for images used in the Wall
    private PImage expectedImage; // Track the expected image for assertions

    // Flags to track method calls for testing
    private boolean imageCalled = false; 
    private boolean noTintCalled = false; 

    /**
     * Sets up the test environment before each test.
     * Initializes the app, image loader, and wall instance.
     */
    @BeforeEach
    void setUp() {
        app = new PApplet(); // Initialize PApplet
        PApplet.runSketch(new String[] { "inkball.App" }, app); // Run the app sketch
        imageLoader = new ImageLoader(app); // Initialize ImageLoader

        // Load wall images (ensure these paths are correct)
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

        wall = new Wall(app, 0, 0, 10, 10, '1', imageLoader); // Create a new Wall instance
        wall.hitCount = 0; // Initialize hit count to 0
        expectedImage = null; // Reset expected image
    }

    /**
     * Simulates the image drawing method to track whether it was called and what image was expected.
     */
    public void image(PImage img, float x, float y) {
        imageCalled = true; // Mark that the image method was called
        expectedImage = img; // Track the expected image for assertions
    }

    /**
     * Tests the color display of the wall based on hit count.
     * Asserts that the correct image is displayed for each color and hit count.
     */
    @Test
    void testColoursWithHitDisplay() {
        imageLoader.loadImages(); // Load images for the test
        wall.hitCount = 1; // Set hit count to 1
        wall.setColour('1'); // Set color to '1' and display
        wall.display(); // Call display method
        assertEquals(imageLoader.wall1, wall.newImage); // Assert the displayed image

        // Repeat for colors '2', '3', and '4'
        wall.setColour('2');
        wall.display();
        assertEquals(imageLoader.wall2, wall.newImage);

        wall.setColour('3');
        wall.display();
        assertEquals(imageLoader.wall3, wall.newImage);

        wall.setColour('4');
        wall.display();
        assertEquals(imageLoader.wall4, wall.newImage);

        // Check for smashed wall images after 2 hits
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

    /**
     * Tests that hitting the wall increments the hit count correctly.
     * Asserts the wall's removal status based on the hit count.
     */
    @Test
    void testHitIncrementsHitCount() {
        wall.hit(); // First hit
        wall.hit(); // Second hit

        // Assert that the wall is not removed after two hits
        assertFalse(wall.isRemoved(), "Wall should not be removed after two hits.");

        wall.hit(); // Third hit

        // After 3 hits, the wall should be removed
        assertTrue(wall.isRemoved(), "Wall should be removed after three hits.");
    }

    /**
     * Tests if the wall can be damaged based on the color of the hit.
     * Asserts the expected behavior for matching and non-matching colors.
     */
    @Test
    void testCanBeDamagedByMatchingColour() {
        assertTrue(wall.canBeDamagedBy('1'), "Wall should be damaged by the same color.");
        assertFalse(wall.canBeDamagedBy('2'), "Wall should not be damaged by a different color.");

        // Test a universal wall that can be damaged by any color
        Wall universalWall = new Wall(app, 0, 0, 10, 10, '0', imageLoader);
        assertTrue(universalWall.canBeDamagedBy('1'), "Universal wall should be damaged by any color.");
    }

    /**
     * Tests the removal status of the wall after hits.
     * Asserts that the wall is initially not removed and then becomes removed after three hits.
     */
    @Test
    void testIsRemovedAfterHits() {
        assertFalse(wall.isRemoved(), "Initially, wall should not be removed."); // Before hits

        wall.hit(); // First hit
        assertFalse(wall.isRemoved(), "Wall should not be removed after one hit."); // After one hit

        wall.hit(); // Second hit
        assertFalse(wall.isRemoved(), "Wall should not be removed after two hits."); // After two hits

        wall.hit(); // Third hit
        assertTrue(wall.isRemoved(), "Wall should be removed after three hits."); // After three hits
    }


}
