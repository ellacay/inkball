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

public class BoardManager {
    private PApplet app;
    private ImageLoader imageLoader;
    public static int score;
    public static char[][] board;
    public static Spawner spawner;
    public static List<Wall> walls = new ArrayList<>();
    public static List<Hole> holes = new ArrayList<>();
    public static List<Ball> balls = new ArrayList<>(); // List to store balls

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
        float cellSize = App.CELLSIZE;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                char cell = board[y][x];
                float xPos = x * cellSize;
                float yPos = y * cellSize;
    
                // Check if the cell contains '1', '2', '3', or '4'
                if (cell == '1' || cell == '2' || cell == '3' || cell == '4') {
                    // Check if the cell to the left contains 'h'
                    if (x > 0 && board[y][x - 1] == 'h') {
                       continue;
                    } else {
                        // Add walls around the cell in all four directions
                        walls.add(new Wall(app,xPos, yPos, xPos + cellSize, yPos,cell)); // Top
                        walls.add(new Wall(app,xPos + cellSize, yPos, xPos + cellSize, yPos + cellSize,cell)); // Right
                        walls.add(new Wall(app,xPos + cellSize, yPos + cellSize, xPos, yPos + cellSize,cell)); // Bottom
                        walls.add(new Wall(app, xPos, yPos + cellSize, xPos, yPos,cell)); // Left
                    }
                }
    
                // Original condition: Add walls for 'X' cells
                else if (cell == 'X') {
                    float x1 = xPos;
                    float y1 = yPos;
                    float x2 = x1 + cellSize;
                    float y2 = y1 + cellSize;
                    // Add walls in all four directions around 'X'
                    walls.add(new Wall(app,x1, y1, x2, y1, '0')); // Top
                    walls.add(new Wall(app,x2, y1, x2, y2, '0')); // Right
                    walls.add(new Wall(app,x2, y2, x1, y2,'0')); // Bottom
                    walls.add(new Wall(app,x1, y2, x1, y1, '0')); // Left
                }
            }
        }
      
        // List<Wall> walls2 = new ArrayList<>();
        // walls2.add(new Wall(0.0f, 0.0f, 32.0f, 0.0f));
        // walls2.add(new Wall(32.0f, 0.0f, 32.0f, 32.0f));
        // walls2.add(new Wall(32.0f, 32.0f, 0.0f, 32.0f));
        // walls2.add(new Wall(0.0f, 32.0f, 0.0f, 0.0f));
        // walls2.add(new Wall(32.0f, 0.0f, 64.0f, 0.0f));
        // walls2.add(new Wall(64.0f, 0.0f, 64.0f, 32.0f));
        // walls2.add(new Wall(64.0f, 32.0f, 32.0f, 32.0f));
        // walls2.add(new Wall(32.0f, 32.0f, 32.0f, 0.0f));
        
        
        // printGrid(walls2, 80, 50);


    }
    public static void printGrid(List<Wall> walls, int width, int height) {
        char[][] grid = new char[height][width];

        // Initialize the grid with spaces
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = ' ';
            }
        }

        // Fill in the walls
        for (Wall wall : walls) {
            int x1 = (int) wall.x1;
            int y1 = (int) wall.y1;
            int x2 = (int) wall.x2;
            int y2 = (int) wall.y2;

            if (x1 == x2) { // Vertical wall
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    if (y >= 0 && y < height && x1 >= 0 && x1 < width) {
                        grid[y][x1] = '|';
                    }
                }
            } else if (y1 == y2) { // Horizontal wall
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    if (x >= 0 && x < width && y1 >= 0 && y1 < height) {
                        grid[y1][x] = '-';
                    }
                }
            }
        }

        // Print the grid
        for (char[] row : grid) {
            System.out.println(new String(row));
        }
    }
    public void increaseScore(int baseScore) {
        int increment = (int) (baseScore * ConfigLoader.scoreIncrease);
        score += increment;
        System.out.println("Score increased! Current score: " + score);
    }
    
    private void initializeHoles() {
        float cellSize = App.CELLSIZE;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                char cell = board[y][x];
                if (cell == 'H') {
                    float xPos = x * cellSize + cellSize / 2;
                    float yPos = y * cellSize + cellSize / 2;
                    char colour = board[y][x+1];
                    holes.add(new Hole(xPos, yPos, cellSize / 2, colour));
                }
            }
        }
    }

    public void displayBoard() {
     
        if (board != null) {
            int cellSize = App.CELLSIZE;

            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    char cell = board[y][x];
                    float xPos = x * cellSize;
                    float yPos = y * cellSize;
                    float xPosOffset = (x + 1) * cellSize;

                    switch (cell) {
                       
                        case 'B':
                            handleBallCell(x, y, xPos, yPos, xPosOffset);
                            x++;
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
                            if (y > 0) { 
                                char rightUpper = board[y - 1][x];
                                if (rightUpper == 'H') {
                                    x++;
                                    break;
                                }
                            }
                            app.image(imageLoader.tile, xPos, yPos);
                            break;
                    }
                }
            }
        }
        for (Wall wall : walls) {
        int status = wall.display();
        char color = wall.color;

        switch(color){
            
        case '1':
        if(status == 0){
            app.image(imageLoader.wall1, wall.x1, wall.y1);
            break;
        }
        else{
            app.image(imageLoader.smashedWall1, wall.x1, wall.y1);
            break;
        }
           
        case '2':
        if(status == 0){
            app.image(imageLoader.wall2, wall.x1, wall.y1);
            break;
        }
        else{
            app.image(imageLoader.smashedWall2, wall.x1, wall.y1);
            break;
        }
           
        case '3':
        if(status == 0){
            app.image(imageLoader.wall3, wall.x1, wall.y1);
            break;
        }
        else{
            app.image(imageLoader.smashedWall3, wall.x1, wall.y1);
            break;
        }
           
        case '4':
        if(status == 0){
            app.image(imageLoader.wall4, wall.x1, wall.y1);
            break;
        }
        else{
            app.image(imageLoader.smashedWall4, wall.x1, wall.y1);
            break;
        }
           
        }}
    }

    public void removeBall(Ball ball) {
        balls.remove(ball);
    }
    private void handleBallCell(int x, int y, float xPos, float yPos, float xPosOffset) {
        if (x + 1 < board[y].length) {
            char nextCell = board[y][x + 1];
            app.image(imageLoader.tile, xPos, yPos);
            switch (nextCell) {
                case '0':
                    app.image(imageLoader.ball0, xPos, yPos);
                    break;
                case '1':
                    app.image(imageLoader.ball1, xPos, yPos);
                    break;
                case '2':
                    app.image(imageLoader.ball2, xPos, yPos);
                    break;
                case '3':
                    app.image(imageLoader.ball3, xPos, yPos);
                    break;
                case '4':
                    app.image(imageLoader.ball4, xPos, yPos);
                    break;
                default:
                    app.image(imageLoader.tile, xPos, yPos);
                    break;
            }
            app.image(imageLoader.tile, xPosOffset, yPos);
            x++;
        } else {
            app.image(imageLoader.tile, xPos, yPos);
        }
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
