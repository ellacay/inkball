package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import processing.core.PImage;
import inkball.loaders.ConfigLoader;
import inkball.loaders.ImageLoader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {
    private ImageLoader imageLoader; // Loader for images used in the app
    private App app; // Instance of the main application class
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream(); // To capture output streams
  
    @TempDir
    Path tempDir; // Temporary directory for creating test files

    /**
     * Sets up the test environment before each test.
     * Creates a mock config.json file and a level text file for testing.
     */
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

        // Write the mock config content to a JSON file in the temporary directory
        Files.write(tempDir.resolve("config.json"), jsonContent.getBytes());

        // Create a sample level text file with a simple layout
        String levelContent = "####\n#  #\n####";
        Files.write(tempDir.resolve("level0.txt"), levelContent.getBytes());
        
        // Set the working directory for the app to the temporary directory for file access
        System.setProperty("user.dir", tempDir.toString());
        app = new App(); // Instantiate the main application
        imageLoader = new ImageLoader(app); // Initialize ImageLoader with the app instance
        System.setErr(new PrintStream(outContent)); // Redirect error output to capture it
    }

    /**
     * Tests reading a file as a string.
     * Asserts that the content is not empty and contains the expected key.
     */
    @Test
    public void testReadFileAsString() throws IOException {
        // Read the content of the config.json file
        String content = ConfigLoader.readFileAsString(tempDir.resolve("config.json").toString());
        
        // Assertions to verify the content
        assertFalse(content.isEmpty(), "Content should not be empty."); // Content should be present
        assertTrue(content.contains("levels"), "Content should contain 'levels'."); // Check for a specific key
    }

    /**
     * Tests the image loading functionality when a null image is provided.
     * Asserts that the correct error message is generated.
     */
    @Test
    void testCheckImageLoad_NullImage() {
        // Arrange: Prepare a null image scenario
        PImage nullImage = null; // Simulate a null image
        String imageName = "testImage"; // Name of the image to be loaded

        // Act: Attempt to check the image loading
        imageLoader.checkImageLoad(nullImage, imageName);

        // Assert: Verify the output message matches the expected error message
        String expectedOutput = "Failed to load image: testImage"; // Expected error message
        assertEquals(expectedOutput, outContent.toString().trim(), "Error message should indicate image loading failure.");
    }
}
