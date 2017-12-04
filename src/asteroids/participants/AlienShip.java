package asteroids.participants;

import static asteroids.game.Constants.ALIENSHIP_SCALE;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;

/**
 * Represents the alien ship that appears in levels beginning with level 2. The ship moves in a zig zag pattern.
 */
public class AlienShip extends Participant implements ShipDestroyer, AsteroidDestroyer
{
    /** The outline of the alien ship */
    private Shape outline;

    /** Game controller */
    private Controller controller;

    public AlienShip (int size, Controller controller)
    {
        this.controller = controller;
//        setPosition(x, y);
//        setRotation(direction);
        
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(20, 10);
        poly.lineTo(40, 10);
        poly.lineTo(55, 25);
        poly.lineTo(40, 40);
        poly.lineTo(20, 40);
        poly.lineTo(5, 25);
        poly.lineTo(55, 25);
        poly.lineTo(5, 25);
        poly.closePath();
        outline = poly;
        
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
        if (p instanceof ShipDestroyer)
        {
            // Tell the controller the ship was destroyed
            controller.shipDestroyed();
        }
        if (p instanceof AsteroidDestroyer) {

            // Tell the controller the ship was destroyed
            controller.asteroidDestroyed();
        }

    }

}
