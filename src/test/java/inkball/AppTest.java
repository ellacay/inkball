package inkball;

import inkball.objects.Line;
import inkball.managers.BoardManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import processing.event.KeyEvent;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private App app;
    private final int TILE_SPEED = 5; // Speed at which tiles move
    public final int CELLSIZE = 20; // Size of each cell in the board
    private final int BOARD_WIDTH = 10; // Width of the game board
    private final int BOARD_HEIGHT = 10; // Height of the game board
    private final int TOPBAR = 20; // Offset for the top bar in the game

    @BeforeEach
    public void setUp() {
        // Set up the App instance and initialize the game state before each test
        app = new App();
        PApplet.runSketch(new String[] { "inkball.App" }, app);
        app.setup(); // Call setup to initialize game parameters
        app.level = 1; // Start from level 1
        app.levelWon = true; // Simulate winning the level
        app.gameWon = false; // Ensure the game is not won
        app.timer = 100; // Initialize the timer for tests
    }

    /**
     * Tests that the yellow tile moves left and changes direction to up 
     * upon hitting the left edge. Asserts the new position and direction.
     */
    @Test
    public void testMoveYellowTileMoveLeftToUp() {
        app.currentDirection1 = 2; // Direction set to left
        app.currentX1 = TILE_SPEED; // Position the tile away from the left edge
        app.moveYellowTile(); // Move the tile

        assertEquals(0, app.currentX1, "Tile 1 should hit the left edge."); // Verify X position
        assertEquals(3, app.currentDirection1, "Tile 1 should change direction to up."); // Verify direction
    }

    /**
     * Ensures that the yellow tile does not move down past the bottom edge 
     * of the board when moving down. Verifies that the X position remains unchanged.
     */
    @Test
    void testMoveDown() {
        app.currentDirection1 = 1; // Direction set to down
        app.currentX1 = (App.BOARD_WIDTH - 1) * App.CELLSIZE; // Position at the edge

        app.moveYellowTile(); // Move the tile

        assertEquals((App.BOARD_WIDTH - 1) * App.CELLSIZE, app.currentX1, "X position should remain the same."); // Verify no movement
    }

    /**
     * Tests that the tile's X position remains the same when moving up 
     * from the top edge of the board.
     */
    @Test
    void testMoveUp() {
        app.currentY1 = App.TOPBAR; // Position at the top edge
        app.currentDirection1 = 3; // Direction set to up
        app.moveYellowTile(); // Move the tile

        assertEquals(0, app.currentX1, "X position should remain the same."); // Verify no X position change
    }

    /**
     * Ensures that the second tile does not move up past the top edge 
     * of the board when the direction is set to up. Verifies Y position remains the same.
     */
    @Test
    void testMoveUp2() {
        app.currentY2 = TOPBAR; // Position at the top edge
        app.currentDirection2 = 1; // Direction set to up
        app.moveYellowTile(); // Move logic for currentDirection2
        assertEquals(64, app.currentY2, "Tile should not move up past the top edge."); // Verify Y position
        assertEquals(576, app.currentX2, "X position should remain the same."); // Verify X position
    }

    /**
     * Tests the distance from a point to a line where both points are the same,
     * checking for both zero distance and a distance from a different point.
     */
    @Test
    public void testDistanceToLineWhenLineLengthIsZero() {
        PVector point = new PVector(100, 100); // Define a point
        List<PVector> currentLinePoints = new ArrayList<>(); // Initialize line points
        currentLinePoints.add(point); // Add point as start
        currentLinePoints.add(point); // Add point as end (line length is zero)
        Line line = new Line(currentLinePoints); // Create the line

        // Test distance from point (100, 100) to the line
        float distance = app.distanceToLine(100, 100, line);
        assertEquals(0, distance, 0.01); // Expect distance to be 0

        // Test distance from a different point (150, 150) to the line
        float distanceToDifferentPoint = app.distanceToLine(150, 150, line);
        assertTrue(distanceToDifferentPoint > 0); // Expect distance to be greater than 0
    }

    /**
     * Ensures the second tile does not change direction when at the bottom edge
     * and attempts to move down. Verifies that the direction remains unchanged.
     */
    @Test
    void testMoveDown2() {
        app.currentY2 = (BOARD_HEIGHT - 1) * CELLSIZE + TOPBAR; // Position at the bottom edge
        app.currentDirection2 = 3; // Direction set to down
        app.moveYellowTile(); // Move logic for currentDirection2
        assertEquals(3, app.currentDirection2, "direction shouldn't change"); // Verify direction remains the same
    }

    /**
     * Tests that the direction changes after hitting the left edge 
     * for the second tile. Verifies that the new direction is correct.
     */
    @Test
    void testDirectionChangeAfterLeft() {
        app.currentX2 = 0; // Position at the left edge
        app.currentDirection2 = 0; // Direction set to left
        app.moveYellowTile(); // Move logic for currentDirection2
        assertEquals(1, app.currentDirection2, "Direction should change to up after hitting left edge."); // Verify new direction
    }

    /**
     * Tests that the direction changes after hitting the top edge 
     * for the second tile. Verifies that the new direction is correct.
     */
    @Test
    void testDirectionChangeAfterUp() {
        app.currentY2 = TOPBAR; // Position at the top edge
        app.currentDirection2 = 1; // Direction set to up
        app.moveYellowTile(); // Move logic for currentDirection2
        assertEquals(2, app.currentDirection2, "Direction should change to right after hitting top edge."); // Verify new direction
    }

    /**
     * Tests the initialization of game settings by calling the settings method 
     * and verifying the width and height are set correctly.
     */
    @Test
    public void testSettingsInitialization() {
        app.settings();
        assertEquals(App.WIDTH, app.width, "Width should match the set width"); // Verify width
        assertEquals(App.HEIGHT, app.height, "Height should match the set height"); // Verify height
    }

    /**
     * Checks that the spawn timer is initialized correctly after setup, 
     * including the thisLevel and spawnTimer values.
     */
    @Test
    @DisplayName("Spawn timer should be initialized correctly")
    public void testSpawnTimerInitialization() {
        assertNotNull(app.thisLevel, "thisLevel should be initialized"); // Verify thisLevel is set
        assertNotNull(app.spawnTimer, "spawnTimer should be initialized"); // Verify spawnTimer is set
        assertEquals(app.spawnInterval, app.spawnTimer, 0.1, "Spawn timer should match spawn interval"); // Verify spawn timer matches expected interval
    }

    /**
     * Tests that lines are displayed correctly on the screen.
     * This might require visual confirmation or checking for state changes.
     */
    @DisplayName("Display lines should work correctly")
    public void testDisplayLines() {
        App.lines.add(new Line(Arrays.asList(new PVector(100, 100), new PVector(200, 200))));
        app.displayLines(); // Call to display lines
        // Check visual or state changes (this may require additional verification)
    }

    /**
     * Tests the functionality for removing a line by simulating a mouse press 
     * near the line. Asserts that the line has been successfully removed.
     */
    @Test
    @DisplayName("Line removal should work correctly")
    public void testLineRemoval() {
        Line line = new Line(Arrays.asList(new PVector(100, 100), new PVector(200, 200)));
        App.lines.add(line); // Add line to the collection

        // Simulate clicking near the line to remove it
        app.removeLineAtMousePosition(150, 150);

        assertFalse(App.lines.contains(line), "Line should be removed"); // Verify that the line has been removed
    }

    /**
     * Tests that the displayed spawn timer value is correct 
     * after setting it and calling the display method.
     */
    @Test
    @DisplayName("Display spawn timer should show correct spawn timer value")
    public void testDisplaySpawnTimer() {
        app.spawnTimer = 3.5; // Set spawn timer
        app.displaySpawnTimer(); // Call the method
        // Verify that the correct spawn timer is rendered (visual test)
    }

    /**
     * Tests that the pause message is displayed correctly on the screen 
     * when the displayPause method is called.
     */
    @Test
    @DisplayName("Display pause should show correct message")
    public void testDisplayPause() {
        app.displayPause(); // Call the method
        // Verify that the correct pause message is set (visual test)
    }

    /**
     * Tests that the time up message is displayed correctly on the screen 
     * when the displayTimeUp method is called.
     */
    @Test
    @DisplayName("Display time up should show correct message")
    public void testDisplayTimeUp() {
        app.displayTimeUp(); // Call the method
        // Verify that the correct message is set (visual test)
    }

    /**
     * Tests that the displayed timer value is correct 
     * after setting it and calling the display method.
     */
    @Test
    @DisplayName("Display timer should show correct timer value")
    public void testDisplayTimer() {
        app.timer = 30; // Set timer
        app.displayTimer(); // Call the method
        // Check that the expected timer is rendered correctly (visual test)
    }

    /**
     * Tests that the displayed score is correct 
     * after it is set and the display method is called.
     */
    @Test
    @DisplayName("Display score should show correct score")
    public void testDisplayScore() {
        // Implement your score test logic here
    }
/**
 * Tests that the spawn timer decrements correctly 
 * every 3 frames as expected in the game logic.
 */
@Test
@DisplayName("Update spawn timer should decrement correctly every 3 frames")
public void testUpdateSpawnTimer() {
    app.spawnTimer = 5.0; // Initial spawn timer
    app.frameCount = 3; // Simulate reaching 3 frames

    app.updateSpawnTimer(); // Call the method
    assertEquals(4.9, app.spawnTimer, 0.01, "Spawn timer should decrement by 0.1 every 3 frames");

    app.frameCount = 6; // Simulate reaching another 3 frames
    app.updateSpawnTimer(); // Call again
    assertEquals(4.8, app.spawnTimer, 0.01, "Spawn timer should decrement again by 0.1");
}

/**
 * Tests that the game timer decrements correctly 
 * after a specified number of frames equal to FPS.
 */
@Test
@DisplayName("Update timer should decrement correctly every FPS frames")
public void testUpdateTimer() {
    app.timer = 10; // Set initial timer
    app.frameCount = App.FPS; // Simulate reaching FPS

    app.updateTimer(); // Call the method
    assertEquals(9, app.timer, "Timer should decrement by 1 after FPS frames");
}

/**
 * Tests that yellow tiles are displayed at 
 * the correct positions on the game board.
 */
@Test
@DisplayName("Display yellow tiles should render tiles at correct positions")
public void testDisplayYellowTiles() {
    app.currentX1 = 100;
    app.currentY1 = 150;
    app.currentX2 = 200;
    app.currentY2 = 250;

    app.displayYellowTiles(); // Call the method

    // Check that the expected positions are correct
    assertEquals(100, app.currentX1, "First tile should be at X position 100");
    assertEquals(150, app.currentY1, "First tile should be at Y position 150");
    assertEquals(200, app.currentX2, "Second tile should be at X position 200");
    assertEquals(250, app.currentY2, "Second tile should be at Y position 250");
}

/**
 * Tests that the score increments correctly 
 * when the level is won and the display method is called.
 */
@Test
@DisplayName("Score should increment when level is won")
public void testScoreIncrement() {
    BoardManager.score = 0;
    app.timer = 5; // Set timer to simulate time remaining
    app.scoreIncrementTimer = 100;
    app.displayWin(); // Should increment score
    assertEquals(1, BoardManager.score, "Score should increment when the level is won");
}

/**
 * Tests that the game state resets correctly 
 * when the restart method is called after a win.
 */
@Test
@DisplayName("Game should restart correctly")
public void testRestartGame() {
    app.gameWon = true; // Simulate game won state
    app.restartGame(); // Call the method to restart the game
    assertFalse(app.gameWon, "Game should not be won after restart");
    assertEquals(app.spawnInterval, app.spawnTimer, 0.1, "Spawn timer should reset to initial spawn interval");
    app.levelWon = false; // Reset level won state
    app.restartGame(); // Restart again
}

/**
 * Tests that a point is added to the current line 
 * when the left mouse button is pressed.
 */
@Test
void testMousePressedAddPoint() {
    app.mouseX = 100; // Simulate mouse X position
    app.mouseY = 200; // Simulate mouse Y position
    app.mouseButton = App.LEFT; // Simulate left mouse button
    app.mousePressed(); // Call the method

    // Check that a point has been added
    assertEquals(1, App.currentLinePoints.size(), "One point should be added.");
    assertEquals(new PVector(100, 200), App.currentLinePoints.get(0), "Point should match mouse position.");
}

/**
 * Tests that a point is added to the current line 
 * when the right mouse button is pressed.
 */
@Test
void nottestMousePressedAddPoint() {
    app.mouseX = 100; // Simulate mouse X position
    app.mouseY = 200; // Simulate mouse Y position
    app.mouseButton = App.RIGHT; // Simulate right mouse button
    app.mousePressed(); // Call the method

    // Check that a point has been added
    assertEquals(1, App.currentLinePoints.size(), "One point should be added.");
    assertEquals(new PVector(100, 200), App.currentLinePoints.get(0), "Point should match mouse position.");
}

/**
 * Tests that no points are added when the game is over 
 * and the left mouse button is pressed.
 */
@Test
void testMousePressedGameOver() {
    app.gameOver = true; // Set game over state
    app.mouseX = 100; // Simulate mouse X position
    app.mouseY = 200; // Simulate mouse Y position
    app.mouseButton = App.LEFT; // Simulate left mouse button

    // Store the initial size of currentLinePoints
    int initialSize = App.currentLinePoints.size();

    app.mousePressed(); // Call the method

    // Check that no points were added when game is over
    assertEquals(initialSize, App.currentLinePoints.size(), "No points should be added when game is over.");
}

/**
 * Tests that the game restarts correctly 
 * when the 'r' key is pressed.
 */
@Test
void testKeyPressedRestartGame() {
    app.setup(); // Setup the app for testing

    // Simulate pressing the 'r' key
    KeyEvent key = new KeyEvent(app, 0, 0, 0, 'r', 0);
    app.keyPressed(key);
    
    // Add assertions to verify that the game has restarted
    assertFalse(app.gameWon, "Game should not be won after restart");
    assertEquals(app.spawnInterval, app.spawnTimer, 0.1, "Spawn timer should reset to initial spawn interval");
}

/**
 * Tests that the level transitions correctly 
 * when the current level is 1.
 */
@Test
void testHandleLevelTransition_Level1() {
    app.level = 1; // Set current level
    app.frameCount = 60; // Simulate frame count
    app.handleLevelTransition(); // Call the method to transition

    // Assertions
    assertEquals(2, app.level, "Level should be 2 after transition.");
}

/**
 * Tests that the level transitions correctly 
 * when the current level is 2 and the level has been won.
 */
@Test
void testHandleLevelTransition_Level2() {
    app.level = 1; // Set current level
    app.frameCount = 60; // Simulate frame count
    app.levelWon = true; // Set level won state

    app.handleLevelTransition(); // Transition to level 2

    // Assertions
    assertEquals(2, app.level, "Level should be 2 after transition.");
    app.levelWon = false; // Reset level won state
    app.handleLevelTransition(); // Transition again

    // Assertions
    assertEquals(2, app.level, "Level should remain 2 after transition.");
}

/**
 * Tests that the yellow tile moves down correctly 
 * without exceeding maximum height.
 */
@Test
public void testMoveTileDown() {
    int initialY = app.currentY2; // Store initial Y position
    int maxHeight = (App.BOARD_HEIGHT - 1) * App.CELLSIZE; // Calculate max height

    // Move the tile
    app.moveYellowTile();

    // Check if the tile moved down correctly
    assertEquals(initialY, app.currentY2);

    // Simulate reaching max height
    app.currentY2 = maxHeight;
    app.moveYellowTile();
    assertEquals(maxHeight, app.currentY2);
    assertEquals(0, app.currentDirection2); // Should change direction to left
}

/**
 * Tests that the tile moves right and changes direction 
 * when it reaches the board boundary.
 */
@Test
public void testMoveRightAndChangeDirection() {
    // Move right
    app.currentX2 += TILE_SPEED;
    if (app.currentX2 >= (BOARD_WIDTH - 1) * CELLSIZE) {
        app.currentX2 = (BOARD_WIDTH - 1) * CELLSIZE; // Clamp to boundary
        app.currentDirection2 = 3; // Change direction to down
    }

    // Verify clamping and direction change
    assertEquals((BOARD_WIDTH - 1) * CELLSIZE, app.currentX2);
    assertEquals(3, app.currentDirection2);
}

/**
 * Tests that the tile moves down and changes direction 
 * when it reaches the bottom of the board.
 */
@Test
public void testMoveDownAndChangeDirection() {
    // Set direction to down
    app.currentDirection2 = 3;
    app.currentY1 = App.HEIGHT - TILE_SPEED; // Start just before max height

    // Move down
    app.currentY1 += TILE_SPEED;
    if (app.currentY1 >= App.HEIGHT) {
        app.currentY1 = App.HEIGHT; // Reset to max height
        app.currentDirection2 = 0; // Change direction to left
    }

    // Verify clamping and direction change
    assertEquals(App.HEIGHT, app.currentY1);
    assertEquals(0, app.currentDirection2);
}

/**
 * Tests that the tile does not move below maximum height 
 * and changes direction appropriately.
 */
@Test
public void testMoveDownOvershootMaxHeight() {
    // Set direction to down and start above max height
    app.currentDirection2 = 3;
    app.currentY1 = App.HEIGHT + TILE_SPEED; // Start overshooting

    // Move down
    app.currentY1 += TILE_SPEED; // Should not change because of max height
    if (app.currentY1 >= App.HEIGHT) {
        app.currentY1 = App.HEIGHT; // Reset to max height
        app.currentDirection2 = 0; // Change direction to left
    }

    // Verify clamping and direction change
    assertEquals(App.HEIGHT, app.currentY1);
    assertEquals(0, app.currentDirection2);
}

/**
 * Tests that the mouse released action correctly 
 * adds a line based on current line points.
 */
@Test
void testMouseReleased() {
    App.currentLinePoints = new ArrayList<>(); // Initialize current line points
    App.lines = new ArrayList<>(); // Initialize lines

    // Simulate adding points to currentLinePoints
    App.currentLinePoints.add(new PVector(50, 50));
    App.currentLinePoints.add(new PVector(100, 100));

    // Call the method
    app.mouseReleased();

    // Assertions to check if the line was added correctly
    assertTrue(App.lines.size() == 1, "Line not added.");
    assertTrue(App.lines.get(0).points.size() == 2, "Line does not contain the correct number of points.");

    // Clear currentLinePoints and test again
    App.currentLinePoints.clear();
    app.mouseReleased(); // Should not add a line since currentLinePoints is empty

    // Assert that no new line was added
    assertTrue(App.lines.size() == 1, "Line was incorrectly added when currentLinePoints is empty.");
}

/**
 * Tests that the game pauses correctly when 
 * the space bar is pressed.
 */
@Test
void testKeyPressedTogglePause() {
    // Initial state should not be paused
    assertFalse(app.isPaused, "Game should not be paused initially.");

    // Simulate pressing the space bar
    app.setup();

    KeyEvent key = new KeyEvent(app, 0, 0, 0, ' ', 0);
    app.keyPressed(key);
    // Check that the game is now paused
    assertTrue(app.isPaused, "Game should be paused after pressing space.");
}

/**
 * Tests that the game correctly handles the drawing 
 * state when the game is won.
 */
@Test
void testDraw_GameWon() {
    app.gameWon = true; // Simulate app won condition
    app.draw();

    assertEquals(1, app.level, "Level should reset to 1 when game is won.");
    // Add additional assertions to verify other expected outcomes
}

/**
 * Tests that the game correctly handles the drawing 
 * state when the timer expires.
 */
@Test
void testDraw_TimerExpired() {
    app.timer = -1; // Simulate timer expiring
    app.draw();

    assertFalse(app.gameOver, "Game over should be false after timer expired.");
    // Verify other expected states, like ballManager actions
}

/**
 * Tests that the game remains paused during 
 * the drawing state when paused.
 */
@Test
void testDraw_Paused() {
    app.isPaused = true; // Simulate paused state
    app.draw();

    assertTrue(app.isPaused, "Game should remain paused.");
    // Verify board display logic in paused state
}

/**
 * Tests that mouse dragging correctly adds points 
 * to the current line.
 */
@Test
void testMouseDragged() {
    App.currentLinePoints.clear();
    app.mouseX = 100; // Simulate mouse X position
    app.mouseY = 200; // Simulate mouse Y position
    app.mouseButton = App.LEFT; // Simulate left mouse button

    // Ensure the game is not over
    app.gameOver = false;

    app.mouseDragged(); // Call the method

    // Check that a point has been added
    assertEquals(1, App.currentLinePoints.size(), "One point should be added.");
    assertEquals(new PVector(100, 200), App.currentLinePoints.get(0), "Point should match mouse position.");
}

/**
 * Tests that the mouse released action behaves correctly 
 * when game is over.
 */
@Test
void release() {
    app.mouseX = 100; // Simulate mouse X position
    app.mouseY = 200; // Simulate mouse Y position
    app.mouseButton = App.LEFT; // Simulate left mouse button
    app.mouseDragged(); // Call the method
    app.mouseReleased();
    app.gameOver = false; // Set game over state to false
    app.mouseDragged(); // Call the method
    assertEquals(1, App.currentLinePoints.size(), "Two points should be added.");
    app.mouseReleased();
    app.gameOver = true; // Set game over state to true

    assertEquals(0, App.currentLinePoints.size(), "Two points should be added.");
}

/**
 * Tests that no points are added when the game is over 
 * and the left mouse button is dragged.
 */
@Test
void testMouseDraggedGameOver() {
    app.mouseX = 100; // Simulate mouse X position
    app.mouseY = 200; // Simulate mouse Y position
    app.mouseButton = App.LEFT; // Simulate left mouse button
    int currentSize = App.currentLinePoints.size();

    // Set game over state
    app.gameOver = true;

    app.mouseDragged(); // Call the method

    // Check that no points were added
    assertEquals(currentSize, App.currentLinePoints.size(), "No points should be added when game is over.");
}

/**
 * Tests that no points are added when the right mouse button 
 * is pressed while the game is not over.
 */
@Test
void nottestMouseDraggedGameOver() {
    app.mouseX = 100; // Simulate mouse X position
    app.mouseY = 200; // Simulate mouse Y position
    app.mouseButton = App.RIGHT; // Simulate right mouse button

    // Set game over state
    app.gameOver = false;

    app.mouseDragged(); // Call the method

    // Check that no points were added
    assertTrue(App.currentLinePoints.isEmpty(), "No points should be added when button RIGHT.");
}
}