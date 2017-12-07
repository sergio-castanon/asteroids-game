package asteroids.participants;

import static asteroids.game.Constants.ALIENSHIP_SCALE;
import static asteroids.game.Constants.SIZE;
import static asteroids.game.Constants.RANDOM;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.AlienShipDestroyer;
import asteroids.destroyers.ShipDestroyer;
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

    /** Indicates size of the alien ship (0 for small and 1 for medium) */
    private int size;

    public double[] shipDirections = { 0.0, 1.0, -1.0 };

    public AlienShip (int size, Controller controller)
    {
        this.controller = controller;
        this.size = size;
        double side = Math.random() * 1;
        if (side > 0.5)
        {
            setPosition(0, Math.random() * SIZE);
        }
        else
        {
            setPosition(SIZE, Math.random() * SIZE);
        }
        setVelocity(5, shipDirections[RANDOM.nextInt(3)]);

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
            setVelocity(6, shipDirections[RANDOM.nextInt(3)]);
            new ParticipantCountdownTimer(this, "direction", 200);
        }
        else
        {
            setVelocity(4, shipDirections[RANDOM.nextInt(3)]);
            new ParticipantCountdownTimer(this, "direction", 600);
        }
        // Scale to the desired size
        double scale = ALIENSHIP_SCALE[size];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));

        new ParticipantCountdownTimer(this, "bullet", 2000);
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
            // Tell the controller the ship was destroyed
            Participant.expire(this);
            controller.playClip(0);
            controller.alienShipDestroyed();
        }

    }

    @Override
    public void countdownComplete (Object payload)
    {
        if (payload.equals("direction"))
        {
            this.setDirection(shipDirections[RANDOM.nextInt(3)]);

            if (this.size == 0)
            {
                new ParticipantCountdownTimer(this, "direction", 200);
            }
            else
            {
                new ParticipantCountdownTimer(this, "direction", 600);
            }
        }
        if (payload.equals("bullet"))
        {
            if (this.size == 1)
            {
                AlienBullet bullet = new AlienBullet(this.getX(), this.getY(),
                        RANDOM.nextDouble() * 2 * Math.PI, controller);
                this.controller.addParticipant(bullet);
                new ParticipantCountdownTimer(this, "bullet", 2000);
            }
            if (this.size == 0)
            {
                AlienBullet bullet = new AlienBullet(this.getX(), this.getY(), this.getDirectionSmall(), controller);
                this.controller.addParticipant(bullet);
                new ParticipantCountdownTimer(this, "bullet", 2000);
            }
        }
    }

    /**
     * Randomly chooses a direction within 5 degrees of the ship up or down
     */
    public double getDirectionSmall ()
    {
        try
        {
            double shipx = this.controller.getShip().getX();
            double shipy = this.controller.getShip().getY();

            double alienx = this.getX();
            double alieny = this.getY();

            double hypo = Math.sqrt(Math.pow(shipx - alienx, 2) + Math.pow(shipy - alieny, 2));
            double cos = Math.acos(Math.abs(shipx - alienx) / hypo);
            
            if ((shipy - alieny) < 0)
            {
                cos = -cos;
            }
            double degrees = RANDOM.nextDouble() * Math.PI / 18;
            double directionSmall =  degrees + cos - (Math.PI / 36);
            return directionSmall;
        }
        catch (NullPointerException e)
        {

        }
        return 0.0;
    }

}
