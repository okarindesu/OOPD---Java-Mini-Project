package engine;

import entities.*;
import entities.Robot;
import physics.CollisionHandler;
import physics.CollisionResolver;
import physics.GameRenderer;
import physics.PhysicsSystem;
import utils.Vector2D;
import weapons.Handgun;
import weapons.MeleeWeapon;
import weapons.ProjectileSystem;

import java.awt.*;
import java.awt.event.KeyEvent;

public class GameLoop extends Canvas implements Runnable {
    private Thread thread ;
    private boolean running = false ;
    public boolean restartHandled = false;
    private Vector2D initPos1 ;
    private Vector2D initPos2 ;
    private ProjectileSystem projectileSystem;

    private GameStateManager gameStateManager;

    private PlayerController playerController ;
    private InputHandler inputHandler ;
    private Robot robot1 ;
    private Robot robot2 ;
    private RobotSystem robotSystem ;
    private PhysicsSystem physicsSystem ;
    private GameRenderer gameRenderer ;
    private CollisionHandler collisionHandler ;
    private CollisionResolver collisionResolver ;
    private String filePath ;

    private Level level ;
    private Level startMenuLevel;

    private Camera camera ;

    public static final int WIDTH = 1280 ;
    public static final int HEIGHT = 720 ;


    public GameLoop() {
        // Create robots with colors: Player 1 = BLUE, Player 2 = GREEN
        robot1 = new Robot(400, 0, Color.BLUE);
        robot2 = new Robot(800, 0, Color.GREEN);
        
        // Set projectile colors
        robot1.setProjectileColor(Color.RED);   // Red projectiles for Player 1
        robot2.setProjectileColor(Color.GREEN);   // Green projectiles for Player 2

        robotSystem = new RobotSystem(robot1, robot2);

        projectileSystem = new ProjectileSystem();

        robot1.setProjectileSystem(projectileSystem);
        robot2.setProjectileSystem(projectileSystem);

        robot1.setWeapon(new Handgun());     // Gun
        robot2.setWeapon(new MeleeWeapon()); // Sword

        inputHandler = new InputHandler();
        addKeyListener(inputHandler);
        setFocusable(true);

        gameStateManager = new GameStateManager();

        camera = new Camera(0, 0);

        playerController = new PlayerController(robot1, robot2, inputHandler);
        physicsSystem = new PhysicsSystem();
        gameRenderer = new GameRenderer(this, WIDTH, HEIGHT);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        collisionHandler = new CollisionHandler();
        collisionResolver = new CollisionResolver();

        filePath = "resources/levels/Vertical_Movement.wrl";
        level = LevelLoader.loadlevel(filePath);

        startMenuLevel = LevelLoader.loadlevel("resources/levels/start_menu.wrl");

        // ========== LOAD ANIMATIONS ==========
        
        // Player 1 (BLUE - Gun) animations
        robot1.getAnimationManager().addAnimation("idle_right", 
            SpriteLoader.loadFrames("/sprites/player1/idle_right/frame1.png"));
        robot1.getAnimationManager().addAnimation("idle_left", 
            SpriteLoader.loadFrames("/sprites/player1/idle_left/frame1.png"));
        robot1.getAnimationManager().addAnimation("walk_right", 
            SpriteLoader.loadFrames(
                "/sprites/player1/walk_right/frame1.png",
                "/sprites/player1/walk_right/frame2.png",
                "/sprites/player1/walk_right/frame3.png",
                "/sprites/player1/walk_right/frame4.png"
            ));
        robot1.getAnimationManager().addAnimation("walk_left", 
            SpriteLoader.loadFrames(
                "/sprites/player1/walk_left/frame1.png",
                "/sprites/player1/walk_left/frame2.png",
                "/sprites/player1/walk_left/frame3.png",
                "/sprites/player1/walk_left/frame4.png"
            ));
        robot1.getAnimationManager().addAnimation("jump_left", 
            SpriteLoader.loadFrames("/sprites/player1/jump_left/frame1.png"));
        robot1.getAnimationManager().addAnimation("jump_right", 
            SpriteLoader.loadFrames("/sprites/player1/jump_right/frame1.png"));
        robot1.getAnimationManager().addAnimation("attack_gun_left", 
            SpriteLoader.loadFrames(
                "/sprites/player1/attack_gun_left/frame1.png",
                "/sprites/player1/attack_gun_left/frame2.png",
                "/sprites/player1/attack_gun_left/frame3.png"
            ));
        robot1.getAnimationManager().addAnimation("attack_gun_right", 
            SpriteLoader.loadFrames(
                "/sprites/player1/attack_gun_right/frame1.png",
                "/sprites/player1/attack_gun_right/frame2.png",
                "/sprites/player1/attack_gun_right/frame3.png"
            ));

        // Player 2 (GREEN - Sword) animations
        robot2.getAnimationManager().addAnimation("idle_right", 
            SpriteLoader.loadFrames("/sprites/player2/idle_right/frame1.png"));
        robot2.getAnimationManager().addAnimation("idle_left", 
            SpriteLoader.loadFrames("/sprites/player2/idle_left/frame1.png"));
        robot2.getAnimationManager().addAnimation("walk_right", 
            SpriteLoader.loadFrames(
                "/sprites/player2/walk_right/frame1.png",
                "/sprites/player2/walk_right/frame2.png",
                "/sprites/player2/walk_right/frame3.png",
                "/sprites/player2/walk_right/frame4.png"
            ));
        robot2.getAnimationManager().addAnimation("walk_left", 
            SpriteLoader.loadFrames(
                "/sprites/player2/walk_left/frame1.png",
                "/sprites/player2/walk_left/frame2.png",
                "/sprites/player2/walk_left/frame3.png",
                "/sprites/player2/walk_left/frame4.png"
            ));
        robot2.getAnimationManager().addAnimation("jump_left", 
            SpriteLoader.loadFrames("/sprites/player2/jump_left/frame1.png"));
        robot2.getAnimationManager().addAnimation("jump_right", 
            SpriteLoader.loadFrames("/sprites/player2/jump_right/frame1.png"));
        robot2.getAnimationManager().addAnimation("attack_sword_left", 
            SpriteLoader.loadFrames(
                "/sprites/player2/attack_sword_left/frame1.png",
                "/sprites/player2/attack_sword_left/frame2.png",
                "/sprites/player2/attack_sword_left/frame3.png"
            ));
        robot2.getAnimationManager().addAnimation("attack_sword_right", 
            SpriteLoader.loadFrames(
                "/sprites/player2/attack_sword_right/frame1.png",
                "/sprites/player2/attack_sword_right/frame2.png",
                "/sprites/player2/attack_sword_right/frame3.png"
            ));

        // Set initial animation states
        robot1.getAnimationManager().setState("idle_right");
        robot2.getAnimationManager().setState("idle_left");
    }

    public synchronized void start() {
        if(running) return ;
        running = true ;

        while (!this.isDisplayable()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.createBufferStrategy(3);
        this.requestFocus();
        thread = new Thread(this) ;
        thread.start() ;
    }

    public synchronized void stop() {
        if(!running) return ;
        running = false ;
        try {
            thread.join() ;
        } catch (InterruptedException e) {
            e.printStackTrace() ;
        }
    }


    public void restartGame() {
        // Create new robots with colors
        robot1 = new Robot(100, 0, Color.BLUE);
        robot2 = new Robot(400, 0, Color.GREEN);
        
        // Set projectile colors
        robot1.setProjectileColor(Color.RED);   // Red projectiles for Player 1
        robot2.setProjectileColor(Color.RED);   // Red projectiles for Player 2

        robotSystem = new RobotSystem(robot1, robot2);
        
        robot1.setProjectileSystem(projectileSystem);
        robot2.setProjectileSystem(projectileSystem);
        
        robot1.setWeapon(new Handgun());
        robot2.setWeapon(new MeleeWeapon());

        level = LevelLoader.loadlevel(filePath);

        playerController = new PlayerController(robot1, robot2, inputHandler);

        camera.setCameraX(0);
        camera.setCameraY(0);
        
        // Reload animations for new robots
        // Player 1 (BLUE - Gun) animations
        robot1.getAnimationManager().addAnimation("idle_right", 
            SpriteLoader.loadFrames("/sprites/player1/idle_right/frame1.png"));
        robot1.getAnimationManager().addAnimation("idle_left", 
            SpriteLoader.loadFrames("/sprites/player1/idle_left/frame1.png"));
        robot1.getAnimationManager().addAnimation("walk_right", 
            SpriteLoader.loadFrames(
                "/sprites/player1/walk_right/frame1.png",
                "/sprites/player1/walk_right/frame2.png",
                "/sprites/player1/walk_right/frame3.png",
                "/sprites/player1/walk_right/frame4.png"
            ));
        robot1.getAnimationManager().addAnimation("walk_left", 
            SpriteLoader.loadFrames(
                "/sprites/player1/walk_left/frame1.png",
                "/sprites/player1/walk_left/frame2.png",
                "/sprites/player1/walk_left/frame3.png",
                "/sprites/player1/walk_left/frame4.png"
            ));
        robot1.getAnimationManager().addAnimation("jump_left", 
            SpriteLoader.loadFrames("/sprites/player1/jump_left/frame1.png"));
        robot1.getAnimationManager().addAnimation("jump_right", 
            SpriteLoader.loadFrames("/sprites/player1/jump_right/frame1.png"));
        robot1.getAnimationManager().addAnimation("attack_gun_left", 
            SpriteLoader.loadFrames(
                "/sprites/player1/attack_gun_left/frame1.png",
                "/sprites/player1/attack_gun_left/frame2.png",
                "/sprites/player1/attack_gun_left/frame3.png"
            ));
        robot1.getAnimationManager().addAnimation("attack_gun_right", 
            SpriteLoader.loadFrames(
                "/sprites/player1/attack_gun_right/frame1.png",
                "/sprites/player1/attack_gun_right/frame2.png",
                "/sprites/player1/attack_gun_right/frame3.png"
            ));

        // Player 2 (GREEN - Sword) animations
        robot2.getAnimationManager().addAnimation("idle_right", 
            SpriteLoader.loadFrames("/sprites/player2/idle_right/frame1.png"));
        robot2.getAnimationManager().addAnimation("idle_left", 
            SpriteLoader.loadFrames("/sprites/player2/idle_left/frame1.png"));
        robot2.getAnimationManager().addAnimation("walk_right", 
            SpriteLoader.loadFrames(
                "/sprites/player2/walk_right/frame1.png",
                "/sprites/player2/walk_right/frame2.png",
                "/sprites/player2/walk_right/frame3.png",
                "/sprites/player2/walk_right/frame4.png"
            ));
        robot2.getAnimationManager().addAnimation("walk_left", 
            SpriteLoader.loadFrames(
                "/sprites/player2/walk_left/frame1.png",
                "/sprites/player2/walk_left/frame2.png",
                "/sprites/player2/walk_left/frame3.png",
                "/sprites/player2/walk_left/frame4.png"
            ));
        robot2.getAnimationManager().addAnimation("jump_left", 
            SpriteLoader.loadFrames("/sprites/player2/jump_left/frame1.png"));
        robot2.getAnimationManager().addAnimation("jump_right", 
            SpriteLoader.loadFrames("/sprites/player2/jump_right/frame1.png"));
        robot2.getAnimationManager().addAnimation("attack_sword_left", 
            SpriteLoader.loadFrames(
                "/sprites/player2/attack_sword_left/frame1.png",
                "/sprites/player2/attack_sword_left/frame2.png",
                "/sprites/player2/attack_sword_left/frame3.png"
            ));
        robot2.getAnimationManager().addAnimation("attack_sword_right", 
            SpriteLoader.loadFrames(
                "/sprites/player2/attack_sword_right/frame1.png",
                "/sprites/player2/attack_sword_right/frame2.png",
                "/sprites/player2/attack_sword_right/frame3.png"
            ));

        // Set initial states
        robot1.getAnimationManager().setState("idle_right");
        robot2.getAnimationManager().setState("idle_left");
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerUpdate = 1_000_000_000.0 / 60.0; // 60 FPS
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerUpdate;
            lastTime = now;

            // === Handle input for state transitions ===
            gameStateManager.handleInput(inputHandler);

            // === Update loop: only if the game is running ===
            if (gameStateManager.isRunning()) {
                // Player input
                playerController.control();

                // Update robot animations
                robot1.updateAnimation();
                robot2.updateAnimation();

                // Handle collisions
                collisionHandler.handleCollisions(collisionResolver, level, robot1, robot2, projectileSystem, WIDTH, HEIGHT);

                // Check attacks, respawns, and win condition
                robotSystem.checkAttacksRobots();
                robotSystem.checkRespawns();
                robotSystem.checkWinCondition();

                // Handle restart input if game is over
                if (robotSystem.getWinner() != null) {
                    if (inputHandler.isKeyPressed(KeyEvent.VK_R) && !restartHandled) {
                        restartGame();
                        restartHandled = true;
                    }
                    if (!inputHandler.isKeyPressed(KeyEvent.VK_R)) {
                        restartHandled = false;
                    }
                }

                // === Physics updates ===
                while (delta >= 1) {
                    physicsSystem.update(robot1, robot2, level, projectileSystem);
                    delta--;
                }
            }

            // === Rendering ===
            if (gameStateManager.isStartScreen()) {
                gameRenderer.renderStartScreen(startMenuLevel);
            } else if (gameStateManager.isRunning()) {
                gameRenderer.render(robot1, robot2, level, camera, robotSystem.getWinner(), projectileSystem);
            } else if (gameStateManager.isGameOver()) {
                restartGame();
            }
        }

        stop();
    }


}
