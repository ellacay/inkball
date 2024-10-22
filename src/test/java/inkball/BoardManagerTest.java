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

    private App app;
    
    private ImageLoader imageLoader;
    private BoardManager boardManager;

    @BeforeEach
    void setUp() {
        app = new App(); // Replace with actual mock implementation
        PApplet.runSketch(new String[] {"inkball.App"}, app);
        imageLoader = new ImageLoader(app); // Replace with actual mock implementation
        boardManager = new BoardManager(app, imageLoader);
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
    void testIncreaseScore() {
        Ball mockBall = new Ball(app, imageLoader.tile, 0, 0, 0, 0, 10, boardManager, '3'); // Assuming 'R' is red
        App.increaseScore.put("green", 10);
        App.increaseScoreMultipler = 2; // Example multiplier

        BoardManager.score = 0; // Reset score
        BoardManager.increaseScore(mockBall);

        assertEquals(20, BoardManager.score, "Score should increase correctly.");
    }

    
    

    
}
