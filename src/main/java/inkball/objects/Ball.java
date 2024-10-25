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

public class Ball extends Object {
    private PApplet app;
    private PImage image;
    public PVector velocity;
    private boolean captured = false;
    private BoardManager boardManager;
    public int scoreValue;
    public boolean spawnedAtStart = false;
    public static final float SHRINK_RATE = 0.05f;
    private static final float NEAR_HOLE_DISTANCE = 32;
    private float originalVelocityX;
    private float originalVelocityY;

    /**
     * Represents a ball in the inkball game. The ball can move, collide with walls,
     * and interact with holes. It can also change colors based on interactions
     * with walls and track its state (e.g., captured or spawned).
     */

    public Ball(PApplet app, PImage image, float x, float y, float xSpeed, float ySpeed, float radius,
            BoardManager boardManager, char colour) {

        super(x, y, colour, radius);
        this.app = app;
        this.image = image;
        this.velocity = new PVector(xSpeed, ySpeed);
        this.boardManager = boardManager;

    }

    /**
     * Displays the ball on the screen.
     */

    public void display() {
        app.image(image, position.x - radius, position.y - radius, radius * 2, radius * 2);
    }

    /**
     * Updates the ball's position and handles its interactions with the game world.
     */
    public void update() {

        applyCollisionLogic();
        gravitateTowardsHole();

        handleLineCollisions();
    }

    /**
     * Makes the ball gravitate towards the nearest hole.
     */
    public void gravitateTowardsHole() {
        List<Hole> holesCopy = new ArrayList<>(BoardManager.holes);
        Hole closestHole = null;
        float closestDistance = 50;
        for (Hole hole : holesCopy) {

            PVector toHole = new PVector(hole.getPosition().x - (getX() + radius / 2),
                    hole.getPosition().y - (getY() + radius / 2));
            float distanceToHole = toHole.mag(); // Get the distance to the center of the hole
            // Update if this hole is closer
            if (distanceToHole < closestDistance) {
                closestDistance = distanceToHole;
                closestHole = hole;
            }
        }

        // float distanceToHole = PVector.dist(position, hole.getPosition());

        if (closestHole != null && closestDistance < 32 + getRadius()) {

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

                } else {
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

    /**
     * Sets the ball's velocity.
     *
     * @param newVelocity The new velocity vector for the ball.
     */
    public void setVelocity(PVector newVelocity) {
        this.velocity = newVelocity;
    }

    /**
     * Determines if the ball is correctly captured by a hole based on color
     * matching.
     *
     * @param hole The hole being checked.
     * @return True if the ball is correctly captured, false otherwise.
     */
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

    /**
     * Checks if the ball is near a specified hole.
     *
     * @param hole The hole to check against.
     * @return True if the ball is near the hole, false otherwise.
     */
    public boolean isNearHole(Hole hole) {
        return PVector.dist(position, hole.getPosition()) - radius < NEAR_HOLE_DISTANCE;
    }

    /**
     * Calculates the distance from the ball's current position to the center of the
     * specified hole.
     * This method utilizes a fixed point (3, 4) for distance calculation, which
     * should be updated
     * to use the actual position of the ball and the hole for accurate results.
     *
     * @param hole The hole to which the distance is being calculated.
     * @return The distance between the ball's position and the center of the hole.
     */
    public float distanceFromHole(Hole hole) {
        return PVector.dist(new PVector(3, 4), new PVector(3, 4));
    }

    /**
     * Calculates the effective radius of the specified hole after applying the
     * shrink rate.
     * This method is used to determine how much the hole is effectively shrunk,
     * which can impact ball capture logic.
     *
     * @param hole The hole for which the shrunk radius is being calculated.
     * @return The shrunk radius of the hole, computed by multiplying the hole's
     *         original radius
     *         by the predefined SHRINK_RATE.
     */
    public float shrinkedHole(Hole hole) {
        return hole.getRadius() * SHRINK_RATE;
    }

    /**
     * Determines whether the ball is captured by the specified hole.
     * A ball is considered captured if it is within a certain distance from the
     * hole and
     * its radius is small enough compared to the effective radius of the hole.
     *
     * @param hole The hole being checked for capturing the ball.
     * @return true if the ball is captured by the hole; false otherwise.
     */
    public boolean isCapturedByHole(Hole hole) {
        boolean isPositionClose = distanceFromHole(hole) < getRadius();
        boolean isSizeSmallEnough = radius <= hole.getRadius() * SHRINK_RATE;
        return isPositionClose && isSizeSmallEnough;
    }

    /**
     * Freezes the ball's movement by setting its velocity to zero.
     * This method stores the current velocity before freezing, allowing the ball to
     * be
     * unfrozen later and resume its previous velocity.
     */
    public void freeze() {
        this.originalVelocityX = this.velocity.x;
        this.originalVelocityY = this.velocity.y;
        setVelocityX(0);
        setVelocityY(0);
    }

    /**
     * Unfreezes the ball, restoring its original velocity.
     * This method is called to resume the ball's movement after it has been frozen.
     */
    public void unfreeze() {

        setVelocityX(this.originalVelocityX);
        setVelocityY(this.originalVelocityY);

    }

    /**
     * Handles collisions between the ball and lines. If the ball collides with any
     * points on the lines, it reflects off the line and adds the line to a list for
     * removal.
     * Once all collisions are processed, the lines that were hit are removed from
     * the scene.
     */
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

    /**
     * Applies collision logic for the ball against all walls in the game.
     * This method checks for collisions and handles them accordingly,
     * ensuring that the ball reacts appropriately when it collides with walls.
     */
    public void applyCollisionLogic() {
        for (Wall wall : BoardManager.walls) {
            if (checkCollisionWithWall(wall)) {
                handleWallCollision(wall);
            }
        }
    }

    /**
     * Handles the collision between the ball and a specified wall.
     * This method checks the color of the ball and wall, applies any necessary
     * effects (like calling the wall's hit method), and reflects the ball's
     * velocity based on the wall's position. It also updates the ball's color
     * and image if they are different from the wall's color.
     *
     * @param wall The wall that the ball is colliding with.
     */
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
            PImage newImage = getBallImage(Character.toString(this.colour), imageLoader);

            if (newImage != null) {
                System.out.println("Colour changed to: " + this.colour);
                this.image = newImage; // Update the ball's image
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

    /**
     * Calculates the score for capturing a ball by a specified hole,
     * taking into account the color of both the ball and the hole, as well
     * as a level multiplier. The score is computed based on matching
     * colors or the presence of special 'grey' colors.
     *
     * @param hole            The hole that is capturing the ball.
     * @param levelMultiplier A multiplier that affects the score value.
     * @return The calculated score if the ball is captured; 0 otherwise.
     */
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

    /**
     * Checks if the ball is colliding with any points of the specified line.
     * This method iterates over each point in the line and checks if the distance
     * from the ball's position to any point is less than or equal to the ball's
     * radius.
     *
     * @param line The line to check for collisions with the ball.
     * @return true if the ball is colliding with any point on the line; false
     *         otherwise.
     */
    public boolean isCollidingWithLinePoints(Line line) {
        for (PVector point : line.points) {
            if (PVector.dist(position, point) <= radius) {
                return true;
            }
        }
        return false;
    }

    public PVector reflect(PVector velocity, PVector normal) {
        float dotProduct = PVector.dot(velocity, normal);
        return PVector.sub(velocity, PVector.mult(normal, 2 * dotProduct));
    }

    /**
     * Reflects the ball's velocity upon colliding with a specified line.
     * This method calculates the normal of the line at the point of collision,
     * updates the ball's velocity based on the reflection formula, and adjusts
     * the ball's position to prevent it from overlapping the line.
     *
     * @param line The line that the ball is colliding with.
     *             This line is used to determine the reflection angle.
     */
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

    /**
     * Checks if the ball is colliding with a specified wall.
     * This method determines if the ball's bounding box overlaps with the
     * bounding box of the wall, indicating a collision.
     *
     * @param wall The wall to check for collision with the ball.
     * @return true if the ball is colliding with the wall; false otherwise.
     */
    public boolean checkCollisionWithWall(Wall wall) {
        float ballLeft = position.x - radius;
        float ballRight = position.x + radius;
        float ballTop = position.y - radius;
        float ballBottom = position.y + radius;

        boolean overlapX = (ballRight > wall.x1 && ballLeft < wall.x2);
        boolean overlapY = (ballBottom > wall.y1 && ballTop < wall.y2);

        return overlapX && overlapY;
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

    public boolean isCaptured() {
        return captured;
    }

    public void setIsCaptured(boolean isCaptured) {
        this.captured = isCaptured;
    }

    /**
     * Updates the position of the ball based on its current velocity.
     * This method calculates the next position of the ball and checks for
     * collisions
     * with the boundaries of the game area. If the ball is about to move out of
     * bounds,
     * its velocity is reversed to simulate a bounce effect.
     *
     * @param ball The ball instance whose position is to be updated.
     *             This parameter is included for clarity but is not used within
     *             the method itself, as it operates on the current object's
     *             position and velocity.
     */

    public void updateBallPosition(Ball ball) {
        float nextX = getX() + getVelocityX();
        float nextY = getY() + getVelocityY();

        float leftBoundary = 0;
        float rightBoundary = BoardManager.board[0].length * App.CELLSIZE;
        float topBoundary = App.TOPBAR;
        float bottomBoundary = BoardManager.board.length * App.CELLSIZE + App.TOPBAR;

        if (nextX <= leftBoundary || nextX >= rightBoundary - getRadius()) {
            setVelocityX(-getVelocityX());
        }
        if (nextY <= topBoundary || nextY >= bottomBoundary - getRadius()) {
            setVelocityY(-getVelocityY());
        }
        setX(nextX);
        setY(nextY);
    }

    /**
     * Gets the image of the ball based on its color.
     *
     * @param color       The color of the ball as a string.
     * @param imageLoader The image loader instance used to retrieve the ball's
     *                    image.
     * @return The PImage associated with the specified color, or null if not found.
     */
    public static PImage getBallImage(String color, ImageLoader imageLoader) {

        switch (color) {
            case "blue":
                return imageLoader.ball0;

            case "orange":
                return imageLoader.ball1;
            case "grey":
                return imageLoader.ball2;
            case "green":
                return imageLoader.ball3;
            case "yellow":
                return imageLoader.ball4;
            case "0":
                return imageLoader.ball0;
            case "1":
                return imageLoader.ball1;
            case "2":
                return imageLoader.ball2;
            case "3":
                return imageLoader.ball3;
            case "4":
                return imageLoader.ball4;
            default:
                return null;
        }
    }
}
