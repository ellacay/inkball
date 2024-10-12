package inkball.managers;

import inkball.objects.Ball;

import java.applet.Applet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import inkball.App;
import inkball.loaders.ConfigLoader;
import inkball.loaders.ImageLoader;

import processing.core.PApplet;
import processing.core.PImage;

public class BallManager {
    private App app;
    private ImageLoader imageLoader;
    public static List<Ball> ballsInPlay = new ArrayList<>(); // Initialize here
    public static List<String> ballQueue; 
    private int finishedBallCount = 0; // Track finished balls

    public BallManager(App app, ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        this.app = app;
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
        System.out.println(app.spawnTimer);
    
        if (app.spawnTimer <=0 ) {
            spawnBall();
            app.spawnTimer = app.spawnInterval;
        }
    }

    private void spawnBall() {
        if (BallManager.ballQueue.isEmpty()) {
            return;
        }
    
        String ballColor = BallManager.ballQueue.remove(0);
        PImage ballImage = BallManager.getBallImage(ballColor, this.imageLoader);
        if (ballImage == null) {
            System.out.println("Ball image for color " + ballColor + " is null.");
            return;
        }
    
        float velocityX = App.random.nextBoolean() ? 2 : -2;
        float velocityY = App.random.nextBoolean() ? 2 : -2;
    
        float x = BoardManager.spawner.x2; // Spawn from spawner
        float y = BoardManager.spawner.y2;
        char colour = 0;
    
        switch (ballColor) {
            case "grey": colour = 0; break;
            case "orange": colour = 1; break;
            case "blue": colour = 2; break;
            case "green": colour = 3; break;
            case "yellow": colour = 4; break;
        }
        float radius = 10;
    
        BoardManager boardManager = new BoardManager(app, imageLoader);
        Ball newBall = new Ball(app, ballImage, x, y, velocityX, velocityY, radius, boardManager, colour);
        BallManager.ballsInPlay.add(newBall);
    }

    public static PImage getBallImage(String color, ImageLoader imageLoader) {
        switch (color) {
            case "blue": return imageLoader.ball0;
            case "orange": return imageLoader.ball1;
            case "grey": return imageLoader.ball2;
            case "green": return imageLoader.ball3;
            case "yellow": return imageLoader.ball4;
            case "0": return imageLoader.ball0;
            case "1": return imageLoader.ball1;
            case "2": return imageLoader.ball2;
            case "3": return imageLoader.ball3;
            case "4": return imageLoader.ball4;
            default: return null;
        }
    }


    
    public void updateBallDisplay() {
        List<PImage> upcomingBalls = new ArrayList<>();
        
        for (int i = 0; i < Math.min(5, ballQueue.size()); i++) {
            String ballColor = ballQueue.get(i);
            PImage ballImage = getBallImage(ballColor, this.imageLoader);
            if (ballImage != null) {
                upcomingBalls.add(ballImage);
            }
        }
        
        int xOffset = 20;
        for (PImage img : upcomingBalls) {
            app.image(img, xOffset, 20);
            xOffset += img.width;
        }
    }
    
    public void reset() {
        ballsInPlay.clear();
        finishedBallCount = 0; // Reset finished ball count
        initializeBallQueue();
    }
}
