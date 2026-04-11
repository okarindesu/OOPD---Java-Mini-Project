package Weapons;

import entities.Robot;
import utils.Vector2D;

public class HandGun {
    private float bulletVelocity ;
    private float bulletOffset ;
    private float bulletDamage ;
    private ProjectileSystem projectileSystem ;

    public HandGun() {
        this.bulletVelocity = 400.0f ;
        this.bulletOffset = 30.0f ;
        this.bulletDamage = 20.0f ;
        this.projectileSystem = new ProjectileSystem() ;
    }

    public void createProjectile(Vector2D pos , Vector2D roboVel , int dir) {
        Vector2D vel ;
        vel = new Vector2D(dir * bulletVelocity , 0.0f) ;

        Vector2D spawn = new Vector2D(pos.getVector2DX(), pos.getVector2DY()) ;
        spawn.addLocal(new Vector2D(dir * bulletOffset , bulletOffset));

        projectileSystem.addProjectile(new Projectile(spawn , vel , bulletDamage));
    }

    public float getBulletDamage() { return this.bulletDamage ; }
    public void setBulletDamage(float bulletDamage) { this.bulletDamage = bulletDamage ; }
    public float getBulletVelocity() { return this.bulletVelocity ; }
    public ProjectileSystem getProjectileSystem() { return this.projectileSystem ; }
}
