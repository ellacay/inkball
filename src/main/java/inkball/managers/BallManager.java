package inkball.managers;

import inkball.objects.Ball;
import inkball.objects.Spawner;
import inkball.App;
import inkball.loaders.ImageLoader;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BallManager {
    private static App app;
    private static ImageLoader imageLoader;
    public static List<Ball> ballsInPlay = new ArrayList<>();
    public static List<String> ballQueue;
    public static boolean hasSpawnedBall = false;
    public static boolean initalised = false;
public static boolean ballRemoved = false; 
    public BallManager(App appRef, ImageLoader imgLoader) {
        app = appRef;
        imageLoader = imgLoader;

    }

    public void initializeBallQueue() {
        ballQueue = new LinkedList<>();

        // Add elements from the array to the LinkedList
        if(App.initalBalls !=null && initalised!=true){
       
          
            for (String ball : App.initalBalls) {
               
               
                String ballColour = "0";
                
                switch(ball){
                    case("grey"):
      
                        ballColour = "0";
                        ballQueue.add(ballColour);
                        break;
                    case("orange"):
                  
                        ballColour = "1";
                        ballQueue.add(ballColour);
                        break;
                    case("blue"):
                    
                        ballColour = "2";
                        ballQueue.add(ballColour);
                        break;
                    case("green"):
                 
                        ballColour = "3";
                        ballQueue.add(ballColour);
                        break;
                    case("yellow"):
        
                        ballColour = "4";
                        ballQueue.add(ballColour);
                        break;
                    
                }
              
            }
            initalised = true;
        }
       
    }


    public void handleBallSpawning() {
        if (app.spawnTimer <= 0) {
            spawnBall();
            app.spawnTimer = app.spawnInterval;
        }
    }
 // List to store x positions of balls
private static List<Float> ballPositions = new ArrayList<>();
private static final float MOVE_SPEED = 0.1f; // Speed of movement towards the gap

public static void updateBallDisplay() {
    app.fill(0); // Set color to black
            app.rect(20,20,140,25); // Draw rectangle below the ball
            
    // Update ballPositions list to match the size of ballQueue
    while (ballPositions.size() < ballQueue.size()) {
        String ballColor = ballQueue.get(ballPositions.size());
        PImage ballImage = Ball.getBallImage(ballColor, imageLoader);
        if (ballImage != null) {
            // Initialize positions based on their width
            ballPositions.add(20 + ballPositions.size() * (float)ballImage.width);
        }
    }

    

    // Update the positions of the balls
    for (int i = 0; i < Math.min(5, ballQueue.size()-1); i++) {
        String ballColor = ballQueue.get(i);
        PImage ballImage = Ball.getBallImage(ballColor, imageLoader);
        
        if (ballImage != null) {
            // Calculate target position
            float targetPosition = 20 + i * ballImage.width;

            // Move the ball towards the target position
            float currentPosition = ballPositions.get(i);
            if(ballRemoved){
                currentPosition+=20;
                
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
    ballRemoved = false;
}

    public void updateAndDisplayBalls() {
        Iterator<Ball> iterator = ballsInPlay.iterator();

        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            
            // Update the ball before checking if it should be removed
            ball.update();
            ball.updateBallPosition(ball);
            ball.display();
    
            // Check if the ball should be removed (e.g., if it's captured)
            if (ball.isCaptured()) { // Replace with your actual condition
                iterator.remove(); // Safe removal
            }
        }
    }

   
    public void freezeToggle(boolean toggle) {

        // Stop updating balls by setting their velocities to zero
        if (toggle) {
            for (Ball ball : ballsInPlay) {
                ball.freeze();
            }
        } else {
            for (Ball ball : ballsInPlay) {
                ball.unfreeze();
            }
        }

    }
    public static void removeBall(Ball ball) {
        ballsInPlay.remove(ball);
    }
    public void spawnBall() {

        System.out.println(ballQueue.size());

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
        float x = selectedSpawner.x2 + (App.CELLSIZE / 2); // Use x of the selected spawner
        float y = selectedSpawner.y2 + (App.CELLSIZE / 2);
        ; // Use y of the selected spawner

        char colour = ballColor.charAt(0);
        float radius = 12;
        BoardManager boardManager = new BoardManager(app, imageLoader);
        Ball newBall = new Ball(app, ballImage, x, y, velocityX, velocityY, radius, boardManager, colour);
        ballsInPlay.add(newBall);
        hasSpawnedBall = true;
    }

  

    public static void addToQueueAgain(Ball ball) {
        ballQueue.add(ball.getColourAsString());
        updateBallDisplay();

    }

   

    public void reset() {
        initalised = false;
        ballsInPlay.clear();
        initializeBallQueue();
    }

    

}
