package asteroids.participants;

import static asteroids.game.Constants.ALIENSHIP_SCALE;
import static asteroids.game.Constants.SIZE;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents the alien ship that appears in levels beginning with level 2. The ship moves in a zig zag pattern.
 */
public class AlienShip extends Participant implements ShipDestroyer, AsteroidDestroyer
{
    /** The outline of the alien ship */
    private Shape outline;

    /** Game controller */
    private Controller controller; 
    
    /** Used for randomly changing direction every few seconds */
    private ParticipantCountdownTimer changeDirection; 
    
    private ParticipantCountdownTimer newAlienShip;
    
    public double[] shipDirections = {0.0, 1.0, -1.0};

    public AlienShip (int size, Controller controller)
    {
        this.controller = controller;
        double side = Math.random() * 1;
        if (side > 0.5) {
          setPosition(0, Math.random() * SIZE);
        } else {
          setPosition(SIZE, Math.random() * SIZE);
        }
        setVelocity(5, shipDirections[Constants.RANDOM.nextInt(3)]);
        
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(15, 10);
        poly.lineTo(35, 10);
        poly.lineTo(45, 20);
        poly.lineTo(35, 30);
        poly.lineTo(15, 30);
        poly.lineTo(5, 20);
        poly.lineTo(45, 20);
        poly.lineTo(5, 20); 
        poly.closePath();
        poly.moveTo(34, 10);
        poly.lineTo(30, 5);
        poly.lineTo(20, 5);
        poly.lineTo(16, 10);
        outline = poly; 
        
        // Creates the timer for changing directions
        new ParticipantCountdownTimer(this, "direction", 200); 
        
        //new ParticipantCountdownTimer(this, "new", (Constants.RANDOM.nextInt(6) + 5) * 100);
        
        
        // Scale to the desired size
        double scale = ALIENSHIP_SCALE[size];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));
    }

    @Override
    protected Shape getOutline ()
    {
        return this.outline;
    }

    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer || p instanceof AsteroidDestroyer)
        {
            new ParticipantCountdownTimer(this, "new", (Constants.RANDOM.nextInt(6) + 5) * 1000);
            
            // Tell the controller the ship was destroyed
            Participant.expire(this); 
        }

    }
    
    @Override
    public void countdownComplete (Object payload)
    {
        if (payload.equals("direction"))
        {
            this.setDirection(shipDirections[Constants.RANDOM.nextInt(3)]);
            
            new ParticipantCountdownTimer(this, "direction", 200);
        }
        if (payload.equals("new"))
        {
            controller.addParticipant(new AlienShip(1, controller)); 
        }
    }

}
