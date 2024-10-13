package inkball.managers;

import java.util.ArrayList;
import java.util.Arrays;
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
    public ImageLoader imageLoader;
    public static int score;
    public static char[][] board;
    public static Spawner spawner;
    public static List<Wall> walls = new ArrayList<>();
    public static List<Hole> holes = new ArrayList<>();
    public static List<Ball> balls = new ArrayList<>();
    public boolean ballSpawned = false;
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
        printWallGrid();
    }

    private void initializeWalls() {
        walls.clear();
        float cellSize = App.CELLSIZE;
        boolean[][] wallAdded = new boolean[board.length][board[0].length];

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                char cell = board[y][x];
                float xPos = x * cellSize;
                float yPos = (y * cellSize) + App.TOPBAR;

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
                if (board[y][x] == 'H') {
                    float xPos = x * cellSize + cellSize / 2;
                    float yPos = (y * cellSize) + App.TOPBAR + (cellSize / 2);
                    char colour = board[y][x + 1];
                    holes.add(new Hole(xPos, yPos, cellSize / 2, colour));
                }
            }
        }
    }

    public void addFinishedBall() {
        finishedBallCount++;
    }

    public boolean checkIfFinished() {
        if (finishedBallCount >= BallManager.ballsInPlay.size() + BallManager.ballQueue.size()) {
            System.out.println("Finished!");
            return true;
        }
        return false;
    }

    public void reset() {
        finishedBallCount = 0;
        walls.clear();
        holes.clear();
        balls.clear();
        loadBoard();
    }
public void printWallGrid() {
    int rows = board.length;
    int cols = board[0].length;
    char[][] grid = new char[rows][cols];

    // Initialize the grid with spaces
    for (int y = 0; y < rows; y++) {
        Arrays.fill(grid[y], ' ');
    }

    // Mark wall positions in the grid
    for (Wall wall : walls) {
        int x = (int) (wall.x1 / App.CELLSIZE);
        int y = (int) ((wall.y1 - App.TOPBAR) / App.CELLSIZE);
        if (x >= 0 && y >= 0 && y < rows && x < cols) {
            grid[y][x] = '#';
        }
    }

    // Print the grid
    for (int y = 0; y < rows; y++) {
        for (int x = 0; x < cols; x++) {
            System.out.print(grid[y][x]);
            if (x < cols - 1) System.out.print(" ");
        }
        System.out.println();
    }
}

    public static void increaseScore(int baseScore) {
        score += baseScore + 1;
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
                    float yPos = (y * cellSize) + yOffset;

                    switch (cell) {
                        case 'B':
                            if (!ballSpawned && x + 1 < board[y].length) {
                                char nextCell = board[y][x + 1];
                                spawnBallAtPosition(xPos, yPos, Character.toString(nextCell));
                                ballSpawned = true;
                            }
                            app.image(imageLoader.tile, xPos, yPos);
                            x++;
                            app.image(imageLoader.tile, xPos, yPos);
                            break;
                        case 'H':
                            handleHoleCell(x, y, xPos, yPos);
                            x++;
                            break;
                        case 'S':
                            app.image(imageLoader.entryPoint, xPos, yPos);
                            spawner = new Spawner(x, y, xPos, yPos);
                            break;
                        default:
                            if (y > 0 && board[y - 1][x] == 'H') {
                                x++;
                                break;
                            }
                            app.image(imageLoader.tile, xPos, yPos);
                            break;
                    }
                }
            }
            for (Wall wall : walls) {
                wall.display();
            }
        }
    }

    private void spawnBallAtPosition(float x, float y, String ballColor) {
        PImage ballImage = BallManager.getBallImage(ballColor, imageLoader);
        if (ballImage == null) {
            System.out.println("Ball image for color " + ballColor + " is null.");
            return;
        }

        float velocityX = App.random.nextBoolean() ? 2 : -2;
        float velocityY = App.random.nextBoolean() ? 2 : -2;
        char colour = ballColor.charAt(0);

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
