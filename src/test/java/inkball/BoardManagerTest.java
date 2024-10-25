package inkball;

import inkball.loaders.ImageLoader;
import inkball.managers.BoardManager;
import inkball.objects.Ball;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

class BoardManagerTest {

    private App app;
    private ImageLoader imageLoader;
    private BoardManager boardManager;

    /**
     * Sets up the environment for each test case.
     * Initializes the App, ImageLoader, and BoardManager instances.
     * Also sets up initial game state, including score and board configuration.
     */
    @BeforeEach
    void setUp() {
        app = new App(); // Instantiate the main application class.
        PApplet.runSketch(new String[] { "inkball.App" }, app); // Run the application sketch.
        imageLoader = new ImageLoader(app); // Initialize ImageLoader with the app instance.
        boardManager = new BoardManager(app, imageLoader); // Create BoardManager with app and imageLoader.
        
        // Set initial score configuration for the game.
        App.decreaseScore = new HashMap<>();
        App.decreaseScore.put("color1", 10); 
        App.decreaseScore.put("color2", 20); 
        App.decreaseScoreMultipler = 2; 
        BoardManager.score = 100; // Set initial score for testing.

        // Initialize the board with predefined holes and walls.
        BoardManager.board = new char[][] {
            {' ', ' ', 'H', '0', ' '},
            {' ', ' ', 'H', '1', ' '},
            {' ', ' ', 'H', '2', ' '},
            {' ', ' ', 'H', '3', ' '},
            {' ', ' ', 'H', '4', ' '}
        };
    }

    /**
     * Tests the loading of the board.
     * Asserts that the board is initialized correctly and that walls and holes are present.
     */
    @Test
    void testLoadBoard() {
        app.level = 1; // Set the current level.
        boardManager.loadBoard(); // Load the board.

        // Verify that the board, walls, and holes are properly initialized.
        assertNotNull(BoardManager.board, "Board should not be null after loading.");
        assertFalse(BoardManager.walls.isEmpty(), "Walls should be initialized.");
        assertFalse(BoardManager.holes.isEmpty(), "Holes should be initialized.");
    }

    /**
     * Tests the score increase functionality.
     * Asserts that the score increases correctly based on the multiplier and color.
     */
    @Test
    void testIncreaseScore() {
        Ball mockBall = new Ball(app, imageLoader.tile, 0, 0, 0, 0, 10, boardManager, '3'); // Create a mock ball.
        App.increaseScore.put("green", 10); // Set the score increase for the "green" ball.
        App.increaseScoreMultipler = 2; // Set the multiplier for score increase.

        BoardManager.score = 0; // Reset the score to zero.
        BoardManager.increaseScore(mockBall); // Increase the score based on the mock ball.

        // Assert that the score has increased correctly.
        assertEquals(20, BoardManager.score, "Score should increase correctly.");
    }

    /**
     * Tests the spawning of a ball at a specified position.
     * Asserts that the ball is properly spawned and that the appropriate state is set.
     */
    @Test
    void testSpawnBallAtPosition() {
        Ball ball = new Ball(app, imageLoader.tile, 0, 0, 0, 0, 10, boardManager, '3'); // Create a ball.
        boardManager.spawnBallAtPosition(ball); // Attempt to spawn the ball at the given position.
        
        // Assert that the ball is null after spawning (indicating a failure or a specific behavior).
        assertTrue(boardManager.ballIsNull, "Should be null.");
    }

    /**
     * Tests the functionality of setting the finished ball count.
     * Asserts that the count is set correctly.
     */
    @Test
    void testSetFinishedBallCount() {
        BoardManager.setFinishedBallCount(5); // Set the finished ball count to 5.
        // Assert that the finished ball count is correctly set.
        assertEquals(5, BoardManager.getFinishedBallCount(), "Count should be set to 5.");
    }

    /**
     * Tests the addition of finished balls.
     * Asserts that the finished ball count increases as balls are added.
     */
    @Test
    void testAddFinishedBall() {
        BoardManager.setFinishedBallCount(0); // Reset the finished ball count to 0.
        boardManager.addFinishedBall(); // Add one finished ball.
        // Assert that the count increases to 1.
        assertEquals(1, BoardManager.getFinishedBallCount(), "Count should be 1 after adding one finished ball.");
        
        boardManager.addFinishedBall(); // Add another finished ball.
        // Assert that the count increases to 2.
        assertEquals(2, BoardManager.getFinishedBallCount(), "Count should be 2 after adding another finished ball.");
    }

    /**
     * Tests the functionality of adding multiple finished balls.
     * Asserts that the count reflects the total number of balls added.
     */
    @Test
    void testMultipleAdds() {
        BoardManager.setFinishedBallCount(0); // Reset the finished ball count to 0.
        for (int i = 0; i < 10; i++) {
            boardManager.addFinishedBall(); // Add finished balls in a loop.
        }
        // Assert that the count is 10 after adding 10 finished balls.
        assertEquals(10, BoardManager.getFinishedBallCount(), "Count should be 10 after adding 10 finished balls.");
    }

}
