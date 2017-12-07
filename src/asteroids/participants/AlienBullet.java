package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.Bullet;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class AlienBullet extends Participant implements Bullet, ShipDestroyer, AsteroidDestroyer
{
    /** The game's controller */
    private Controller controller;

    /** The outline of the bullet */
    private Shape outline; 
    
    /** Used for the self destruction of bullets */
    private ParticipantCountdownTimer selfDestruct;
    
    /** 
     * Creates an alien bullet
     */
    public AlienBullet (double x, double y, double direction, Controller controller)
    {
     // Creates the bullet 
        this.controller = controller;
        setPosition(x, y);
        setVelocity(Constants.BULLET_SPEED, direction);
        
        outline = new Ellipse2D.Double(0, 0, 2, 2); 
        
        // Creates the timer which removes the bullet from the game after BULLET_DURATION
        selfDestruct = new ParticipantCountdownTimer(this, Constants.BULLET_DURATION);
    }
    
    @Override 
    public void collidedWith (Participant p)
    {
        if (!(p instanceof AlienShip))
        {
            Participant.expire(this);
        }
    }

    @Override
    protected Shape getOutline ()
    {
        // TODO Auto-generated method stub
        return this.outline;
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
