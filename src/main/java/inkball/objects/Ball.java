package inkball.objects;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import inkball.loaders.ImageLoader;

import java.applet.Applet;
import java.util.ArrayList;
import java.util.List;

import inkball.App;
import inkball.loaders.ImageLoader;
import inkball.managers.BallManager;
import inkball.managers.BoardManager;

public class Ball {
    private PApplet app;
    private PImage image;
    private PVector position;
    private PVector velocity;
    private float radius;
    private char colour;
    private boolean captured = false;
    private BoardManager boardManager;
    private int scoreValue;

    private static final float GRAVITY_STRENGTH = 0.5f;
    private static final float SHRINK_RATE = 0.05f;
    private static final float STOPPING_THRESHOLD = 0.1f;
    private static final float NEAR_HOLE_DISTANCE = 32;
    private static final float BOARD_LEFT = 0;
    private static final float BOARD_RIGHT = 800; // Example width
    private static final float BOARD_TOP = 0;
    private static final float BOARD_BOTTOM = 600; // Example height

    public Ball(PApplet app, PImage image, float x, float y, float xSpeed, float ySpeed, float radius, BoardManager boardManager, char colour) {
        this.app = app;
        this.colour = colour;
        this.image = image;
        this.position = new PVector(x, y);
        this.velocity = new PVector(xSpeed, ySpeed);
        this.radius = radius;
        this.boardManager = boardManager;
    }

    public void display() {
        app.image(image, position.x - radius, position.y - radius, radius * 2, radius * 2);
    }

    public void update() {
        position.add(velocity);
        applyCollisionLogic();
        gravitateTowardsHole();
  
        handleLineCollisions();
    }


    private void gravitateTowardsHole() {
        for (Hole hole : BoardManager.holes) {
            if (isNearHole(hole)) {
                float distanceToHole = PVector.dist(position, hole.getPosition());
                PVector direction = PVector.sub(hole.getPosition(), position).normalize();
                velocity = direction.mult(2); // Move towards the hole

                float shrinkAmount = PApplet.map(distanceToHole, 0, hole.getRadius(), SHRINK_RATE, 1);
                radius = PApplet.max(radius - shrinkAmount, 0);

                position.add(velocity);

                if (distanceToHole < STOPPING_THRESHOLD) {
                    position = hole.getPosition().copy(); // Snap to hole's center
                    velocity.set(0, 0); // Stop the ball
                }

                if (isCapturedByHole(hole) && !captured) {

                    if(!correctBall(hole)){
                        
                        BallManager.addToQueueAgain(this);
                       
                    }
                    captured = true;
                    BoardManager.increaseScore(1);
                    setFinished(); // Notify that this ball is finished
                    boardManager.checkIfFinished();
                }
            }
        }
    }

    public String getColour(){
        return Character.toString(this.colour);
    }

    private boolean correctBall(Hole hole){
      
        if(this.colour == hole.getColour()){
            return true;
        }
        else if(this.colour == '0'){
   
            return true;
        }
        else if(hole.getColour()=='0'){
            return true;
        }
        return false;

    }

    private void setFinished() {
        boardManager.addFinishedBall(); // Notify the board manager that this ball is finished
        boardManager.removeBall(this); // Remove the ball from play
    }

    private boolean isNearHole(Hole hole) {
        return PVector.dist(position, hole.getPosition()) < NEAR_HOLE_DISTANCE;
    }

    private boolean isCapturedByHole(Hole hole) {
        boolean isPositionClose = PVector.dist(position, hole.getPosition()) < hole.getRadius();
        boolean isSizeSmallEnough = radius <= hole.getRadius() * 0.5f;
        return isPositionClose && isSizeSmallEnough;
    }

    private void handleLineCollisions() {
        List<Line> linesToRemove = new ArrayList<>();
        for (Line line : App.lines) {
            if (isColliding(line)) {
                reflect(line);
                linesToRemove.add(line); // Mark for removal
            }
        }
        App.lines.removeAll(linesToRemove); // Remove marked lines
    }

    private void applyCollisionLogic() {
        for (Wall wall : BoardManager.walls) {
            if (checkCollisionWithWall(wall)) {
                handleWallCollision(wall);
            }
        }
    }

    private void handleWallCollision(Wall wall) {
        wall.hit();
    

        // Calculate the distance to the edges of the wall
        float leftEdge = wall.x1;
        float rightEdge = wall.x2;
        float topEdge = wall.y1;
        float bottomEdge = wall.y2;
    
        // Calculate distances to each edge
        float distanceToLeft = (position.x + radius) - leftEdge;   // Right side of ball to left edge of wall
        float distanceToRight = rightEdge - (position.x - radius); // Left side of ball to right edge of wall
        float distanceToTop = (position.y + radius) - topEdge;     // Bottom side of ball to top edge of wall
        float distanceToBottom = bottomEdge - (position.y - radius); // Top side of ball to bottom edge of wall
    
        // Determine the smallest penetration
        float minXPenetration = Math.min(distanceToLeft, distanceToRight);
        float minYPenetration = Math.min(distanceToTop, distanceToBottom);
    
        PVector normal = new PVector(); // Normal vector for reflection
    
        if (minXPenetration < minYPenetration) {
            // Horizontal Collision
            if (distanceToLeft < distanceToRight) {
                // Collided with left side

                position.x = leftEdge - radius; // Push the ball out
                normal.set(1, 0); // Normal pointing right
            } else {
                // Collided with right side
          
                position.x = rightEdge + radius; // Push the ball out
                normal.set(-1, 0); // Normal pointing left
            }
            velocity = reflect(velocity, normal); // Reflect the velocity
        } else {
            // Vertical Collision
            if (distanceToTop < distanceToBottom) {
                // Collided with top side

                position.y = topEdge - radius; // Push the ball out
                normal.set(0, 1); // Normal pointing down
            } else {
                // Collided with bottom side
    
                position.y = bottomEdge + radius; // Push the ball out
                normal.set(0, -1); // Normal pointing up
            }
            velocity = reflect(velocity, normal); // Reflect the velocity
        }
        

    }
    
        // Stop the ball if it is no longer colliding with the wall
       
           
        
    
    
    
        // PVector wallDirection = new PVector(wall.x2 - wall.x1, wall.y2 - wall.y1);
        // PVector normal = new PVector(-wallDirection.y, wallDirection.x).normalize();
        // velocity = reflect(velocity, normal);
    

    public int getScoreForCapture(Hole hole, Integer levelMultiplier) {
        if (hole != null && levelMultiplier != null && isCapturedByHole(hole)) {
            char ballColor = this.colour;
            int holeColor = hole.getColour();

            boolean isColourMatch = ballColor == holeColor;
            boolean isGreyBall = ballColor == '0';
            boolean isGreyHole = holeColor == '0';

            if (isColourMatch || isGreyBall || isGreyHole) {
                return scoreValue * levelMultiplier;
            }
        }
        return 0;
    }

    private PVector reflect(PVector velocity, PVector normal) {
        float dotProduct = PVector.dot(velocity, normal);
        return PVector.sub(velocity, PVector.mult(normal, 2 * dotProduct));
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

        PVector closestPoint = PVector.add(line.getStart(), PVector.mult(lineDirection, PVector.dot(PVector.sub(position, line.getStart()), lineDirection)));
        float penetrationDepth = radius - PVector.dist(position, closestPoint);
        if (penetrationDepth > 0) {
            position.add(PVector.mult(normal, penetrationDepth + 1));
        }
    }

    private boolean checkCollisionWithWall(Wall wall) {
        float ballLeft = position.x - radius;
        float ballRight = position.x + radius;
        float ballTop = position.y - radius;
        float ballBottom = position.y + radius;

        boolean overlapX = (ballRight > wall.x1 && ballLeft < wall.x2);
        boolean overlapY = (ballBottom > wall.y1 && ballTop < wall.y2);
        
        return overlapX && overlapY;
    }
}

