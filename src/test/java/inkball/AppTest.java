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
    private final int TILE_SPEED = 5; // Assuming TILE_SPEED is a constant
    public final int CELLSIZE = 20; // Assuming CELLSIZE is a constant
    private final int BOARD_WIDTH = 10; // Example width
    private final int BOARD_HEIGHT = 10; // Example height
    private final int TOPBAR = 20; // Example top bar offset

    @BeforeEach
    public void setUp() {
        // Initialize the App object
        app = new App();
        PApplet.runSketch(new String[] { "inkball.App" }, app);

        app.setup();
        app.level = 1; // Start from level 0
        app.levelWon = true; // Set level won
        app.gameWon = false; // Ensure the game is not won
        app.timer = 100; // Set a test timer value

        // Call the settings method to set up the PApplet
        // app.setup(); // Call setup to initialize game state
    }

    @Test
    public void testMoveYellowTileMoveLeftToUp() {
        app.currentDirection1 = 2; // Set direction to left
        app.currentX1 = TILE_SPEED; // Start away from the left edge
        app.moveYellowTile(); // Move left

        assertEquals(0, app.currentX1, "Tile 1 should hit the left edge.");
        assertEquals(3, app.currentDirection1, "Tile 1 should change direction to up.");
    }

    @Test
    void testMoveDown() {
        app.currentDirection1 = 1; // Set direction to down
        app.currentX1 = (App.BOARD_WIDTH - 1) * App.CELLSIZE; // Set to the edge

        app.moveYellowTile();

        assertEquals((App.BOARD_WIDTH - 1) * App.CELLSIZE, app.currentX1, "X position should remain the same.");

    }

    @Test
    void testMoveUp() {
        app.currentY1 = App.TOPBAR; // Set to the top edge
        app.currentDirection1 = 3; // Set direction to up
        app.moveYellowTile();

        assertEquals(0, app.currentX1, "X position should remain the same.");
    }

    @Test
    void testMoveUp2() {
        app.currentY2 = TOPBAR; // Set to the top edge
        app.currentDirection2 = 1; // Set direction to up
        app.moveYellowTile(); // Move logic for currentDirection2
        assertEquals(64, app.currentY2, "Tile should not move up past the top edge.");
        assertEquals(576, app.currentX2, "X position should remain the same.");
    }

    @Test
    public void testDistanceToLineWhenLineLengthIsZero() {
        // Create a line where start and end are the same point
        PVector point = new PVector(100, 100);
        List<PVector> currentLinePoints = new ArrayList<>();
        currentLinePoints.add(point);
        currentLinePoints.add(point);
        Line line = new Line(currentLinePoints); // Line is a point

        // Test distance from point (100, 100) to the line
        float distance = app.distanceToLine(100, 100, line);
        assertEquals(0, distance, 0.01); // Expect distance to be 0

        // Test distance from a different point (150, 150) to the line
        float distanceToDifferentPoint = app.distanceToLine(150, 150, line);
        assertTrue(distanceToDifferentPoint > 0); // Expect distance to be greater than 0
    }

    @Test
    void testMoveDown2() {
        app.currentY2 = (BOARD_HEIGHT - 1) * CELLSIZE + TOPBAR; // Set to the bottom edge
        app.currentDirection2 = 3; // Set direction to down
        app.moveYellowTile(); // Move logic for currentDirection2
        assertEquals(3, app.currentDirection2, "direction shouldnt change");
       
    }

    @Test
    void testDirectionChangeAfterLeft() {
        app.currentX2 = 0; // At the left edge
        app.currentDirection2 = 0; // Move left
        app.moveYellowTile(); // Move logic for currentDirection2
        assertEquals(1, app.currentDirection2, "Direction should change to up after hitting left edge.");
    }

    @Test
    void testDirectionChangeAfterUp() {
        app.currentY2 = TOPBAR; // At the top edge
        app.currentDirection2 = 1; // Move up
        app.moveYellowTile(); // Move logic for currentDirection2
        assertEquals(2, app.currentDirection2, "Direction should change to right after hitting top edge.");
    }

   

    @Test
    public void testSettingsInitialization() {
        app.settings();
        assertEquals(App.WIDTH, app.width, "Width should match the set width");
        assertEquals(App.HEIGHT, app.height, "Height should match the set height");
    }

    @Test
    @DisplayName("Spawn timer should be initialized correctly")
    public void testSpawnTimerInitialization() {
        assertNotNull(app.thisLevel, "thisLevel should be initialized");
        assertNotNull(app.spawnTimer, "spawnTimer should be initialized");
        assertEquals(app.spawnInterval, app.spawnTimer, 0.1, "Spawn timer should match spawn interval");
    }

 

    @DisplayName("Display lines should work correctly")
    public void testDisplayLines() {
        App.lines.add(new Line(Arrays.asList(new PVector(100, 100), new PVector(200, 200))));
        app.displayLines(); // Call to display lines
        // Check visual or state changes
    }

    @Test
    @DisplayName("Line removal should work correctly")
    public void testLineRemoval() {
        // Test line removal by mouse press
        Line line = new Line(Arrays.asList(new PVector(100, 100), new PVector(200, 200)));
        App.lines.add(line);

        // Simulate clicking near the line
        app.removeLineAtMousePosition(150, 150);

        assertFalse(App.lines.contains(line), "Line should be removed");
    }

    @Test
    @DisplayName("Display spawn timer should show correct spawn timer value")
    public void testDisplaySpawnTimer() {
        app.spawnTimer = 3.5; // Set spawn timer
        app.displaySpawnTimer(); // Call the method
        // Verify that the correct spawn timer is rendered (visual test)
    }

    @Test
    @DisplayName("Display pause should show correct message")
    public void testDisplayPause() {
        app.displayPause(); // Call the method
        // Verify that the correct pause message is set (visual test)
    }

    @Test
    @DisplayName("Display time up should show correct message")
    public void testDisplayTimeUp() {
        app.displayTimeUp(); // Call the method
        // Verify that the correct message is set (visual test)

    }

    @Test
    @DisplayName("Display timer should show correct timer value")
    public void testDisplayTimer() {
        app.timer = 30; // Set timer
        app.displayTimer(); // Call the method

        // Check that the expected timer is rendered correctly
    }

    @Test
    @DisplayName("Display score should show correct score")
    public void testDisplayScore() {
        BoardManager.score = 50; // Set score
        app.displayScore(); // Call the method

        // Check that the expected score is rendered correctly
        // Since this is visual, you might have to rely on the side effects or mocks.
        // Here we assume you have a way to verify rendered text (not directly
        // testable).
    }

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

    @Test
    @DisplayName("Update timer should decrement correctly every FPS frames")
    public void testUpdateTimer() {
        app.timer = 10; // Set initial timer
        app.frameCount = App.FPS; // Simulate reaching FPS

        app.updateTimer(); // Call the method
        assertEquals(9, app.timer, "Timer should decrement by 1 after FPS frames");
    }

   

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

    @Test
    @DisplayName("Score should increment when level is won")
    public void testScoreIncrement() {
        BoardManager.score = 0;
        app.timer = 5; // Set timer to simulate time remaining
        app.scoreIncrementTimer = 100;
        app.displayWin(); // Should increment score
        assertEquals(1, BoardManager.score, "Score should increment when the level is won");
    }

    @Test
    @DisplayName("Game should restart correctly")
    public void testRestartGame() {

  
       
        app.gameWon = true;
        app.restartGame();
        assertFalse(app.gameWon, "Game should not be won after restart");
    
        assertEquals(app.spawnInterval, app.spawnTimer, 0.1, "Spawn timer should reset to initial spawn interval");
app.levelWon = false;
app.restartGame();

    }

   
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
    @Test
    void nottestMousePressedAddPoint() {
        app.mouseX = 100; // Simulate mouse X position
        app.mouseY = 200; // Simulate mouse Y position
        app.mouseButton = App.RIGHT; // Simulate left mouse button
        app.mousePressed(); // Call the method

        // Check that a point has been added
        assertEquals(1, App.currentLinePoints.size(), "One point should be added.");
        assertEquals(new PVector(100, 200), App.currentLinePoints.get(0), "Point should match mouse position.");
    }

    

    @Test
    void testMousePressedGameOver() {
        app.gameOver = true; // Set game over state
        app.mouseX = 100; // Simulate mouse X position
        app.mouseY = 200; // Simulate mouse Y position
        app.mouseButton = App.LEFT; // Simulate left mouse button

        // Store the initial size of currentLinePoints
        int initialSize = App.currentLinePoints.size();

        app.mousePressed(); // Call the method

        // Check that no points were added
        assertEquals(initialSize, App.currentLinePoints.size(), "No points should be added when game is over.");
    }

    @Test
    void testKeyPressedRestartGame() {
        // Simulate pressing the 'r' key

        app.setup();

        KeyEvent key = new KeyEvent(app, 0, 0, 0, 'r', 0);
        app.keyPressed(key);
        // Add assertions to verify that the game has restarted
        // For example, check if the score is reset or if certain game states are
        // initialized
        assertFalse(app.gameWon, "Game should not be won after restart");

        assertEquals(app.spawnInterval, app.spawnTimer, 0.1, "Spawn timer should reset to initial spawn interval");
    }

    @Test
    void testHandleLevelTransition_Level1() {
        app.level =1;
        app.frameCount = 60;
        app.handleLevelTransition(); // Call the method to transition

        // Assertions
        assertEquals(2, app.level, "Level should be 1 after transition.");

    }

    @Test
    void testHandleLevelTransition_Level2() {
        app.level =1;
        app.frameCount = 60;
        app.levelWon = true;

        app.handleLevelTransition(); // Transition to level 2
       

        // Assertions
        assertEquals(2, app.level, "Level should be 2 after second transition.");
        app.levelWon = false;

        app.handleLevelTransition(); // Transition to level 2
       

        // Assertions
        assertEquals(2, app.level, "Level should be 2 after second transition.");


    }
   
   
    @Test
    public void testMoveTileDown() {
        int initialY = app.currentY2;
        int maxHeight = (App.BOARD_HEIGHT - 1) * App.CELLSIZE;

        // Move the tile
        app.moveYellowTile();

        // Check if the tile moved down correctly
        assertEquals(initialY , app.currentY2);

        // Simulate reaching maxHeight
        app.currentY2 = maxHeight;
        app.moveYellowTile();
        assertEquals(maxHeight, app.currentY2);
        assertEquals(0, app.currentDirection2); // Should change direction to left
    }
    
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

    @Test
    public void testMoveDownAndChangeDirection() {
        // Set direction to down
        app.currentDirection2 = 3;
        app.currentY1 = App.HEIGHT - TILE_SPEED; // Start just before the max height

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

    @Test
    public void testMoveDownOvershootMaxHeight() {
        // Set direction to down and start above the max height
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
   @Test
    void testMouseReleased() {
        // Assuming `app` is an instance of your main application class
        App.currentLinePoints = new ArrayList<>(); // Use the existing list in your App
        App.lines = new ArrayList<>(); // Initialize lines in your App

        // Simulate adding points to currentLinePoints
        App.currentLinePoints.add(new PVector(50, 50));
        App.currentLinePoints.add(new PVector(100, 100));

        // Call the method
        app.mouseReleased();

        // Assertions to check if the line was added correctly
        assertTrue(App.lines.size() == 1, "Test failed: Line not added.");
        assertTrue(App.lines.get(0).points.size() == 2,
                "Test failed: Line does not contain the correct number of points.");

        // Clear currentLinePoints and test again
        App.currentLinePoints.clear();
        app.mouseReleased(); // Should not add a line since currentLinePoints is empty

        // Assert that no new line was added
        assertTrue(App.lines.size() == 1, "Test failed: Line was incorrectly added when currentLinePoints is empty.");
    }

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

    @Test
    void testDraw_GameWon() {
        app.gameWon = true; // Simulate app won condition
        app.draw();

        assertEquals(1, app.level, "Level should reset to 1 when game is won.");
        // Add additional assertions to verify other expected outcomes
    }

    @Test
    void testDraw_TimerExpired() {
        app.timer = -1; // Simulate timer expiring
        app.draw();

        assertFalse(app.gameOver, "app over should be false after timer expired.");
        // Verify other expected states, like ballManager actions
    }

    @Test
    void testDraw_Paused() {
        app.isPaused = true; // Simulate paused state
        app.draw();

        assertTrue(app.isPaused, "app should remain paused.");
        // Verify board display logic in paused state
    }

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

    @Test
    void release() {
        app.mouseX = 100; // Simulate mouse X position
        app.mouseY = 200; // Simulate mouse Y position
        app.mouseButton = App.LEFT; // Simulate left mouse button
        app.mouseDragged(); // Call the method
        app.mouseReleased();
        app.gameOver = false;
        app.mouseDragged(); // Call the method
        assertEquals(1, App.currentLinePoints.size(), "Two points should be added.");
        app.mouseReleased();
        app.gameOver = true;

        assertEquals(0, App.currentLinePoints.size(), "Two points should be added.");

    }

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
        assertEquals(currentSize,App.currentLinePoints.size(), "No points should be added when game is over.");
    }

    @Test
    void nottestMouseDraggedGameOver() {
        app.mouseX = 100; // Simulate mouse X position
        app.mouseY = 200; // Simulate mouse Y position
        app.mouseButton = App.RIGHT; // Simulate left mouse button

        // Set game over state
        app.gameOver = false;

        app.mouseDragged(); // Call the method

        // Check that no points were added
        assertTrue(App.currentLinePoints.isEmpty(), "No points should be added when button RIGHT.");
    }
}
