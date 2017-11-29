package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import asteroids.game.Constants;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Debris extends Participant
{
    /** The outline of the debris */
    private Shape outline;

    private ParticipantCountdownTimer destroyDebris;

    /**
     * Creates debris that appears after a participant is destroyed, except for the bullet. If the debrisType is 0, then
     * a circle debris will be created with a radius of 0.5 and a random position close to the X and Y coordinates of
     * the Participant that was destroyed.
     */
    public Debris (int debrisType, double x, double y)
    {
        setPosition(x, y);
        setVelocity(Constants.RANDOM.nextInt(1) + 1, (Constants.RANDOM.nextDouble() * 2 * (Math.PI)) + 1.0);
        
        // Creates an outline for the debris
        if (debrisType == 0)
        {
            Ellipse2D.Double debris = new Ellipse2D.Double(Constants.RANDOM.nextInt(3), Constants.RANDOM.nextInt(3),
                    0.5, 0.5);
            outline = debris;
        }
        if (debrisType == 1)
        {
            Path2D.Double debris = new Path2D.Double();
            debris.moveTo(-5, 0);
            debris.lineTo(5, 21);
            debris.moveTo(10, 5);
            debris.lineTo(0, 5);
            
            outline = debris;
        }
        
        // Creates the timer for self-destruction of the debris after one second
        destroyDebris = new ParticipantCountdownTimer(this, 1000);
    }

    @Override
    protected Shape getOutline ()
    {
        return this.outline;
    }

    @Override
    public void collidedWith (Participant p)
    {
    }
    
    // Expires the debris after one second
    @Override
    public void countdownComplete (Object payload)
    {
        Participant.expire(this);
    }
}
