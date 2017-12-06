package asteroids.participants;

import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;

public class AlienBullet extends Bullet implements ShipDestroyer
{

    public AlienBullet (double x, double y, double direction, Controller controller)
    {
        super(x, y, direction, controller);
    }
    
    @Override 
    public void collidedWith (Participant p)
    {
        if (!(p instanceof AlienBullet))
        {
            Participant.expire(this);
        }
    }

}
