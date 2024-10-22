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
    public void testGravitateTowardsHoleDoesNotCaptureWrongColor() {
        Hole hole = new Hole(100, 100, 10, '2'); // Different color
        BoardManager.holes.add(hole);

        ball.position = new PVector(100, 100);
        ball.gravitateTowardsHole();

        assertFalse(ball.isCapturedByHole(hole), "Ball should not be captured by the hole of a different color.");
        assertFalse(ball.isCaptured(), "Ball should not be marked as captured.");
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
    public void testReflect() {
        PVector velocity = new PVector(1, -1);
        PVector normal = new PVector(0, 1); // Reflecting off a horizontal surface
        PVector reflected = ball.reflect(velocity, normal);

        assertEquals(1, reflected.x, "X velocity should remain the same after reflection.");
        assertEquals(1, reflected.y, "Y velocity should be inverted after reflection.");
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
    public void testHandleWallCollision() {
        Wall wall = new Wall(app, 90, 100, 90, 110, '1', imageLoader);
        ball.position = new PVector(85, 100); // Position the ball near the wall
        ball.velocity = new PVector(-10, 0); // Moving towards the wall

        ball.handleWallCollision(wall);

        assertTrue(ball.getX() > 20, "Ball should have moved away from the wall after collision.");
    }

    

}
