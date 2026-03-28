package weapons;

import java.util.ArrayList;
import java.util.List;

public class ProjectileSystem {
    private List<Projectile> projectiles = new ArrayList<>();

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    public void update(float dt) {
        for (Projectile p : projectiles) {
            if (!p.isActive()) continue;

            p.update(dt);

            if (p.isExpired()) {
                p.deactivate();
            }
        }
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }
}