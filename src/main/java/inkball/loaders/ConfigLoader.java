package inkball.loaders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Responsible for loading and parsing game configuration data from JSON and text files.
 * This class manages the settings for different game levels, including scoring rules and layout information.
 */
public class ConfigLoader {

    /** Flag indicating if extension features are enabled. */
    public static boolean extensionFeature = true;

    /**
     * Represents the configuration for a specific level in the game.
     */
    public static class LevelConfig {
        public int level;
        public String layout;
        public int time;
        public int spawnInterval;
        public Map<String, Integer> scoreDecreaseMap = new HashMap<>();
        public Map<String, Integer> scoreIncreaseMap = new HashMap<>();
        public double scoreIncreaseMultipler;
        public double scoreDecreaseMultipler;
        public String[] balls;

        /**
         * Constructs a LevelConfig object with the specified parameters.
         *
         * @param layout                 The layout of the level.
         * @param time                   The time limit for the level.
         * @param spawnInterval          The interval for spawning balls.
         * @param scoreIncrease          A map of score increases from ball captures.
         * @param scoreDecrease          A map of score decreases from wrong captures.
         * @param scoreIncreaseMultipler  The multiplier for score increases.
         * @param scoreDecreaseMultipler  The multiplier for score decreases.
         * @param balls                  An array of balls available in the level.
         */
        public LevelConfig(String layout, int time, int spawnInterval, Map<String, Integer> scoreIncrease,
                           Map<String, Integer> scoreDecrease, double scoreIncreaseMultipler, 
                           double scoreDecreaseMultipler, String[] balls) {
            this.layout = layout;
            this.time = time;
            this.spawnInterval = spawnInterval;
            this.scoreIncreaseMap = scoreIncrease; // Initialize here
            this.scoreDecreaseMap = scoreDecrease; // Initialize here
            this.scoreIncreaseMultipler = scoreIncreaseMultipler;
            this.scoreDecreaseMultipler = scoreDecreaseMultipler;
            this.balls = balls;
        }
    }

    private static List<LevelConfig> levelsConfig = new ArrayList<>();

    /**
     * Reads the configuration from the JSON file and initializes the game settings.
     * It loads the level configurations and the scoring rules from the specified JSON file.
     */
    public static void readConfig() {
        String jsonContent = readFileAsString("config.json");
        JSONObject gameConfig = JSONObject.parse(jsonContent);

        JSONArray levels = gameConfig.getJSONArray("levels");

        // Read score increase map from the JSON
        Map<String, Integer> scoreIncreaseMap = new HashMap<>();
        JSONObject scoreIncreaseJson = gameConfig.getJSONObject("score_increase_from_hole_capture");
        @SuppressWarnings("unchecked")
        Set<String> increaseKeys = scoreIncreaseJson.keys(); // Get keys
        for (String key : increaseKeys) {
            scoreIncreaseMap.put(key, scoreIncreaseJson.getInt(key));
        }

        // Read score decrease map from the JSON
        Map<String, Integer> scoreDecreaseMap = new HashMap<>();
        JSONObject scoreDecreaseJson = gameConfig.getJSONObject("score_decrease_from_wrong_hole");
        @SuppressWarnings("unchecked")
        Set<String> decreaseKeys = scoreDecreaseJson.keys(); // Get keys
        for (String key : decreaseKeys) {
            scoreDecreaseMap.put(key, scoreDecreaseJson.getInt(key)); // Fixed here
        }

        for (int i = 0; i < levels.size(); i++) {
            JSONObject level = levels.getJSONObject(i);
            String layout = level.getString("layout");
            int time = level.getInt("time");
            int spawnInterval = level.getInt("spawn_interval");

            double scoreIncreaseMultipler = level.getDouble("score_increase_from_hole_capture_modifier");
            double scoreDecreaseMultipler = level.getDouble("score_decrease_from_wrong_hole_modifier");
            JSONArray ballsJsonObject = level.getJSONArray("balls");

            String[] balls = new String[ballsJsonObject.size()];
            for (int j = 0; j < ballsJsonObject.size(); j++) {
                balls[j] = ballsJsonObject.getString(j);
            }

            // Use the map for score increases and decreases
            levelsConfig.add(new LevelConfig(layout, time, spawnInterval, scoreIncreaseMap, scoreDecreaseMap,
                    scoreIncreaseMultipler, scoreDecreaseMultipler, balls));
        }
        try {
            extensionFeature = gameConfig.getBoolean("extensionEnabled");
        } catch (Exception e) {
            System.out.println("Extension not enabled");
        }
    }

    /**
     * Reads the contents of a file and returns it as a String.
     *
     * @param fileName The name of the file to read.
     * @return The content of the file as a String, or an empty string if an error occurs.
     */
    public static String readFileAsString(String fileName) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * Retrieves the list of level configurations loaded from the configuration file.
     *
     * @return A list of LevelConfig objects representing the game levels.
     */
    public static List<LevelConfig> getLevelsConfig() {
        return levelsConfig;
    }

    /**
     * Sets up the board array for a specified level based on the contents of a text file.
     *
     * @param index The index of the level to load (corresponding to a levelN.txt file).
     * @return A 2D char array representing the layout of the level.
     */
    public static char[][] setBoardArray(int index) {
        File file = new File("level" + index + ".txt");

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

    /**
     * The main method to initiate the configuration loading process.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        readConfig();
    }
}
