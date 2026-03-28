package entities;

import utils.Vector2D;

public class Robot {
    private float health ;
    private Vector2D position ;
    private Vector2D spawnPosition ;
    private int lives ;
    private Vector2D velocity ;

    private float roboWidth ;
    private float roboHeight ;

    public float jumpForce = 400.0f ;
    public float defaultVel = 200.0f ;
    public float damageInflicted = 50.0f ;
    public boolean onSurface = false ;
    public boolean gameOverforRobo = false ;
    public Tile currentPlatform = null ;
    public boolean isAttacking = false ;
    public long lastAttackTime = 0 ;
    private static final long ATTACK_COOLDOWN = 500; // 500ms cooldown between attacks
    private static final float ATTACK_DAMAGE = 20.0f ;
    private static final float MAX_HEALTH = 100.0f ;
    private static final int MAX_LIVES = 2 ;

    public Robot(float x , float y) {
        this.position = new Vector2D(x , y) ;
        this.spawnPosition = new Vector2D(x , y);
        this.velocity = new Vector2D() ;
        this.health = MAX_HEALTH ;
        this.lives = MAX_LIVES ;

        this.roboWidth = 20 ;
        this.roboHeight = 20 ;
    }

    public Robot(Vector2D vec) {
        this.position = vec ;
        this.spawnPosition = new Vector2D(vec.getVector2DX() , vec.getVector2DY());
        this.velocity = new Vector2D() ;
        this.health = MAX_HEALTH ;
        this.lives = MAX_LIVES ;

        this.roboWidth = 20 ;
        this.roboHeight = 20 ;
    }

    public void jump() {
        if(onSurface) {
            this.velocity.subLocal(new Vector2D(0,jumpForce)) ;
            onSurface = false ;
        }
    }

    public void shoot() {

    }

    public void attack() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime >= ATTACK_COOLDOWN) {
            isAttacking = true ;
            lastAttackTime = currentTime ;
        }
    }

    public void takeDamage() {
        this.health -= damageInflicted ;
        if (this.health < 0) {
            this.health = 0 ;
        }
    }

    public boolean isDead() { return health <= 0 ; }
    public boolean hasLivesRemaining() {
        return lives > 0 ;
    }
    public void respawn() {
        // health lost from previous life is consumed in RobotSystem (lives decrement)
        health = MAX_HEALTH ;
        position.set(spawnPosition);
        velocity.set(0, 0);
        onSurface = false ;
        isAttacking = false ;
    }

    public void loseLife() {
        if (lives > 0) {
            lives-- ;
        }
    }

    public void reset() {
        lives = MAX_LIVES ;
        health = MAX_HEALTH ;
        position.set(spawnPosition);
        velocity.set(0, 0);
        onSurface = false ;
        isAttacking = false ;
        gameOverforRobo = false ;
    }

    public Vector2D getPosition() { return this.position ; }
    public void setPosition(float x , float y) { position.set(x , y) ; }
    public void setPosition(Vector2D vec) { position.set(vec) ; }

    public Vector2D getVelocity() { return this.velocity ; }
    public void setVelocity(float xvel , float yvel) { velocity.set(xvel , yvel) ; }
    public void setVelocity(Vector2D vec) { velocity.set(vec) ; }

    public float getRoboWidth() { return this.roboWidth ; }
    public void setRoboWidth(float roboWidth) { this.roboWidth = roboWidth ; }
    public float getRoboHeight() { return this.roboHeight ; }
    public void setRoboHeight(float roboHeight) { this.roboHeight = roboHeight ; }

    public float getHealth() {
        return this.health ;
    }
    public void setHealth(float health) {
        this.health = health ;
    }
    public int getLives() {
        return this.lives + 1; // added + 1 here for display issue
    }
    public static float getMaxHealth() {
        return MAX_HEALTH ;
    }
    public static float getAttackDamage() {
        return ATTACK_DAMAGE ;
    }
}
