package inkball;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;

public class BallManager {
    private PApplet app;
    private ImageLoader imageLoader;
    private List<String> ballQueue = new ArrayList<>();
    private List<Ball> ballsInPlay;
    private int spawnCounter;
    private int spawnInterval = 60; // Example interval in frames

    public BallManager(PApplet app, ImageLoader imageLoader) {
        this.app = app;
        this.imageLoader = imageLoader;
        this.ballQueue = new LinkedList<>();
        this.ballsInPlay = new ArrayList<>();
        this.spawnCounter = 0;
    }

    public void initializeBallQueue() {
        ballQueue.add("blue");
        ballQueue.add("orange");
        ballQueue.add("grey");
        ballQueue.add("green");
        ballQueue.add("yellow");
    }

    public void updateAndDisplayBalls() {
        for (Ball ball : ballsInPlay) {
            ball.update();
            ball.display();
        }
    }

    public void handleBallSpawning() {
        spawnCounter++;
        if (spawnCounter >= spawnInterval) {
            spawnBall();
            spawnCounter = 0;
        }
    }

    private String pollBall() {
        if (ballQueue.isEmpty()) {
            return null; // or throw an exception if needed
        }
        return ballQueue.remove(0); // Remove and return the first element
    }

    private void spawnBall() {
        if (ballQueue.isEmpty()) {
            System.out.println("Ball queue is empty. No balls to spawn.");
            return;
        }

        String ballColor = pollBall(); // Use the custom poll method
        PImage ballImage = getBallImage(ballColor);
        if (ballImage == null) {
            System.out.println("Ball image for color " + ballColor + " is null.");
            return;
        }

        float velocityX = App.random.nextBoolean() ? 2 : -2;
        float velocityY = App.random.nextBoolean() ? 2 : -2;

        Ball newBall = new Ball(app, ballImage, velocityX, velocityY, App.WIDTH, App.HEIGHT);
        ballsInPlay.add(newBall);
    }

    private PImage getBallImage(String color) {
        switch (color) {
            case "blue": return imageLoader.ball0;
            case "orange": return imageLoader.ball1;
            case "grey": return imageLoader.ball2;
            case "green": return imageLoader.ball3;
            case "yellow": return imageLoader.ball4;
            default: return null;
        }
    }

    public void updateBallDisplay() {
        List<PImage> upcomingBalls = new ArrayList<>();
        
        for (int i = 0; i < Math.min(5, ballQueue.size()); i++) {
            String ballColor = ballQueue.get(i);
            PImage ballImage = getBallImage(ballColor);
            if (ballImage != null) {
                upcomingBalls.add(ballImage);
            }
        }
    
        int xOffset = 0;
        for (PImage img : upcomingBalls) {
            app.image(img, xOffset, 0);
            xOffset += img.width;
        }
    }

    public void reset() {
        ballsInPlay.clear();
        initializeBallQueue();
    }
}
