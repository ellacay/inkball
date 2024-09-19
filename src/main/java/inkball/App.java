package inkball;

import processing.core.PApplet;
import processing.event.KeyEvent;


import java.util.*;

import inkball.loaders.ImageLoader;
import inkball.managers.BallManager;
import inkball.managers.BoardManager;

public class App extends PApplet {
    private ImageLoader imageLoader;
    private BallManager ballManager;
    private BoardManager boardManager;

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
        
        imageLoader = new ImageLoader(this);
        imageLoader.loadImages();
        
        boardManager = new BoardManager(this, imageLoader);
        ballManager = new BallManager(this, imageLoader);
      
        this.timer = 120; 

        boardManager.loadBoard();
        ballManager.initializeBallQueue();
    }
    public void updateTimer() {
        if (frameCount % App.FPS == 0) {
            timer--;
        }
    }

    public void displayTimer() {
        fill(0);
        textSize(16);
        text("Time Left: " + timer, 10, App.HEIGHT - 20);
    }

    @Override
public void draw() {
    if (!isPaused) {
        // Update and display game elements
        updateAndDisplayGameElements();
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
private void updateAndDisplayGameElements() {
background(255);

    updateTimer();
    displayTimer();

    boardManager.displayBoard();
    ballManager.updateAndDisplayBalls();
    ballManager.handleBallSpawning();
    ballManager.updateBallDisplay();
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
        loop();
    }
}
