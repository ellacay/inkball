// package inkball;

// import processing.core.PApplet;
// import processing.core.PImage;
// import processing.core.PVector;
// import java.util.List;


// import java.util.Random;

// public class Ball {
//     private static final Random random = new Random();
//     private static final int VELOCITY_OPTIONS[] = {-2, 2};
//     private float x; // X position of the ball
//     private float y; // Y position of the ball
//     private String color; // Color of the ball based on its ID
//     public float velocityX; // Velocity in the x direction
//     public float velocityY; // Velocity in the y direction
//     private PImage[] balls; // Reference to ball images
//     private int originalSize = 24; // Original size of the ball (width and height of the image)
//     private float currentSize; // Current size of the ball for scaling
//     private boolean hasCollided;
//     public boolean captured;
//     private boolean outOfBounds =false;
//     private boolean scoreUpdated = false; // New flag to check if score is already updated
//     public boolean added = false;
//     public static boolean isCaptureSuccessfulFlag = false;

//     // Variables for shrinking animation
//     public boolean shrinking = false;
//     private float targetSize = 12; // Target smaller size for shrinking
//     private float shrinkRate = 0.5f; // Rate at which the ball will shrink

//     public Ball(float x, float y, String ballColor, PImage[] balls) {
//         this.x = x;
//         this.y = y;
//         this.color = ballColor;
//         this.balls = balls; // Store the reference
//         this.velocityX = VELOCITY_OPTIONS[random.nextInt(2)];
//         this.velocityY = VELOCITY_OPTIONS[random.nextInt(2)];
//         this.hasCollided = false;
//         this.captured = false;
//         this.currentSize = originalSize; // Start at the original size
//     }
//     public String getBallId(){
//         return color;
//     }

//     public void update( ) {
//             // Move the ball by velocity
//             x += velocityX;
//             y += velocityY;

//             // Handle shrinking if the shrinking flag is set
//             if (shrinking) {
//                 currentSize -= shrinkRate; // Decrease size by shrinkRate

//                 // Ensure currentSize doesn't go below targetSize
//                 if (currentSize <= targetSize) {
//                     currentSize = targetSize; // Clamp the size
//                     shrinking = false; // Stop shrinking
//                 }
//             }
//     }

//      public void display(PApplet app) {
//         if (!captured) { // Only display if not captured
//             int ballIndex = getColorIndex(color);
//             // Display the ball image at the current size (scale the image)
//             app.image(balls[ballIndex], x, y, currentSize, currentSize);
//         }
//     }

//     private int getColorIndex(String color) {
//         switch (color) {
//             case "grey":
//                 return 0;
//             case "orange":
//                 return 1;
//             case "blue":
//                 return 2;
//             case "green":
//                 return 3;
//             case "yellow":
//                 return 4;
//             default:
//                 return 0; // Default to grey if not found
//         }
//     }

//     public void startShrinking() {
//         targetSize = 12; // Set target size for shrinking
//         shrinking = true; // Start the shrinking process
//     }

//     public float getX() {
//         return x;
//     }

//     public float getY() {
//         return y;
//     }

//     public void setVelocity(float newVelocityX, float newVelocityY) {
//         this.velocityX = newVelocityX;  // Update the actual velocity
//         this.velocityY = newVelocityY;  // Update the actual velocity
//         System.out.println("VelocityX" + velocityX);
//         System.out.println("VelocityY" + velocityY);
//     }

//     public void reverseHorizontalDirection() {
//         velocityX *= -1;  // Reverse X velocity (horizontal direction)
//     }

//     public void reverseVerticalDirection() {
//         velocityY *= -1;  // Reverse Y velocity (vertical direction)
//     }

//     public void setColor(String new_color) {
//         color = new_color;
//     }

//     public void setX(float xCordinate) {
//         this.x = xCordinate;
//     }

//     public void setY(float yCordinate) {
//         this.y = yCordinate;
//     }

//     public void resetCollision() {
//         hasCollided = false; // Reset for the next frame
//     }

//     public boolean hasCollided() {
//         return hasCollided; // Getter for collision status
//     }

//     public void setHasCollided(boolean hasCollided) {
//         this.hasCollided = hasCollided; // Setter for collision status
//     }

//     public void applyForce(float forceX, float forceY) {
//         this.velocityX += forceX;
//         this.velocityY += forceY;
//     }

//     public String getColor() {
//         return color;
//     }

//     public int getRadius() {
//         return 12;
//     }

//     public void setCaptured(boolean captured) {
//         this.captured = captured; // or whatever logic you want for capturing
//     }

//     public void setCurrentSize(float newSize) {
//         this.currentSize = newSize;
//     }

//     private float constrain(float value, float min, float max) {
//         return Math.max(min, Math.min(max, value));
//     }

//     public float getCurrentSize() {
//         return currentSize; // Getter for current size
//     }

//     public float getTargetSize() {
//         return targetSize;
//     }


// public void attractToHoles(List<Hole> holes) {
//     Hole closestHole = null;
//     float closestDistance = Float.MAX_VALUE;

//     // Find the closest hole
//     for (Hole hole : holes) {
//         float holeCenterX = hole.getCenterX();
//         float holeCenterY = hole.getCenterY();

//         // Calculate distance from the ball's center to the hole's center
//         PVector toHole = new PVector(holeCenterX - (x + currentSize / 2), holeCenterY - (y + currentSize / 2));
//         float distance = toHole.mag(); // Get the distance to the center of the hole

//         // Update if this hole is closer
//         if (distance < closestDistance) {
//             closestDistance = distance;
//             closestHole = hole;
//         }
//     }

//     if (closestHole != null && closestDistance < 32 + getRadius()) {
//         // The ball is within attraction range of the closest hole
//         PVector toHole = new PVector(
//                 closestHole.getCenterX() - (x + currentSize / 2),
//                 closestHole.getCenterY() - (y + currentSize / 2)
//         );
//         toHole.normalize(); // Normalize direction to the hole

//         // Apply attraction force based on proximity
//         float attractionForce = 0.005f * closestDistance; // 0.5% of distance
//         applyForce(toHole.x * attractionForce, toHole.y * attractionForce);

//         // Start shrinking if not already shrinking
//         if (!shrinking && !outOfBounds) {
//             startShrinking();
//         }

//         // Reduce size based on proximity to the closest hole
//         currentSize = originalSize * (closestDistance / 32);
//         currentSize = constrain(currentSize, targetSize, originalSize);

//         // Check if the ball is captured by the hole
//         if (closestDistance < getRadius()) {
//             setCaptured(true);
//             String holeType = String.valueOf(closestHole.type); // Get hole type as a String
//             isCaptureSuccessfulFlag = isCaptureSuccessful(holeType);
//             updateScore(isCaptureSuccessfulFlag, holeType); // Update score based on capture success
//         }
//     } else {
//         // Ball is no longer in range of any hole, reset size
//         resetToOriginalSize();
//         outOfBounds = true; // Set flag indicating the ball is out of bounds
//     }
// }


//     public void resetToOriginalSize() {
//         // Only reset the ball size if it's not shrinking and hasn't been captured
//         if (!captured) {
//             currentSize = originalSize;  // Restore the original size
//             shrinking = false;  // Stop any shrinking process
//             outOfBounds = true; // Keep outOfBounds flag true
//         }
//     }

//     private boolean isCaptureSuccessful(String holeType) {
//             // Grey ball or grey hole
//         if (color.equals("grey") || holeType.equals("0")) {
//                 return true; // Always successful for grey
//             }
//             // Check if the ball's color matches the hole's type
//             return color.equals(getColorFromHoleType(holeType));
//         }

//     private String getColorFromHoleType(String holeType) {
//             switch (holeType) {
//                 case "0": return "grey";
//                 case "1": return "orange";
//                 case "2": return "blue";
//                 case "3": return "green";
//                 case "4": return "yellow";
//                 default: return null; // Invalid type
//             }
//         }

//     private void updateScore(boolean isCaptureSuccessful, String holeType) {
//         if (!scoreUpdated) {
//             System.out.println("Capture Successful: " + isCaptureSuccessful + ", Hole Type: " + holeType);
//             if (isCaptureSuccessful) {
//                 // Successful capture
//                 float scoreIncrease = App.scoreIncreaseModifier * (App.currentLevelIndex+1);
//                 App.score += scoreIncrease; // Increase score
//                 System.out.println("Score increased by: " + scoreIncrease + ", New Score: " + App.score);
//             } else {
//                 // Unsuccessful capture
//                 float scoreDecrease = App.scoreDecreaseModifier;
//                 App.score -= scoreDecrease; // Decrease score
//                 System.out.println("Score decreased by: " + scoreDecrease + ", New Score: " + App.score);
//             }
//             scoreUpdated = true; // Mark the score as updated to prevent further increments
//         }
//     }
    
// }