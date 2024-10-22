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
    public void testReadFileAsString() throws IOException {
        String content = ConfigLoader.readFileAsString(tempDir.resolve("config.json").toString());
        assertFalse(content.isEmpty(), "Content should not be empty.");
        assertTrue(content.contains("levels"), "Content should contain 'levels'.");
    }
}
