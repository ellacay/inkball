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
                if (wall.isVertical()) {
                    xSpeed = -xSpeed; // Reverse horizontal direction
                } else if (wall.isHorizontal()) {
                    ySpeed = -ySpeed; // Reverse vertical direction
                }
                x -= xSpeed;
                y -= ySpeed;
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
