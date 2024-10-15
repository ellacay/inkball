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
    void testHitCountThreeChangesIsRemoved() {
        // Simulate 3 hits to activate the condition hitCount >= 3
        wall.hit();
        wall.hit();
        wall.hit();
        
        // After three hits, isRemoved should be set to true
        wall.display(); // Call display to simulate the effect of hitting three times
        assertTrue(wall.isRemoved, "The wall should be marked as removed after three hits.");
    }

    @Test
    void testDisplayWithZeroHits() {
        // Call display without any hits
        wall.display();
        
        // Verify that the wall is not removed initially
        assertFalse(wall.isRemoved, "The wall should not be removed with zero hits.");
    }

    @Test
    void testDisplayWithOneHit() {
        // Apply one hit
        wall.hit();
        
        // Call display after one hit
        wall.display();
        
        // Verify that the wall is still not removed after one hit
        assertFalse(wall.isRemoved, "The wall should not be removed after one hit.");
    }

    @Test
    void testDisplayWithTwoHits() {
        // Apply two hits
        wall.hit();
        wall.hit();
        
        // Call display after two hits
        wall.display();
        
        // Verify that the wall is still not removed after two hits
        assertFalse(wall.isRemoved, "The wall should not be removed after two hits.");
    }

    @Test
    void testTintAndNoTintBehavior() {
        // First hit
        wall.hit();
        wall.display();
        
        // Verify that with one hit, the wall is not removed
        assertFalse(wall.isRemoved, "The wall should not be removed after one hit.");
        
        // Second hit (no specific behavior for tint specified in the code for 2 hits)
        wall.hit();
        wall.display();
        
        // Verify still not removed
        assertFalse(wall.isRemoved, "The wall should not be removed after two hits.");

        // Third hit
        wall.hit();
        wall.display();
        
        // After three hits, the wall should be removed
        assertTrue(wall.isRemoved, "The wall should be removed after three hits.");
    }
    
    @Test
    void testDisplayNoTintWithZeroHits() {
        // Verify no tint is applied and not removed initially
        wall.display();
        
        assertFalse(wall.isRemoved, "The wall should not be removed initially.");
        
        // Additional hits can be tested as needed
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

    @Test
    void testDisplayChangesIsRemovedStatus() {
        wall.hit();
        wall.display();
        
        // After three hits, verify the wall is marked as removed
        wall.hit();
        wall.display();
        wall.hit();
        wall.display();
        
        assertTrue(wall.isRemoved, "isRemoved should be true after three hits.");
    }
    @Test
void testHitCountTriggersRemovalAndTint() {
    wall.hit();
    wall.hit();
    wall.hit(); // Should trigger the removal condition
    
    // Call display to simulate the effect of hitting three times
    wall.display();
    
    // Assert the wall is marked as removed
    assertTrue(wall.isRemoved(), "The wall should be marked as removed after three hits.");
    // Note: You won't check for tinting since you don't want to add helper methods
}

@Test
void testDisplayChangesImagesBasedOnHitCount() {
    // Test for hit count less than 2
    wall.hit(); // 1 hit
    wall.display();
    assertFalse(wall.isRemoved(), "Wall should not be removed after one hit.");
    
    // Check if the correct image is displayed after one hit
    // You may need to modify Wall to keep track of the current image if you want to validate it.
    wall.hit(); // 2 hits
    wall.display();
    assertFalse(wall.isRemoved(), "Wall should not be removed after two hits.");
    
    wall.hit(); // 3 hits
    wall.display();
    assertTrue(wall.isRemoved(), "Wall should be removed after three hits.");
    
    // You may want to assert the image displayed after three hits if you have access to that logic
}

@Test
void testHitCountDoesNotTriggerRemovalBeforeThreeHits() {
    wall.hit(); // 1 hit
    wall.display();
    assertFalse(wall.isRemoved(), "Wall should not be removed after one hit.");
    
    wall.hit(); // 2 hits
    wall.display();
    assertFalse(wall.isRemoved(), "Wall should not be removed after two hits.");
    
    // The wall should still not be removed
    assertFalse(wall.isRemoved(), "Wall should still not be removed before three hits.");
}


    
}
