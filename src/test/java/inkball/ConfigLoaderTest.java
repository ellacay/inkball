package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import processing.data.JSONArray;
import processing.data.JSONObject;

import inkball.loaders.ConfigLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        // Set up a mock config.json file with test data
        String jsonContent = "{ \"levels\": [ " +
                "{ \"layout\": \"layout1\", \"time\": 60, \"spawn_interval\": 5, " +
                "\"score_increase_from_hole_capture_modifier\": 1.5, " +
                "\"score_decrease_from_wrong_hole_modifier\": 0.5, " +
                "\"balls\": [\"ball1\", \"ball2\"] } " +
                "], " +
                "\"score_increase_from_hole_capture\": { \"key1\": 10, \"key2\": 20 }, " +
                "\"score_decrease_from_wrong_hole\": { \"key1\": 5, \"key2\": 15 } }";

        Files.write(tempDir.resolve("config.json"), jsonContent.getBytes());

        // Create a sample level text file
        String levelContent = "####\n#  #\n####";
        Files.write(tempDir.resolve("level0.txt"), levelContent.getBytes());
        
        // Update ConfigLoader to use the tempDir path for reading files
        System.setProperty("user.dir", tempDir.toString());
    }

    @Test
    public void testReadConfig() {
        ConfigLoader.readConfig();
        List<ConfigLoader.LevelConfig> levelsConfig = ConfigLoader.getLevelsConfig();

        assertEquals(1, levelsConfig.size(), "Should load one level configuration.");
        ConfigLoader.LevelConfig level = levelsConfig.get(0);

        assertEquals("layout1", level.layout, "Layout should match.");
        assertEquals(60, level.time, "Time should match.");
        assertEquals(5, level.spawnInterval, "Spawn interval should match.");
        assertArrayEquals(new String[]{"ball1", "ball2"}, level.balls, "Balls should match.");

        assertEquals(2, level.scoreIncreaseMap.size(), "Score increase map should have two entries.");
        assertEquals(10, level.scoreIncreaseMap.get("key1"), "Score increase for key1 should match.");
        assertEquals(20, level.scoreIncreaseMap.get("key2"), "Score increase for key2 should match.");

        assertEquals(2, level.scoreDecreaseMap.size(), "Score decrease map should have two entries.");
        assertEquals(5, level.scoreDecreaseMap.get("key1"), "Score decrease for key1 should match.");
        assertEquals(15, level.scoreDecreaseMap.get("key2"), "Score decrease for key2 should match.");
    }

    @Test
    public void testSetBoardArray() {
        char[][] board = ConfigLoader.setBoardArray(0);
        
        assertNotNull(board, "Board array should not be null.");
        assertEquals(3, board.length, "Board should have 3 rows.");
        assertEquals(4, board[0].length, "Board should have 4 columns.");

        assertArrayEquals(new char[]{'#', '#', '#', '#'}, board[0], "First row should match.");
        assertArrayEquals(new char[]{'#', ' ', ' ', '#'}, board[1], "Second row should match.");
        assertArrayEquals(new char[]{'#', '#', '#', '#'}, board[2], "Third row should match.");
    }

    @Test
    public void testReadFileAsString() throws IOException {
        String content = ConfigLoader.readFileAsString(tempDir.resolve("config.json").toString());
        assertFalse(content.isEmpty(), "Content should not be empty.");
        assertTrue(content.contains("levels"), "Content should contain 'levels'.");
    }
}
