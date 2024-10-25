package inkball.managers;

import inkball.objects.Ball;
import inkball.objects.Spawner;
import inkball.App;
import inkball.loaders.ImageLoader;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the spawning, updating, and rendering of balls in the game.
 * This class handles ball creation, maintains their states, and manages their
 * display.
 */
public class BallManager {
    private static App app;
    private static ImageLoader imageLoader;
    public static List<Ball> ballsInPlay = new ArrayList<>();
    public static List<String> ballQueue;
    public static boolean hasSpawnedBall = false;
    public static boolean initalised = false;
    public static boolean ballRemoved = false;
    private static List<Float> ballPositions = new ArrayList<>();
    private static final float MOVE_SPEED = 0.1f;

    /**
     * Constructs a BallManager with references to the application and image loader.
     *
     * @param appRef    The application instance.
     * @param imgLoader The image loader used to fetch ball images.
     */
    public BallManager(App appRef, ImageLoader imgLoader) {
        app = appRef;
        imageLoader = imgLoader;
    }

    /**
     * Initializes the queue of balls to be spawned based on the initial ball colors
     * defined in the application.
     * This method converts color names to their corresponding identifiers and
     * populates the ball queue.
     */
    public void initializeBallQueue() {
        ballQueue = new LinkedList<>();

        if (App.initalBalls != null && !initalised) {
            for (String ball : App.initalBalls) {
                String ballColour = "0"; // Default color

                switch (ball) {
                    case ("grey"):
                        ballColour = "0";
                        break;
                    case ("orange"):
                        ballColour = "1";
                        break;
                    case ("blue"):
                        ballColour = "2";
                        break;
                    case ("green"):
                        ballColour = "3";
                        break;
                    case ("yellow"):
                        ballColour = "4";
                        break;
                }
                ballQueue.add(ballColour);
            }
            initalised = true;
        }
    }

    /**
     * Checks the spawn timer and spawns a new ball if the timer has reached zero.
     */
    public void handleBallSpawning() {
        if (app.spawnTimer <= 0) {
            spawnBall();
            app.spawnTimer = app.spawnInterval;
        }
    }

    /**
     * Updates the display of balls in the queue.
     * This method handles the rendering of the queued balls, including their
     * movement and positioning.
     */
    // List to store x positions of balls

    public static void updateBallDisplay() {
        app.fill(0); // Set color to black
        app.rect(20, 20, 140, 25); // Draw rectangle below the ball

        // Update ballPositions list to match the size of ballQueue
        while (ballPositions.size() < ballQueue.size()) {
            String ballColor = ballQueue.get(ballPositions.size());
            PImage ballImage = Ball.getBallImage(ballColor, imageLoader);
            if (ballImage != null) {
                // Initialize positions based on their width
                ballPositions.add(20 + ballPositions.size() * (float) ballImage.width);
            }
        }

        // Update the positions of the balls
        for (int i = 0; i < Math.min(5, ballPositions.size() - 1); i++) {
            if (i < ballQueue.size()) { // Check if index is valid
                String ballColor = ballQueue.get(i);
                PImage ballImage = Ball.getBallImage(ballColor, imageLoader);
    
                if (ballImage != null) {
                    // Calculate target position
                    float targetPosition = 20 + i * ballImage.width;
    
                    // Move the ball towards the target position
                    float currentPosition = ballPositions.get(i);
                    if (ballRemoved) {
                        currentPosition += 20;
                    }
                    if (currentPosition < targetPosition) {
                        ballPositions.set(i, Math.min(currentPosition + MOVE_SPEED, targetPosition));
                    } else {
                        ballPositions.set(i, Math.max(currentPosition - MOVE_SPEED, targetPosition));
                    }
    
                    // Draw the ball at the updated position
                    app.image(ballImage, ballPositions.get(i), 20);
                }
            }
        }
        ballRemoved = false;
    }

    /**
     * Updates and displays all balls currently in play.
     * This method handles the updating of each ball's state and rendering them on
     * the screen.
     */
    public void updateAndDisplayBalls() {
        Iterator<Ball> iterator = ballsInPlay.iterator();

        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            ball.update();
            ball.updateBallPosition(ball);
            ball.display();

            if (ball.isCaptured()) {
                iterator.remove();
            }
        }
    }

    /**
     * Freezes or unfreezes all balls in play based on the specified toggle.
     *
     * @param toggle True to freeze all balls, false to unfreeze them.
     */
    public void freezeToggle(boolean toggle) {
        for (Ball ball : ballsInPlay) {
            if (toggle) {
                ball.freeze();
            } else {
                ball.unfreeze();
            }
        }
    }

    /**
     * Removes a specific ball from the list of balls in play.
     *
     * @param ball The ball to be removed.
     */
    public static void removeBall(Ball ball) {
        ballsInPlay.remove(ball);
    }

    /**
     * Spawns a new ball from the ball queue and adds it to the list of balls in
     * play.
     * The new ball's properties, such as position and velocity, are initialized
     * randomly.
     */
    public void spawnBall() {
        if (ballQueue.isEmpty()) {
            return;
        }
        String ballColor = ballQueue.remove(0);
        ballRemoved = true;

        PImage ballImage = Ball.getBallImage(ballColor, imageLoader);
        if (ballImage == null) {
            System.out.println("Ball image for color " + ballColor + " is null.");
            return;
        }

        float velocityX = (App.random.nextBoolean() ? 2 : -2);
        float velocityY = (App.random.nextBoolean() ? 2 : -2);
        Spawner selectedSpawner = BoardManager.spawners.get(App.random.nextInt(BoardManager.spawners.size()));
        float x = selectedSpawner.x2 + (App.CELLSIZE / 2);
        float y = selectedSpawner.y2 + (App.CELLSIZE / 2);

        char colour = ballColor.charAt(0);
        float radius = 12;
        BoardManager boardManager = new BoardManager(app, imageLoader);
        Ball newBall = new Ball(app, ballImage, x, y, velocityX, velocityY, radius, boardManager, colour);
        ballsInPlay.add(newBall);
        hasSpawnedBall = true;
    }

    /**
     * Adds a ball back to the queue for spawning again.
     *
     * @param ball The ball to be added back to the queue.
     */
    public static void addToQueueAgain(Ball ball) {
        ballQueue.add(ball.getColourAsString());
        updateBallDisplay();
    }

    /**
     * Resets the ball manager by clearing the list of balls in play and
     * reinitializing the ball queue.
     */
    public void reset() {
        initalised = false;
        ballsInPlay.clear();
        initializeBallQueue();
    }
}
