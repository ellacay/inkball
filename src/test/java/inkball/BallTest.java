// package inkball.objects;

// import inkball.objects.Ball;
// import inkball.objects.Hole;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import static org.junit.jupiter.api.Assertions.*;

// class BallTest {
//     private Ball ball;

//     @BeforeEach
//     void setUp() {
//         // Initialize a Ball with color '1'
//         ball = new Ball(/* parameters */);
//     }

//     @Test
//     void testBallColor() {
//         // Testing if the color of the ball is set correctly
//         assertEquals('1', ball.getColor(), "Ball color should be '1'");
//     }

//     @Test
//     void testBallCollisionWithGreyHole() {
//         Hole greyHole = new Hole(/* parameters with grey color */);
//         // Simulate collision logic here
//         // Assert that the ball is not added to the respawn queue
//         assertFalse(/* condition for respawn queue */, "Ball should not respawn with grey hole");
//     }

//     @Test
//     void testBallCollisionWithDifferentColorHole() {
//         Hole redHole = new Hole(/* parameters with color '2' */);
//         // Simulate collision logic here
//         // Assert that the ball is added to the respawn queue
//         assertTrue(/* condition for respawn queue */, "Ball should respawn when colliding with a different colored hole");
//     }

//     // Add more tests for edge cases and other functionalities
// }
