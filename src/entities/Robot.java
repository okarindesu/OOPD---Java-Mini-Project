package entities;

import utils.Vector2D;
import weapons.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.imageio.ImageIO;

public class Robot {
    // Position and physics
    private Vector2D position;
    private Vector2D spawnPosition;
    private Vector2D velocity;
    
    public float x, y;
    public float roboWidth = 60;
    public float roboHeight = 60;
    
    // Health and lives
    private float health = 100.0f;
    private int lives = 3;
    private static final float MAX_HEALTH = 100.0f;
    private static final int MAX_LIVES = 3;
    
    // Physics
    public float velocityY = 0;
    public boolean onGround = false;
    public boolean onSurface = false;
    public float jumpForce = 18.0f;
    public float defaultVel = 200.0f;
    
    // Animation and sprites
    private AnimationManager animationManager;
    public String direction = "RIGHT";
    private Color color;
    private Color projectileColor;
    
    // Attack
    public boolean isAttacking = false;
    public long lastAttackTime = 0;
    private static final long ATTACK_COOLDOWN = 900;
    private static final float ATTACK_DAMAGE = 20.0f;
    
    // Hit effect
    private long hitTime = 0;
    private static final long HIT_DURATION = 500; // 500ms = half a second
    
    // Weapons
    private Weapon weapon;
    private ProjectileSystem projectileSystem;
    
    // Game state
    public boolean gameOverforRobo = false;
    public Tile currentPlatform = null;
    public float damageInflicted = 50.0f;

    public Robot(float x, float y, Color color) {
        this.x = x;
        this.y = y;
        this.position = new Vector2D(x, y);
        this.spawnPosition = new Vector2D(x, y);
        this.velocity = new Vector2D();
        this.color = color;
        this.projectileColor = color; // Default to same color as sprite
        this.health = MAX_HEALTH;
        this.lives = MAX_LIVES;
        
        this.animationManager = new AnimationManager(200); // 200ms per frame
        this.direction = "RIGHT";
    }

    public Robot(Vector2D vec, Color color) {
        this.x = vec.getVector2DX();
        this.y = vec.getVector2DY();
        this.position = new Vector2D(vec.getVector2DX(), vec.getVector2DY());
        this.spawnPosition = new Vector2D(vec.getVector2DX(), vec.getVector2DY());
        this.velocity = new Vector2D();
        this.color = color;
        this.projectileColor = color; // Default to same color as sprite
        this.health = MAX_HEALTH;
        this.lives = MAX_LIVES;
        
        this.animationManager = new AnimationManager(200);
        this.direction = "RIGHT";
    }

    // Old constructor for backwards compatibility
    public Robot(float x, float y) {
        this(x, y, Color.RED);
    }

    public Robot(Vector2D vec) {
        this(vec, Color.RED);
    }

    public void moveRight() {
        direction = "RIGHT";
        animationManager.setDirection("right");
        animationManager.setState("walk_right");
        // Update velocity for physics system
        this.velocity.set(defaultVel, velocity.getVector2DY());
    }

    public void moveLeft() {
        direction = "LEFT";
        animationManager.setDirection("left");
        animationManager.setState("walk_left");
        // Update velocity for physics system
        this.velocity.set(-defaultVel, velocity.getVector2DY());
    }

    public void jump() {
        if (onGround) {
            velocityY = -jumpForce;
            onGround = false;
        }
        
        // Also handle old physics system
        if (onSurface) {
            this.velocity.subLocal(new Vector2D(0, jumpForce * 33.33f));
            onSurface = false;
        }
        
        // Set jump animation
        String dir = direction.equals("RIGHT") ? "right" : "left";
        animationManager.setState("jump_" + dir);
    }

    public void idle() {
        String dir = direction.equals("RIGHT") ? "right" : "left";
        animationManager.setState("idle_" + dir);
        // Stop horizontal movement
        this.velocity.set(0, velocity.getVector2DY());
    }

    public void attack() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime >= ATTACK_COOLDOWN) {
            isAttacking = true;
            lastAttackTime = currentTime;
            
            // Use the weapon (fire projectile or melee)
            if (weapon != null) {
                weapon.use(this);
            }
            
            // Set attack animation
            String weaponType = (weapon instanceof Handgun) ? "gun" : "sword";
            String dir = direction.equals("RIGHT") ? "right" : "left";
            animationManager.setState("attack_" + weaponType + "_" + dir);
        }
    }

    public void knockback(int power) {
        x += power;
    }

    public void takeDamage(float damage) {
        this.health -= damage;
        this.hitTime = System.currentTimeMillis(); // Start hit effect
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public void respawn() {
    lives--;  // decrement first

    if (lives >= 0) {
        if (lives == 0) {
            death();  // last life used → game over
            return;
        }

        health = MAX_HEALTH;
        x = spawnPosition.getVector2DX();
        y = spawnPosition.getVector2DY();
        position.set(spawnPosition);
        velocity.set(0, 0);
        velocityY = 0;
        onGround = false;
        onSurface = false;
        isAttacking = false;
        direction = "RIGHT";
        animationManager.setState("idle_right");
    }
}

    public void death() {
        x = 0;
        y = 0;
        roboWidth = 0;
        roboHeight = 0;
        velocityY = 0;
        lives = 0;
        gameOverforRobo = true;
    }

    public void draw(Graphics g) {
        if (gameOverforRobo) return;

        // Sync position with x, y (in case physics system updated position)
        this.x = position.getVector2DX();
        this.y = position.getVector2DY();

        BufferedImage sprite = animationManager.getCurrentFrame();
        
        // Check if we're in hit effect (grayscale mode)
        boolean isHit = (System.currentTimeMillis() - hitTime) < HIT_DURATION;
        
        if (sprite != null) {
            // Apply grayscale if currently being hit
            if (isHit) {
                sprite = toGrayscale(sprite);
            }
            g.drawImage(sprite, (int)x, (int)y, (int)roboWidth, (int)roboHeight, null);
        } else {
            if (isHit) {
                g.setColor(Color.DARK_GRAY); // Gray fallback color
            } else {
                g.setColor(color);
            }
            g.fillRect((int)x, (int)y, (int)roboWidth, (int)roboHeight);
        }
    }
    
    private BufferedImage toGrayscale(BufferedImage image) {
        // Create a new image with same dimensions but preserving alpha channel
        BufferedImage grayscale = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        // Apply grayscale filter while preserving transparency
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                
                // Extract color components
                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;
                
                // Calculate grayscale value using luminosity formula
                int gray = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
                
                // Combine alpha with grayscale value
                int grayscaleArgb = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                grayscale.setRGB(x, y, grayscaleArgb);
            }
        }
        
        return grayscale;
    }

    public void updateAnimation() {
        if (animationManager != null) {
            animationManager.update();
        }
    }

    // Getters and setters
    public Vector2D getPosition() { 
        // Sync x, y with position for drawing
        this.x = position.getVector2DX();
        this.y = position.getVector2DY();
        return position; 
    }
    
    public void setPosition(float x, float y) { 
        this.x = x;
        this.y = y;
        this.position.set(x, y); 
    }
    
    public void setPosition(Vector2D vec) { 
        this.x = vec.getVector2DX();
        this.y = vec.getVector2DY();
        this.position.set(vec); 
    }

    public Vector2D getVelocity() { return this.velocity; }
    public void setVelocity(float xvel, float yvel) { 
        this.velocity.set(xvel, yvel); 
    }
    public void setVelocity(Vector2D vec) { 
        this.velocity.set(vec); 
    }

    public float getRoboWidth() { return this.roboWidth; }
    public void setRoboWidth(float roboWidth) { this.roboWidth = roboWidth; }
    public float getRoboHeight() { return this.roboHeight; }
    public void setRoboHeight(float roboHeight) { this.roboHeight = roboHeight; }

    public float getHealth() { return this.health; }
    public void setHealth(float health) { this.health = health; }
    public int getLives() { return this.lives; }
    public static float getMaxHealth() { return MAX_HEALTH; }
    public static float getAttackDamage() { return ATTACK_DAMAGE; }

    public AnimationManager getAnimationManager() { return animationManager; }
    public BufferedImage getCurrentSprite() {
        if (animationManager == null) return null;
        return animationManager.getCurrentFrame();
    }

    public void setWeapon(Weapon weapon) { this.weapon = weapon; }
    public Weapon getWeapon() { return weapon; }
    public void setProjectileSystem(ProjectileSystem ps) { this.projectileSystem = ps; }
    public ProjectileSystem getProjectileSystem() { return projectileSystem; }
    public Color getColor() { return color; }
    public Color getProjectileColor() { return projectileColor; }
    public void setProjectileColor(Color color) { this.projectileColor = color; }

    public boolean isDead() { return health <= 0; }
    public boolean hasLivesRemaining() { return lives > 0; }

    public void reset() {
    this.lives = MAX_LIVES;
    this.health = MAX_HEALTH;

    this.gameOverforRobo = false;

    this.x = spawnPosition.getVector2DX();
    this.y = spawnPosition.getVector2DY();
    this.position.set(spawnPosition);

    this.velocity.set(0, 0);
    this.velocityY = 0;

    this.onGround = false;
    this.onSurface = false;

    this.isAttacking = false;

    this.direction = "RIGHT";
    animationManager.setState("idle_right");
}
}

