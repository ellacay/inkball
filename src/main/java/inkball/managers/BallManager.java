package inkball.managers;
import inkball.objects.Ball;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import inkball.App;
import inkball.loaders.ImageLoader;

import processing.core.PApplet;
import processing.core.PImage;

public class BallManager {
    private PApplet app;
    private ImageLoader imageLoader;
    private List<Ball> ballsInPlay;
    private List<String> ballQueue; 
    private int spawnCounter;
    private int spawnInterval = 60; 



    public BallManager(PApplet app, ImageLoader imageLoader) {
        this.app = app;
        this.imageLoader = imageLoader;
        this.ballsInPlay = new ArrayList<>();
        this.spawnCounter = 0;
        initializeBallQueue();
    }

    public void initializeBallQueue() {
        ballQueue = new LinkedList<>(Arrays.asList("blue", "orange", "grey", "green", "yellow"));
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

    private void spawnBall() {
        if (ballQueue.isEmpty()) {
            System.out.println("Ball queue is empty. No balls to spawn.");
            return;
        }

        String ballColor = ballQueue.remove(0); // Use the custom poll method
        PImage ballImage = getBallImage(ballColor);
        if (ballImage == null) {
            System.out.println("Ball image for color " + ballColor + " is null.");
            return;
        }

        float velocityX = App.random.nextBoolean() ? 2 : -2;
        float velocityY = App.random.nextBoolean() ? 2 : -2;
        float x = App.random.nextFloat() * App.WIDTH;
        float y = App.random.nextFloat() * App.HEIGHT;
        float radius = 10; // Example radius

        Ball newBall = new Ball(app, ballImage, x, y, velocityX, velocityY, radius);
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
