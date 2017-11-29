package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents bullets
 */
public class Bullet extends Participant implements AsteroidDestroyer
{
    /** The outline of the bullet */
    private Shape outline; 
    
    /** The game controller */ 
    private Controller controller; 
    
    /** Timer for BULLET_DURATION */
    private ParticipantCountdownTimer selfDestruct;
    
    /** 
     * Creates a bullet and places it at the ship's nose with the velocity being the BULLET_SPEED and the rotation of the ship. 
     * A bullet has a timer which goes off after BULLET_DURATION and removes the bullet from the game.
     */
    public Bullet (double x, double y, double direction, Controller controller)
    {
        // Creates the bullet 
        this.controller = controller;
        setPosition(x, y);
        setVelocity(Constants.BULLET_SPEED, direction);
        
        outline = new Ellipse2D.Double(0, 0, 2, 2); 
        
        // Creates the timer which removes the bullet from the game after BULLET_DURATION
        selfDestruct = new ParticipantCountdownTimer(this, Constants.BULLET_DURATION);
        
    }
    
    /** 
     * Returns the outline of the bullet
     */
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Sets the bullet as expired if it collides with a BulletDestroyer
     */
    @Override
    public void collidedWith (Participant p)
    {
            Participant.expire(this);
    }
    
    /**
     * Sets the bullet as expired once the ParticipantCountdownTimer has gone off
     */
    @Override
    public void countdownComplete (Object payload)
    {
        Participant.expire(this);
    }
    
}
