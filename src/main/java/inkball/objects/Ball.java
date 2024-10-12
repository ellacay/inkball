package inkball.objects;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import inkball.App;
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
        checkBoundaries();
        handleLineCollisions();
    }

    private void checkBoundaries() {
        if (isOutOfBound()) {
            setFinished(); // Notify that this ball is finished
        }
    }

    private boolean isOutOfBound() {
        return position.x - radius < BOARD_LEFT || position.x + radius > BOARD_RIGHT ||
               position.y - radius < BOARD_TOP || position.y + radius > BOARD_BOTTOM;
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
                    captured = true;
                    BoardManager.increaseScore(1);
                    setFinished(); // Notify that this ball is finished
                    boardManager.checkIfFinished();
                }
            }
        }
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

        float penetrationX = Math.min((position.x + radius) - wall.x1, wall.x2 - (position.x - radius));
        float penetrationY = Math.min((position.y + radius) - wall.y1, wall.y2 - (position.y - radius));

        if (penetrationX < radius && penetrationY < radius) {
            velocity.x = -velocity.x; 
            velocity.y = -velocity.y;
        } else if (penetrationX < penetrationY) {
            position.x += position.x + radius > wall.x1 ? -(radius + 1) : (radius + 1);
        } else {
            position.y += position.y + radius > wall.y1 ? -(radius + 1) : (radius + 1);
        }

        PVector wallDirection = new PVector(wall.x2 - wall.x1, wall.y2 - wall.y1);
        PVector normal = new PVector(-wallDirection.y, wallDirection.x).normalize();
        velocity = reflect(velocity, normal);
    }

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

        boolean overlapX = (ballRight >= wall.x1 && ballLeft <= wall.x2);
        boolean overlapY = (ballBottom >= wall.y1 && ballTop <= wall.y2);

        return overlapX && overlapY;
    }
}

