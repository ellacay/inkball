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

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BallManagerTest {
    private App app;

    private ImageLoader imageLoader;
    private BallManager ballManager;

    @BeforeEach
    void setUp() {
        app = new App(); // Instantiate your main application
        PApplet.runSketch(new String[] { "inkball.App" }, app);
        imageLoader = new ImageLoader(app); // Initialize your ImageLoader
        ballManager = new BallManager(app, imageLoader);
        ballManager.initializeBallQueue();

        // Initialize BoardManager and add spawners for the tests
        BoardManager.spawners.clear(); // Ensure no spawners from previous tests
        Spawner spawner = new Spawner(10, 10, 20, 20); // Create a new spawner
        BoardManager.spawners.add(spawner); // Add spawner to the board manager
    }

   

   

    @Test
    void testGetBallImage() {
        PImage blue = Ball.getBallImage("blue", imageLoader);
        PImage orange = Ball.getBallImage("orange", imageLoader);
        PImage grey = Ball.getBallImage("grey", imageLoader);
        PImage green = Ball.getBallImage("green", imageLoader);
        PImage yellow = Ball.getBallImage("yellow", imageLoader);
        PImage black = Ball.getBallImage("black", imageLoader);

        assertEquals(blue, imageLoader.ball0, "Should be blue");
        assertEquals(orange, imageLoader.ball1, "Should be orange");
        assertEquals(grey, imageLoader.ball2, "Should be grey");
        assertEquals(green, imageLoader.ball3, "Should be green");
        assertEquals(yellow, imageLoader.ball4, "Should be yellow");
        assertEquals(black, null, "Should be null");
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
     
    }

    @Test

    public void testAddToQueueAgain() {
        imageLoader.loadImages();
        // Ensure app and imageLoader are properly initialized
        assertNotNull(app, "app shouldnt be null");
        assertNotNull(imageLoader,"image loader shoulnt be null");
        assertNotNull(imageLoader.ball0, "ball shouldnt be null");
    
        PImage ballImage = imageLoader.ball0;
        Ball ball = new Ball(app, ballImage, 10, 10, 2, 2, 10, new BoardManager(app, imageLoader), '0');
        int size = BallManager.ballQueue.size();
        
        BallManager.addToQueueAgain(ball);
        
        assertEquals(size + 1, BallManager.ballQueue.size(), "Should be matching");
    }
   

    @Test
    public void testSpawnBallEmptyQueue() {

        BallManager.ballQueue.clear();
        
        // Act: Call the method when the queue is empty
        ballManager.spawnBall();

        // Assert: Check that no balls have been spawned
        assertEquals(0,  BallManager.ballQueue.size(), "No balls should be spawned when the queue is empty");
    }

    @Test
    public void testRemoveBall(){
        BallManager.ballsInPlay.clear();

        PImage ballImage = imageLoader.ball0; // Use any valid image
        Ball ball = new Ball(app, ballImage, 10, 10, 2, 2, 10, new BoardManager(app, imageLoader), '0');
        BallManager.ballsInPlay.add(ball);
        int size = BallManager.ballsInPlay.size();
        BallManager.removeBall(ball);
        assertEquals(size-1,  BallManager.ballsInPlay.size(),"Should be matching");
    }

}
