package inkball;

import inkball.loaders.ImageLoader;
import inkball.managers.BallManager;
import inkball.managers.BoardManager;
import inkball.objects.Ball;
import inkball.objects.Spawner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class BallManagerTest {
    private App app;
    private ImageLoader imageLoader;
    private BallManager ballManager;

    /**
     * Sets up the environment for each test case.
     * Initializes the App instance, ImageLoader, and BallManager.
     * Also creates a spawner and adds it to the BoardManager's spawners.
     */
    @BeforeEach
    void setUp() {
        app = new App(); // Instantiate the main application class.
        PApplet.runSketch(new String[] { "inkball.App" }, app); // Run the application sketch.
        imageLoader = new ImageLoader(app); // Initialize ImageLoader with the app instance.
        ballManager = new BallManager(app, imageLoader); // Create BallManager with app and imageLoader.
        ballManager.initializeBallQueue(); // Initialize the ball queue.
        BoardManager.spawners.clear(); // Clear existing spawners.
        Spawner spawner = new Spawner(10, 10, 20, 20); // Create a new spawner.
        BoardManager.spawners.add(spawner); // Add the spawner to the BoardManager's spawners.


        // Set up the ball queue with a color
        BallManager.ballQueue = new ArrayList<>();
        BallManager.ballQueue.add("1"); // Assuming '1' is a valid color
        BallManager.ballsInPlay = new ArrayList<>(); // Initialize balls in play
       
    }

    /**
     * Tests the retrieval of ball images by verifying that
     * the correct images are returned for each ball color.
     */
    @Test
    void testGetBallImage() {
        PImage blue = Ball.getBallImage("blue", imageLoader);
        PImage orange = Ball.getBallImage("orange", imageLoader);
        PImage grey = Ball.getBallImage("grey", imageLoader);
        PImage green = Ball.getBallImage("green", imageLoader);
        PImage yellow = Ball.getBallImage("yellow", imageLoader);
        PImage black = Ball.getBallImage("black", imageLoader);

        // Verify that the loaded images match the expected values.
        assertEquals(blue, imageLoader.ball0, "Should be blue");
        assertEquals(orange, imageLoader.ball1, "Should be orange");
        assertEquals(grey, imageLoader.ball2, "Should be grey");
        assertEquals(green, imageLoader.ball3, "Should be green");
        assertEquals(yellow, imageLoader.ball4, "Should be yellow");
        assertEquals(black, null, "Should be null");
    }

    /**
     * Tests the freeze and unfreeze functionality of the balls.
     * Asserts that balls have zero velocity when frozen
     * and regain their velocity when unfrozen.
     */
    @Test
    void testFreezeToggle() {
        PImage ballImage = imageLoader.ball0;
        Ball ball1 = new Ball(app, ballImage, 10, 10, 2, 2, 10, new BoardManager(app, imageLoader), '0');
        Ball ball2 = new Ball(app, ballImage, 20, 20, 3, 3, 10, new BoardManager(app, imageLoader), '1');
        BallManager.ballsInPlay.add(ball1);
        BallManager.ballsInPlay.add(ball2);

        // Freeze the balls and assert their velocities are zero.
        ballManager.freezeToggle(true);
        assertEquals(0f, ball1.getVelocityX(), "Ball1 should be frozen.");
        assertEquals(0f, ball1.getVelocityY(), "Ball1 should be frozen.");
        assertEquals(0f, ball2.getVelocityX(), "Ball2 should be frozen.");
        assertEquals(0f, ball2.getVelocityY(), "Ball2 should be frozen.");

        // Unfreeze the balls and assert their velocities are non-zero.
        ballManager.freezeToggle(false);
        assertNotEquals(0f, ball1.getVelocityX(), "Ball1 should not be frozen.");
        assertNotEquals(0f, ball1.getVelocityY(), "Ball1 should not be frozen.");
        assertNotEquals(0f, ball2.getVelocityX(), "Ball2 should not be frozen.");
        assertNotEquals(0f, ball2.getVelocityY(), "Ball2 should not be frozen.");
    }

    /**
     * Tests the reset functionality of the BallManager.
     * Asserts that all balls in play are cleared upon reset.
     */
    @Test
    void testReset() {
        PImage ballImage = imageLoader.ball0;
        Ball ball = new Ball(app, ballImage, 10, 10, 2, 2, 10, new BoardManager(app, imageLoader), '0');
        BallManager.ballsInPlay.add(ball); // Add a ball to the play list.
        ballManager.reset(); // Reset the BallManager.

        // Assert that no balls are left in play after reset.
        assertTrue(BallManager.ballsInPlay.isEmpty(), "Balls in play should be cleared on reset.");
    }

    /**
     * Tests the functionality of adding a ball back to the queue.
     * Verifies that the size of the queue increases after adding a ball.
     */
    @Test
    public void testAddToQueueAgain() {
        imageLoader.loadImages(); // Load the images to ensure they are available.
        assertNotNull(app, "app shouldnt be null"); // Assert that app is not null.
        assertNotNull(imageLoader, "image loader shoulnt be null"); // Assert that imageLoader is not null.
        assertNotNull(imageLoader.ball0, "ball shouldnt be null"); // Assert that the first ball image is not null.

        PImage ballImage = imageLoader.ball0; // Get the ball image.
        Ball ball = new Ball(app, ballImage, 10, 10, 2, 2, 10, new BoardManager(app, imageLoader), '0');
        int size = BallManager.ballQueue.size(); // Store the current size of the queue.
        BallManager.addToQueueAgain(ball); // Add the ball back to the queue.

        // Assert that the size of the queue has increased by 1.
        assertEquals(size + 1, BallManager.ballQueue.size(), "Should be matching");
    }

    /**
     * Tests the spawning of balls when the ball queue is empty.
     * Asserts that no balls are spawned if the queue is empty.
     */
    @Test
    public void testSpawnBallEmptyQueue() {
        BallManager.ballQueue.clear(); // Clear the ball queue.
        ballManager.spawnBall(); // Attempt to spawn a ball.

        // Assert that the queue size remains zero as no balls should be spawned.
        assertEquals(0, BallManager.ballQueue.size(), "No balls should be spawned when the queue is empty");
    }

    /**
     * Tests the removal of a ball from the balls in play.
     * Asserts that the ball is successfully removed from the list.
     */
    @Test
    public void testRemoveBall() {
        BallManager.ballsInPlay.clear(); // Clear the balls in play.
        PImage ballImage = imageLoader.ball0;
        Ball ball = new Ball(app, ballImage, 10, 10, 2, 2, 10, new BoardManager(app, imageLoader), '0');
        BallManager.ballsInPlay.add(ball); // Add a ball to the play list.
        int size = BallManager.ballsInPlay.size(); // Get the current size.
        BallManager.removeBall(ball); // Remove the ball.

        // Assert that the size of the list has decreased by 1.
        assertEquals(size - 1, BallManager.ballsInPlay.size(), "Should be matching");
    }

    @Test
void testSpawnBall_WithColorInQueue() {
    // Call the spawnBall method to attempt to spawn a ball when there is a color in the queue
    ballManager.spawnBall();

    // Verify that the ball has been marked as removed from the queue
    assertTrue(BallManager.ballRemoved, "Ball should be marked as removed.");
}

@Test
void testSpawnBall_WithoutColorInQueue() {
    // Clear the ball queue to simulate the scenario where no colors are available for spawning
    BallManager.ballQueue.clear(); // Empty the queue
    
    // Attempt to spawn a ball with an empty queue
    ballManager.spawnBall();

    // Verify that the ball has not been marked as removed since the queue was empty
    assertFalse(BallManager.ballRemoved, "Ball should not be marked as removed.");
    
    // Check that the flag indicating a ball has been spawned is also false
    assertFalse(BallManager.hasSpawnedBall, "Ball should not be marked as spawned.");
    
    // Ensure that there are no balls in play after the spawn attempt
    assertEquals(0, BallManager.ballsInPlay.size(), "No balls should be in play.");
}

}
