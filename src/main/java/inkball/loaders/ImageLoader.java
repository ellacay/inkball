package inkball.loaders;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Handles loading and resizing images used in the Inkball game. 
 * This class provides methods to load images from specified paths, 
 * and it maintains references to various game-related images.
 */
public class ImageLoader {
    private PApplet app;
    public PImage ball0, ball1, ball2, ball3, ball4;
    public PImage entryPoint, hole0, hole1, hole2, hole3, hole4;
    public PImage inkballSpritesheet, tile, wall0, wall1, wall2, wall3, wall4, smashedWall0, smashedWall1, 
                  smashedWall2, smashedWall3, smashedWall4;

    int newWidth = 32;
    int newHeight = 32;

    /**
     * Constructs an ImageLoader instance with the specified PApplet.
     *
     * @param app The PApplet instance used for loading images.
     */
    public ImageLoader(PApplet app) {
        this.app = app;
    }

    /**
     * Loads all game images from their respective file paths and resizes them to the specified dimensions.
     */
    public void loadImages() {
        ball0 = app.loadImage("src/main/resources/inkball/ball0.png");
        ball1 = app.loadImage("src/main/resources/inkball/ball1.png");
        ball2 = app.loadImage("src/main/resources/inkball/ball2.png");
        ball3 = app.loadImage("src/main/resources/inkball/ball3.png");
        ball4 = app.loadImage("src/main/resources/inkball/ball4.png");
        entryPoint = loadAndResizeImage("src/main/resources/inkball/entrypoint.png", newWidth, newHeight);
        hole0 = app.loadImage("src/main/resources/inkball/hole0.png");
        hole1 = app.loadImage("src/main/resources/inkball/hole1.png");
        hole2 = app.loadImage("src/main/resources/inkball/hole2.png");
        hole3 = app.loadImage("src/main/resources/inkball/hole3.png");
        hole4 = app.loadImage("src/main/resources/inkball/hole4.png");

        inkballSpritesheet = loadAndResizeImage("src/main/resources/inkball/inkball_spritesheet.png", newWidth, newHeight);
        tile = loadAndResizeImage("src/main/resources/inkball/tile.png", newWidth, newHeight);
        wall0 = loadAndResizeImage("src/main/resources/inkball/wall0.png", newWidth, newHeight);
        wall1 = loadAndResizeImage("src/main/resources/inkball/wall1.png", newWidth, newHeight);
        wall2 = loadAndResizeImage("src/main/resources/inkball/wall2.png", newWidth, newHeight);
        wall3 = loadAndResizeImage("src/main/resources/inkball/wall3.png", newWidth, newHeight);
        wall4 = loadAndResizeImage("src/main/resources/inkball/wall4.png", newWidth, newHeight);
        smashedWall0 = loadAndResizeImage("src/main/resources/inkball/SmashedTile0.png", newWidth, newHeight);
        smashedWall1 = loadAndResizeImage("src/main/resources/inkball/SmashedTile1.png", newWidth, newHeight);
        smashedWall2 = loadAndResizeImage("src/main/resources/inkball/SmashedTile2.png", newWidth, newHeight);
        smashedWall3 = loadAndResizeImage("src/main/resources/inkball/SmashedTile3.png", newWidth, newHeight);
        smashedWall4 = loadAndResizeImage("src/main/resources/inkball/SmashedTile4.png", newWidth, newHeight);

        checkImageLoad(smashedWall0, "smashed0");
        checkImageLoad(smashedWall1, "smashed1");
        checkImageLoad(smashedWall2, "smashed2");
        checkImageLoad(smashedWall3, "smashed3");
        checkImageLoad(smashedWall4, "smashed4");
    }

    /**
     * Loads an image from the specified path and resizes it to the given dimensions.
     *
     * @param path     The file path of the image to load.
     * @param newWidth The desired width of the loaded image.
     * @param newHeight The desired height of the loaded image.
     * @return The loaded and resized PImage, or null if the image could not be loaded.
     */
    private PImage loadAndResizeImage(String path, int newWidth, int newHeight) {
        PImage img = app.loadImage(path);
        if (img != null) {
            img.resize(newWidth, newHeight);
        }
        return img;
    }

    /**
     * Checks if an image was loaded successfully and prints an error message if it was not.
     *
     * @param img     The PImage to check.
     * @param imgName The name of the image for error reporting.
     */
    public void checkImageLoad(PImage img, String imgName) {
        if (img == null) {
            System.err.println("Failed to load image: " + imgName);
        }
    }
}
