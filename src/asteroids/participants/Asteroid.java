package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.AlienShipDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;

/**
 * Represents asteroids
 */
public class Asteroid extends Participant implements ShipDestroyer, AlienShipDestroyer
{
    /** The size of the asteroid (0 = small, 1 = medium, 2 = large) */
    private int size;

    /** The outline of the asteroid */
    private Shape outline;

    /** The game controller */
    private Controller controller;

    /**
     * Throws an IllegalArgumentException if size or variety is out of range.
     * 
     * Creates an asteroid of the specified variety (0 through 3) and size (0 = small, 1 = medium, 2 = large) and
     * positions it at the provided coordinates with a random rotation. Its velocity has the given speed but is in a
     * random direction.
     */
    public Asteroid (int variety, int size, double x, double y, int speed, Controller controller)
    {
        // Make sure size and variety are valid
        if (size < 0 || size > 2)
        {
            throw new IllegalArgumentException("Invalid asteroid size: " + size);
        }
        else if (variety < 0 || variety > 3)
        {
            throw new IllegalArgumentException();
        }

        // Create the asteroid
        this.controller = controller;
        this.size = size;
        setPosition(x, y);
        setVelocity(speed, RANDOM.nextDouble() * 2 * Math.PI);
        setRotation(2 * Math.PI * RANDOM.nextDouble());
        createAsteroidOutline(variety, size);
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Creates the outline of the asteroid based on its variety and size.
     */
    private void createAsteroidOutline (int variety, int size)
    {
        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        // Fill out according to variety
        if (variety == 0)
        {
            poly.moveTo(0, -30);
            poly.lineTo(28, -15);
            poly.lineTo(20, 20);
            poly.lineTo(4, 8);
            poly.lineTo(-1, 30);
            poly.lineTo(-12, 15);
            poly.lineTo(-5, 2);
            poly.lineTo(-25, 7);
            poly.lineTo(-10, -25);
            poly.closePath();
        }
        else if (variety == 1)
        {
            poly.moveTo(10, -28);
            poly.lineTo(7, -16);
            poly.lineTo(30, -9);
            poly.lineTo(30, 9);
            poly.lineTo(10, 13);
            poly.lineTo(5, 30);
            poly.lineTo(-8, 28);
            poly.lineTo(-6, 6);
            poly.lineTo(-27, 12);
            poly.lineTo(-30, -11);
            poly.lineTo(-6, -15);
            poly.lineTo(-6, -28);
            poly.closePath();
        }
        else if (variety == 2)
        {
            poly.moveTo(10, -30);
            poly.lineTo(30, 0);
            poly.lineTo(15, 30);
            poly.lineTo(0, 15);
            poly.lineTo(-15, 30);
            poly.lineTo(-30, 0);
            poly.lineTo(-10, -30);
            poly.closePath();
        }
        else
        {
            poly.moveTo(30, -18);
            poly.lineTo(5, 5);
            poly.lineTo(30, 15);
            poly.lineTo(15, 30);
            poly.lineTo(0, 25);
            poly.lineTo(-15, 30);
            poly.lineTo(-25, 8);
            poly.lineTo(-10, -25);
            poly.lineTo(0, -30);
            poly.lineTo(10, -30);
            poly.closePath();
        }

        // Scale to the desired size
        double scale = ASTEROID_SCALE[size];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));

        // Save the outline
        outline = poly;
    }

    /**
     * Returns the size of the asteroid
     */
    public int getSize ()
    {
        return size;
    }

    /**
     * When an Asteroid collides with an AsteroidDestroyer, it expires.
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AsteroidDestroyer)
        {
            for (int i = 0; i < 4; i++)
            {
                controller.addParticipant(new Debris(0, this.getX(), this.getY()));
            }
            // Creates two medium asteroids if a large asteroid is destroyed
            if(this.size == 2)
            {
                controller.addParticipant(new Asteroid(RANDOM.nextInt(3), 1, this.getX(), this.getY(), RANDOM.nextInt(MAXIMUM_MEDIUM_ASTEROID_SPEED - MAXIMUM_LARGE_ASTEROID_SPEED) + MAXIMUM_LARGE_ASTEROID_SPEED, this.controller));
                controller.addParticipant(new Asteroid(RANDOM.nextInt(3), 1, this.getX(), this.getY(), RANDOM.nextInt(MAXIMUM_MEDIUM_ASTEROID_SPEED - MAXIMUM_LARGE_ASTEROID_SPEED) + MAXIMUM_LARGE_ASTEROID_SPEED, this.controller));
            }
            
            // Creates two small asteroids if a medium asteroid is destroyed
            if (this.size == 1)
            {
                controller.addParticipant(new Asteroid(RANDOM.nextInt(3), 0, this.getX(), this.getY(), RANDOM.nextInt(MAXIMUM_SMALL_ASTEROID_SPEED - MAXIMUM_MEDIUM_ASTEROID_SPEED) + MAXIMUM_MEDIUM_ASTEROID_SPEED, this.controller));
                controller.addParticipant(new Asteroid(RANDOM.nextInt(3), 0, this.getX(), this.getY(), RANDOM.nextInt(MAXIMUM_SMALL_ASTEROID_SPEED - MAXIMUM_MEDIUM_ASTEROID_SPEED) + MAXIMUM_MEDIUM_ASTEROID_SPEED, this.controller));
            }
            
            // Sends what score should be added to the total score in the controller and plays the sound clip
            if (this.size == 2) 
            {
                controller.scoreIncrease(20); 
                controller.playClip(1);
            } 
            else if (this.size == 1) 
            {
                controller.scoreIncrease(50);
                controller.playClip(2);
            } 
            else if (this.size == 0) 
            {
                controller.scoreIncrease(100);
                controller.playClip(3);
            }
            
            // Expire the asteroid
            Participant.expire(this); 
            
            // Inform the controller
            controller.asteroidDestroyed();
        }
    }
}
