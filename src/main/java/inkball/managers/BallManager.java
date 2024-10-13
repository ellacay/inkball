package inkball.managers;
import inkball.objects.Ball;
import inkball.App;
import inkball.loaders.ImageLoader;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BallManager {
    private static App app;
    private static ImageLoader imageLoader;
    public static List<Ball> ballsInPlay = new ArrayList<>();
    public static List<String> ballQueue;

    public BallManager(App appRef, ImageLoader imgLoader) {
        app = appRef;
        imageLoader = imgLoader;
        initializeBallQueue();
    }

    public void initializeBallQueue() {
        ballQueue = new LinkedList<>(Arrays.asList("2", "1", "0", "3", "4"));
    }

    public void updateAndDisplayBalls() {
        for (Ball ball : ballsInPlay) {
            ball.update();
            ball.display();
        }
    }

    public void handleBallSpawning() {
        if (app.spawnTimer <= 0) {
            spawnBall();
            app.spawnTimer = app.spawnInterval;
        }
    }

    private void spawnBall() {
        if (ballQueue.isEmpty()) return;
        String ballColor = ballQueue.remove(0);
        PImage ballImage = getBallImage(ballColor, imageLoader);
        if (ballImage == null) {
            System.out.println("Ball image for color " + ballColor + " is null.");
            return;
        }

        float velocityX = App.random.nextBoolean() ? 2 : -2;
        float velocityY = App.random.nextBoolean() ? 2 : -2;
        float x = BoardManager.spawner.x2; // Spawn from spawner
        float y = BoardManager.spawner.y2;
   
        char colour = ballColor.charAt(0);
        float radius = 10;
        BoardManager boardManager = new BoardManager(app, imageLoader);
        Ball newBall = new Ball(app, ballImage, x, y, velocityX, velocityY, radius, boardManager, colour);
        ballsInPlay.add(newBall);
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

    public static void addToQueueAgain(Ball ball){
        ballQueue.add(ball.getColour());
        updateBallDisplay();


    }

    public static void updateBallDisplay() {
        List<PImage> upcomingBalls = new ArrayList<>();


        for (int i = 0; i < Math.min(5, ballQueue.size()); i++) {
            String ballColor = ballQueue.get(i);
            
            PImage ballImage = getBallImage(ballColor, imageLoader);
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
        initializeBallQueue();
    }
}
