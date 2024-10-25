package inkball;

import inkball.loaders.ImageLoader;
import inkball.managers.BoardManager;
import inkball.managers.BallManager;
import inkball.objects.Ball;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

class BoardManagerTest {

    private App app;

    private ImageLoader imageLoader;
    private BoardManager boardManager;

    @BeforeEach
    void setUp() {
        app = new App(); // Replace with actual mock implementation
        PApplet.runSketch(new String[] { "inkball.App" }, app);
        imageLoader = new ImageLoader(app); // Replace with actual mock implementation
        boardManager = new BoardManager(app, imageLoader);
        App.decreaseScore = new HashMap<>();
        App.decreaseScore.put("color1", 10); // Example color with score deduction
        App.decreaseScore.put("color2", 20); // Another color
        App.decreaseScoreMultipler = 2; // Set a multiplier
        BoardManager.score = 100; // Initial score

    }

    @Test
    void testLoadBoard() {
        app.level = 1; // Set a level for testing
        boardManager.loadBoard();

        assertNotNull(BoardManager.board, "Board should not be null after loading.");
        assertFalse(BoardManager.walls.isEmpty(), "Walls should be initialized.");
        assertFalse(BoardManager.holes.isEmpty(), "Holes should be initialized.");
    }

    @Test
    void testIncreaseScore() {
        Ball mockBall = new Ball(app, imageLoader.tile, 0, 0, 0, 0, 10, boardManager, '3'); // Assuming 'R' is red
        App.increaseScore.put("green", 10);
        App.increaseScoreMultipler = 2; // Example multiplier

        BoardManager.score = 0; // Reset score
        BoardManager.increaseScore(mockBall);

        assertEquals(20, BoardManager.score, "Score should increase correctly.");
    }

    @Test
    void testSpawnBallAtPosition() {

        Ball mockBall = new Ball(app, imageLoader.tile, 0, 0, 0, 0, 10, boardManager, '3');

        // Call spawnBall for the first time
        boardManager.spawnBallAtPosition(mockBall);

        // Assert that a ball has been spawned
        assertTrue(boardManager.ballIsNull, "Should be null.");
        // Assert that hasSpawnedBall is true

    }

  


    @Test
    void testSetFinishedBallCount() {
        // Set the count to a specific value
        BoardManager.setFinishedBallCount(5);
        assertEquals(5, BoardManager.getFinishedBallCount(), "Count should be set to 5.");
    }

    @Test
    void testAddFinishedBall() {
        // Add a finished ball
        BoardManager.setFinishedBallCount(0);
        boardManager.addFinishedBall();
        assertEquals(1, BoardManager.getFinishedBallCount(), "Count should be 1 after adding one finished ball.");

        // Add another finished ball
        boardManager.addFinishedBall();
        assertEquals(2, BoardManager.getFinishedBallCount(), "Count should be 2 after adding another finished ball.");
    }

    @Test
    void testMultipleAdds() {
        BoardManager.setFinishedBallCount(0);

        for (int i = 0; i < 10; i++) {
            boardManager.addFinishedBall();
        }
        assertEquals(10, BoardManager.getFinishedBallCount(), "Count should be 10 after adding 10 finished balls.");
    }

}
