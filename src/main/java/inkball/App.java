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
    public int getSpawnTimer(){
        return this.spawnTimer;
    }
    private ImageLoader imageLoader;
    private BallManager ballManager;
    private BoardManager boardManager;
    public int spawnInterval;

    public static List<Line> lines = new ArrayList<>(); // Make it static or instance variable based on your needs
    public static List<PVector> currentLinePoints = new ArrayList<>(); // For storing points of the current line
    private PVector startPoint;
    private PVector endPoint;


    // Add this to your main game class or PApplet subclass
private boolean isPaused = false;

    private int timer;
    public static Random random = new Random();
   
    public static final int WIDTH = 576;
    public static final int HEIGHT = 640;
    public static final int FPS = 30;
    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int INITIAL_PARACHUTES = 1;

    ConfigLoader configLoader = new ConfigLoader();
    public int spawnTimer;
    

   
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }
   
    
    @Override
    public void setup() {
        frameRate(FPS);
        ConfigLoader.readConfig();
        
        imageLoader = new ImageLoader(this);
        imageLoader.loadImages();
        
        boardManager = new BoardManager(this, imageLoader);
        ballManager = new BallManager(this, imageLoader);
      
        
        List<ConfigLoader.LevelConfig> levels = ConfigLoader.getLevelsConfig();
        ConfigLoader.LevelConfig firstLevel = levels.get(0);
        this.timer = firstLevel.time; 
        this.spawnInterval = firstLevel.spawnInterval;
        this.spawnTimer = firstLevel.spawnInterval;

        boardManager.loadBoard();
        ballManager.initializeBallQueue();
    }

   
    public void updateTimer() {
        if (frameCount % App.FPS == 0) {
            this.timer--;
        }
    }
    public void updateSpawnTimer() {
        if (frameCount % App.FPS == 0) {
            this.spawnTimer--;
        }
    }

    public void displayScore() {
        fill(0);
        textSize(16);
        text("Score:  " + BoardManager.score, 10, App.HEIGHT -45);
    }

    public void displayTimer() {
        fill(0);
        textSize(16);
        text("Time Left: " + this.timer, 10, App.HEIGHT - 20);
    }

    public void displaySpawnTimer() {
        fill(0);
        textSize(16);
        text("Spawn Interval: " + this.spawnTimer, 10, App.HEIGHT - 10);
    }

    @Override
    public void draw() {
        if (!isPaused) {
            // Check if the game has ended
            if (boardManager.checkIfFinished()) {
                displayGameOver();
            } else {
                // Update and display game elements
                updateAndDisplayGameElements();
            }
        } else {
            // Optionally, display a pause message or overlay
            displayPauseOverlay();
        }
    }
private void displayPauseOverlay() {
    fill(0, 0, 0, 150); // Semi-transparent black background
    rect(0, 0, width, height); // Cover the entire screen

    fill(255); // White text
    textSize(32);
    textAlign(CENTER, CENTER);
    text("PAUSED", width / 2, height / 2);
}

private void displayGameOver() {
    fill(0, 0, 0, 150); // Semi-transparent black background
    rect(0, 0, width, height); // Cover the entire screen

    fill(255); // White text
    textSize(32);
    textAlign(CENTER, CENTER);
    text("GAME OVER", width / 2, height / 2 - 20);
    textSize(16);
    text("Press 'R' to Restart", width / 2, height / 2 + 20);
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
    ballManager.updateBallDisplay();
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

public void mousePressed() {
    currentLinePoints.clear(); // Clear previous points
    currentLinePoints.add(new PVector(mouseX, mouseY)); // Add the starting point
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
        boardManager.loadBoard();
        ballManager.reset();
        timer = 120;
        lines.clear(); // Clear all lines
        loop();
    }
}
