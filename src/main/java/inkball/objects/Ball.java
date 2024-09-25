package inkball.objects;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import inkball.App;
import inkball.loaders.ConfigLoader;
import inkball.managers.BoardManager;

public class Ball {
    private PApplet app;
    private PImage image;
    private PVector position; // Use PVector for position
    private PVector velocity; // Use PVector for velocity
    private float radius;
    private char colour;
    private boolean captured = false; // Flag to indicate if ball is captured
    private BoardManager boardManager;
    private int scoreValue;

  
    private static final float GRAVITY_STRENGTH = 0.5f; // Strength of gravitational pull towards hole
    private static final float SHRINK_RATE = 0.05f; // Rate at which the ball shrinks near the hole

    public Ball(PApplet app, PImage image, float x, float y, float xSpeed, float ySpeed, float radius, BoardManager boardManager, char colour) {
        this.app = app;
        this.colour = colour;
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
      gravitateTowardsHole();
        

        // gravitateTowardsHole(); // Make sure this is called here
    

    
        for (Line line : App.lines) {
            if (isColliding(line)) {
                reflect(line);
            }
        }
    }
    
    public int getScoreForCapture(Hole hole, Integer levelMultiplier) {
        // Check if the ball is captured by the hole
        if (hole!=null && levelMultiplier!=null) {
        
        
        if (isCapturedByHole(hole)) {
            char ballColor = this.colour; // Assuming this.colour is a String
            int holeColor = hole.getColour(); // Assuming hole.getColour() returns a String
    
            boolean isColourMatch = ballColor == holeColor; // Check color match
            boolean isGreyBall = ballColor=='0'; // Check if the ball is grey
            boolean isGreyHole = holeColor=='0'; // Check if the hole is grey
    
            // Calculate the score based on the conditions
            if (isColourMatch || isGreyBall || isGreyHole) {
                return scoreValue * levelMultiplier; // Score based on configuration
            }
        }
            // Your logic here
        }
        return 0; // No score if not captured or color does not match
    }

    // Method to check if the ball is near a hole and gravitate towards it
    private void gravitateTowardsHole() {
        for (Hole hole : BoardManager.holes) {
            if(isNearHole(hole)){
                // Define a very small threshold to determine when to stop the ball
        float stoppingThreshold = 0.1f;  // Adjust this as needed (smaller value for more precision)
    
        // Calculate the direction vector towards the hole
        PVector direction = PVector.sub(hole.getPosition(), position).normalize();
        
        // Apply a force towards the hole, making the ball move directly to the center
        velocity = direction.mult(2); // Adjust the speed as necessary
    
        // Check the distance between the ball and the center of the hole
        float distanceToHole = PVector.dist(position, hole.getPosition());
    
        // If the ball is very close to the center of the hole, stop it
        if (distanceToHole < stoppingThreshold) {
            velocity = new PVector(0, 0);  // Stop the ball completely
            position = hole.getPosition().copy();  // Snap the ball to the exact center of the hole
           
        }
    
        // Gradually reduce the radius for a visual effect as the ball gets closer
        radius = PApplet.max(radius - SHRINK_RATE, 0);
    
        // Check if the ball should be captured by the hole
        if (isCapturedByHole(hole)) {
            captured = true;
            boardManager.removeBall(this);
            
            // int score = getScoreForCapture(hole, ConfigLoader.scoreMultiplier.get("grey"));
            // boardManager.increaseScore(score); // Update score
        }
            }
        }
    }
    
    
    private boolean isNearHole(Hole hole) {
        // Check if the ball is within a reasonable distance to start gravitating towards the hole
        boolean isNear = PVector.dist(position, hole.getPosition()) < 32;
        return isNear;
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
        boolean collided = false;
    
        for (Wall wall : BoardManager.walls) {
            if (checkCollisionWithWall(wall)) {
                collided = true;
    
                // Calculate penetration depths
                float penetrationX = Math.min((position.x + radius) - wall.x1, wall.x2 - (position.x - radius));
                float penetrationY = Math.min((position.y + radius) - wall.y1, wall.y2 - (position.y - radius));
                
    
                boolean isCornerCollision = (penetrationX < radius && penetrationY < radius);

                if (isCornerCollision) {
                    // this.velocity = new PVector(0,0);
                    // position.x = wall.x1 - radius;
                    // position.y = wall.y1 - radius;
                    velocity.x = -velocity.x; 
                    velocity.y = -velocity.y;
                }

                else if (penetrationX < penetrationY) {
                    // Horizontal collision
                    if (position.x + radius > wall.x1 && position.x - radius < wall.x1) {
                        // Colliding from the left
                        position.x = wall.x1 - radius; // Move ball out of the wall
                    } else if (position.x - radius < wall.x2 && position.x + radius > wall.x2) {
                        // Colliding from the right
                        position.x = wall.x2 + radius; // Move ball out of the wall
                    }
                    velocity.x = -velocity.x; // Reverse horizontal direction
                } else {
                    // Vertical collision
                    if (position.y + radius > wall.y1 && position.y - radius < wall.y1) {
                        // Colliding from the top
                        position.y = wall.y1 - radius; // Move ball out of the wall
                    } else if (position.y - radius < wall.y2 && position.y + radius > wall.y2) {
                        // Colliding from the bottom
                        position.y = wall.y2 + radius; // Move ball out of the wall
                    }
                    velocity.y = -velocity.y; // Reverse vertical direction
                }
            }
        }
    }
    
    
    
    private void moveToCenter() {
        // Assuming the board's center is at (centerX, centerY)
        float centerX = App.WIDTH / 2; // Replace with your board's actual width
        float centerY = App.HEIGHT / 2; // Replace with your board's actual height
        position.set(centerX, centerY);
        velocity.set(0, 0); // Optionally reset the velocity
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
