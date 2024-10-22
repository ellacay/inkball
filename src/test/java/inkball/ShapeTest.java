package inkball;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkball.objects.Line;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

class ShapeTest extends PApplet {

    private ArrayList<PVector> points;
    private Line shape; // Replace with your actual class name

    // Variables to capture calls
    private float strokeWeight;
    private boolean noFillCalled = false;
    private boolean beginShapeCalled = false;
    private boolean endShapeCalled = false;
    private ArrayList<PVector> vertices = new ArrayList<>();
    private App app;
    @BeforeEach
    void setUp() {
              app = new App(); // Replace with actual mock implementation
        PApplet.runSketch(new String[] { "inkball.App" }, app);
        points = new ArrayList<>();
        points.add(new PVector(1, 2));
        points.add(new PVector(3, 4));
        shape = new Line(points); // Initialize your shape with points
    }
    @Override
    public void strokeWeight(float weight) {
        this.strokeWeight = weight;
    }

    @Test
    void testDisplay() {
        // Call the display method
  
        shape.display(this);

        // Verify that strokeWeight was set correctly
        assertEquals(10, strokeWeight, "Expected stroke weight to be 10");
        assertTrue(noFillCalled, "Expected noFill to be called");
        assertTrue(beginShapeCalled, "Expected beginShape to be called");
        assertTrue(endShapeCalled, "Expected endShape to be called");
        
        // Verify that vertex was called with the correct points
        assertEquals(2, vertices.size(), "Expected 2 vertices to be added");
        assertEquals(new PVector(1, 2), vertices.get(0), "Expected first vertex to match");
        assertEquals(new PVector(3, 4), vertices.get(1), "Expected second vertex to match");
    }

 

    @Override
    public void noFill() {
        this.noFillCalled = true;
    }

    @Override
    public void beginShape() {
        this.beginShapeCalled = true;
    }

    @Override
    public void vertex(float x, float y) {
        vertices.add(new PVector(x, y));
    }

    @Override
    public void endShape() {
        this.endShapeCalled = true;
    }

    // Add main method to run the test if needed
    public static void main(String[] args) {
        PApplet.main("ShapeTest");
    }
}
