package inkball;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkball.objects.Line; // Import the Line class
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

class LineTest extends PApplet {

    private ArrayList<PVector> points; // List to hold points that define the line
    private Line line; // Instance of the Line being tested

    // Variables to capture calls to PApplet methods
    private float strokeWeight; // Variable to track stroke weight
    private boolean noFillCalled = false; // Flag to check if noFill was called
    private boolean beginShapeCalled = false; // Flag to check if beginShape was called
    private boolean endShapeCalled = false; // Flag to check if endShape was called
    private ArrayList<PVector> vertices = new ArrayList<>(); // List to capture vertices
    private App app; // Instance of the application

    /**
     * Sets up the test environment before each test.
     * Initializes the app and prepares the line with points.
     */
    @BeforeEach
    void setUp() {
        app = new App(); // Initialize the main application (replace with actual mock if necessary)
        PApplet.runSketch(new String[] { "inkball.App" }, app); // Run the PApplet sketch
        points = new ArrayList<>(); // Initialize the points list
        points.add(new PVector(1, 2)); // Add first point
        points.add(new PVector(3, 4)); // Add second point
        line = new Line(points); // Initialize the Line instance with the points
    }

    /**
     * Captures the stroke weight set for the line.
     */
    @Override
    public void strokeWeight(float weight) {
        this.strokeWeight = weight; // Store the stroke weight
    }

    /**
     * Tests the display functionality of the Line.
     * Asserts that all graphical properties and vertex points are correctly set.
     */
    @Test
    void testDisplay() {
        // Call the display method of the Line
        line.display(this);

        // Verify that the stroke weight was set correctly
        assertEquals(10, strokeWeight, "Expected stroke weight to be 10");
        assertTrue(noFillCalled, "Expected noFill to be called");
        assertTrue(beginShapeCalled, "Expected beginShape to be called");
        assertTrue(endShapeCalled, "Expected endShape to be called");

        // Verify that vertex was called with the correct points
        assertEquals(2, vertices.size(), "Expected 2 vertices to be added");
        assertEquals(new PVector(1, 2), vertices.get(0), "Expected first vertex to match");
        assertEquals(new PVector(3, 4), vertices.get(1), "Expected second vertex to match");
    }

    /**
     * Captures the call to noFill method for the PApplet.
     */
    @Override
    public void noFill() {
        this.noFillCalled = true; // Mark that noFill was called
    }

    /**
     * Captures the call to beginShape method for the PApplet.
     */
    @Override
    public void beginShape() {
        this.beginShapeCalled = true; // Mark that beginShape was called
    }

    /**
     * Captures the vertex points added during the shape display.
     * @param x The x-coordinate of the vertex
     * @param y The y-coordinate of the vertex
     */
    @Override
    public void vertex(float x, float y) {
        vertices.add(new PVector(x, y)); // Add the vertex to the list
    }

    /**
     * Captures the call to endShape method for the PApplet.
     */
    @Override
    public void endShape() {
        this.endShapeCalled = true; // Mark that endShape was called
    }


}
