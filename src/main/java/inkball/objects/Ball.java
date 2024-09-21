package inkball.objects;

import processing.core.PApplet;
import processing.core.PImage;
import inkball.managers.BoardManager;

public class Ball {
    private PApplet app;
    private PImage image;
    private float x, y;
    private float xSpeed, ySpeed;
    private float radius;

    public Ball(PApplet app, PImage image, float x, float y, float xSpeed, float ySpeed, float radius) {
        this.app = app;
        this.image = image;
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.radius = radius;
    }

    public void update() {
        x += xSpeed;
        y += ySpeed;
        checkWallCollisions();
    }

    public void display() {
        app.image(image, x - radius, y - radius); // Adjust to center ball image
    }

    private void checkWallCollisions() {
        for (Wall wall : BoardManager.walls) {
            if (checkCollisionWithWall(wall)) {
                float overlapX = 0;
                float overlapY = 0;
    
                // Calculate overlap amounts
                if (wall.isVertical()) {
                    // Move the ball out of the wall horizontally
                    if (x < wall.x1) {
                        overlapX = (x - radius) - wall.x2; // Push left
                        System.out.println("4");
                    } else {
                        overlapX = (x - radius) + wall.x2; // Push right
                        System.out.println("3");
                    }
                    xSpeed = -xSpeed; // Reverse horizontal direction
                    x += overlapX; // Move ball out of the wall
                } else if (wall.isHorizontal()) {
                    // Move the ball out of the wall vertically
                    if (y < wall.y1) {
                        overlapY = (y - radius) - wall.y2; // Push up
                        System.out.println("1");
                    } else {
                        overlapY = (y + radius) - wall.y1; // Push down
                        System.out.println("2");
                    }
                    ySpeed = -ySpeed; // Reverse vertical direction
                    y += overlapY; // Move ball out of the wall
                }
            }
        }
    }
    
    private boolean checkCollisionWithWall(Wall wall) {
        float ballLeft = x - radius;
        float ballRight = x + radius;
        float ballTop = y - radius;
        float ballBottom = y + radius;

        float wallLeft = wall.x1;
        float wallRight = wall.x2;
        float wallTop = wall.y1;
        float wallBottom = wall.y2;

        boolean overlapX = (ballRight >= wallLeft && ballLeft <= wallRight);
        boolean overlapY = (ballBottom >= wallTop && ballTop <= wallBottom);

        return overlapX && overlapY;
    }
}
