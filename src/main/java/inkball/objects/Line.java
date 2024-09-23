package inkball.objects;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;

public class Line {
    private List<PVector> points;

    public Line(List<PVector> points) {
        this.points = points;
    }

    public void display(PApplet applet) {
        applet.noFill();
        applet.beginShape();
        for (PVector point : points) {
            applet.vertex(point.x, point.y);
        }
        applet.endShape();
    }
}
