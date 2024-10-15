package inkball;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import inkball.loaders.ImageLoader;
import inkball.managers.BallManager;
import inkball.managers.BoardManager;
import inkball.objects.Ball;
import inkball.objects.Hole;
import inkball.objects.Line;
import inkball.objects.Wall;

public class BallTest {
    private App app;
    private PImage image;
    private ImageLoader imageLoader;
    private BoardManager boardManager;
    private BallManager ballManager;
    private Ball ball;

    @BeforeEach
    public void setUp() {
        app = new App(); // Create a new PApplet instance
        PApplet.runSketch(new String[] {"inkball.App"}, app);
        app.settings(); // Call the settings method to set up the PApplet
        app.setup(); // Call setup to initialize game state
        imageLoader = new ImageLoader(app);
        ballManager = new BallManager(app, imageLoader);
        boardManager = new BoardManager(app, imageLoader); // Initialize BoardManager
        ball = new Ball(app, image, 100, 100, 5, 5, 10, boardManager, '1'); // Initial position and velocity
    }

    @Test
    public void testDisplay() {
        ball.display();
        // Manual inspection needed for visual tests.
    }

    @Test
    public void testUpdate() {
        ball.update(); // Update ball's position
        assertEquals(105, ball.getX(), "Ball X position should be updated to 105");
        assertEquals(105, ball.getY(), "Ball Y position should be updated to 105");
    }
    @Test
    public void testGetColour() {
        ball.setColour('2'); // Set the ball's color to blue
        assertEquals("2", ball.getColour(), "Ball color should be '2'.");
    }
    
    @Test
    public void testGetColourString() {
        ball.setColour('0'); // Set the ball's color to grey
        assertEquals("grey", ball.getColourString(), "Ball color string should be 'grey'.");
    
        ball.setColour('1'); // Set the ball's color to orange
        assertEquals("orange", ball.getColourString(), "Ball color string should be 'orange'.");
    
        ball.setColour('2'); // Set the ball's color to blue
        assertEquals("blue", ball.getColourString(), "Ball color string should be 'blue'.");
    
        ball.setColour('3'); // Set the ball's color to green
        assertEquals("green", ball.getColourString(), "Ball color string should be 'green'.");
    
        ball.setColour('4'); // Set the ball's color to yellow
        assertEquals("yellow", ball.getColourString(), "Ball color string should be 'yellow'.");
    
        ball.setColour('5'); // Set the ball's color to an invalid color
        assertNull(ball.getColourString(), "Ball color string should be null for invalid color.");
    }
    
    @Test
    public void testCorrectBallSameColor() {
        Hole hole = new Hole(100, 100, 10, '2'); // Create a hole with the same color
        ball.setColour('2'); // Set the ball's color to blue
        assertTrue(ball.correctBall(hole), "Ball should be correctly matched with the hole of the same color.");
    }
    
    @Test
    public void testCorrectBallZeroColor() {
        Hole hole = new Hole(100, 100, 10, '3'); // Create a hole with a different color
        ball.setColour('0'); // Set the ball's color to grey
        assertTrue(ball.correctBall(hole), "Ball should be correctly matched with the hole when ball color is '0'.");
        
        hole.setColour('0'); // Set the hole's color to grey
        assertTrue(ball.correctBall(hole), "Ball should be correctly matched with the hole when hole color is '0'.");
    }
    
    @Test
    public void testCorrectBallDifferentColor() {
        Hole hole = new Hole(100, 100, 10, '3'); // Create a hole with a different color
        ball.setColour('2'); // Set the ball's color to blue
        assertFalse(ball.correctBall(hole), "Ball should not be correctly matched with the hole of a different color.");
    }
    
    @Test
    public void testSpawnBall() {
        // Add a ball color to the queue
        BallManager.ballQueue.add("blue");

        // Call the spawnBall method
        ballManager.spawnBall();

        // Check the size of ballsInPlay to ensure a ball has been added
        assertEquals(1, BallManager.ballsInPlay.size(), "One ball should be spawned from the queue.");

        // Verify the properties of the spawned ball
        Ball spawnedBall = BallManager.ballsInPlay.get(0);
        assertEquals('b', spawnedBall.getColour(), "Spawned ball should have the correct color.");
        assertEquals(10, spawnedBall.getRadius(), "Spawned ball should have the correct radius.");
        assertTrue(spawnedBall.getVelocityX() == 2 || spawnedBall.getVelocityX() == -2,
                "Spawned ball should have a valid X velocity.");
        assertTrue(spawnedBall.getVelocityY() == 2 || spawnedBall.getVelocityY() == -2,
                "Spawned ball should have a valid Y velocity.");
    }

    @Test
    public void testFreezeToggle() {
        Ball testBall = new Ball(app, image, 100, 100, 5, 5, 10, boardManager, '1');
        BallManager.ballsInPlay.add(testBall); // Add the ball to the ballsInPlay list

        // Test freezing
        ballManager.freezeToggle(true);
        assertEquals(0, testBall.getVelocityX(), "Ball's X velocity should be 0 when frozen.");
        assertEquals(0, testBall.getVelocityY(), "Ball's Y velocity should be 0 when frozen.");

        // Test unfreezing
        ballManager.freezeToggle(false);
        assertNotEquals(0, testBall.getVelocityX(), "Ball's X velocity should be restored after unfreeze.");
        assertNotEquals(0, testBall.getVelocityY(), "Ball's Y velocity should be restored after unfreeze.");
    }

    @Test
    public void testHandleBallSpawning() {
        // Initialize the app and set spawnTimer to 0
        app.spawnTimer = 0;
        app.spawnInterval = 1000; // Set some spawn interval

        // Before spawning, check the number of balls
        int initialCount = BallManager.ballQueue.size();

        // Call the handleBallSpawning method
        ballManager.handleBallSpawning();

        // Check that a ball is spawned
        assertEquals(initialCount + 1, BallManager.ballQueue.size(), "A new ball should be spawned.");
        assertEquals(app.spawnInterval, app.spawnTimer, "Spawn timer should reset to spawn interval.");
    }

    @Test
    public void testGravitateTowardsHoleMovesBall() {
        Hole hole = new Hole(150, 150, 20, '2');
        BoardManager.holes.add(hole);

        ball.position = new PVector(140, 140);
        ball.gravitateTowardsHole();

        // Check that the ball is closer to the hole after gravitation
        assertTrue(PVector.dist(ball.position, hole.getPosition()) < 10, "Ball should be moving closer to the hole.");
    }

    @Test
    public void testGravitateTowardsHoleShrinksRadius() {
        Hole hole = new Hole(150, 150, 20, '2');
        BoardManager.holes.add(hole);

        ball.position = new PVector(140, 140);
        float initialRadius = ball.getRadius();
        ball.gravitateTowardsHole();

        assertTrue(ball.getRadius() < initialRadius, "Ball's radius should shrink when gravitating towards the hole.");
    }

    @Test
    public void testGravitateTowardsHoleCapturesCorrectColor() {
        Hole hole = new Hole(100, 100, 10, '1'); // Same color
        BoardManager.holes.add(hole);

        ball.position = new PVector(100, 100);
        ball.gravitateTowardsHole();

        assertTrue(ball.isCapturedByHole(hole), "Ball should be captured by the hole of the same color.");
        assertTrue(ball.isCaptured(), "Ball should be marked as captured.");
    }

    @Test
    public void testGravitateTowardsHoleDoesNotCaptureWrongColor() {
        Hole hole = new Hole(100, 100, 10, '2'); // Different color
        BoardManager.holes.add(hole);

        ball.position = new PVector(100, 100);
        ball.gravitateTowardsHole();

        assertFalse(ball.isCapturedByHole(hole), "Ball should not be captured by the hole of a different color.");
        assertFalse(ball.isCaptured(), "Ball should not be marked as captured.");
    }

    @Test
    public void testIsNearHole() {
        Hole hole = new Hole(100, 100, 20, '1'); // Create a hole
        ball.position = new PVector(110, 110); // Position the ball outside the hole's radius

        assertFalse(ball.isNearHole(hole), "Ball should not be near the hole.");

        ball.position = new PVector(105, 105); // Move the ball inside the hole's radius
        assertTrue(ball.isNearHole(hole), "Ball should be near the hole.");

        ball.gravitateTowardsHole();

        // Check the ball's new position and radius
        assertEquals(150, ball.getX(), "Ball should move towards the hole's position.");
        assertEquals(150, ball.getY(), "Ball should move towards the hole's position.");
        assertTrue(ball.getRadius() < 10, "Ball should shrink when gravitating towards the hole.");

        // Check if the ball is captured
        assertTrue(ball.isCapturedByHole(hole), "Ball should be captured by the hole.");
    }

    @Test
    public void testScoreUpdatesOnCapture() {
        Hole hole = new Hole(100, 100, 10, '1'); // Create a hole with the same color
        BoardManager.holes.add(hole);

        ball.position = new PVector(100, 100);
        ball.gravitateTowardsHole();

        // Assuming initial score is 0 and capture increases it
        int initialScore = BoardManager.score; // Replace with actual method to get score
        ball.gravitateTowardsHole();

        assertEquals(initialScore + 1, BoardManager.score, "Score should increase by 1 on capturing the ball.");
    }

    @Test
    public void testGravitateTowardsHoleWithEdgeCases() {
        Hole hole = new Hole(100, 100, 10, '1'); // Same color
        BoardManager.holes.add(hole);

        ball.position = new PVector(90, 90); // Near hole, not inside
        ball.gravitateTowardsHole();
        assertFalse(ball.isCaptured(), "Ball should not be captured if not directly at the hole.");

        ball.position = new PVector(100, 100); // Directly at hole
        ball.gravitateTowardsHole();
        assertTrue(ball.isCaptured(), "Ball should be captured when positioned directly at the hole.");
    }

    @Test
    public void testCorrectBall() {
        Hole hole = new Hole(100, 100, 20, '2'); // Different color
        assertFalse(ball.correctBall(hole), "Ball should not be correctly matched with the hole.");

        hole.setColour('1'); // Set same color
        assertTrue(ball.correctBall(hole), "Ball should be correctly matched with the hole.");
    }

    @Test
    public void testGravitateTowardsHoleNoCapture() {
        Hole hole = new Hole(150, 150, 20, '2'); // Create a hole
        BoardManager.holes.add(hole); // Add hole to the BoardManager

        ball.position = new PVector(140, 140); // Position the ball near the hole
        ball.update(); // Update ball's position
        assertFalse(ball.isCaptured(), "Ball should not be captured by the hole.");
    }

    @Test
    public void testCheckCollisionWithWall() {
        Wall wall = new Wall(app, 90, 100, 90, 110, '1', imageLoader); // Create a wall
        ball.position = new PVector(85, 100); // Position the ball to check for collision

        assertTrue(ball.checkCollisionWithWall(wall), "Ball should be colliding with the wall.");

        ball.position = new PVector(95, 100); // Move the ball outside of the wall
        assertFalse(ball.checkCollisionWithWall(wall), "Ball should not be colliding with the wall.");
    }

    @Test
    public void testIsCapturedByHole() {
        Hole hole = new Hole(100, 100, 10, '1'); // Create a hole with the same color
        ball.position = new PVector(100, 100); // Position ball at the hole's center

        assertTrue(ball.isCapturedByHole(hole), "Ball should be captured by the hole.");

        ball.setY(120); // Move the ball away from the hole
        assertFalse(ball.isCapturedByHole(hole), "Ball should not be captured if it's not near the hole.");
    }

    @Test
    public void testReflect() {
        PVector velocity = new PVector(1, -1);
        PVector normal = new PVector(0, 1); // Reflecting off a horizontal surface
        PVector reflected = ball.reflect(velocity, normal);

        assertEquals(1, reflected.x, "X velocity should remain the same after reflection.");
        assertEquals(1, reflected.y, "Y velocity should be inverted after reflection.");
    }

    @Test
    public void testIsCaptured() {
        Hole hole = new Hole(100, 100, 10, '1'); // Create hole with same color
        ball.position = new PVector(100, 100); // Position ball at the hole's center

        // Simulate capturing the ball
        assertTrue(ball.isCapturedByHole(hole), "Ball should be captured by the hole.");
    }

    @Test
    public void testSetVelocity() {
        ball.setVelocityX(10);
        ball.setVelocityY(15);
        assertEquals(10, ball.getVelocityX(), "Ball's X velocity should be set to 10.");
        assertEquals(15, ball.getVelocityY(), "Ball's Y velocity should be set to 15.");
    }

    @Test
    public void testFreezeUnfreeze() {
        ball.freeze();
        assertEquals(0, ball.getVelocityX(), "Ball's X velocity should be 0 when frozen.");
        assertEquals(0, ball.getVelocityY(), "Ball's Y velocity should be 0 when frozen.");

        ball.unfreeze();
        assertNotEquals(0, ball.getVelocityX(), "Ball's X velocity should be restored after unfreeze.");
        assertNotEquals(0, ball.getVelocityY(), "Ball's Y velocity should be restored after unfreeze.");
    }

    @Test
    public void testGetScoreForCapture() {
        Hole hole = new Hole(100, 100, 10, '1'); // Create hole
        ball.scoreValue = 10; // Set score value for the ball
        int score = ball.getScoreForCapture(hole, 2); // Level multiplier of 2
        assertEquals(20, score, "Score should be calculated correctly for capture.");
    }

    @Test
    public void testCollisionWithWall() {
        Wall wall = new Wall(app, 90, 110, 90, 110, '1', imageLoader); // Create a wall
        assertTrue(ball.checkCollisionWithWall(wall), "Ball should be colliding with the wall.");
        ball.handleWallCollision(wall);
        assertTrue(ball.getX() < 90, "Ball position should be adjusted after collision with wall.");
    }

    @Test
    public void testHandleWallCollision() {
        Wall wall = new Wall(app, 90, 100, 90, 110, '1', imageLoader);
        ball.position = new PVector(85, 100); // Position the ball near the wall
        ball.velocity = new PVector(-10, 0); // Moving towards the wall

        ball.handleWallCollision(wall);

        assertTrue(ball.getX() > 90, "Ball should have moved away from the wall after collision.");
    }

    // @Test
    // public void testHandleLineCollisions() {
    // Line line = new Line(new PVector(90, 90), new PVector(110, 110));
    // boardManager.addLine(line); // Add line to BoardManager
    // ball.position = new PVector(100, 100); // Position ball at the center of the
    // line
    // ball.velocity = new PVector(5, 5); // Set an initial velocity

    // ball.handleLineCollisions(); // Process collisions

    // // Check if the ball's position has been adjusted
    // assertNotEquals(100, ball.getX(), "Ball's position should change after line
    // collision.");
    // }

    @Test
    public void testApplyCollisionLogic() {
        Wall wall = new Wall(app, 90, 100, 90, 110, '1', imageLoader); // Create a wall
        ball.position = new PVector(85, 100); // Position the ball to check for collision

        ball.applyCollisionLogic(); // Apply collision logic

        assertNotEquals(85, ball.getX(), "Ball's position should change after applying collision logic.");
    }
}
