package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import java.util.Iterator;
import javax.swing.*;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;

    /** Game controller */
    private Controller controller;

    /** Number of lives left */
    private int lives;

    /** Level of the game */
    private int levels;

    /** Score of the game */
    private int score;
    
    /** Keeps track if the game is enhanced */
    private boolean isEnhanced = false;
    
    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        legend = "";
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setBackground(Color.black);
        setForeground(Color.white);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }

    /**
     * Sets the lives
     */
    public void setLives (int lives)
    {
        this.lives = lives;
    }

    /**
     * Sets the level
     */
    public void setLevel (int level)
    {
        this.levels = level;
    }

    /**
     * Sets the score
     */
    public void setScore (int score)
    {
        this.score = score;
    }

    /**
     * Decreases the lives count by 1
     */
    public void decreaseLives ()
    {
        this.lives--;
    }
    
    /**
     * Increases the lives count by 1
     */
    public void increasesLives ()
    {
        this.lives++;
    }

    /**
     * Increases the levels count by 1
     */
    public void increaseLevels ()
    {
        this.levels++;
    }
    
    /**
     * Sets enhanced mode to true
     */
    public void isEnhanced() {
        this.isEnhanced = true;
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {
        // Use better resolution
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);

        // Draw each participant in its proper place
        Iterator<Participant> iter = controller.getParticipants();
        while (iter.hasNext())
        {
            iter.next().draw(g);
        }
        // Keeps track of the original font and color
        Font font = g.getFont();
        Color color = g.getColor();

        // The spacing between each ship
        int incremenation = 30;

        // Keeps track of how many lives are left and need to be displayed
        int countAtLives = lives;
        
        float r = RANDOM.nextFloat();
        float h = RANDOM.nextFloat() / 2f;
        float b = RANDOM.nextFloat() / 2f;

        // Creates the graphics to keep track of the lives left
        while (countAtLives > 0)
        {
            g.setFont(new Font("TimesRoman", font.getStyle(), 28));
            if (isEnhanced) {
                g.setColor(new Color(r,h, b));
            }
            g.drawString(Integer.toString(score), 55, 30);
            g.drawString(Integer.toString(levels), SIZE - 55, 30);
            g.drawLine(-20 + incremenation, 50, -30 + incremenation, 80);
            g.drawLine(-20 + incremenation, 50, -10 + incremenation, 80);
            g.drawLine(-28 + incremenation, 75, -12 + incremenation, 75);
            if (isEnhanced) {
                g.setFont(new Font("TimesRoman", font.getStyle(), 20));
                g.drawString("Overall High Score", 5, 105);
                g.drawString(Integer.toString(Statistics.getHighScore()), 190, 105);
                g.drawString("Your High Score", 5, 125);
                g.drawString(Integer.toString(Statistics.getSessionHighScore()), 190, 125);
            }
            incremenation += 50;
            countAtLives--;
        }

        // Draw the legend across the middle of the panel
        g.setFont(font);
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);

    }

}
