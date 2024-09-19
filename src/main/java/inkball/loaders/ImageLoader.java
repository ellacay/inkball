package inkball.loaders;

import processing.core.PApplet;
import processing.core.PImage;

public class ImageLoader {
    private PApplet app;
    public PImage ball0, ball1, ball2, ball3, ball4;
    public PImage entryPoint, hole0, hole1, hole2, hole3, hole4;
    public PImage inkballSpritesheet, tile, wall0, wall1, wall2, wall3, wall4;

    public ImageLoader(PApplet app) {
        this.app = app;
    }

    public void loadImages() {
        ball0 = app.loadImage("src/main/resources/inkball/ball0.png");
        ball1 = app.loadImage("src/main/resources/inkball/ball1.png");
        ball2 = app.loadImage("src/main/resources/inkball/ball2.png");
        ball3 = app.loadImage("src/main/resources/inkball/ball3.png");
        ball4 = app.loadImage("src/main/resources/inkball/ball0.png");
        entryPoint = app.loadImage("src/main/resources/inkball/entrypoint.png");
        hole0 = app.loadImage("src/main/resources/inkball/hole0.png");
        hole1 = app.loadImage("src/main/resources/inkball/hole1.png");
        hole2 = app.loadImage("src/main/resources/inkball/hole2.png");
        hole3 = app.loadImage("src/main/resources/inkball/hole3.png");
        hole4 = app.loadImage("src/main/resources/inkball/hole4.png");
        inkballSpritesheet = app.loadImage("src/main/resources/inkball/inkball_spritesheet.png");
        tile = app.loadImage("src/main/resources/inkball/tile.png");
        wall0 = app.loadImage("src/main/resources/inkball/wall0.png");
        wall1 = app.loadImage("src/main/resources/inkball/wall1.png");
        wall2 = app.loadImage("src/main/resources/inkball/wall2.png");
        wall3 = app.loadImage("src/main/resources/inkball/wall3.png");
        wall4 = app.loadImage("src/main/resources/inkball/wall4.png");
    }
}
