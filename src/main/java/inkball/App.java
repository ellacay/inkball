package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App extends PApplet {
    private PImage ball0;
    private PImage ball1;
    private PImage ball2;
    private PImage ball3;
    private PImage ball4;
    private PImage entryPoint;
    private PImage hole0;
    private PImage hole1;
    private PImage hole2;
    private PImage hole3;
    private PImage hole4;
    private PImage inkballSpritesheet;
    private PImage tile;
    private PImage wall0;
    private PImage wall1;
    private PImage wall2;
    private PImage wall3;
    private PImage wall4;

    public static char[][] board;

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
        loadImages();
        board = Config.setBoardArray(); // Ensure this returns a valid char[][]

        if (board == null) {
            System.err.println("Failed to load board configuration.");
            exit(); // Exit if board is not properly initialized
        }
		//See PApplet javadoc:
		//loadJSONObject(configPath)
		// the image is loaded from relative path: "src/main/resources/inkball/..."
		/*try {
            result = loadImage(URLDecoder.decode(this.getClass().getResource(filename+".png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }*/
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKey() == 'r') {
            restartGame();
        }
    }

    private void restartGame() {
        Config.setBoardArray();
        System.out.println("stop");
        loop(); 
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held
		
		// remove player-drawn line object if right mouse button is held 
		// and mouse position collides with the line
    }

    @Override
    public void mouseReleased(MouseEvent e) {
		
    }

    public void loadImages(){
        ball0 = loadImage("src/main/resources/inkball/ball0.png");
        ball1 = loadImage("src/main/resources/inkball/ball1.png");
        ball2 = loadImage("src/main/resources/inkball/ball2.png");
        ball3 = loadImage("src/main/resources/inkball/ball3.png");
        ball4 = loadImage("src/main/resources/inkball/ball0.png");
        entryPoint = loadImage("src/main/resources/inkball/entrypoint.png");
        hole0 = loadImage("src/main/resources/inkball/hole0.png");
        hole1 = loadImage("src/main/resources/inkball/hole1.png");
        hole2 = loadImage("src/main/resources/inkball/hole2.png");
        hole3 = loadImage("src/main/resources/inkball/hole3.png");
        hole4 = loadImage("src/main/resources/inkball/hole4.png");
        inkballSpritesheet = loadImage("src/main/resources/inkball/inkball_spritesheet.png");
        tile = loadImage("src/main/resources/inkball/tile.png");
        wall0 = loadImage("src/main/resources/inkball/wall0.png");
        wall1 = loadImage("src/main/resources/inkball/wall1.png");
        wall2 = loadImage("src/main/resources/inkball/wall2.png");
        wall3 = loadImage("src/main/resources/inkball/wall3.png");
        wall4 = loadImage("src/main/resources/inkball/wall4.png");
      
    }

 
	@Override
    public void draw() {
        background(255);

        if (board != null) {
            int cellSize = 32; 

            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    char cell = board[y][x];
                    float xPos = x * cellSize;
                    float yPos = y * cellSize;
                    float xPosOffset = (x+1) * cellSize;
    
                    switch (cell) {
                        case 'X':
                            image(wall0, xPos, yPos);
                            break;
                        case 'B':
                            if (x + 1 < board[y].length) {
                                char nextCell = board[y][x + 1];
                                image(tile, xPos,yPos);
                                switch (nextCell) {
                                    case '0':
                                        image(ball0, xPos, yPos);
                                        break;
                                    case '1':
                                        image(ball1, xPos, yPos);
                                    
                                        break;
                                    case '2':
                                        image(ball2, xPos, yPos);
                                        break;
                                    case '3':
                                        image(ball3, xPos, yPos);
                                        break;
                                    case '4':
                                        image(ball4, xPos, yPos);
                                        break;
                                    default:
                                        image(tile, xPos, yPos);
                                        break;
                                }
                                image(tile, xPosOffset,yPos);
                                x++;
                            } else {
                                image(tile, xPos, yPos);
                            }
                            break;
                        case 'H':
                            if (x + 1 < board[y].length) {
                                char nextCell = board[y][x + 1];
                                switch (nextCell) {
                                    case '0':
                                        image(hole0, xPos, yPos);
                                        break;
                                    case '1':
                                        image(hole1, xPos, yPos);
                                        break;
                                    case '2':
                                        image(hole2, xPos, yPos);
                                        break;
                                    case '3':
                                        image(hole3, xPos, yPos);
                                        break;
                                    case '4':
                                        image(hole4, xPos, yPos);
                                        break;
                                    default:
                                        image(tile, xPos, yPos);
                                        break;
                                }
                                x++;
                            } else {
                                image(tile, xPos, yPos);
                            }
                            break;
                        case 'S':
                            image(entryPoint, xPos, yPos);
                            break;
                        case '1':
                            image(wall1, xPos, yPos);
                            break;
                        case '2':
                            image(wall2, xPos, yPos);
                            break;
                        case '3':
                            image(wall3, xPos, yPos);
                            break;
                        case '4':
                            image(wall4, xPos, yPos);
                            break;
                        default:
                            char rightUpper = board[y-1][x];
                    
                            if(rightUpper == 'H'){
                                x++;
                                break;
                            }
                            image(tile, xPos, yPos);
                            break;
                    }
                }
            }
        }
    }


            //----------------------------------
            //display Board for current level:
            //----------------------------------
            //TODO

            //----------------------------------
            //display score
            //----------------------------------
            //TODO
            
            //----------------------------------
            //----------------------------------
            //display game end message

    


    public static void main(String[] args) {
        PApplet.main("inkball.App");
       
        
      

       
        


    }

}
