package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents ships
 */
public class Ship extends Participant implements AsteroidDestroyer, AlienShipDestroyer
{
    /** The outline of the ship */
    private Shape outline;

    /** Game controller */
    private Controller controller;
    
    /** Ship image */
    private Path2D.Double poly;

    /**
     * Constructs a ship at the specified coordinates that is pointed in the given direction.
     */
    public Ship (int x, int y, double direction, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y);
        setRotation(direction);

        if (controller.enhanced() == false)
        {
            createShip();
        }
        else
        {
            createShipE();
            createPersonStanding();
        }
    }
    /**
     * Creates the shape of the ship
     */
    public void createShip() {
        this.poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.lineTo(21, 0);
        poly.closePath();
        outline = poly; 
    }
        /**
     * Creates the shape of the ship in the enhanced version
     */
    public void createShipE ()
    {
        this.poly = new Path2D.Double();
        poly.moveTo(20, 0);
        poly.lineTo(0, 10);
        poly.lineTo(0, 0);
        poly.lineTo(-30, 0);
        poly.lineTo(-30, 40);
        poly.lineTo(-70, -10);
        poly.lineTo(-30, -60);
        poly.lineTo(-30, 0);
        poly.closePath();
        outline = poly;
    }
    
    /**
     * Creates a person standing on the ship
     */
    public void createPersonStanding ()
    {
        poly.moveTo(-30, -50);
        poly.lineTo(-25, -45);
        poly.lineTo(-30, -40);
        poly.lineTo(-25, -45);
        poly.lineTo(-5, -45);
        poly.lineTo(0, -50);
        poly.lineTo(-5, -45);
        poly.lineTo(0, -40);
        poly.lineTo(-5, -45);
        poly.lineTo(5, -45);
        poly.lineTo(10, -50);
        poly.lineTo(15, -45);
        poly.lineTo(10, -40);
        poly.lineTo(5, -45);
    }
    
     /**
     * Adds a line to the shape of the ship
     */
    public void addLine (double d, double e)
    {
        poly.lineTo(d, e);
    }

    /**
     * Moves the adding starting point of the ship to the specified point
     */
    public void moveToShip (double d, double e)
    {
        poly.moveTo(d, e);
    }

    /**
     * Resets the shape of the ship
     */
    public void resetShape ()
    {
        poly.reset();
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getY();
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        applyFriction(SHIP_FRICTION);
        super.move();
    }

    /**
     * Turns right by Pi/16 radians
     */
    public void turnRight ()
    {
        rotate(Math.PI / 16);
    }

    /**
     * Turns left by Pi/16 radians
     */
    public void turnLeft ()
    {
        rotate(-Math.PI / 16);
    }

    /**
     * Accelerates by SHIP_ACCELERATION
     */
    public void accelerate ()
    {
        accelerate(SHIP_ACCELERATION);
    }

    /**
     * When a Ship collides with a ShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            for (int i = 0; i < 4; i++)
            {
                controller.addParticipant(new Debris(0, this.getX(), this.getY()));
            }
            
            controller.addParticipant(new Debris(1, this.getX(), this.getY()));
            
            // Plays sound clip
            controller.playClip(10);
            
            // Expire the ship from the game
            Participant.expire(this);

            // Tell the controller the ship was destroyed
            controller.shipDestroyed();
        }
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Give a burst of acceleration, then schedule another
        // burst for 200 msecs from now.
        if (payload.equals("move"))
        {
            accelerate();
            new ParticipantCountdownTimer(this, "move", 200);
        }
    }
}
