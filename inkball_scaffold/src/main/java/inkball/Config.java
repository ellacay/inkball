package inkball;
import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.util.Map;
import java.util.Scanner;



public class Config {

    public static char[][] board;
    public String layout;
    public int time;
    public int spawnInterval;
    public int scoreIncrease;
    public int scoreDecrease;
    public Map<String, Integer> scoreMultiplyer;
    public Map<String, Integer> scoreDivider;


    public static void readConfig(){

    }

    public String[] addSurroundingWall(){
        return new String[] {""};
    }
    public String[] addTiles(){
        return new String[] {""};
    }
    public String[] addBalls(){
        return new String[] {""};
    }
    public String[] addHoles(){
        return new String[] {""};
    }
    public String[] addSpawners(){
        return new String[] {""};
    }



    public void setBoardArray(){

        File file = new File("/Users/ellacaysmith/Desktop/inkball_scaffold/level1.txt");

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                int count= 0;
                String line = scanner.nextLine();
                

                for( int i =0; i<line.length(); i++){
                    board[count][i] = line.charAt(i);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
  
    public static void main(String[] args) {
        System.out.println("board");
}
}
