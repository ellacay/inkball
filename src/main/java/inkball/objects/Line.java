package inkball.objects;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;

/**
 * Represents a line defined by a series of points in 2D space.
 * The line can be displayed on a PApplet canvas and provides
 * methods to access its start and end points.
 */
public class Line {
    public List<PVector> points;

    /**
     * Constructs a Line object with a list of points.
     *
     * @param points A list of PVector points that define the line.
     */
    public Line(List<PVector> points) {
        this.points = points;
    }

    /**
     * Returns the starting point of the line.
     *
     * @return A PVector representing the starting point of the line.
     */

    public PVector getStart() {
        return points.get(0);
    }

    /**
     * Returns the ending point of the line.
     *
     * @return A PVector representing the ending point of the line.
     */
    public PVector getEnd() {
        return points.get(points.size() - 1);
    }

    /**
     * Displays the line on the given PApplet canvas.
     * The line is drawn with a specified stroke weight and is not filled.
     *
     * @param applet The PApplet instance used for rendering the line.
     */
    public void display(PApplet applet) {
        applet.strokeWeight(10);

        applet.noFill();
        applet.beginShape();
        for (PVector point : points) {
            applet.vertex(point.x, point.y);
        }
        applet.endShape();
    }
}
