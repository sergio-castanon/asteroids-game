package asteroids.participants;

import static asteroids.game.Constants.ALIENSHIP_SCALE;
import static asteroids.game.Constants.SIZE;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import asteroids.destroyers.AlienShipDestroyer;
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

    /** Indicates size of the alien ship (0 for small and 1 for medium) */
    private int size;

    public double[] shipDirections = { 0.0, 1.0, -1.0 };

    public AlienShip (int size, Controller controller)
    {
        this.controller = controller;
        double side = Math.random() * 1;
        if (side > 0.5)
        {
            setPosition(0, Math.random() * SIZE);
        }
        else
        {
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
        if (size == 0)
        {
            setVelocity(6, shipDirections[Constants.RANDOM.nextInt(3)]);
            new ParticipantCountdownTimer(this, "direction", 200);
        }
        else
        {
            setVelocity(4, shipDirections[Constants.RANDOM.nextInt(3)]);
            new ParticipantCountdownTimer(this, "direction", 600);
        }
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
        if (p instanceof AlienShipDestroyer)
        {
            // Tell the controller the ship was destroyed
            Participant.expire(this); 
            controller.alienShipDestroyed();
            controller.playClip(0);

            // Creates debris
            for (int i = 0; i < 4; i++)
            {
                controller.addParticipant(new Debris(0, this.getX(), this.getY()));
            }

            // Increases score based on alien ship size
            if (this.size == 0)
            {
                controller.scoreIncrease(1000);
            }
            if (this.size == 1)
            {
                controller.scoreIncrease(200);
            }
        }

    } 
    
    /**
     * Returns the x-coordinate of the point on the screen where the ship's left side is located.
     */
    public double getXLeft ()
    {
        Point2D.Double point = new Point2D.Double(5, 20);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the y-coordinate of the point on the screen where the ship's left side is located.
     */
    public double getYLeft ()
    {
        Point2D.Double point = new Point2D.Double(5, 20);
        transformPoint(point);
        return point.getY();
    }
    
    /**
     * Returns the x-coordinate of the point on the screen where the ship's right side is located.
     */
    public double getXRight ()
    {
        Point2D.Double point = new Point2D.Double(45, 20);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the y-coordinate of the point on the screen where the ship's right side is located.
     */
    public double getYRight ()
    {
        Point2D.Double point = new Point2D.Double(45, 20);
        transformPoint(point);
        return point.getY();
    }

    @Override
    public void countdownComplete (Object payload)
    {
        if (payload.equals("direction"))
        {
            this.setDirection(shipDirections[Constants.RANDOM.nextInt(3)]);

            if (this.size == 0)
            {
                new ParticipantCountdownTimer(this, "direction", 200);
            }
            else
            {
                new ParticipantCountdownTimer(this, "direction", 600);
            }
        }
    }

}
