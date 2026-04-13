package Weapons;

import java.util.ArrayList;
import java.util.List;

public class ProjectileSystem {
    private List<Projectile> projectiles ;
    public ProjectileSystem() { this.projectiles = new ArrayList<>() ; }

    public void addProjectile(Projectile p) { projectiles.add(p) ; }
    public List<Projectile> getProjectiles() { return projectiles ; }
}
