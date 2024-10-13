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
    private boolean isMovingTiles = false;
    private static int moveTimer = 0;
    private boolean levelWon = false;
    public static List<Line> lines = new ArrayList<>();
    public static List<PVector> currentLinePoints = new ArrayList<>();
    private int scoreIncrementTimer;
    private final int SCORE_INCREMENT_RATE = 67;
    private final int TILE_SPEED = 67;
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
    private int currentDirection = 0;
    private int currentX = 0;
    private int currentY = 0;

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
        currentX = 0;
        currentY = 0;
    }

    private void moveYellowTile() {
        switch (currentDirection) {
            case 0: // Move right
                currentX += TILE_SPEED;
                if (currentX >= (BOARD_WIDTH - 1) * CELLSIZE) {
                    currentX = (BOARD_WIDTH - 1) * CELLSIZE;
                    currentDirection = 1;
                }
                break;
            case 1: // Move down
                currentY += TILE_SPEED;
                if (currentY >= (BOARD_HEIGHT - 1) * CELLSIZE) {
                    currentY = (BOARD_HEIGHT - 1) * CELLSIZE;
                    currentDirection = 2;
                }
                break;
            case 2: // Move left
                currentX -= TILE_SPEED;
                if (currentX < 0) {
                    currentX = 0;
                    currentDirection = 3;
                }
                break;
            case 3: // Move up
                currentY -= TILE_SPEED;
                if (currentY < 0) {
                    currentY = 0;
                    currentDirection = 0;
                }
                break;
        }
    }

    private void displayYellowTiles() {
        strokeWeight(0);
        fill(255, 255, 0);
        rect(currentX, currentY, CELLSIZE, CELLHEIGHT);
    }

    public void updateTimer() {
        if (frameCount % App.FPS == 0) {
            this.timer--;
        }
    }

    public void updateSpawnTimer() {
        if (frameCount % 3 == 0 && spawnTimer > 0) {
            spawnTimer -= 0.1;
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
            if (gameWon) {
                displayGameOver();
                level = 1;
            } else if (boardManager.checkIfFinished() && this.timer > 0) {
                displayWin();
            } else if (this.timer < 0) {
                displayGameOver();
            } else {
                updateAndDisplayGameElements();
            }
        } else {
            displayPauseOverlay();
        }
        if (isMovingTiles) {
            moveYellowTile();
            displayYellowTiles();
            moveTimer++;
            if (moveTimer > 30) {
                isMovingTiles = false;
                levelWon = true;
                handleLevelTransition();
            }
        }
    }

    public void mousePressed() {
        if (mouseButton == RIGHT || (keyPressed && keyCode == CONTROL)) {
            removeLineAtMousePosition(mouseX, mouseY);
        } else {
            currentLinePoints.clear();
            currentLinePoints.add(new PVector(mouseX, mouseY));
        }
    }

    private void removeLineAtMousePosition(float mouseX, float mouseY) {
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

    private float distanceToLine(float x, float y, Line line) {
        PVector start = line.getStart();
        PVector end = line.getEnd();
        float lineLength = PVector.dist(start, end);
        if (lineLength == 0)
            return PVector.dist(new PVector(x, y), start);
        float t = PVector.dot(new PVector(x, y).copy().sub(start), end.copy().sub(start)) / (lineLength * lineLength);
        t = PApplet.constrain(t, 0, 1);
        PVector projection = PVector.add(start, PVector.mult(end.copy().sub(start), t));
        return PVector.dist(new PVector(x, y), projection);
    }

    private void displayPauseOverlay() {
        fill(0, 0, 0, 150);
        rect(0, 0, width, height);
        fill(255);
        textSize(32);
        textAlign(CENTER, CENTER);
        text("PAUSED", width / 2, height / 2);
    }



    private void displayGameOver() {
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

    private void displayWin() {
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

    private void handleLevelTransition() {
        if (levelWon) {
            if (frameCount % 60 == 0) {
                levelWon = false;
                level++;
                if (level <= levels.size()) {
                    this.spawnTimer = levels.get(level - 1).spawnInterval;
                    this.timer = levels.get(level - 1).time;
                    lines.clear();
                    restartGame();
                    loop();
                } else {
                    level = levels.size();
                    gameWon = true;
                    System.out.println("No more levels available.");
                    draw();
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

    public void mouseDragged() {
        currentLinePoints.add(new PVector(mouseX, mouseY));
    }

    public void mouseReleased() {
        if (currentLinePoints.size() > 0) {
            lines.add(new Line(new ArrayList<>(currentLinePoints)));
        }
        currentLinePoints.clear();
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
