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

/**
 * Main application class that extends PApplet for the InkBall game.
 * This class manages the game loop, handles user inputs, and coordinates 
 * between different game components such as the board, balls, and game state.
 */
public class App extends PApplet {
    public Integer level;
    private ImageLoader imageLoader;
    private BallManager ballManager;
    private BoardManager boardManager;
    public int spawnInterval;
    public boolean gameWon;
    public boolean gameOver;
    public boolean isMovingTiles = false;
    public static int moveTimer = 0;
    public boolean levelWon = false;
    public static List<Line> lines = new ArrayList<>();
    public static List<PVector> currentLinePoints = new ArrayList<>();
    public int scoreIncrementTimer;
    private final int SCORE_INCREMENT_RATE = 67;
    private final int TILE_SPEED = 30;
    public boolean isPaused = false;
    public int timer;
    public static Random random = new Random();
    public static final int WIDTH = 576;
    public static final int HEIGHT = 640;
    public static final int FPS = 30;
    public static final int CELLSIZE = 32;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int INITIAL_PARACHUTES = 1;
    public static List<ConfigLoader.LevelConfig> levels;
    public int currentDirection1 = 0; // For the first tile
    public int currentX1 = 0; // Top left tile's X position
    public int currentY1 = 0; // Top left tile's Y position
    ConfigLoader.LevelConfig thisLevel;

    public int currentDirection2 = 0; // For the second tile
    public int currentX2 = WIDTH - CELLSIZE; // Bottom right tile's X position
    public int currentY2 = HEIGHT - CELLSIZE; // Bottom right tile's Y position

    ConfigLoader configLoader = new ConfigLoader();
    public double spawnTimer;
    private boolean freeze;
    public static Map<String, Integer> increaseScore;
    public static double increaseScoreMultipler;
    public static Map<String, Integer> decreaseScore;
    public static double decreaseScoreMultipler;
    public static String[] initalBalls;

    /**
     * Main method to launch the application.
     *
     * @param args Command line arguments.
     */
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
        level = 1;
        levelSetup();
        boardManager.loadBoard();
        ballManager.initializeBallQueue();
        currentX1 = 0;
        currentY1 = 64;
        currentX2 = WIDTH;
        currentY2 = HEIGHT;
    }

    /**
     * Sets up the current level based on the level configuration.
     */
    public void levelSetup() {
        thisLevel = levels.get(level - 1);
        this.timer = thisLevel.time;
        initalBalls = thisLevel.balls;
        this.spawnInterval = thisLevel.spawnInterval;
        this.spawnTimer = thisLevel.spawnInterval;
        increaseScore = thisLevel.scoreIncreaseMap;
        increaseScoreMultipler = thisLevel.scoreIncreaseMultipler;
        decreaseScore = thisLevel.scoreDecreaseMap;
        decreaseScoreMultipler = thisLevel.scoreDecreaseMultipler;
        spawnTimer = thisLevel.spawnInterval;
    }

    /**
     * Moves the yellow tiles in their defined directions.
     */
    public void moveYellowTile() {
        int maxHeight = (BOARD_HEIGHT - 1) * CELLSIZE; // Maximum Y boundary
        
        // Move the first yellow tile
        switch (currentDirection1) {
            case 0: // Move right
                currentX1 += TILE_SPEED;
                if (currentX1 >= (BOARD_WIDTH - 1) * CELLSIZE) {
                    currentX1 = (BOARD_WIDTH - 1) * CELLSIZE;
                    currentDirection1 = 1; // Change direction to down
                }
                break;
            case 1: // Move down
                currentY1 += TILE_SPEED;
                if (currentY1 >= maxHeight) {
                    currentY1 = maxHeight; // Reset to max height
                    currentDirection1 = 2; // Change direction to left
                }
                break;
            case 2: // Move left
                currentX1 -= TILE_SPEED;
                if (currentX1 < 0) {
                    currentX1 = 0;
                    currentDirection1 = 3; // Change direction to up
                }
                break;
            case 3: // Move up
                currentY1 -= TILE_SPEED;
                if (currentY1 < TOPBAR) {
                    currentY1 = TOPBAR; // Reset to the top bar offset
                    currentDirection1 = 0; // Change direction to right
                }
                break;
        }

        // Move the second yellow tile
        switch (currentDirection2) {
            case 0: // Move left
                currentX2 -= TILE_SPEED;
                if (currentX2 < 0) {
                    currentX2 = 0;
                    currentDirection2 = 1; // Change direction to up
                }
                break;
            case 1: // Move up
                currentY2 -= TILE_SPEED;
                if (currentY2 < TOPBAR) {
                    currentY2 = TOPBAR; // Reset to the top bar offset
                    currentDirection2 = 2; // Change direction to right
                }
                break;
            case 2: // Move right
                currentX2 += TILE_SPEED;
                if (currentX2 >= (BOARD_WIDTH - 1) * CELLSIZE) {
                    currentX2 = (BOARD_WIDTH - 1) * CELLSIZE;
                    currentDirection2 = 3; // Change direction to down
                }
                break;
            case 3: // Move down
                currentY2 += TILE_SPEED;
                if (currentY2 >= maxHeight) {
                    currentY2 = maxHeight; // Reset to max height
                    currentDirection2 = 0; // Change direction to left
                }
                break;
        }
    }

    /**
     * Displays the yellow tiles on the board.
     */
    void displayYellowTiles() {
        strokeWeight(0);
        fill(255, 255, 0);
        rect(currentX1, currentY1, CELLSIZE, CELLHEIGHT); // Display the first tile
        rect(currentX2, currentY2, CELLSIZE, CELLHEIGHT); // Display the second tile
    }

    /**
     * Updates the timer countdown every second.
     */
    public void updateTimer() {
        if (frameCount % App.FPS == 0) {
            this.timer--;
        }
    }

    /**
     * Updates the spawn timer for the balls.
     */
    public void updateSpawnTimer() {
        if (frameCount % 3 == 0 && spawnTimer > 0) {
            spawnTimer -= 0.1;
        }
    }

    /**
     * Displays the current score on the screen.
     */
    public void displayScore() {
        fill(0);
        textSize(16);
        text("Score:  " + BoardManager.score, WIDTH - 150, 50);
    }

    /**
     * Displays the remaining time on the screen.
     */
    public void displayTimer() {
        fill(0);
        textSize(16);
        text("Time Left: " + this.timer, WIDTH - 150, 30);
    }

    /**
     * Displays a message indicating that time is up.
     */
    public void displayTimeUp() {
        fill(0);
        textSize(16);
        textAlign(CENTER);
        text("=== TIME’S UP ===", WIDTH / 2, 50);
    }

    /**
     * Displays a pause message on the screen.
     */
    public void displayPause() {
        fill(0);
        textSize(16);
        textAlign(CENTER);
        text("*** PAUSED *** ", WIDTH / 2, 50);
    }

    /**
     * Displays the current spawn timer.
     */
    public void displaySpawnTimer() {
        fill(0);
        textSize(16);
        text("Spawn Interval: " + String.format("%.1f", this.spawnTimer), WIDTH - 330, 40);
    }

    @Override
    public void draw() {
        background(255);
        if (!isPaused) {
            if (gameWon) {
                displayGameOver();
                level = 1;
            } else if (boardManager.checkIfFinished() && this.timer > 0) {
                displayWin();
                displayTimer();
                displayScore();
                boardManager.displayBoard();
                ballManager.updateAndDisplayBalls();
            } else if (this.timer < 0) {
                gameOver = false;
                ballManager.freezeToggle(true);
                displayTimeUp();
                boardManager.displayBoard();
                ballManager.updateAndDisplayBalls();
            } else {
                updateAndDisplayGameElements();
            }
        } else {
            displayPause();
            boardManager.displayBoard();
            ballManager.updateAndDisplayBalls();
        }
        if (isMovingTiles) {
            moveYellowTile();
            displayYellowTiles();
            moveTimer++;
            if (moveTimer > 60) {
                isMovingTiles = false;
                levelWon = true;
                handleLevelTransition();
            }
        }
    }

    /**
     * Handles mouse press events to create or remove lines.
     */
    public void mousePressed() {
        if (!gameOver) {
            if (mouseButton == RIGHT || (keyPressed && keyCode == CONTROL)) {
                removeLineAtMousePosition(mouseX, mouseY);
            } else {
                currentLinePoints.clear();
                currentLinePoints.add(new PVector(mouseX, mouseY));
            }
        }
    }

    /**
     * Removes a line at the specified mouse position if close enough.
     *
     * @param mouseX The X position of the mouse.
     * @param mouseY The Y position of the mouse.
     */
    public void removeLineAtMousePosition(float mouseX, float mouseY) {
        Line closestLine = null;
        float closestDistance = Float.MAX_VALUE;
        for (Line line : lines) {
            float distance = distanceToLine(mouseX, mouseY, line);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestLine = line;
            }
        }
        if (closestLine != null && closestDistance < 10) {
            lines.remove(closestLine);
        }
    }

    /**
     * Calculates the distance from a point to a line segment.
     *
     * @param x The X coordinate of the point.
     * @param y The Y coordinate of the point.
     * @param line The line segment.
     * @return The distance from the point to the line segment.
     */
    public float distanceToLine(float x, float y, Line line) {
        PVector start = line.getStart();
        PVector end = line.getEnd();
        float lineLength = PVector.dist(start, end);
        if (lineLength == 0) {
            return PVector.dist(new PVector(x, y), start);
        }
        float t = PVector.dot(new PVector(x, y).copy().sub(start), end.copy().sub(start)) / (lineLength * lineLength);
        t = PApplet.constrain(t, 0, 1);
        PVector projection = PVector.add(start, PVector.mult(end.copy().sub(start), t));
        return PVector.dist(new PVector(x, y), projection);
    }

    /**
     * Displays the game over screen with a restart message.
     */
    void displayGameOver() {
        background(255);
        fill(0, 0, 0, 150);
        rect(0, 0, width, height);
        fill(255);
        textSize(32);
        textAlign(CENTER, CENTER);
        text("GAME OVER", width / 2, height / 2 - 20);
        textSize(16);
        text("Press 'R' to Restart", width / 2, height / 2 + 20);
    }

    /**
     * Handles the actions to take when the player wins a level.
     */
    public void displayWin() {
        isMovingTiles = true;
        if (this.timer > 0) {
            scoreIncrementTimer++;
            if (scoreIncrementTimer >= SCORE_INCREMENT_RATE) {
                BoardManager.score++;
                scoreIncrementTimer = 0;
            }
        }
        levelWon = true;
    }

    /**
     * Handles level transition when the player completes a level.
     */
    public void handleLevelTransition() {
        if (levelWon) {
            level++;
            if (level <= levels.size()) {
                BoardManager.score += (this.timer * 0.067);
                this.spawnTimer = levels.get(level - 1).spawnInterval;
                this.timer = levels.get(level - 1).time;
                levelSetup();
                lines.clear();
                BoardManager.levelScore = BoardManager.score;
                restartGame();
                levelWon = false;
            } else {
                level = levels.size();
                gameWon = true;
                System.out.println("No more levels available.");
            }
        }
    }

    /**
     * Updates and displays the main game elements.
     */
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

    /**
     * Displays the lines drawn by the player.
     */
    void displayLines() {
        strokeWeight(10);
        for (Line line : lines) {
            line.display(this);
        }
        if (currentLinePoints.size() > 0) {
            stroke(0);
            noFill();
            beginShape();
            for (PVector point : currentLinePoints) {
                vertex(point.x, point.y);
            }
            vertex(mouseX, mouseY);
            endShape();
        }
    }

    /**
     * Handles mouse drag events to draw lines.
     */
    public void mouseDragged() {
        if (mouseButton == LEFT) {
            strokeWeight(10);
            if (!gameOver) {
                currentLinePoints.add(new PVector(mouseX, mouseY));
            }
        }
    }

    /**
     * Handles mouse release events to finalize the drawn line.
     */
    public void mouseReleased() {
        strokeWeight(10);
        if (!gameOver) {
            if (currentLinePoints.size() > 0) {
                lines.add(new Line(new ArrayList<>(currentLinePoints)));
            }
            currentLinePoints.clear();
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKey() == 'r') {
            restartGame();
        }

        if (event.getKey() == ' ') {
            isPaused = !isPaused;
            this.freeze = !this.freeze;
            ballManager.freezeToggle(this.freeze);
        }
    }

    /**
     * Restarts the game to the initial state.
     */
    public void restartGame() {
        this.isPaused = false;

        if (this.gameWon) {
            level = 1; // Reset level to 1
            levelSetup();
            BoardManager.score = 0; // Reset score
            gameWon = false; // Reset game won status
        } else {
            if (!this.levelWon) {
                BoardManager.score = BoardManager.levelScore; // Maintain current level score
            }
        }

        this.levelWon = false;
        this.isMovingTiles = false;
        boardManager.reset();
        ballManager.reset();
        this.spawnTimer = spawnInterval;
        this.timer = thisLevel.time;
        boardManager.ballSpawned = false;
        lines.clear();
        loop();
    }
}
