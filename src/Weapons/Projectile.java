package Weapons;

import utils.Vector2D;

public class Projectile {
    private Vector2D position ;
    private Vector2D velocity ;
    private float damage ;
    private boolean hasHitRobot ;
    private boolean hasHitTile ;
    private boolean hasReachedBoundary ;
    private float projectileRadius ;

    public Projectile(Vector2D pos , Vector2D vel , float damage) {
        this.position = pos ;
        this.velocity = vel ;
        this.damage = damage ;
        this.projectileRadius = 6.0f ;
        this.hasHitRobot = false ;
        this.hasHitTile = false ;
        this.hasReachedBoundary = false ;
    }

    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public float getDamage() { return damage; }

    public boolean getHasHitRobot() { return this.hasHitRobot ; }
    public void setHasHitRobot(boolean hasHitRobot) { this.hasHitRobot = hasHitRobot ; }

    public boolean getHasHitTile() { return this.hasHitTile ; }
    public void setHasHitTile(boolean hasHitTile) { this.hasHitTile = hasHitTile ; }

    public boolean getHasReachedBoundary() { return this.hasReachedBoundary ; }
    public void setHasReachedBoundary(boolean hasReachedBoundary) { this.hasReachedBoundary = hasReachedBoundary ; }

    public float getProjectileRadius() { return projectileRadius ; }
}
