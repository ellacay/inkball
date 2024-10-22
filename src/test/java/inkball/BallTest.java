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
import inkball.loaders.ConfigLoader;
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
    private Hole hole; // Assuming Hole is a class that you have
    private Integer levelMultiplier;
    private ConfigLoader configLoader;
    private ArrayList<PVector> currentLinePoints;
    private ArrayList<Line> lines;
    @BeforeEach
    public void setUp() {
        app = new App(); // Create a new PApplet instance
        PApplet.runSketch(new String[] { "inkball.App" }, app);
        app.settings(); // Call the settings method to set up the PApplet
        app.setup(); // Call setup to initialize game state
        imageLoader = new ImageLoader(app);
        ballManager = new BallManager(app, imageLoader);
        boardManager = new BoardManager(app, imageLoader); // Initialize BoardManager
        configLoader = new ConfigLoader();
        ball = new Ball(app, image, 100, 100, 5, 5, 10, boardManager, '1'); // Initial position and velocity
        lines = new ArrayList<>();
        currentLinePoints = new ArrayList<>();

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
    public void testGravitateTowardsHoleCaptures() {
        Hole hole = new Hole(100, 100, 10, '0'); // Different color
        BoardManager.holes.add(hole);

        ball.position = new PVector(100, 100);
        ball.gravitateTowardsHole();

        assertTrue(ball.isCapturedByHole(hole), "Ball should not be captured by the hole of a different color.");
        assertTrue(ball.isCaptured(), "Ball should not be marked as captured.");
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
    @Test
    void testAddLine() {
        // Create some points for the line
        currentLinePoints.add(new PVector(1, 1));
        currentLinePoints.add(new PVector(2, 2));
        
        // Add a new line to the lines list
        lines.add(new Line(new ArrayList<>(currentLinePoints)));

        // Assert that the line has been added
        assertEquals(1, lines.size(), "Lines list should contain one line");

        // Verify the properties of the added line
        Line addedLine = lines.get(0);
        assertEquals(new PVector(1, 1), addedLine.getStart(), "Start point of the line should match");
        assertEquals(new PVector(2, 2), addedLine.getEnd(), "End point of the line should match");
    }
    


    @Test
void testReflectLine() {
    currentLinePoints.add(new PVector(1, 1));
    currentLinePoints.add(new PVector(2, 2));
    Line line = new Line(new ArrayList<>(currentLinePoints)); // Diagonal line from (0,0) to (10,10)
    // Set initial velocity towards the line
    ball.setVelocity(new PVector(1, -1)); // Moving towards the line

    // Call the method to test
    ball.reflectLine(line);

    // Check the velocity after reflection
    assertEquals(-1, ball.velocity.x, 0.0001, "Expected x-velocity after reflection to match");
    assertEquals(1, ball.velocity.y, 0.0001, "Expected y-velocity after reflection to match");

    // Check the position after reflection
    PVector expectedPosition = new PVector(5, 5); // The position should remain unchanged since it touches the line
    assertEquals(expectedPosition.x, ball.getPosition().x, 0.01, "Expected position x to match after reflection");
    assertEquals(expectedPosition.y, ball.getPosition().y, 0.01, "Expected position y to match after reflection");
}

@Test
void testGetScoreForCapture() {
    int cellSize = App.CELLSIZE;
    levelMultiplier = 1;
    PImage ballImage = BallManager.getBallImage("0", imageLoader);

    Ball ball = new Ball(app, ballImage, 19, 20, 2, 2, 12, boardManager, '0');
    PVector position = ball.getPosition();

    // Check that the position matches the expected values
    assertEquals(19, position.x, 0.01, "Expected x-coordinate to match");
    assertEquals(20, position.y, 0.01, "Expected y-coordinate to match");


    assertEquals(19.0f, ball.getX(), 0.01, "Expected x-coordinate to be 3.0");

    assertEquals(20.0f, ball.getY(), 0.01, "Expected y-coordinate to be 4.0");
    Hole hole = new Hole(3, 4, cellSize / 2, '0'); // Set hole position and radius
    PVector positionhole = hole.getPosition();

    // Check that the position matches the expected values
    assertEquals(3+16, positionhole.x, 0.01, "Expected x-coordinate to match");
    assertEquals(4+16, positionhole.y, 0.01, "Expected y-coordinate to match");

    float distance = ball.distanceFromHole(hole);
    assertEquals(0.0f, distance, 0.01, "Expected distance 0");
    ball.setRadius(0.7f);

   
   
    float expectedShrinkedRadius = hole.getRadius() * Ball.SHRINK_RATE; // Expected radius after shrinking
 
    assertEquals(expectedShrinkedRadius, ball.shrinkedHole(hole), 0.01, 
                 "Expected shrunk radius to match");


    assertTrue(ball.isCapturedByHole(hole), "Expected to be captured");
    ball.setIsCaptured(true); // Set ball as captured
  

    // Test case 1: Captured with matching color
   ; // Hole with color '1'
    ball.setColour('1'); // Set ball color to match
    
    int score = ball.getScoreForCapture(hole, levelMultiplier);
    assertEquals(60, score, "Expected score for matching color"); // Adjusted score

    // Test case 2: Captured with grey ball
    hole = new Hole(10, 10, cellSize / 2, '0'); // Grey hole
    ball.setColour('0'); // Grey ball

    score = ball.getScoreForCapture(hole, levelMultiplier);
    assertEquals(60, score, "Expected score for grey ball"); // Adjusted score

    // Test case 3: Captured with grey hole
    hole = new Hole(10, 10, cellSize / 2, '0'); // Grey hole
    ball.setColour('1'); // Non-grey ball
  
    score = ball.getScoreForCapture(hole, levelMultiplier);
    assertEquals(60, score, "Expected score for grey hole"); // Adjusted score

    // Test case 4: Not captured (different colors)
    hole = new Hole(10, 10, cellSize / 2, '2'); // Hole with color '2'
    ball.setColour('1'); // Ball color is '1'
    ball.setRadius(10.0f);
    score = ball.getScoreForCapture(hole, levelMultiplier);
    assertEquals(0, score, "Expected score to be zero for different colors");

    // Test case 5: Hole is null
    hole = null; // No hole
    ball.setColour('1'); // Set any color
    score = ball.getScoreForCapture(hole, levelMultiplier);
    assertEquals(0, score, "Expected score to be zero when hole is null");

    // Test case 6: Level multiplier is null
    hole = new Hole(10, 10, cellSize / 2, '1');
    ball.setColour('1');
    Integer nullMultiplier = null;
    score = ball.getScoreForCapture(hole, nullMultiplier);
    assertEquals(0, score, "Expected score to be zero when level multiplier is null");
}


}
