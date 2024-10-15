package inkball;

import inkball.loaders.ImageLoader;
import inkball.managers.BallManager;
import inkball.managers.BoardManager;
import inkball.objects.Ball;
import inkball.objects.Spawner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PImage;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BallManagerTest {
    private App app;
    private ImageLoader imageLoader;
    private BallManager ballManager;

    @BeforeEach
    void setUp() {
        app = new App(); // Instantiate your main application
        imageLoader = new ImageLoader(app); // Initialize your ImageLoader
        ballManager = new BallManager(app, imageLoader);
        
        // Initialize BoardManager and add spawners for the tests
        BoardManager.spawners.clear(); // Ensure no spawners from previous tests
        Spawner spawner = new Spawner(10,10,20,20); // Create a new spawner
        BoardManager.spawners.add(spawner); // Add spawner to the board manager
    }

    @Test
void testInitializeBallQueue() {
    assertEquals(Arrays.asList("2", "1", "0", "3", "4"), BallManager.ballQueue, "Ball queue should initialize correctly.");
}
    @Test
    void testSpawnBall() {
        // Spawn a ball
        ballManager.spawnBall();

        // Check if a ball was added
        assertEquals(1, BallManager.ballsInPlay.size(), "One ball should be spawned.");
        Ball spawnedBall = BallManager.ballsInPlay.get(0);
        assertEquals(50 + (App.CELLSIZE / 2), spawnedBall.getX(), "Ball X position should be set correctly.");
        assertEquals(50 + (App.CELLSIZE / 2), spawnedBall.getY(), "Ball Y position should be set correctly.");
    }

    @Test
    void testUpdateAndDisplayBalls() {
        // Create a new ball and add it to the ballsInPlay
        PImage ballImage = imageLoader.ball0; // Get an image for the ball
        Ball ball = new Ball(app, ballImage, 10, 10, 2, 2, 10, new BoardManager(app, imageLoader), '0');
        BallManager.ballsInPlay.add(ball);

        // Call updateAndDisplayBalls
        ballManager.updateAndDisplayBalls();

        // Check if the ball's position has been updated correctly
        assertEquals(12f, ball.getX(), "Ball X position should be updated correctly.");
        assertEquals(12f, ball.getY(), "Ball Y position should be updated correctly.");
    }

    @Test
    void testFreezeToggle() {
        // Create two balls and add them to the ballsInPlay
        PImage ballImage = imageLoader.ball0; // Use any valid image
        Ball ball1 = new Ball(app, ballImage, 10, 10, 2, 2, 10, new BoardManager(app, imageLoader), '0');
        Ball ball2 = new Ball(app, ballImage, 20, 20, 3, 3, 10, new BoardManager(app, imageLoader), '1');
        BallManager.ballsInPlay.add(ball1);
        BallManager.ballsInPlay.add(ball2);

        // Test freezing balls
        ballManager.freezeToggle(true);
        assertEquals(0f, ball1.getVelocityX(), "Ball1 should be frozen.");
        assertEquals(0f, ball1.getVelocityY(), "Ball1 should be frozen.");
        assertEquals(0f, ball2.getVelocityX(), "Ball2 should be frozen.");
        assertEquals(0f, ball2.getVelocityY(), "Ball2 should be frozen.");

        // Test unfreezing balls
        ballManager.freezeToggle(false);
        assertNotEquals(0f, ball1.getVelocityX(), "Ball1 should not be frozen.");
        assertNotEquals(0f, ball1.getVelocityY(), "Ball1 should not be frozen.");
        assertNotEquals(0f, ball2.getVelocityX(), "Ball2 should not be frozen.");
        assertNotEquals(0f, ball2.getVelocityY(), "Ball2 should not be frozen.");
    }

    @Test
    void testReset() {
        // Prepare a ball and add it to the ballsInPlay
        PImage ballImage = imageLoader.ball0; // Use any valid image
        Ball ball = new Ball(app, ballImage, 10, 10, 2, 2, 10, new BoardManager(app, imageLoader), '0');
        BallManager.ballsInPlay.add(ball);

        // Call reset
        ballManager.reset();

        // Verify the ballsInPlay is cleared and ballQueue is reinitialized
        assertTrue(BallManager.ballsInPlay.isEmpty(), "Balls in play should be cleared on reset.");
        assertEquals(Arrays.asList("2", "1", "0", "3", "4"), BallManager.ballQueue, "Ball queue should be reset to initial state.");
    }
}
