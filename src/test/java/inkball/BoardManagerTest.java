package inkball;
import inkball.loaders.ImageLoader;
import inkball.managers.BoardManager;
import inkball.managers.BallManager;
import inkball.objects.Ball;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import static org.junit.jupiter.api.Assertions.*;

class BoardManagerTest {

    private PApplet mockApp;
    private ImageLoader mockLoader;
    private BoardManager boardManager;

    @BeforeEach
    void setUp() {
        mockApp = new PApplet(); // Replace with actual mock implementation
        mockLoader = new ImageLoader(mockApp); // Replace with actual mock implementation
        boardManager = new BoardManager(mockApp, mockLoader);
    }

    @Test
    void testLoadBoard() {
        App.level = 1; // Set a level for testing
        boardManager.loadBoard();

        assertNotNull(BoardManager.board, "Board should not be null after loading.");
        assertFalse(BoardManager.walls.isEmpty(), "Walls should be initialized.");
        assertFalse(BoardManager.holes.isEmpty(), "Holes should be initialized.");
    }

    @Test
    void testInitializeWalls() {
        App.level = 1; // Set a level with known walls
        boardManager.loadBoard();

        int expectedWallCount = 5; // Replace with the expected count based on level 1
        assertEquals(expectedWallCount, BoardManager.walls.size(), "Unexpected number of walls initialized.");
    }

    @Test
    void testInitializeHoles() {
        App.level = 2; // Assume level 2 has holes
        boardManager.loadBoard();

        int expectedHoleCount = 3; // Replace with the expected count based on level 2
        assertEquals(expectedHoleCount, BoardManager.holes.size(), "Unexpected number of holes initialized.");
    }

    @Test
    void testIncreaseScore() {
        Ball mockBall = new Ball(mockApp, mockLoader.tile, 0, 0, 0, 0, 10, boardManager, '3'); // Assuming 'R' is red
        App.increaseScore.put("green", 10);
        App.increaseScoreMultipler = 2; // Example multiplier

        BoardManager.score = 0; // Reset score
        BoardManager.increaseScore(mockBall);

        assertEquals(20, BoardManager.score, "Score should increase correctly.");
    }

    @Test
    void testDecreaseScore() {
        Ball mockBall = new Ball(mockApp, mockLoader.tile, 0, 0, 0, 0, 10, boardManager, 'B'); // Assuming 'B' is blue
        App.decreaseScore.put("B", 5);
        App.decreaseScoreMultipler = 2; // Example multiplier

        BoardManager.score = 30; // Set initial score
        BoardManager.decreaseScore(mockBall);

        assertEquals(20, BoardManager.score, "Score should decrease correctly.");
    }

    @Test
    void testCheckIfFinished() {
        BoardManager.setFinishedBallCount(3); // Simulate two balls finished
        BallManager.ballsInPlay.add(new Ball(mockApp, mockLoader.tile, 0, 0, 0, 0, 10, boardManager, 'R'));
        BallManager.ballQueue.add("3");

        assertTrue(boardManager.checkIfFinished(), "Check if the game is finished should return true.");
    }

    @Test
    void testReset() {
        BoardManager.setFinishedBallCount(3);
        boardManager.reset();

        assertEquals(0, BoardManager.getFinishedBallCount(), "Finished ball count should be reset.");
        assertTrue(BoardManager.walls.isEmpty(), "Walls should be cleared on reset.");
        assertTrue(BoardManager.holes.isEmpty(), "Holes should be cleared on reset.");
        assertNotNull(BoardManager.board, "Board should be loaded again after reset.");
    }
}
