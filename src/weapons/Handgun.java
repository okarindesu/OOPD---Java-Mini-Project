package weapons;

import entities.Robot;
import utils.Vector2D;

public class Handgun extends RangedWeapon {

    @Override
    protected Projectile createProjectile(Vector2D pos, String dir, Robot owner) {
        float speed = 400f;

        Vector2D vel = new Vector2D(0, 0);

        switch (dir.toLowerCase()) {
            case "right": vel.set(speed, 0); break;
            case "left": vel.set(-speed, 0); break;
            case "up": vel.set(0, -speed); break;
            case "down": vel.set(0, speed); break;
        }

        // Spawn from center of sprite (30px offset for 60x60 sprite)
        Vector2D spawn = new Vector2D(pos.getVector2DX() + 30, pos.getVector2DY() + 30);
        float offset = 20f;

        switch (dir.toLowerCase()) {
            case "right": spawn.addLocal(new Vector2D(offset, 0)); break;
            case "left": spawn.addLocal(new Vector2D(-offset, 0)); break;
            case "up": spawn.addLocal(new Vector2D(0, -offset)); break;
            case "down": spawn.addLocal(new Vector2D(0, offset)); break;
        }

        return new Projectile(spawn, vel, 20f, owner, owner.getProjectileColor());
    }
}