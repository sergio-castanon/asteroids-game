package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullet;
import asteroids.participants.Debris;
import asteroids.participants.Ship;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener
{
    /** The state of all the Participants */
    private ParticipantState pstate;

    /** The ship (if one is active) or null (otherwise) */
    private Ship ship;

    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

    /** Keeps track of the key press of the right arrow or D */
    private boolean turningRight;

    /** Keeps track of the key press of the left arrow or A */
    private boolean turningLeft;

    /** Keeps track of the key press of the up arrow or W */
    private boolean goingForward;

    /** Keeps track of the key press of the spacebar or down arrow */
    private boolean bulletFire;

    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    /** Number of lives left */
    private int lives;

    /** Level of the game */
    private int level;

    /** The game display */
    private Display display;

    /** Score of the game */
    private int score; 
    
    /** */
    private Clip[] soundClips;
    
    /** */
    private String[] soundStrings;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller ()
    {
        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Record the display object
        display = new Display(this);

        // Reset key presses
        goingForward = false;
        turningLeft = false;
        turningRight = false;
        bulletFire = false; 
        
        //Set sound clips 
        soundClips = new Clip[11];
        soundStrings = new String[11];
        fillSoundStrings(); 
        createClips(soundStrings);
        

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
    }

    /**
     * Fills the array containing the sound strings
     */
    private void fillSoundStrings ()
    {
        soundStrings[0] = "/sounds/bangAlienShip.wav";
        soundStrings[1] = "/sounds/bangLarge.wav";
        soundStrings[2] = "/sounds/bangMedium.wav";
        soundStrings[3] = "/sounds/bangSmall.wav";
        soundStrings[4] = "/sounds/beat1.wav";
        soundStrings[5] = "/sounds/beat2.wav";
        soundStrings[6] = "/sounds/fire.wav";
        soundStrings[7] = "/sounds/saucerBig.wav";
        soundStrings[8] = "/sounds/saucerSmall.wav";
        soundStrings[9] = "/sounds/thrust.wav";
        soundStrings[10] = "/sounds/bangShip.wav";
    }
    
    /** 
     * Creates the sound clips
     */
    private void createClips (String[] soundClip)
    {
        for (int i = 0; i < 11; i++)
        {
            try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundClip[i])))
            {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(sound));
                soundClips[i] = clip;
            }
            catch (IOException e)
            {
                soundClips[i] = null;
            }
            catch (LineUnavailableException e)
            {
                soundClips[i] = null;
            }
            catch (UnsupportedAudioFileException e)
            {
                soundClips[i] = null;
            }
            
        }
    } 
    
    /**
     * Plays a certain sound clip according to the int passed in. 
     */
    public void playClip (int soundClip)
    {
        if (soundClip > 11 || soundClip < 0)
        {
            throw new IllegalArgumentException();
        }
        
        if (soundClips[soundClip].isRunning())
        {
            soundClips[soundClip].stop();
        }
        soundClips[soundClip].setFramePosition(0);
        soundClips[soundClip].start();
    }
    
    /**
     * Returns the ship, or null if there isn't one
     */
    public Ship getShip ()
    {
        return ship;
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();
    }

    /**
     * The game is over. Displays a message to that effect.
     */
    private void finalScreen ()
    {
        display.setLegend(GAME_OVER);
        display.removeKeyListener(this);
    }

    /**
     * Place a new ship in the center of the screen. Remove any existing ship first.
     */
    private void placeShip ()
    {
        // Place a new ship
        Participant.expire(ship);
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        display.setLegend("");

        // Resets the key presses
        turningLeft = false;
        turningRight = false;
        goingForward = false;
        bulletFire = false;
    }

    /**
     * Places an asteroid near one corner of the screen. Gives it a random velocity and rotation.
     */
    private void placeAsteroids ()
    {
        addParticipant(new Asteroid(0, 2, -EDGE_OFFSET, EDGE_OFFSET, 3, this));
        addParticipant(new Asteroid(0, 2, EDGE_OFFSET, -EDGE_OFFSET, 3, this));
        addParticipant(new Asteroid(0, 2, -EDGE_OFFSET, -EDGE_OFFSET, 3, this));
        addParticipant(new Asteroid(0, 2, EDGE_OFFSET, EDGE_OFFSET, 3, this));
        for (int i = 1; i < this.level; i++)
        {
            addParticipant(new Asteroid(0, 2, -EDGE_OFFSET, EDGE_OFFSET, 3, this));
        }
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        pstate.clear();
        display.setLegend("");
        ship = null;
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        // Clear the screen
        clear();

        // Place asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Reset statistics
        lives = 3;
        level = 1;
        score = 0;

        // Send statistics to the display
        display.setLives(lives);
        display.setLevel(level);
        display.setScore(score);

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**
     * The ship has been destroyed
     */
    public void shipDestroyed ()
    {
        // Null out the ship
        ship = null;

        // Display a legend
        display.setLegend("Ouch!");

        // Decrement lives
        lives--;
        display.decreaseLives();

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);
    }

    /**
     * The score increases depending on what size asteroid was hit
     */
    public void scoreIncrease (int scoreIncrease)
    {
            this.score += scoreIncrease;
            display.changeScore(this.score);
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed ()
    {
        // If all the asteroids are gone, schedule a transition
        if (pstate.countAsteroids() == 0)
        {
            level++;
            display.setLegend("Level " + level);
            display.setLevel(level);
            scheduleTransition(END_DELAY);
        }
    }

    /**
     * Schedules a transition m msecs in the future
     */
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            // Fixes an issue where the incorrect number of asteroids were added
            level = 1;
            display.setLevel(level);
            initialScreen();
        }

        // Time to refresh the screen and deal with keyboard input
        else if (e.getSource() == refreshTimer)
        {
            // It may be time to make a game transition
            performTransition();

            // Move the participants to their new locations
            pstate.moveParticipants();

            // Check key presses and perform the actions indicated
            if (turningLeft && ship != null)
            {
                ship.turnLeft();
            }
            if (turningRight && ship != null)
            {
                ship.turnRight();
            }
            if (goingForward && ship != null && soundClips[9] != null)
            {
                // Play the thrust clip
                playClip(9);
                
                ship.accelerate();
            }
            if (bulletFire && ship != null)
            {
                if (pstate.trackBullets() < 8)
                {
                    addParticipant(new Bullet(ship.getXNose(), ship.getYNose(), ship.getRotation(), this));
                    bulletFire = false;

                    // Play the firing clip
                    playClip(6);
                }
            }
            else if (ship != null)
            {
                ship.applyFriction(SHIP_FRICTION);
            }

            // Refresh screen
            display.refresh();
        }
    }

    /**
     * Returns an iterator over the active participants
     */
    public Iterator<Participant> getParticipants ()
    {
        return pstate.getParticipants();
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            // If there are no lives left, the game is over. Show the final
            // screen.
            if (lives <= 0)
            {
                finalScreen();
            }
            else if (lives > 0)
            {
                placeShip();
            }

            if (pstate.countAsteroids() == 0)
            {

                placeAsteroids();

                placeShip();
            }
        }
    }

    /**
     * If a key of interest is pressed, record that it is down.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship != null)
        {
            turningRight = true;
        }
        if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship != null)
        {
            turningLeft = true;
        }
        if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && ship != null)
        {
            goingForward = true;
        }
        if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && ship != null)
        {
            bulletFire = true;
        }

    }

    /**
     * These events are ignored.
     */
    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
     * If a key of interest is released, record that it is up
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
        if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship != null)
        {
            turningRight = false;
        }
        if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship != null)
        {
            turningLeft = false;
        }
        if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && ship != null)
        {
            goingForward = false;
        }
        if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && ship != null)
        {
            bulletFire = false;
        }
    }
}
