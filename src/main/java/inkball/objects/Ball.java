package inkball.objects;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import inkball.App;
import inkball.managers.BoardManager;

public class Ball {
    private PApplet app;
    private PImage image;
    private PVector position; // Use PVector for position
    private PVector velocity; // Use PVector for velocity
    private float radius;

    public Ball(PApplet app, PImage image, float x, float y, float xSpeed, float ySpeed, float radius) {
        this.app = app;
        this.image = image;
        this.position = new PVector(x, y);
        this.velocity = new PVector(xSpeed, ySpeed);
        this.radius = radius;
    }

    public void display() {
        app.image(image, position.x - radius, position.y - radius, radius * 2, radius * 2);
    }

    public void update() {
        position.add(velocity);
        checkWallCollisions();
    
        // Debugging prints
        System.out.println("Position: " + position + " Velocity: " + velocity);
    
        for (Line line : App.lines) {
            if (isColliding(line)) {
                reflect(line);
            }
        }
    }

    private void checkWallCollisions() {
        for (Wall wall : BoardManager.walls) {
            if (checkCollisionWithWall(wall)) {
                // Reflect the ball on collision
                if (wall.isVertical()) {
                    // Check if ball is moving towards the wall before reflecting
                    if ((velocity.x > 0 && position.x >= wall.x1) || (velocity.x < 0 && position.x <= wall.x2)) {
                        velocity.x = -velocity.x; // Reverse horizontal direction
                    }
                    // Move the ball out of the wall
                    position.x = (velocity.x > 0) ? wall.x2 + radius : wall.x1 - radius;
                } else if (wall.isHorizontal()) {
                    // Check if ball is moving towards the wall before reflecting
                    if ((velocity.y > 0 && position.y >= wall.y1) || (velocity.y < 0 && position.y <= wall.y2)) {
                        velocity.y = -velocity.y; // Reverse vertical direction
                    }
                    // Move the ball out of the wall
                    position.y = (velocity.y > 0) ? wall.y2 + radius : wall.y1 - radius;
                }
            }
        }
    }
    
    
    private boolean isColliding(Line line) {
        PVector lineVector = PVector.sub(line.getEnd(), line.getStart());
        PVector ballToLineStart = PVector.sub(position, line.getStart());
        
        float t = PVector.dot(ballToLineStart, lineVector) / lineVector.magSq();
        t = PApplet.constrain(t, 0, 1);
        
        PVector closestPoint = PVector.add(line.getStart(), PVector.mult(lineVector, t));
        float distance = PVector.dist(position, closestPoint);
        
        return distance <= radius;
    }
    
    private void reflect(Line line) {
        PVector lineDirection = PVector.sub(line.getEnd(), line.getStart()).normalize();
        PVector normal = new PVector(-lineDirection.y, lineDirection.x).normalize();
    
        float dotProduct = PVector.dot(velocity, normal);
        velocity.sub(PVector.mult(normal, 2 * dotProduct));
    
        // Adjust position to prevent sticking
        PVector closestPoint = PVector.add(line.getStart(), PVector.mult(lineDirection, PVector.dot(PVector.sub(position, line.getStart()), lineDirection)));
        float penetrationDepth = radius - PVector.dist(position, closestPoint);
        if (penetrationDepth > 0) {
            position.add(PVector.mult(normal, penetrationDepth + 1)); // Move ball slightly more to avoid sticking
        }
    }
    
    private boolean checkCollisionWithWall(Wall wall) {
        float ballLeft = position.x - radius;
        float ballRight = position.x + radius;
        float ballTop = position.y - radius;
        float ballBottom = position.y + radius;

        float wallLeft = wall.x1;
        float wallRight = wall.x2;
        float wallTop = wall.y1;
        float wallBottom = wall.y2;

        boolean overlapX = (ballRight >= wallLeft && ballLeft <= wallRight);
        boolean overlapY = (ballBottom >= wallTop && ballTop <= wallBottom);

        return overlapX && overlapY;
    }
}
