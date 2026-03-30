package entities;

import Weapons.HandGun;
import Weapons.MeleeWeapon;
import utils.Vector2D;

public class Robot {
    private float health ;
    private Vector2D position ;
    private Vector2D spawnPosition ;
    private int lives ;
    private Vector2D velocity ;
    private int direction ;

    private float roboWidth ;
    private float roboHeight ;
    private HandGun handGun ;
    private MeleeWeapon meleeWeapon ;
    private AnimationManager animationManager ;

    public float jumpForce = 400.0f ;
    public float defaultVel = 200.0f ;
    public float meleeDamage = 40.0f ;
    public boolean onSurface = false ;
    public boolean gameOverforRobo = false ;
    public Tile currentPlatform = null ;
    public boolean isAttacking = false ;
    public boolean isShooting = false ;
    public long lastAttackTime = 0 ;
    public long lastShootTime = 0 ;
    public static final long ATTACK_COOLDOWN = 500;
    public static final long SHOOT_COOLDOWN = 500 ;
    private static final float ATTACK_DAMAGE = 20.0f ;
    private static final float MAX_HEALTH = 100.0f ;
    private static final int MAX_LIVES = 3 ;

    public boolean isHit = false ;
    public long hitTime = 0 ;
    public static final long HIT_DURATION = 500 ;

    public Robot(float x , float y) {
        this.position = new Vector2D(x , y) ;
        this.spawnPosition = new Vector2D(x , y);
        this.velocity = new Vector2D() ;
        this.health = MAX_HEALTH ;
        this.lives = MAX_LIVES ;

        this.roboWidth = 60 ;
        this.roboHeight = 60 ;
        this.handGun = new HandGun() ;
        this.meleeWeapon = new MeleeWeapon(meleeDamage) ;
        this.direction = 1 ;
        this.animationManager = new AnimationManager(200) ;
    }

    public Robot(Vector2D vec) {
        this.position = vec ;
        this.spawnPosition = new Vector2D(vec.getVector2DX() , vec.getVector2DY());
        this.velocity = new Vector2D() ;
        this.health = MAX_HEALTH ;
        this.lives = MAX_LIVES ;

        this.roboWidth = 60 ;
        this.roboHeight = 60 ;
        this.handGun = new HandGun() ;
        this.meleeWeapon = new MeleeWeapon(meleeDamage) ;
        this.direction = 1 ;
        this.animationManager = new AnimationManager(200) ;
    }

    public void jump() {
        if(onSurface) {
            this.velocity.subLocal(new Vector2D(0,jumpForce)) ;
            onSurface = false ;
        }

        String dir = (direction == 1 ? "right" : "left") ;
        animationManager.setState("jump_" + dir) ;
    }

    public void shoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShootTime >= SHOOT_COOLDOWN) {
            handGun.createProjectile(position , velocity , direction) ;
            isShooting = true ;
            lastShootTime = currentTime ;
        }

        String dir = (direction == 1 ? "right" : "left") ;
        animationManager.setState("attack_" + "gun_" + dir) ;
    }

    public void moveRight() {
        direction = 1;
        animationManager.setDirection("right");
        animationManager.setState("walk_right");
        this.velocity.set(defaultVel, velocity.getVector2DY());
    }

    public void moveLeft() {
        direction = -1;
        animationManager.setDirection("left");
        animationManager.setState("walk_left");
        this.velocity.set(-defaultVel, velocity.getVector2DY());
    }

    public void idle() {
        String dir = (direction == 1 ? "right" : "left") ;
        animationManager.setState("idle_" + dir);
        // Stop horizontal movement
        if(onSurface) this.velocity.set(0, velocity.getVector2DY());
    }

    public void attack() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime >= ATTACK_COOLDOWN) {
            isAttacking = true ;
            lastAttackTime = currentTime ;
        }

        String dir = (direction == 1 ? "right" : "left") ;
        animationManager.setState("attack_" + "sword_" + dir);
    }

    public void takeDamage(float damage) {
        this.health -= damage ;
        this.hitTime = System.currentTimeMillis() ;
        this.isHit = true ;
        if (this.health < 0) {
            this.health = 0 ;
        }
    }

    public boolean isDead() { return health <= 0 ; }
    public boolean hasLivesRemaining() {
        return lives > 0 ;
    }
    public void respawn() {
        if (lives > 0) {
            lives-- ;
            health = MAX_HEALTH ;
            position.set(spawnPosition);
            velocity.set(0, 0);
            onSurface = false ;
            isAttacking = false ;
            direction = 1 ;
            animationManager.setState("idle_right") ;
        }
    }

    public void death() {
        position.set(0.0f , 0.0f);
        roboWidth = 0;
        roboHeight = 0;
        velocity.set(0.0f , 0.0f);
        lives = 0;
        health = 0.0f;
    }

    public void updateAnimation() {
        if(animationManager != null) animationManager.update() ;
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
        return this.lives ;
    }
    public static float getMaxHealth() {
        return MAX_HEALTH ;
    }
    public static float getAttackDamage() {
        return ATTACK_DAMAGE ;
    }

    public int getDirection() { return this.direction ; }
    public void setDirection(int direction) { this.direction = direction ; }

    public HandGun getHandGun() { return this.handGun ; }
    public MeleeWeapon getMeleeWeapon() { return this.meleeWeapon ; }

    public AnimationManager getAnimationManager() { return animationManager; }
}
