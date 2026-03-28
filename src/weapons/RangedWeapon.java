package weapons;

import entities.Robot;
import utils.Vector2D;

public abstract class RangedWeapon implements Weapon {
    protected long lastFireTime = 0;
    protected long cooldown = 300;

    protected boolean canFire() {
        return System.currentTimeMillis() - lastFireTime >= cooldown;
    }

    protected abstract Projectile createProjectile(Vector2D position, String direction, Robot owner);

    @Override
    public void use(Robot user) {
        if (!canFire()) return;

        lastFireTime = System.currentTimeMillis();

        Projectile p = createProjectile(user.getPosition(), user.direction, user);

        if (p != null && user.getProjectileSystem() != null) {
            user.getProjectileSystem().addProjectile(p);
        }
    }
}