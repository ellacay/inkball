package inkball.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import inkball.App;
import inkball.loaders.ConfigLoader;
import inkball.loaders.ImageLoader;
import inkball.objects.Ball;
import inkball.objects.Wall;
import inkball.objects.Spawner;
import inkball.objects.Hole;
import processing.core.PImage;

/**
 * Manages the game board, including loading configurations, initializing objects,
 * and displaying the board state.
 * This class handles walls, holes, balls, and spawners as defined by the game board.
 */
public class BoardManager {
    private App app;
    public ImageLoader imageLoader;
    public static int score;
    public static int levelScore;
    public static char[][] board;
    public static Spawner spawner;
    public static List<Wall> walls = new ArrayList<>();
    public static List<Hole> holes = new ArrayList<>();

    public static List<Ball> startBalls = new ArrayList<>();
    public static List<Spawner> spawners = new ArrayList<>();
    public boolean ballSpawned = false;
    public boolean ballIsNull = false;
    private static int finishedBallCount;
    boolean ballsSpawned = false;

    /**
     * Constructs a BoardManager with references to the application and image loader.
     *
     * @param app        The application instance.
     * @param imageLoader The image loader used to fetch images for the board.
     */
    public BoardManager(App app, ImageLoader imageLoader) {
        this.app = app;
        this.imageLoader = imageLoader;
    }

    /**
     * Loads the game board configuration and initializes walls, balls, and holes.
     * If the board cannot be loaded, the application will exit.
     */
    public void loadBoard() {
        board = ConfigLoader.setBoardArray(app.level);
        if (board == null) {
            System.err.println("Failed to load board configuration.");
            app.exit();
        }
        initializeWalls();
        initializeBalls();
        initializeHoles();
        spawners.clear();
    }

    /**
     * Initializes the balls to be placed on the board based on the configuration.
     * Clears any previously initialized balls before setting up the new ones.
     */
    private void initializeBalls() {
        startBalls.clear(); // Clear previous balls
        float cellSize = App.CELLSIZE;

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                char cell = board[y][x];
                float xPos = x * cellSize;
                float yPos = (y * cellSize) + App.TOPBAR;

                if (cell == 'B') { // Check for 'B' cells
                    char ballColour = '0'; // Default to grey
                    if (x + 1 < board[y].length) {
                        ballColour = board[y][x + 1]; // Get color from the next cell
                    }
                    startBalls.add(new Ball(app, Ball.getBallImage(Character.toString(ballColour), imageLoader),
                            xPos, yPos, 0, 0, cellSize / 2, this, ballColour));
                }
            }
        }
    }

    /**
     * Initializes the walls on the board based on the configuration.
     * This method reads the board's cells and adds walls as needed.
     */
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

    /**
     * Initializes the holes on the board based on the configuration.
     * This method places holes at the appropriate positions defined in the board layout.
     */
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

    /**
     * Gets the count of finished balls.
     *
     * @return The number of finished balls.
     */
    public static int getFinishedBallCount() {
        return finishedBallCount;
    }

    /**
     * Sets the count of finished balls to the specified value.
     *
     * @param count The new count of finished balls.
     */
    public static void setFinishedBallCount(int count) {
        finishedBallCount = count;
    }

    /**
     * Increments the count of finished balls by one.
     */
    public void addFinishedBall() {
        finishedBallCount++;
    }

    /**
     * Checks if all balls have been captured and the level is finished.
     *
     * @return True if finished, otherwise false.
     */
    public boolean checkIfFinished() {
        return BallManager.ballsInPlay.size() == 0 && BallManager.ballQueue.size() == 0;
    }

    /**
     * Resets the board manager, clearing walls and holes,
     * and reloading the board configuration.
     */
    public void reset() {
        finishedBallCount = 0;
        walls.clear();
        holes.clear();
        loadBoard();
    }

    /**
     * Increases the score based on the color of the specified ball.
     *
     * @param ball The ball to calculate score increase for.
     */
    public static void increaseScore(Ball ball) {
        score += (App.increaseScore.get(ball.getColourString())) * App.increaseScoreMultipler;
    }

    /**
     * Decreases the score based on the color of the specified ball.
     *
     * @param ball The ball to calculate score decrease for.
     */
    public static void decreaseScore(Ball ball) {
        score -= (App.decreaseScore.get(ball.getColourString())) * App.decreaseScoreMultipler;
    }

    /**
     * Displays the current state of the board, including walls, holes, and balls.
     * This method handles rendering each component based on the board configuration.
     */
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

            Iterator<Wall> iterator = walls.iterator();
            while (iterator.hasNext()) {
                Wall wall = iterator.next();
                wall.display();
                if (wall.isRemoved()) {
                    iterator.remove(); // Safe removal
                }
            }

            for (Ball ball : startBalls) {
                if (!ball.spawnedAtStart) {
                    spawnBallAtPosition(ball);
                    ball.spawnedAtStart = true;
                }
            }
        }
    }

    /**
     * Spawns a ball at the specified position.
     *
     * @param ball The ball to spawn.
     */
    public void spawnBallAtPosition(Ball ball) {
        PImage ballImage = Ball.getBallImage(ball.getColourAsString(), imageLoader);
        if (ballImage == null) {
            System.out.println("Ball image for color " + ball.getColour() + " is null.");
            ballIsNull = true;
            return;
        }

        float velocityX = App.random.nextBoolean() ? 2 : -2;
        float velocityY = App.random.nextBoolean() ? 2 : -2;
        char colour = (ball.getColourAsString()).charAt(0);
        float radius = 12;

        Ball newBall = new Ball(app, ballImage, ball.getX(), ball.getY(), velocityX, velocityY, radius, this, colour);
        BallManager.ballsInPlay.add(newBall);
    }

    /**
     * Handles the rendering of a hole cell based on its configuration.
     *
     * @param x     The x-coordinate of the hole cell.
     * @param y     The y-coordinate of the hole cell.
     * @param xPos  The x position for rendering.
     * @param yPos  The y position for rendering.
     */
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
