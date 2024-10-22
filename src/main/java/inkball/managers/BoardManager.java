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
    public ImageLoader imageLoader;
    public static int score;
    public static int levelScore;
    public static char[][] board;
    public static Spawner spawner;
    public static List<Wall> walls = new ArrayList<>();
    public static List<Hole> holes = new ArrayList<>();
    public static List<Ball> balls = new ArrayList<>();
    public static List<Spawner> spawners = new ArrayList<>();
    public boolean ballSpawned = false;
    private static int finishedBallCount;

    public BoardManager(PApplet app, ImageLoader imageLoader) {
        this.app = app;
        this.imageLoader = imageLoader;
    }

    public void loadBoard() {
        board = ConfigLoader.setBoardArray(App.level);
        if (board == null) {
            System.err.println("Failed to load board configuration.");
            app.exit();
        }
        initializeWalls();
        initializeHoles();
        spawners.clear();

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

                boolean isPreviousCellBall = (x > 0 && board[y][x - 1] == 'B');
                boolean isPreviousCellHole = (x > 0 && board[y][x - 1] == 'H');
                if ((cell >= '1' && cell <= '4') && !wallAdded[y][x] && !isPreviousCellBall && !isPreviousCellHole) {
                    walls.add(new Wall(app, xPos, yPos, xPos + cellSize, yPos + cellSize, cell, imageLoader));
                    wallAdded[y][x] = true;
                } else if (cell == 'X' && !wallAdded[y][x] && !isPreviousCellBall && !isPreviousCellHole) {
                    walls.add(new Wall(app, xPos, yPos, xPos + cellSize, yPos + cellSize, '0', imageLoader));
                    wallAdded[y][x] = true;
                } else if (isPreviousCellBall || isPreviousCellHole) {
                    cell = ' ';
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

    public static int getFinishedBallCount() {
        return finishedBallCount;
    }

    public static void setFinishedBallCount(int count) {
        finishedBallCount = count;
    }

    public void addFinishedBall() {
        finishedBallCount++;

    }

    public boolean checkIfFinished() {

        if (finishedBallCount >= BallManager.ballsInPlay.size() +
                BallManager.ballQueue.size()) {
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

    public static void increaseScore(Ball ball) {
        System.out.println("Increase");
        System.out.println("Ball colour: " + ball.getColourString());
        System.out.println("Ball score: " + App.increaseScore.get(ball.getColourString()));
        System.out.println("Multiplyer: " + App.increaseScoreMultipler);
        System.out.println("Before score: " + score);
        score += (App.increaseScore.get(ball.getColourString())) * App.increaseScoreMultipler;
        System.out.println("Current score: " + score);
        System.out.println();
    }

  
    public static void decreaseScore(Ball ball) {
        String colorKey = ball.getColourString();
        if (App.decreaseScore.containsKey(colorKey)) {
            score -= (App.decreaseScore.get(ball.getColourString())) * App.decreaseScoreMultipler;
        }
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
                            app.image(imageLoader.tile, xPos, yPos);
                            if (!ballSpawned && x + 1 < board[y].length) {

                                char nextCell = board[y][x + 1];
                                spawnBallAtPosition(xPos, yPos, Character.toString(nextCell));
                                ballSpawned = true;

                            }

                            break;
                        case 'H':
                            handleHoleCell(x, y, xPos, yPos);
                            x++;
                            break;
                        case 'S':
                            app.image(imageLoader.entryPoint, xPos, yPos);
                            spawner = new Spawner(x, y, xPos, yPos);
                            spawners.add(spawner);
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

            List<Wall> wallsToRemove = new ArrayList<>();
            for (Wall wall : walls) {
                wall.display();
                if (wall.isRemoved()) {
                    wallsToRemove.add(wall);
                }
            }
            walls.removeAll(wallsToRemove); // Remove all marked walls after iteration

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
                case '0':
                    app.image(imageLoader.hole0, xPos, yPos);
                    break;
                case '1':
                    app.image(imageLoader.hole1, xPos, yPos);
                    break;
                case '2':
                    app.image(imageLoader.hole2, xPos, yPos);
                    break;
                case '3':
                    app.image(imageLoader.hole3, xPos, yPos);
                    break;
                case '4':
                    app.image(imageLoader.hole4, xPos, yPos);
                    break;
                default:
                    app.image(imageLoader.tile, xPos, yPos);
                    break;
            }
        } else {
            app.image(imageLoader.tile, xPos, yPos);
        }
    }
}
