package asteroids.game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Statistics
{
    /** High Score overall */
    private static int highScore;

    /** Session high score */
    private static int sessionHighScore;

    public static void setHighScore (int score)
    {
        if (score > highScore)
        {
            try
            {
                PrintWriter print = new PrintWriter("scores.txt");       
                print.println(score);
                print.close();
            }
            catch (FileNotFoundException e)
            {
            }
        }
    }

    public static void setSessionHighScore (int highscore)
    {
        if (highscore > sessionHighScore)
        {
            sessionHighScore = highscore;
        }
    }

    public static int getHighScore ()
    {
        try {
            BufferedReader read = new BufferedReader(new FileReader("scores.txt"));
            String score = read.readLine();
            while (score != null)             
            {
                try {
                    int specificScore = Integer.parseInt(score); 
                    if (specificScore > highScore)                     
                    { 
                        highScore = specificScore; 
                    }
                } catch (NumberFormatException e) {

                }
                score = read.readLine();
            }
            read.close();

        } catch (IOException e) {
        }
        return highScore;
    }

    public static int getSessionHighScore ()
    {
        return sessionHighScore;
    }
    
    public static void resetHighScore() {
        highScore = 0;
    }
    
    public static void resetSessionHighScore() {
        sessionHighScore = 0;
    }
}
