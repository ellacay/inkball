package inkball.objects;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import inkball.loaders.ConfigLoader;
import inkball.loaders.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import inkball.App;
import inkball.managers.BallManager;
import inkball.managers.BoardManager;

public class Ball {
    private PApplet app;
    private PImage image;
    public PVector position;
    public PVector velocity;
    private float radius;
    private char colour;
    private boolean captured = false;
    private BoardManager boardManager;
    public int scoreValue;
    public boolean spawnedAtStart = false;
    public static final float SHRINK_RATE = 0.05f;
    private static final float NEAR_HOLE_DISTANCE = 32;

    public Ball(PApplet app, PImage image, float x, float y, float xSpeed, float ySpeed, float radius,
            BoardManager boardManager, char colour) {
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
     
        applyCollisionLogic();
        gravitateTowardsHole();

        handleLineCollisions();
    }

    public void gravitateTowardsHole() {
        List<Hole> holesCopy = new ArrayList<>(BoardManager.holes);
        Hole closestHole = null;
        float closestDistance = 50;
        for (Hole hole : holesCopy) {
                  
                    PVector toHole = new PVector(hole.getPosition().x - (getX() + radius / 2), hole.getPosition().y - (getY() + radius / 2));
                    float distanceToHole = toHole.mag(); // Get the distance to the center of the hole
                    // Update if this hole is closer
                    if (distanceToHole < closestDistance) {
                        closestDistance = distanceToHole;
                        closestHole = hole;
                    }
                }

    
         
            // float distanceToHole = PVector.dist(position, hole.getPosition());
    
            if (closestHole!=null && closestDistance < 32 + getRadius()) {
                
                // Calculate attraction force and shrink amount
                float attractionForce = 0.005f * closestDistance;
                PVector direction = PVector.sub(closestHole.getPosition(), position).normalize();
                velocity.add(PVector.mult(direction, attractionForce));
    
                radius = radius * (closestDistance / 32);
                radius = Math.max(0, Math.min(12, radius));
        
                // Check if captured by the hole
                if (isCapturedByHole(closestHole) && !captured) {
                    if (!correctBall(closestHole)) {
                        captured = true;
                        BallManager.addToQueueAgain(this);
                        BoardManager.decreaseScore(this);
                     
                    
                    }
                    else{
                        captured = true;
                        BoardManager.increaseScore(this);
                        setFinished();
                        boardManager.checkIfFinished();
                    }
            
               
                }
            } else {
                // If not captured, gradually restore the radius to the original size
                
                    
                    // Calculate how much to increase based on the distance from the hole
                 
                        float increaseAmount = PApplet.map(closestDistance, 32 + getRadius(), 64, 0, 1);
                        radius = PApplet.min(radius + increaseAmount, 12); // Ensure it doesn't exceed the original size
                    
                  
                
            }
        
    }
    
    public String getColour() {
        return Character.toString(this.colour);
    }

    public void setColour(char colour) {
        this.colour = colour;
    }

    public String getColourString() {
        switch ((this.colour)) {
            case '0':
                return "grey";
            case '1':
                return "orange";
            case '2':
                return "blue";
            case '3':
                return "green";
            case '4':
                return "yellow";
            default:
                return null;
        }
    }

    public void setVelocity(PVector newVelocity) {
        this.velocity = newVelocity;
    }

    public boolean correctBall(Hole hole) {
       

        if (this.colour == hole.getColour()) {
            return true;
        } else if (this.colour == '0') {

            return true;
        } else if (hole.getColour() == '0') {
            return true;
        }

        return false;

    }

    public void setFinished() {
        boardManager.addFinishedBall();
  
    }

    public boolean isNearHole(Hole hole) {
        return PVector.dist(position, hole.getPosition())-radius < NEAR_HOLE_DISTANCE;
    }

    public float distanceFromHole(Hole hole) {
        return PVector.dist(new PVector(3, 4), new PVector(3, 4));
    }

    public float shrinkedHole(Hole hole) {
        return hole.getRadius() * SHRINK_RATE;
    }

    public boolean isCapturedByHole(Hole hole) {
        boolean isPositionClose = distanceFromHole(hole) < getRadius();
        boolean isSizeSmallEnough = radius <= hole.getRadius() * SHRINK_RATE;
        return isPositionClose && isSizeSmallEnough;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    private float originalVelocityX;
    private float originalVelocityY;

    public void freeze() {
        this.originalVelocityX = this.velocity.x;
        this.originalVelocityY = this.velocity.y;
        setVelocityX(0);
        setVelocityY(0);
    }

    public void unfreeze() {
        
        setVelocityX(this.originalVelocityX);
        setVelocityY(this.originalVelocityY);

    }

    public void handleLineCollisions() {
        List<Line> linesToRemove = new ArrayList<>();
        for (Line line : App.lines) {
            if (isCollidingWithLinePoints(line)) {
                reflectLine(line);
                linesToRemove.add(line);
            }
        }
        App.lines.removeAll(linesToRemove);
    }

    public void applyCollisionLogic() {
        for (Wall wall : BoardManager.walls) {
            if (checkCollisionWithWall(wall)) {
                handleWallCollision(wall);
            }
        }
    }

    public void handleWallCollision(Wall wall) {

        if (ConfigLoader.extensionFeature) {
            if (this.colour == wall.colour) {
                wall.hit();
            } else if (wall.colour == '0') {
                wall.hit();
            }

        }

        if (this.colour != wall.colour && this.colour != '0' && wall.colour != '0') {
            // Change the ball's color to the wall's color
            this.colour = wall.colour;
    
            // Load the new image based on the new color
            ImageLoader imageLoader = new ImageLoader(app);
            imageLoader.loadImages();
            PImage newImage = BallManager.getBallImage(Character.toString(this.colour), imageLoader);
    
            if (newImage != null) {
                System.out.println("Colour changed to: " + this.colour);
                this.image = newImage;  // Update the ball's image
            } else {
                System.out.println("Image not found for color: " + this.colour);
            }
        }
        float leftEdge = wall.x1;
        float rightEdge = wall.x2;
        float topEdge = wall.y1;
        float bottomEdge = wall.y2;

        float distanceToLeft = (position.x + radius) - leftEdge;
        float distanceToRight = rightEdge - (position.x - radius);
        float distanceToTop = (position.y + radius) - topEdge;
        float distanceToBottom = bottomEdge - (position.y - radius);

        float minXPenetration = Math.min(distanceToLeft, distanceToRight);
        float minYPenetration = Math.min(distanceToTop, distanceToBottom);

        PVector normal = new PVector();

        if (minXPenetration < minYPenetration) {
            if (distanceToLeft < distanceToRight) {
                position.x = leftEdge - radius;
                normal.set(1, 0);
            } else {
                position.x = rightEdge + radius;
                normal.set(-1, 0);
            }
            velocity = reflect(velocity, normal);
        } else {
            if (distanceToTop < distanceToBottom) {
                position.y = topEdge - radius;
                normal.set(0, 1);
            } else {
                position.y = bottomEdge + radius;
                normal.set(0, -1);
            }
            velocity = reflect(velocity, normal);
        }
    }

    public int getScoreForCapture(Hole hole, Integer levelMultiplier) {

        if (hole != null && levelMultiplier != null && isCapturedByHole(hole)) {
            char ballColor = this.colour;
            int holeColor = hole.getColour();

            boolean isColourMatch = ballColor == holeColor;
            boolean isGreyBall = ballColor == '0';
            boolean isGreyHole = holeColor == '0';

            if (isColourMatch || isGreyBall || isGreyHole) {
                return 60;
                // return scoreValue * levelMultiplier;
            }
            if (isColourMatch == false) {
                return 1;
            }
            return 4;

        }
        return 0;
    }

    public boolean isCollidingWithLinePoints(Line line) {
        for (PVector point : line.points) {
            if (PVector.dist(position, point) <= radius) {
                return true; // Collision detected with a point on the line
            }
        }
        return false; // No collision detected
    }
    public PVector reflect(PVector velocity, PVector normal) {
        float dotProduct = PVector.dot(velocity, normal);
        return PVector.sub(velocity, PVector.mult(normal, 2 * dotProduct));
    }

    public void reflectLine(Line line) {
        PVector lineDirection = PVector.sub(line.getEnd(), line.getStart()).normalize();
        PVector normal = new PVector(-lineDirection.y, lineDirection.x).normalize();

        float dotProduct = PVector.dot(velocity, normal);
        velocity.sub(PVector.mult(normal, 2 * dotProduct));

        PVector closestPoint = PVector.add(line.getStart(),
                PVector.mult(lineDirection, PVector.dot(PVector.sub(position, line.getStart()), lineDirection)));
        float penetrationDepth = radius - PVector.dist(position, closestPoint);
        if (penetrationDepth > 0) {
            position.add(PVector.mult(normal, penetrationDepth + 1));
        }
    }

    public boolean checkCollisionWithWall(Wall wall) {
        float ballLeft = position.x - radius;
        float ballRight = position.x + radius;
        float ballTop = position.y - radius;
        float ballBottom = position.y + radius;

        boolean overlapX = (ballRight > wall.x1 && ballLeft < wall.x2);
        boolean overlapY = (ballBottom > wall.y1 && ballTop < wall.y2);

        return overlapX && overlapY;
    }

    public PVector getPosition() {
        return position.copy();
    }

    public float getVelocityY() {
        return velocity.y;
    }

    public float getVelocityX() {
        return velocity.x;
    }

    public void setVelocityY(float newVelocity) {
        velocity.y = newVelocity;
    }

    public void setVelocityX(float newVelocity) {
        velocity.x = newVelocity;
    }

    public float getY() {
        return position.y;
    }

    public float getX() {
        return position.x;
    }

    public void setY(float newY) {
        position.y = newY;
    }

    public void setX(float newX) {
        position.x = newX;
    }

    public float getRadius() {
        return radius;
    }

    public char getCharColour() {
        return colour;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setIsCaptured(boolean isCaptured) {
        this.captured = isCaptured;
    }

}
