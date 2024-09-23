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

    public void update() {
        position.add(velocity);
        checkWallCollisions();
        
        // Check for collision with lines
        for (Line line : App.lines) {
            if (isColliding(line)) {
                reflect(line);
            }
        }
    }

    public void display() {
        app.image(image, position.x - radius, position.y - radius, radius * 2, radius * 2);
    }
    
    private void checkWallCollisions() {
        for (Wall wall : BoardManager.walls) {
            if (checkCollisionWithWall(wall)) {
                // Reflect the ball on collision
                // Calculate how to move the ball out of the wall
                if (wall.isVertical()) {
                    velocity.x = -velocity.x; // Reverse horizontal direction
                    if (position.x < wall.x1) {
                        position.x = wall.x1 + radius; // Move the ball out of the wall
                    } else {
                        position.x = wall.x2 - radius; // Move the ball out of the wall
                    }
                } else if (wall.isHorizontal()) {
                    velocity.y = -velocity.y; // Reverse vertical direction
                    if (position.y < wall.y1) {
                        position.y = wall.y1 + radius; // Move the ball out of the wall
                    } else {
                        position.y = wall.y2 - radius; // Move the ball out of the wall
                    }
                }
            }
        }
    }
    private boolean isColliding(Line line) {
        PVector ballNextPosition = PVector.add(position, velocity);
        PVector P1 = line.getStart();
        PVector P2 = line.getEnd();
        
        float distanceP1 = PVector.dist(P1, ballNextPosition);
        float distanceP2 = PVector.dist(P2, ballNextPosition);
        float distanceLine = PVector.dist(P1, P2);
        
        return (distanceP1 + distanceP2 < distanceLine + radius);
    }
    private void reflect(Line line) {
        PVector P1 = line.getStart();
        PVector P2 = line.getEnd();
    
        PVector lineDirection = PVector.sub(P2, P1);
        PVector normal = new PVector(-lineDirection.y, lineDirection.x).normalize();
    
        // Reflect the velocity vector
        float dotProduct = PVector.dot(velocity, normal);
        velocity.sub(PVector.mult(normal, 2 * dotProduct));
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
