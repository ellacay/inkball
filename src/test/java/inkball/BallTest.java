package inkball;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

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
    private App app; // The main application instance for the test
    private PImage image; // Placeholder for ball image
    private ImageLoader imageLoader; // Loader for images
    private BoardManager boardManager; // Manager for game board
    private BallManager ballManager; // Manager for ball-related actions
    private Ball ball; // The ball object being tested
    private Hole hole; // Placeholder for the hole object
    private Integer levelMultiplier; // Multiplier for scoring
    private ConfigLoader configLoader; // Loader for configuration settings
    private ArrayList<PVector> currentLinePoints; // List of current line points
    private ArrayList<Line> lines; // List of lines

    @BeforeEach
    public void setUp() {
        app = new App(); // Create a new instance of the App class
        PApplet.runSketch(new String[] { "inkball.App" }, app); // Start the PApplet
        app.settings(); // Initialize PApplet settings
        app.setup(); // Set up the game environment
        imageLoader = new ImageLoader(app); // Initialize the image loader
        ballManager = new BallManager(app, imageLoader); // Initialize ball manager
        boardManager = new BoardManager(app, imageLoader); // Initialize board manager
        configLoader = new ConfigLoader(); // Initialize config loader
        ball = new Ball(app, image, 100, 100, 5, 5, 10, boardManager, '1'); // Create a ball with specified properties
        lines = new ArrayList<>(); // Initialize line list
        currentLinePoints = new ArrayList<>(); // Initialize current line points list
    }

    /**
     * Tests the ball's color retrieval method.
     */
    @Test
    public void testGetColour() {
        ball.setColour('2'); // Set the ball's color to blue
        assertEquals('2', ball.getColour(), "Ball color should be '2'.");
    }

    /**
     * Tests the ball's color string representation.
     */
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

    /**
     * Tests if the ball correctly matches a hole of the same color.
     */
    @Test
    public void testCorrectBallSameColor() {
        Hole hole = new Hole(100, 100, 10, '2'); // Create a hole with the same color
        ball.setColour('2'); // Set the ball's color to blue
        assertTrue(ball.correctBall(hole), "Ball should be correctly matched with the hole of the same color.");
    }

    /**
     * Tests the behavior of the ball when its color is '0' (grey).
     */
    @Test
    public void testCorrectBallZeroColor() {
        Hole hole = new Hole(100, 100, 10, '3'); // Create a hole with a different color
        ball.setColour('0'); // Set the ball's color to grey
        assertTrue(ball.correctBall(hole), "Ball should be correctly matched with the hole when ball color is '0'.");

        hole.setColour('0'); // Set the hole's color to grey
        assertTrue(ball.correctBall(hole), "Ball should be correctly matched with the hole when hole color is '0'.");
    }

    /**
     * Tests the behavior of the ball when it is a different color than the hole.
     */
    @Test
    public void testCorrectBallDifferentColor() {
        Hole hole = new Hole(100, 100, 10, '3'); // Create a hole with a different color
        ball.setColour('2'); // Set the ball's color to blue
        assertFalse(ball.correctBall(hole), "Ball should not be correctly matched with the hole of a different color.");
    }

    /**
     * Tests toggling the freeze state of the ball.
     */
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

    /**
     * Tests that the ball does not get captured by a hole of a different color.
     */
    @Test
    public void testGravitateTowardsHoleDoesNotCaptureWrongColor() {
        Hole hole = new Hole(100, 100, 10, '2'); // Different color
        BoardManager.holes.add(hole); // Add hole to the board manager

        ball.setX(100); // Set ball position at the hole
        ball.setY(100);

        ball.gravitateTowardsHole(); // Check gravitational effect

        assertFalse(ball.isCapturedByHole(hole), "Ball should not be captured by the hole of a different color.");
        assertFalse(ball.isCaptured(), "Ball should not be marked as captured.");
    }

    /**
     * Tests the behavior when a ball is correctly matched with a hole.
     */
    @Test
    public void testCorrectBall() {
        Hole hole = new Hole(100, 100, 20, '2'); // Create a hole with a different color
        assertFalse(ball.correctBall(hole), "Ball should not be correctly matched with the hole.");

        hole.setColour('1'); // Set same color
        assertTrue(ball.correctBall(hole), "Ball should be correctly matched with the hole.");
    }

    /**
     * Tests that the ball does not get captured by the hole if it is not close enough.
     */
    @Test
    public void testGravitateTowardsHoleNoCapture() {
        Hole hole = new Hole(150, 150, 20, '2'); // Create a hole
        BoardManager.holes.add(hole); // Add hole to the BoardManager
        ball.setX(140); // Set ball's position near the hole
        ball.setY(140);

        ball.update(); // Update ball's position
        assertFalse(ball.isCaptured(), "Ball should not be captured by the hole.");
    }

    /**
     * Tests the reflection of the ball off a surface.
     */
    @Test
    public void testReflect() {
        PVector velocity = new PVector(1, -1); // Incoming velocity
        PVector normal = new PVector(0, 1); // Normal vector of the reflecting surface
        PVector reflected = ball.reflect(velocity, normal); // Calculate reflection

        assertEquals(1, reflected.x, "X velocity should remain the same after reflection.");
        assertEquals(1, reflected.y, "Y velocity should be inverted after reflection.");
    }

    /**
     * Tests the reflection of the ball off a line.
     */
    @Test
    public void testReflectLine_WithReflection() {
        // Set up the ball with initial position and velocity
        ball.setX(5);
        ball.setY(5);
        ball.setVelocity(new PVector(1, 1)); // Moving diagonally
        ball.setRadius(1f);

        PVector point = new PVector(100, 100); // Define a point on the line
        List<PVector> currentLinePoints = new ArrayList<>();
        currentLinePoints.add(point);
        currentLinePoints.add(point); // Line is a point
        Line line = new Line(currentLinePoints); // Create line object

        ball.reflectLine(line); // Reflect the ball off the line

        // Calculate expected position and velocity after reflection
        PVector expectedVelocity = new PVector(1, 1); // Reflecting off the line
        PVector expectedPosition = new PVector(5, 5); // Expected position

        // Assertions
        assertEquals(expectedVelocity.x, ball.velocity.x, 0.001, "velocity x should match");
        assertEquals(expectedVelocity.y, ball.velocity.y, 0.001, "velocity y should match");
        assertEquals(expectedPosition.x, ball.getX(), 0.001, "position x should match");
        assertEquals(expectedPosition.y, ball.getY(), 0.001,"position y should match");
    }

    /**
     * Tests the reflection of the ball off another line.
     */
    @Test
    public void testReflectLine_WithReflection3() {
        // Set up the ball with initial position and velocity
        ball.setX(5);
        ball.setY(5);
        ball.setVelocity(new PVector(1, 1)); // Moving diagonally
        ball.setRadius(1f);

        // Define a line (horizontal for simplicity)
        PVector startPoint = new PVector(0, 10); // Start point of the line
        PVector endPoint = new PVector(100, 10); // End point of the line
        List<PVector> currentLinePoints = new ArrayList<>(); // Correctly named list
        currentLinePoints.add(startPoint);
        currentLinePoints.add(endPoint);

        // Create a line using the list of points
        Line line = new Line(currentLinePoints); // Ensure Line class can accept this list

        // Reflect the ball off the line
        ball.reflectLine(line);

        // Calculate expected position and velocity after reflection
        PVector expectedVelocity = new PVector(1, -1); // Reflecting off a horizontal line
        PVector expectedPosition = new PVector(5, 5); // Adjusting position for penetration depth

        // Assertions
        assertEquals(expectedVelocity.x, ball.getVelocityX(), 0.001, "Velocity x should match");
        assertEquals(expectedVelocity.y, ball.getVelocityY(), 0.001, "Velocity y should match");
        assertEquals(expectedPosition.x, ball.getX(), 0.001, "Position x should match");
        assertEquals(expectedPosition.y, ball.getY(), 0.001, "Position y should match");
    }

    /**
     * Tests adding the ball's color to the queue.
     */
    @Test
    public void testAddToQueueAgain() {
        int size = BallManager.ballQueue.size(); // Get the current size of the queue
        BallManager.ballQueue.add(ball.getColourAsString()); // Add the ball's color to the queue
        assertEquals(size + 1, BallManager.ballQueue.size(), "should be matching");
    }

    /**
     * Tests if the ball is near a hole.
     */
    @Test
    public void testIsNearHole() {
        Hole hole = new Hole(100, 100, 20, '2'); // Create a hole
        boolean result = ball.isNearHole(hole); // Check if the ball is near the hole
        assertTrue(result, "should be near hole");

        Hole hole2 = new Hole(200, 200, 20, '2'); // Create another hole further away
        boolean result2 = ball.isNearHole(hole2); // Check distance to the second hole
        assertFalse(result2, "should not be near hole");
    }

    /**
     * Tests setting the velocity of the ball.
     */
    @Test
    public void testSetVelocity() {
        ball.setVelocityX(10); // Set X velocity
        ball.setVelocityY(15); // Set Y velocity

        assertEquals(10, ball.getVelocityX(), "Ball's X velocity should be set to 10.");
        assertEquals(15, ball.getVelocityY(), "Ball's Y velocity should be set to 15.");

        ball.setVelocity(new PVector(20, 20)); // Set velocity using PVector

        assertEquals(20, ball.getVelocityX(), "Ball's X velocity should be set to 20.");
        assertEquals(20, ball.getVelocityY(), "Ball's Y velocity should be set to 20.");
    }

    /**
     * Tests freezing and unfreezing the ball.
     */
    @Test
    public void testFreezeUnfreeze() {
        ball.freeze(); // Freeze the ball
        assertEquals(0, ball.getVelocityX(), "Ball's X velocity should be 0 when frozen.");
        assertEquals(0, ball.getVelocityY(), "Ball's Y velocity should be 0 when frozen.");

        ball.unfreeze(); // Unfreeze the ball
        assertNotEquals(0, ball.getVelocityX(), "Ball's X velocity should be restored after unfreeze.");
        assertNotEquals(0, ball.getVelocityY(), "Ball's Y velocity should be restored after unfreeze.");
    }

    /**
     * Tests handling collisions with walls.
     */
    @Test
    public void testHandleWallCollision() {
        Wall wall = new Wall(app, 90, 100, 90, 110, '1', imageLoader); // Create a wall
        ball.setX(85); // Set ball's initial position near the wall
        ball.setY(100);

        ball.velocity = new PVector(-10, 0); // Set velocity towards the wall

        ball.handleWallCollision(wall); // Handle collision

        assertTrue(ball.getX() > 20, "Ball should have moved away from the wall after collision.");
    }

    /**
     * Tests adding a line to the lines list.
     */
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

    /**
     * Tests scoring based on capture conditions.
     */
    @Test
    void testGetScoreForCapture() {
        int cellSize = App.CELLSIZE; // Get the cell size
        levelMultiplier = 1; // Set level multiplier
        PImage ballImage = Ball.getBallImage("0", imageLoader); // Load ball image

        Ball ball = new Ball(app, ballImage, 19, 20, 2, 2, 12, boardManager, '0'); // Create a new ball
        PVector position = ball.getPosition(); // Get the ball's position

        // Check that the position matches the expected values
        assertEquals(19, position.x, 0.01, "Expected x-coordinate to match");
        assertEquals(20, position.y, 0.01, "Expected y-coordinate to match");

        assertEquals(19.0f, ball.getX(), 0.01, "Expected x-coordinate to be 3.0");
        assertEquals(20.0f, ball.getY(), 0.01, "Expected y-coordinate to be 4.0");

        Hole hole = new Hole(3, 4, cellSize / 2, '0'); // Set hole position and radius
        PVector positionhole = hole.getPosition(); // Get hole's position

        // Check that the position matches the expected values
        assertEquals(3 + 16, positionhole.x, 0.01, "Expected x-coordinate to match");
        assertEquals(4 + 16, positionhole.y, 0.01, "Expected y-coordinate to match");

        float distance = ball.distanceFromHole(hole); // Calculate distance from the hole
        assertEquals(0.0f, distance, 0.01, "Expected distance 0");
        ball.setRadius(0.7f); // Set ball radius

        float expectedShrinkedRadius = hole.getRadius() * Ball.SHRINK_RATE; // Expected radius after shrinking
        assertEquals(expectedShrinkedRadius, ball.shrinkedHole(hole), 0.01, "Expected shrunk radius to match");

        assertTrue(ball.isCapturedByHole(hole), "Expected to be captured");
        ball.setIsCaptured(true); // Set ball as captured

        // Test case 1: Captured with matching color
        hole = new Hole(10, 10, cellSize / 2, '1'); // Hole with color '1'
        int score = ball.getScoreForCapture(hole, levelMultiplier); // Calculate score

        // Test case 4: Not captured (different colors)
        hole = new Hole(10, 10, cellSize / 2, '2'); // Hole with color '2'
        ball.setColour('1'); // Set ball color to '1'
        ball.setRadius(10.0f); // Set radius
        score = ball.getScoreForCapture(hole, levelMultiplier); // Calculate score
        assertEquals(0, score, "Expected score to be zero for different colors");

        // Test case 5: Hole is null
        hole = null; // No hole
        ball.setColour('1'); // Set any color
        score = ball.getScoreForCapture(hole, levelMultiplier); // Calculate score
        assertEquals(0, score, "Expected score to be zero when hole is null");

        // Test case 6: Level multiplier is null
        hole = new Hole(10, 10, cellSize / 2, '1'); // Create a new hole
        ball.setColour('1'); // Set ball color to '1'
        Integer nullMultiplier = null; // Null multiplier
        score = ball.getScoreForCapture(hole, nullMultiplier); // Calculate score
        assertEquals(0, score, "Expected score to be zero when level multiplier is null");
    }
}
