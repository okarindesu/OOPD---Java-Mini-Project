package entities;

import utils.Vector2D;

public class PowerUp {
    private final PowerUpType type;
    private final Vector2D position;
    private final Vector2D velocity;
    private final float width;
    private final float height;
    private boolean landed;

    public PowerUp(PowerUpType type, float x, float y, float fallSpeed) {
        this.type = type;
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, fallSpeed);
        this.width = 26.0f;
        this.height = 26.0f;
        this.landed = false;
    }

    public PowerUpType getType() { return type; }
    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public boolean isLanded() { return landed; }
    public void setLanded(boolean landed) { this.landed = landed; }
}
