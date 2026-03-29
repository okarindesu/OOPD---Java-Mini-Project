package weapons;

import physics.PhysicsSystem;

import java.util.ArrayList;
import java.util.List;

public class ProjectileSystem {
    private List<Projectile> projectiles = new ArrayList<>();

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    public void update(float dt) {
        // First, update all active projectiles
        for (Projectile p : projectiles) {
            if (!p.isActive()) continue;

            PhysicsSystem.updateProjectilePosition(p, dt);

            if (p.isExpired()) {
                p.deactivate();
            }
        }

        projectiles.removeIf(p -> !p.isActive());
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }
}