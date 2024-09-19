package inkball;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;

public class Config {

    
    public String layout;
    public int time;
    public int spawnInterval;
    public int scoreIncrease;
    public int scoreDecrease;
    public Map<String, Integer> scoreMultiplier;
    public Map<String, Integer> scoreDivider;

    public static void readConfig() {
        

    }

    public String[] addSurroundingWall() {
        return new String[]{""};
    }

    public String[] addTiles() {
        return new String[]{""};
    }

    public String[] addBalls() {
        return new String[]{""};
    }

    public String[] addHoles() {
        return new String[]{""};
    }

    public String[] addSpawners() {
        return new String[]{""};
    }

    public static char[][] setBoardArray() {
        File file = new File("level1.txt");


        int rows = 0;
        int cols = 0;


        List<String> lines = new ArrayList<>();


        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines.add(line);
                if (line.length() > cols) {
                    cols = line.length();
                }
                rows++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        char[][] board = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                board[i][j] = line.charAt(j);
            }
        }
        return board;
    }

    public static void main(String[] args) {

        
    }
}
