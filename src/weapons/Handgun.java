package weapons;

import entities.Robot;
import utils.Vector2D;

public class Handgun extends RangedWeapon {

    @Override
    protected Projectile createProjectile(Vector2D pos, String dir, Robot owner) {
        float speed = 400f;

        // 👇 THIS is vel
        Vector2D vel = new Vector2D(0, 0);

        // set direction
        switch (dir.toLowerCase()) {
            case "right": vel.set(speed, 0); break;
            case "left": vel.set(-speed, 0); break;
            case "up": vel.set(0, -speed); break;
            case "down": vel.set(0, speed); break;
        }

        // 👇 spawn slightly in front
        Vector2D spawn = new Vector2D(pos.getVector2DX(), pos.getVector2DY());
        float offset = 25f;

        switch (dir.toLowerCase()) {
            case "right": spawn.addLocal(new Vector2D(offset, 0)); break;
            case "left": spawn.addLocal(new Vector2D(-offset, 0)); break;
            case "up": spawn.addLocal(new Vector2D(0, -offset)); break;
            case "down": spawn.addLocal(new Vector2D(0, offset)); break;
        }

        return new Projectile(spawn, vel, 20f, owner);
    }
}