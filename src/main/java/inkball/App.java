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
        board = Config.setBoardArray();
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
    public void keyPressed(KeyEvent event){
        
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

    /**
     * Draw all elements in the game by current frame.
     */
	// @Override
    public void draw() {

        background(255);  // Clear the screen with a white background

        if (board != null) {
            for (char[] row : board) {
                System.out.println(java.util.Arrays.toString(row));
            }
        }
        int cellSize = 32;  // Size of each cell in the grid
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                char cell = board[y][x];
                float xPos = x * cellSize;
                float yPos = y * cellSize;

                // Draw different shapes/colors based on the character
                switch (cell) {
                    case 'X':
                        fill(0); 
                        rect(xPos, yPos, cellSize, cellSize);  
                        break;
                    case 'B':
                        fill(200); 
                        rect(xPos, yPos, cellSize, cellSize);  
                        break;
                    case 'H':
                        fill(262);  
                        rect(xPos, yPos, cellSize, cellSize);  
                        break;
                    case 'S':
                        fill(0, 255, 0);  
                        rect(xPos, yPos, cellSize, cellSize);  
                        break;
                    case '1':
                        fill(255, 0, 255);  
                        rect(xPos, yPos, cellSize, cellSize);  
                        break;
                    case '2':
                        fill(255, 4, 255); 
                        rect(xPos, yPos, cellSize, cellSize);  
                        break;
                    case '3':
                        fill(255, 200, 255);  
                        rect(xPos, yPos, cellSize, cellSize); 
                        break;
                    case '4':
                        fill(255, 100, 255);  
                        rect(xPos, yPos, cellSize, cellSize);  
                        break;
                    default:
                        fill(255, 0, 0);  
                        rect(xPos, yPos, cellSize, cellSize);  
                        break;
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

    }


    public static void main(String[] args) {
        PApplet.main("inkball.App");
       
        
      

       
        


    }

}
