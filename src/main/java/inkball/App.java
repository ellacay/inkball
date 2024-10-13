package inkball;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

import java.util.*;

import inkball.loaders.ImageLoader;
import inkball.loaders.ConfigLoader;
import inkball.managers.BallManager;
import inkball.managers.BoardManager;

import inkball.objects.Line;

public class App extends PApplet {
    public static int level = 1;
    private ImageLoader imageLoader;
    private BallManager ballManager;
    private BoardManager boardManager;
    public int spawnInterval;
    public boolean gameWon;

    public static List<Line> lines = new ArrayList<>(); // Make it static or instance variable based on your needs
    public static List<PVector> currentLinePoints = new ArrayList<>(); // For storing points of the current line
    private PVector startPoint;
    private PVector endPoint;
    private int score;
    private int scoreIncrementTimer;
    private final int SCORE_INCREMENT_RATE = 67; // 0.067 seconds in frames (assuming FPS = 30)
    private List<PVector> yellowTiles; // Store positions of yellow tiles
    private int currentTileIndex; // Track current position in the clockwise path
    private final int TILE_SPEED = 67; // Move every 0.067 seconds

    // Add this to your main game class or PApplet subclass
    private boolean isPaused = false;

    private int timer;
    public static Random random = new Random();

    public static final int WIDTH = 576;
    public static final int HEIGHT = 640;
    public static final int FPS = 30;
    public static final int CELLSIZE = 32; // 8;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int INITIAL_PARACHUTES = 1;
    public static List<ConfigLoader.LevelConfig> levels;
    private int currentDirection = 0; // 0 = right, 1 = down, 2 = left, 3 = up
    private int currentX = 0; // Current X position
    private int currentY = 0; // Current Y position
    // private final int TILE_SPEED = 1; // Speed of the tile movement

    ConfigLoader configLoader = new ConfigLoader();
    public double spawnTimer;

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        levels = ConfigLoader.getLevelsConfig();

        frameRate(FPS);
        ConfigLoader.readConfig();

        imageLoader = new ImageLoader(this);
        imageLoader.loadImages();

        boardManager = new BoardManager(this, imageLoader);
        ballManager = new BallManager(this, imageLoader);

        ConfigLoader.LevelConfig firstLevel = levels.get(level - 1);
        this.timer = firstLevel.time;
        this.spawnInterval = firstLevel.spawnInterval;
        this.spawnTimer = firstLevel.spawnInterval;

        boardManager.loadBoard();
        ballManager.initializeBallQueue();
        // Set the initial position of the yellow tile
        currentX = 0;
        currentY = 0;
    }

    private void moveYellowTile() {
        // Move the yellow tile in the current direction
        switch (currentDirection) {
            case 0: // Move right
                currentX += TILE_SPEED;
                if (currentX >= (BOARD_WIDTH - 1) * CELLSIZE) {
                    currentX = (BOARD_WIDTH - 1) * CELLSIZE; // Ensure it doesn't go out of bounds
                    currentDirection = 1; // Change direction to down
                }
                break;
            case 1: // Move down
                currentY += TILE_SPEED;
                if (currentY >= (BOARD_HEIGHT - 1) * CELLSIZE) {
                    currentY = (BOARD_HEIGHT - 1) * CELLSIZE; // Ensure it doesn't go out of bounds
                    currentDirection = 2; // Change direction to left
                }
                break;
            case 2: // Move left
                currentX -= TILE_SPEED;
                if (currentX < 0) {
                    currentX = 0; // Ensure it doesn't go out of bounds
                    currentDirection = 3; // Change direction to up
                }
                break;
            case 3: // Move up
                currentY -= TILE_SPEED;
                if (currentY < 0) {
                    currentY = 0; // Ensure it doesn't go out of bounds
                    currentDirection = 0; // Change direction to right
                }
                break;
        }
    }

    private void displayYellowTiles() {
        strokeWeight(0);
        fill(255, 255, 0); // Yellow color
        rect(currentX, currentY, CELLSIZE, CELLHEIGHT); // Draw single yellow tile at the current position
    }

    public void updateTimer() {
        if (frameCount % App.FPS == 0) {
            this.timer--;
        }
    }

    public void updateSpawnTimer() {
        if (frameCount % 3 == 0 && spawnTimer > 0) { // Decrement every 0.1 seconds
            spawnTimer -= 0.1; // Decrease by 0.1
        }
    }

    public void displayScore() {
        fill(0);
        textSize(16);
        text("Score:  " + BoardManager.score, WIDTH - 150, 50);
    }

    public void displayTimer() {
        fill(0);
        textSize(16);

        text("Time Left: " + this.timer, WIDTH - 150, 30);
    }

    public void displaySpawnTimer() {
        fill(0);
        textSize(16);
        text("Spawn Interval: " + String.format("%.1f", this.spawnTimer), WIDTH - 350, 40);
    }

    @Override
    public void draw() {
        background(255);
        if (!isPaused) {
            // Check if the game has ended
            if (gameWon) {
                displayGameOver();
                level = 1;
            } else if (boardManager.checkIfFinished() && this.timer > 0) {
                displayWin();
            } else if (this.timer < 0) {
                displayGameOver();
            } else {
                // Update and display game elements
                updateAndDisplayGameElements();
            }
        } else {
            // Optionally, display a pause message or overlay
            displayPauseOverlay();
        }

        // Manage yellow tile movement after a win
        if (isMovingTiles) {

            moveYellowTile();
            displayYellowTiles();
            moveTimer++; // Increment move timer

            // Stop moving after a certain duration (adjust as needed)
            if (moveTimer > 60) { // 60 frames (2 seconds at 30 FPS)
                isMovingTiles = false; // Stop moving tiles
                levelWon = true; // Allow level transition
                handleLevelTransition(); // Call to transition to the next level
            }
        }

        // Check for restart input even when the game is over
        if (keyPressed && key == 'r') {
            restartGame(); // Restart the game if 'R' is pressed
        }
    }

    public void mousePressed() {
        // Check if the right mouse button is pressed or Ctrl + left click
        if (mouseButton == RIGHT || (keyPressed && keyCode == CONTROL)) {
            // Remove the nearest line to the mouse position
            removeLineAtMousePosition(mouseX, mouseY);
        } else {
            // Clear previous points and add the starting point for a new line
            currentLinePoints.clear();
            currentLinePoints.add(new PVector(mouseX, mouseY));
        }
    }

    private void removeLineAtMousePosition(float mouseX, float mouseY) {
        // Find and remove the nearest line to the given mouse position
        Line closestLine = null;
        float closestDistance = Float.MAX_VALUE;

        for (Line line : lines) {
            float distance = distanceToLine(mouseX, mouseY, line);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestLine = line;
            }
        }

        // If a close enough line is found, remove it
        if (closestLine != null && closestDistance < 10) { // Threshold for removal
            lines.remove(closestLine);
        }
    }

    // Calculate the distance from a point to a line segment
    private float distanceToLine(float x, float y, Line line) {
        PVector start = line.getStart();
        PVector end = line.getEnd();

        // Calculate the length of the line segment
        float lineLength = PVector.dist(start, end);
        if (lineLength == 0)
            return PVector.dist(new PVector(x, y), start); // The line is a point

        // Calculate the projection of the point onto the line
        float t = PVector.dot(new PVector(x, y).copy().sub(start), end.copy().sub(start)) / (lineLength * lineLength);
        t = PApplet.constrain(t, 0, 1); // Constrain t to the segment
        PVector projection = PVector.add(start, PVector.mult(end.copy().sub(start), t));

        return PVector.dist(new PVector(x, y), projection); // Return distance from the point to the projected point
    }

    private void displayPauseOverlay() {
        fill(0, 0, 0, 150); // Semi-transparent black background
        rect(0, 0, width, height); // Cover the entire screen

        fill(255); // White text
        textSize(32);
        textAlign(CENTER, CENTER);
        text("PAUSED", width / 2, height / 2);
    }

    private boolean isMovingTiles = false; // Flag to check if the yellow tiles are moving
    private static int moveTimer = 0; // Timer for managing the move duration

    private void displayGameOver() {
        background(255);
        fill(0, 0, 0, 150); // Semi-transparent black background
        rect(0, 0, width, height); // Cover the entire screen

        fill(255); // White text
        textSize(32);
        textAlign(CENTER, CENTER);
        text("GAME OVER", width / 2, height / 2 - 20);
        textSize(16);
        text("Press 'R' to Restart", width / 2, height / 2 + 20);
    }

    private boolean levelWon = false; // New variable to track if the level has been won

    private void displayWin() {

        // Start moving the yellow tiles
        isMovingTiles = true; // Set the flag to true

        // Check if the timer is greater than 0 for scoring
        if (this.timer > 0) {
            scoreIncrementTimer++;
            if (scoreIncrementTimer >= SCORE_INCREMENT_RATE) {
                score++; // Increment score
                scoreIncrementTimer = 0; // Reset timer
            }
        }

        // Set levelWon to true after the win condition is displayed
        levelWon = true;
    }

    // Call this method in the draw loop to handle level transition
    private void handleLevelTransition() {
        // isMovingTiles = false; // Stop moving tiles
        // levelWon = true; // Allow level transition

        if (levelWon) {
            if (frameCount % 60 == 0) {
                levelWon = false; // Reset the win state
                level++; // Move to the next level
                print("Level Size: " + levels.size());
                print("Level: " + level);

                if (level <= levels.size()) {

                    // Load the new level

                    // Reset the timer or spawn the new level state if needed
                    this.spawnTimer = levels.get(level - 1).spawnInterval; // Set new spawn interval
                    this.timer = levels.get(level - 1).time; // Set new time from level config
                    lines.clear();
                    restartGame();
                    loop();
                } else {
                    level = levels.size();
                    gameWon = true;

                    System.out.println("No more levels available."); // Inform the user

                    draw();
                    // noLoop(); // Stop the draw loop
                }
            }
        }
    }

    private void updateAndDisplayGameElements() {
        background(255);

        updateTimer();
        displayTimer();
        updateSpawnTimer();
        displaySpawnTimer();
        displayScore();

        boardManager.displayBoard();
        ballManager.updateAndDisplayBalls();
        ballManager.handleBallSpawning();
        BallManager.updateBallDisplay();
        displayLines();

    }

    private void displayLines() {
        for (Line line : lines) {
            line.display(this);
        }

        // Draw the current line being dragged
        if (currentLinePoints.size() > 0) {
            stroke(0);
            noFill();
            beginShape();
            for (PVector point : currentLinePoints) {
                vertex(point.x, point.y);
            }
            vertex(mouseX, mouseY); // Continue to the current mouse position
            endShape();
        }
    }

    public void mouseDragged() {
        currentLinePoints.add(new PVector(mouseX, mouseY)); // Add points while dragging
    }

    public void mouseReleased() {
        if (currentLinePoints.size() > 0) {
            lines.add(new Line(new ArrayList<>(currentLinePoints))); // Store a copy of the current line
        }
        currentLinePoints.clear(); // Clear for the next line
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKey() == 'r') {
            restartGame();
        }
        if (event.getKey() == ' ') {
            isPaused = !isPaused;
        }
    }

    private void restartGame() {
        this.gameWon = false;
        boardManager.reset();
        ballManager.reset();
        this.spawnTimer = spawnInterval;
        this.timer = 120;
        boardManager.ballSpawned = false;
        lines.clear();
        loop();
    }

}
