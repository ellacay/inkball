package inkball;
import java.nio.file.Files;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.data.*;

import java.util.Map;

public class Config {

    
    public String layout;
    public int time;
    public int spawnInterval;
    public double scoreIncrease;
    public double scoreDecrease;
    public String[] balls;
    public Map<String, Integer> scoreMultiplier;
    public Map<String, Integer> scoreDivider;
    static JSONObject gameConfig;
    public static void readConfig() {
        // Load the JSON file
        // Read the JSON file as a String
        String jsonContent = readFileAsString("config.json");

        // Parse the JSON content using Processing's JSONObject
        JSONObject gameConfig = JSONObject.parse(jsonContent);

        // Get the levels array from the JSON object
        JSONArray levels = gameConfig.getJSONArray("levels");

        // Iterate over each level object in the array
        for (int i = 0; i < levels.size(); i++) {
            JSONObject level = levels.getJSONObject(i);
            String layout = level.getString("layout");
            int time = level.getInt("time");
            int spawnInterval = level.getInt("spawn_interval");
            double scoreIncrease = level.getDouble("score_increase_from_hole_capture_modifier");
            double scoreDecrease = level.getDouble("score_decrease_from_wrong_hole_modifier");

            // deal with balls array
            JSONArray ballsJsonObject = level.getJSONArray("balls");
              // Convert JSONArray to String[]
            String[] balls = new String[ballsJsonObject.size()];

            for (int j = 0; j < ballsJsonObject.size(); j++) {
                balls[j] = ballsJsonObject.getString(j);
            }


           

            // printing

            System.out.println("Level Layout: " + layout);
            System.out.println("Level Time: " + time);
            System.out.println("Spawn Interval: " + spawnInterval);
            System.out.println("Score Increase from Hole Capture: " + scoreIncrease);
            System.out.println("Score Decrease from Hole Capture: " + scoreDecrease);
              // Print out the String array
        for (int m = 0; m < balls.length; m++) {
            System.out.println("Ball " + m + ": " + balls[m]);
        }
            System.out.println();
        }
    
        // Retrieve and print score increase from hole capture
        JSONObject increaseScores = gameConfig.getJSONObject("score_increase_from_hole_capture");
        System.out.println("Score Increase from Hole Capture:");
        for (Object keyObj : increaseScores.keys()) {
            String key = (String) keyObj; // Cast to String
            int value = increaseScores.getInt(key);
            System.out.println("  " + key + ": " + value);
        }
        System.out.println();

        // Retrieve and print score decrease from wrong hole
        JSONObject decreaseScores = gameConfig.getJSONObject("score_decrease_from_wrong_hole");
        System.out.println("Score Decrease from Wrong Hole:");
        for (Object keyObj : decreaseScores.keys()) {
            String key = (String) keyObj; // Cast to String
            int value = decreaseScores.getInt(key);
            System.out.println("  " + key + ": " + value);
        }
    }

    // Helper method to read a file and return its content as a String
    private static String readFileAsString(String fileName) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
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
        readConfig();
        
    }
}
