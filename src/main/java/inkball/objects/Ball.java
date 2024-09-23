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
    private boolean captured = false; // Flag to indicate if ball is captured
    private BoardManager boardManager;

    private static final float GRAVITY_STRENGTH = 0.5f; // Strength of gravitational pull towards hole
    private static final float SHRINK_RATE = 0.05f; // Rate at which the ball shrinks near the hole

    public Ball(PApplet app, PImage image, float x, float y, float xSpeed, float ySpeed, float radius, BoardManager boardManager) {
        this.app = app;
        this.image = image;
        this.position = new PVector(x, y);
        this.velocity = new PVector(xSpeed, ySpeed);
        this.radius = radius;
        this.boardManager = boardManager; // Initialize the boardManager
    }
    

    public void display() {
        app.image(image, position.x - radius, position.y - radius, radius * 2, radius * 2);
    }
    public void update() {
        position.add(velocity);
        checkWallCollisions();
        gravitateTowardsHole(); // Make sure this is called here
    
        // Debugging prints
        System.out.println("Position: " + position + " Velocity: " + velocity);
    
        for (Line line : App.lines) {
            if (isColliding(line)) {
                reflect(line);
            }
        }
    }
    
    

    // Method to check if the ball is near a hole and gravitate towards it
    private void gravitateTowardsHole() {
        for (Hole hole : BoardManager.holes) {
            if (isNearHole(hole)) {
                // Calculate the direction vector towards the hole
                PVector direction = PVector.sub(hole.getPosition(), position).normalize();
                
                // Apply a force towards the hole, dampening the velocity for smoother capture
                velocity.add(PVector.mult(direction, GRAVITY_STRENGTH));
                
                // Slow down the ball's velocity for a smoother approach
                velocity.mult(0.98f);
                
                // Gradually reduce the radius
                radius = PApplet.max(radius - SHRINK_RATE, 0);
                
                // Check if the ball should be captured
                if (isCapturedByHole(hole)) {
                    captured = true;
                    boardManager.removeBall(this); // Remove the ball once captured
                    System.out.println("Ball captured by hole!"); // Debugging print
                    return; // Exit the method once captured to avoid further checks
                }
            }
        }
    }
    
    
    private boolean isNearHole(Hole hole) {
        // Check if the ball is within a reasonable distance to start gravitating towards the hole
        return PVector.dist(position, hole.getPosition()) < hole.getRadius() * 5; // Adjust this value as needed
    }
    

    private boolean isCapturedByHole(Hole hole) {
        // Check if the ball's center is close enough to the hole's center
        boolean isPositionClose = PVector.dist(position, hole.getPosition()) < hole.getRadius();
        
        // Check if the ball's radius has shrunk sufficiently to be "captured" by the hole
        boolean isSizeSmallEnough = radius <= hole.getRadius() * 0.5f;
        
        // Ball should be captured if both conditions are met
        return isPositionClose && isSizeSmallEnough;
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
