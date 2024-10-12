package inkball.managers;

import java.util.ArrayList;
import java.util.List;

import inkball.App;
import inkball.loaders.ConfigLoader;
import inkball.loaders.ImageLoader;
import inkball.objects.Ball;
import inkball.objects.Wall;
import inkball.objects.Spawner;
import inkball.objects.Hole;
import processing.core.PApplet;
import processing.core.PImage;

public class BoardManager {
    private PApplet app;
    private ImageLoader imageLoader;
    public static int score;
    public static char[][] board;
    public static Spawner spawner;
    public static List<Wall> walls = new ArrayList<>();
    public static List<Hole> holes = new ArrayList<>();
    public static List<Ball> balls = new ArrayList<>();
    public boolean ballSpawned = false; // Flag to track if a ball has been spawned
    private int finishedBallCount;

    public BoardManager(PApplet app, ImageLoader imageLoader) {
        this.app = app;
        this.imageLoader = imageLoader;
    }

    public void loadBoard() {
        board = ConfigLoader.setBoardArray();
        if (board == null) {
            System.err.println("Failed to load board configuration.");
            app.exit();
        }
        initializeWalls();
        initializeHoles();
    }

    private void initializeWalls() {
        walls.clear();
        float cellSize = App.CELLSIZE;
        boolean[][] wallAdded = new boolean[board.length][board[0].length];

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                char cell = board[y][x];
                float xPos = x * cellSize;
                float yPos = (y * cellSize) + App.TOPBAR; // Apply the top bar offset

                boolean isPreviousCellHole = (x > 0 && board[y][x - 1] == 'H');

                if ((cell >= '1' && cell <= '4') && !wallAdded[y][x] && !isPreviousCellHole) {
                    walls.add(new Wall(app, xPos, yPos, xPos + cellSize, yPos, cell, imageLoader));
                    wallAdded[y][x] = true;
                } else if (cell == 'X' && !wallAdded[y][x]) {
                    walls.add(new Wall(app, xPos, yPos, xPos + cellSize, yPos, '0', imageLoader));
                    wallAdded[y][x] = true;
                }
            }
        }
    }

    private void initializeHoles() {
        float cellSize = App.CELLSIZE;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                char cell = board[y][x];
                if (cell == 'H') {
                    float xPos = x * cellSize + cellSize / 2;
                    float yPos = (y * cellSize) + App.TOPBAR + (cellSize / 2); // Apply the top bar offset
                    char colour = board[y][x + 1];
                    holes.add(new Hole(xPos, yPos, cellSize / 2, colour));
                }
            }
        }
    }

    public void addFinishedBall() {
        finishedBallCount++; // Increment the finished ball count
    }

    public boolean checkIfFinished() {
        if (finishedBallCount >= BallManager.ballsInPlay.size() + BallManager.ballQueue.size()) {
            System.out.println("Finished!"); // Print when all balls are finished
            return true;
        }
        return false;
    }
    public void reset() {
        finishedBallCount = 0; // Reset finished ball count
        walls.clear(); // Clear walls if needed
        holes.clear(); // Clear holes if needed
        balls.clear(); // Clear any existing balls
        // Optionally, reload the board
        loadBoard(); // If you want to reload the initial state
    }
    

    public static void increaseScore(int baseScore) {
        int increment = (int) (score + 1);
        score += increment;
        System.out.println("Score increased! Current score: " + score);
    }

    public void displayBoard() {
        int yOffset = App.TOPBAR;
        if (board != null) {
            int cellSize = App.CELLSIZE;

            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    char cell = board[y][x];
                    float xPos = x * cellSize;
                    float yPos = (y * cellSize) + yOffset; // Apply the offset

                    switch (cell) {
                        case 'B':
                            // Spawn only if no ball has been spawned yet
                            if (!ballSpawned && x + 1 < board[y].length) {
                                char nextCell = board[y][x + 1];
                                String ballColor = Character.toString(nextCell);
                                spawnBallAtPosition(xPos, yPos, ballColor);
                                ballSpawned = true; // Set flag to true after spawning
                            }
                            app.image(imageLoader.tile, xPos, yPos);
                            x++; // Move to the next cell to skip the color cell
                            app.image(imageLoader.tile, xPos, yPos);
                            break;
                        case 'H':
                            handleHoleCell(x, y, xPos, yPos); // Apply offset
                            x++;
                            break;
                        case 'S':
                            app.image(imageLoader.entryPoint, xPos, yPos);
                            spawner = new Spawner(x, y, xPos, yPos); // Apply offset
                            break;
                        default:if (y > 0) {
                            char rightUpper = board[y - 1][x];
                            if (rightUpper == 'H') {
                                x++;
                                break;
                            }
                        }                            app.image(imageLoader.tile, xPos, yPos); // Apply offset
                            break;
                    }
                }
            }
            for (Wall wall : walls) {
                wall.display(); // Pass the top bar height for wall rendering
            }
        }
    }

    private void spawnBallAtPosition(float x, float y, String ballColor) {
        PImage ballImage = BallManager.getBallImage(ballColor, this.imageLoader);
        if (ballImage == null) {
            System.out.println("Ball image for color " + ballColor + " is null.");
            return;
        }

        float velocityX = App.random.nextBoolean() ? 2 : -2;
        float velocityY = App.random.nextBoolean() ? 2 : -2;
        char colour = 0;

        switch (ballColor) {
            case "0": colour = 0; break;
            case "1": colour = 1; break;
            case "2": colour = 2; break;
            case "3": colour = 3; break;
            case "4": colour = 4; break;
        }

        float radius = 10;

        Ball newBall = new Ball(app, ballImage, x, y, velocityX, velocityY, radius, this, colour);
        BallManager.ballsInPlay.add(newBall);
    }

    public void removeBall(Ball ball) {
        balls.remove(ball);
    }

    private void handleHoleCell(int x, int y, float xPos, float yPos) {
        if (x + 1 < board[y].length) {
            char nextCell = board[y][x + 1];
            switch (nextCell) {
                case '0': app.image(imageLoader.hole0, xPos, yPos); break;
                case '1': app.image(imageLoader.hole1, xPos, yPos); break;
                case '2': app.image(imageLoader.hole2, xPos, yPos); break;
                case '3': app.image(imageLoader.hole3, xPos, yPos); break;
                case '4': app.image(imageLoader.hole4, xPos, yPos); break;
                default: app.image(imageLoader.tile, xPos, yPos); break;
            }
        } else {
            app.image(imageLoader.tile, xPos, yPos);
        }
    }
}
