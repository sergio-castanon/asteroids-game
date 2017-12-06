package asteroids.game;

import javax.swing.*;
import static asteroids.game.Constants.*;
import java.awt.*;

/**
 * Defines the top-level appearance of an Asteroids game.
 */
@SuppressWarnings("serial")
public class Display extends JFrame
{
    /** The area where the action takes place */
    private Screen screen; 
    
    /** Number of lives left */
    private int lives;
     
    /** Level of the game */
    private int levels;    
     
    /** Score of the game */
    private int score;
    
    /**
     * Lays out the game and creates the controller
     */
    public Display (Controller controller)
    {
        // Title at the top
        setTitle(TITLE);

        // Default behavior on closing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // The main playing area and the controller
        screen = new Screen(controller);

        // This panel contains the screen to prevent the screen from being
        // resized
        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new GridBagLayout());
        screenPanel.add(screen);

        // This panel contains buttons and labels
        JPanel controls = new JPanel();
        JLabel currLevel = new JLabel("");
        JLabel score = new JLabel(""); 
        controls.add(currLevel);
        controls.add(score);

        // The button that starts the game
        JButton startGame = new JButton(START_LABEL);
        controls.add(startGame);

        // Organize everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(screenPanel, "Center");
        mainPanel.add(controls, "North");
        setContentPane(mainPanel);
        pack();

        // Connect the controller to the start button
        startGame.addActionListener(controller);
    }

    /**
     * Called when it is time to update the screen display. This is what drives the animation.
     */
    public void refresh ()
    {
        screen.repaint();
    }

    /**
     * Sets the large legend
     */
    public void setLegend (String s)
    {
        screen.setLegend(s);
    }
    
     /**
     * Sets the lives
     */
     public void setLives (int lives) {
         screen.setLives(lives);
     }
     
     /**
     * Sets the level
     */
     public void setLevel(int level) {
         screen.setLevel(level);
    }    
     
     /**
     * Sets the score
     */
     public void setScore(int score) {
         screen.setScore(score);
     }    
     
     /**
     * Decreases the lives count by 1
     */
     public void decreaseLives() {
         screen.decreaseLives();
     }    
     /**
     * Increases the lives count by 1
     */
     public void increaseLives() {
         screen.increasesLives();
     } 
     
     /**
     * Increases the levels count by 1
     */
     public void increaseLevels() {
         screen.increaseLevels();
      }
    
    /**
    * Changes the score to the new Score
    */
    public void changeScore(int newScore) {
        screen.setScore(newScore);
    }
    
    /**
     * Sets enhanced mode to true
     */
    public void isEnhanced() {
        screen.isEnhanced();
    }
}
