package inkball;
import processing.core.PApplet;

public class BoardManager {
    private PApplet app;
    private ImageLoader imageLoader;
    public static char[][] board;

    public BoardManager(PApplet app, ImageLoader imageLoader) {
        this.app = app;
        this.imageLoader = imageLoader;
    }

    public void loadBoard() {
        board = Config.setBoardArray();
        if (board == null) {
            System.err.println("Failed to load board configuration.");
            app.exit();
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
                        case 'X':
                            app.image(imageLoader.wall0, xPos, yPos);
                            break;
                        case 'B':
                        handleBallCell(x,y,xPos,yPos,xPosOffset);

                            break;
                        case 'H':
                            handleHoleCell(x,y,xPos,yPos);
                            x++;
                            break;
                        case 'S':
                            app.image(imageLoader.entryPoint, xPos, yPos);
                            break;
                        case '1':
                            app.image(imageLoader.wall1, xPos, yPos);
                            break;
                        case '2':
                            app.image(imageLoader.wall2, xPos, yPos);
                            break;
                        case '3':
                            app.image(imageLoader.wall3, xPos, yPos);
                            break;
                        case '4':
                            app.image(imageLoader.wall4, xPos, yPos);
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
    
    }}