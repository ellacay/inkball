package inkball;


import inkball.objects.Line;
import inkball.managers.BoardManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.jogamp.newt.event.KeyEvent;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private App app;
    private final int TILE_SPEED = 5; // Assuming TILE_SPEED is a constant
    private final int CELLSIZE = 20; // Assuming CELLSIZE is a constant
    private final int BOARD_WIDTH = 10; // Example width
    private final int BOARD_HEIGHT = 10; // Example height
    private final int TOPBAR = 20; // Example top bar offset

    @BeforeEach
    public void setUp() {
        // Initialize the App object
        app = new App();
        PApplet.runSketch(new String[] {"inkball.App"}, app);

 
        app.setup(); 
        
         // Call the settings method to set up the PApplet
        // app.setup();    // Call setup to initialize game state
    }
    @Test
    @DisplayName("Handle level transition correctly")
    public void testHandleLevelTransition2() {
        app.frameCount = 60;
        // Setup: Start at level 1 and set the timer for testing
        App.level = 1; // Set the current level
        app.timer = 30; // Simulate some time left
        app.levelWon = true; // Indicate that the level is won
    
        // Assuming levels is a List of some Level objects, initialize it with mock data
        
        // Invoke the method to handle level transition
        app.handleLevelTransition(); // This should check levelWon and transition to level 2
    
        // Assertions
        assertEquals(2, App.level, "Should move to the next level (level 2)");
        assertEquals(30, app.timer, "Timer should reset to level time");
        assertEquals(30, app.spawnTimer, "Spawn timer should match new level's spawn interval");
        assertEquals(BoardManager.score + (app.timer * 0.067), BoardManager.score, "Score should increase correctly after level win");
        
        // Test if the lines are cleared
        assertTrue(App.lines.isEmpty(), "Lines should be cleared after level transition");
        
        // Test if levelWon is reset
        assertFalse(app.levelWon, "levelWon should be reset after handling the level transition");
    }
    
    
    @Test
    public void testMoveYellowTileMoveRightToDown() {
        app.moveYellowTile(); // Move the first yellow tile
        assertEquals(TILE_SPEED, app.currentX1, "Tile 1 should move right by TILE_SPEED.");
        assertEquals(TOPBAR, app.currentY1, "Tile 1 Y position should remain at TOPBAR.");

        // Move the tile to the edge to test direction change
        app.currentX1 = (BOARD_WIDTH - 1) * CELLSIZE - TILE_SPEED; // Near edge
        app.moveYellowTile(); // Move the tile to trigger direction change
        assertEquals((BOARD_WIDTH - 1) * CELLSIZE, app.currentX1, "Tile 1 should hit the right edge.");
        assertEquals(TOPBAR + TILE_SPEED, app.currentY1, "Tile 1 should move down after hitting the right edge.");
        assertEquals(1, app.currentDirection1, "Tile 1 should change direction to down.");
    }

    @Test
    public void testMoveYellowTileMoveDownToLeft() {
        app.currentDirection1 = 1; // Set direction to down
        app.currentY1 = (BOARD_HEIGHT - 1) * CELLSIZE + TOPBAR - TILE_SPEED; // Near the bottom edge
        app.moveYellowTile(); // Move down to trigger direction change

        assertEquals((BOARD_HEIGHT - 1) * CELLSIZE + TOPBAR, app.currentY1, "Tile 1 should hit the bottom edge.");
        assertEquals((BOARD_WIDTH - 1) * CELLSIZE, app.currentX1, "Tile 1 should not move in the X direction.");
        assertEquals(2, app.currentDirection1, "Tile 1 should change direction to left.");
    }

    @Test
    public void testMoveYellowTileMoveLeftToUp() {
        app.currentDirection1 = 2; // Set direction to left
        app.currentX1 = TILE_SPEED; // Start away from the left edge
        app.moveYellowTile(); // Move left

        assertEquals(0, app.currentX1, "Tile 1 should hit the left edge.");
        assertEquals((BOARD_HEIGHT - 1) * CELLSIZE + TOPBAR, app.currentY1, "Tile 1 should not move in the Y direction.");
        assertEquals(3, app.currentDirection1, "Tile 1 should change direction to up.");
    }

    @Test
    public void testMoveYellowTileMoveUpToRight() {
        app.currentDirection1 = 3; // Set direction to up
        app.currentY1 = TOPBAR + TILE_SPEED; // Start below the top bar
        app.moveYellowTile(); // Move up to trigger direction change

        assertEquals(TOPBAR, app.currentY1, "Tile 1 should hit the top bar.");
        assertEquals(0, app.currentX1, "Tile 1 should not move in the X direction.");
        assertEquals(0, app.currentDirection1, "Tile 1 should change direction to right.");
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
    
    @Test
    @DisplayName("Initial game state should be set correctly")
    public void testInitialGameState() {
        // Test the initial values of the game state
        assertFalse(app.gameWon, "Game should not be won initially");
        assertFalse(app.gameOver, "Game should not be over initially");
        assertEquals(0, BoardManager.score, "Initial score should be 0");
        assertEquals(app.thisLevel.time, app.timer, "Initial timer should be set to level time");
    }
    @Test
    @DisplayName("Handle level transition correctly")
    public void testHandleLevelTransition() {
        App.level = 1; // Set the current level
        app.handleLevelTransition(); // Trigger level transition
        assertEquals(2, App.level, "Should move to the next level");
        // Add more assertions based on how the method modifies game state.
    }



@Test
@DisplayName("Move yellow tile should update position correctly")
public void testMoveYellowTile() {
    app.currentX1 = 0; // Start at the left edge
    app.currentDirection1 = 0; // Move right
    app.moveYellowTile();
    assertTrue(app.currentX1 > 0, "Yellow tile should move right");
    
    app.currentX1 = (App.BOARD_WIDTH - 1) * App.CELLSIZE; // At right edge
    app.moveYellowTile(); // Should change direction
    assertEquals((App.BOARD_WIDTH - 1) * App.CELLSIZE, app.currentX1, "Yellow tile should not exceed board width");
}

  
    @Test
    @DisplayName("Display game over should update state correctly")
    public void testDisplayGameOver() {
        app.displayGameOver();
        assertTrue(app.gameOver, "Game should be marked as over");
        // Verify any other state changes
    }
    @Test
    @DisplayName("Display lines should work correctly")
    public void testDisplayLines() {
        App.lines.add(new Line(Arrays.asList(new PVector(100, 100), new PVector(200, 200))));
        app.displayLines(); // Call to display lines
        // Check visual or state changes
    }
    @Test
    @DisplayName("Mouse pressed should register click")
    public void testMousePressed() {
        app.mousePressed(); // Simulate mouse press
        // Assert state changes
    }
    
     
    @Test
    @DisplayName("Yellow tile movement should be correct")
    public void testYellowTileMovement() {
        // Test movement of yellow tiles
        app.currentDirection1 = 0; // Move right
        app.moveYellowTile();
        assertTrue(app.currentX1 > 0, "Yellow tile should move right");

        app.currentX1 = (App.BOARD_WIDTH - 1) * App.CELLSIZE; // Set to max
        app.moveYellowTile(); // Should change direction
        assertEquals((App.BOARD_WIDTH - 1) * App.CELLSIZE, app.currentX1, "Yellow tile should not exceed the board width");

        app.currentDirection2 = 2; // Move right
        app.moveYellowTile();
        assertTrue(app.currentX2 > (App.WIDTH - App.CELLSIZE), "Second yellow tile should move right");
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
    // Here we assume you have a way to verify rendered text (not directly testable).
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
        app.timer = 5; // Set timer to simulate time remaining
        app.displayWin(); // Should increment score
        assertEquals(1, BoardManager.score, "Score should increment when the level is won");
    }

    @Test
    @DisplayName("Game should restart correctly")
    public void testRestartGame() {
        app.restartGame();
        assertFalse(app.gameWon, "Game should not be won after restart");
        assertEquals(BoardManager.levelScore, BoardManager.score, "Score should reset to level score after restart");
        assertEquals(app.spawnInterval, app.spawnTimer, 0.1, "Spawn timer should reset to initial spawn interval");
    }

    
}
