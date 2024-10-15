package inkball.objects;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;

public class Line {
    public List<PVector> points;

    public Line(List<PVector> points) {
        this.points = points;
    }

   

    public PVector getStart() {
        return points.get(0);
    }

    public PVector getEnd() {
        return points.get(points.size() - 1);
    }

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
