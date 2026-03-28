package weapons;

import utils.Vector2D;
import entities.*;
import java.awt.Color;

public class Projectile {
    private Vector2D position;
    private Vector2D velocity;
    private float damage;
    private boolean active = true;
    private Color color;

    private Robot owner;

    private long spawnTime;
    private long lifeTime = 2000; // 2 seconds

    public Projectile(Vector2D pos, Vector2D vel, float damage, Robot owner, Color color) {
        this.position = new Vector2D(pos.getVector2DX(), pos.getVector2DY());
        this.velocity = vel;
        this.damage = damage;
        this.owner = owner;
        this.color = color;
        this.spawnTime = System.currentTimeMillis();
    }

    public void update(float dt) {
        position.addLocal(new Vector2D(
                velocity.getVector2DX() * dt,
                velocity.getVector2DY() * dt
        ));
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime > lifeTime;
    }

    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public float getDamage() { return damage; }
    public Robot getOwner() {return owner;}
    public Color getColor() { return color; }

    public boolean isActive() { return active; }
    public void deactivate() { active = false; }
}