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
import java.awt.image.BufferStrategy;

public class GameLoop extends Canvas implements Runnable {

    private enum GameState {
        MENU,
        LEVEL_SELECT,
        PLAYING
    }

    private GameState gameState = GameState.MENU;

    private Thread thread ;
    private boolean running = false ;
    public boolean restartHandled = false;
    private Vector2D initPos1 ;
    private Vector2D initPos2 ;
    private ProjectileSystem projectileSystem;

    private PlayerController playerController ;
    private InputHandler inputHandler ;
    private Robot robot1 ;
    private Robot robot2 ;
    private RobotSystem robotSystem ;
    private PhysicsSystem physicsSystem ;
    private GameRenderer gameRenderer ;
    private CollisionHandler collisionHandler ;
    private CollisionResolver collisionResolver ;

    private Level level = null ;
    private Camera camera ;


    //LEVELS

    private String[] levels = {
            "resources/levels/Basic_Platforms.wrl",
            "resources/levels/Vertical_Movement.wrl"
    };

    private int selectedLevel = 0;


    public static final int WIDTH = 1280 ;
    public static final int HEIGHT = 720 ;


    public GameLoop() {
        initPos1 = new Vector2D(100 , 0) ;
        robot1 = new Robot(initPos1) ;

        initPos2 = new Vector2D(400 , 0) ;
        robot2 = new Robot(initPos2) ;

        robotSystem = new RobotSystem(robot1 , robot2) ;

        projectileSystem = new ProjectileSystem();

        robot1.setProjectileSystem(projectileSystem);
        robot2.setProjectileSystem(projectileSystem);

        robot1.setWeapon(new Handgun());     // 🔫
        robot2.setWeapon(new MeleeWeapon());  // 🗡️

        inputHandler = new InputHandler() ;
        addKeyListener(inputHandler) ;
        setFocusable(true) ;

        camera = new Camera(0 , 0) ;

        playerController = new PlayerController(robot1 , robot2 , inputHandler) ;
        physicsSystem = new PhysicsSystem() ;
        gameRenderer = new GameRenderer(this , WIDTH , HEIGHT) ;
        setPreferredSize(new Dimension(WIDTH , HEIGHT)) ;

        collisionHandler = new CollisionHandler() ;
        collisionResolver = new CollisionResolver() ;

    }

    public synchronized void start() {
        if (running) return;
        running = true;

        while (!this.isDisplayable()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.createBufferStrategy(3); // ⚠ Must create buffer strategy BEFORE thread starts
        this.requestFocus();
        thread = new Thread(this);
        thread.start();
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

    private boolean enterPressed = false;

    private void handleMenuInput() {

        if (inputHandler.isKeyPressed(KeyEvent.VK_ENTER)) {
            if (!enterPressed) {
                gameState = GameState.LEVEL_SELECT;
                enterPressed = true;
            }
        } else {
            enterPressed = false;
        }
    }

    private void loadLevel(String path) {
        level = LevelLoader.loadlevel(path);

        if (level == null) {
            System.out.println("❌ LEVEL FAILED TO LOAD: " + path);
            return;
        }

        robot1.respawn();
        robot2.respawn();
    }

    private void handleLevelSelectInput() {

        if (inputHandler.isKeyPressed(KeyEvent.VK_DOWN)) {
            selectedLevel = (selectedLevel + 1) % levels.length;
        }

        if (inputHandler.isKeyPressed(KeyEvent.VK_UP)) {
            selectedLevel = (selectedLevel - 1 + levels.length) % levels.length;
        }

        if (inputHandler.isKeyPressed(KeyEvent.VK_ENTER)) {
            loadLevel(levels[selectedLevel]);
            gameState = GameState.PLAYING;
        }
    }

    public void restartGame() {

        robot1.respawn();
        robot2.respawn();

        level = LevelLoader.loadlevel(levels[selectedLevel]);

        camera.setCameraX(0);
        camera.setCameraY(0);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerUpdate = 1_000_000_000.0 / 60.0; // 60 updates per second
        double delta = 0;

        while (running) {

            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerUpdate;
            lastTime = now;

            // Handle input for all states
            switch (gameState) {
                case MENU -> handleMenuInput();
                case LEVEL_SELECT -> handleLevelSelectInput();
                case PLAYING -> {
                    // Game logic
                    playerController.control();

                    if (level != null) {
                        physicsSystem.update(robot1, robot2, level, projectileSystem);

                        collisionHandler.handleCollisions(
                                collisionResolver, level, robot1, robot2, projectileSystem, WIDTH, HEIGHT
                        );

                        robotSystem.checkAttacksRobots();
                        robotSystem.checkRespawns();
                        robotSystem.checkWinCondition();

                        // Restart logic if someone won
                        if (robotSystem.getWinner() != null) {
                            if (inputHandler.isKeyPressed(KeyEvent.VK_R) && !restartHandled) {
                                restartGame();
                                restartHandled = true;
                            }
                            if (!inputHandler.isKeyPressed(KeyEvent.VK_R)) {
                                restartHandled = false;
                            }
                        }
                    }
                }
            }

            // Update fixed-step game logic
            while (delta >= 1) {
                if (gameState == GameState.PLAYING && level != null) {
                    playerController.control();
                    physicsSystem.update(robot1, robot2, level, projectileSystem);

                    collisionHandler.handleCollisions(
                            collisionResolver, level, robot1, robot2, projectileSystem, WIDTH, HEIGHT
                    );

                    robotSystem.checkAttacksRobots();
                    robotSystem.checkRespawns();
                    robotSystem.checkWinCondition();
                }
                delta--;
            }

            // Render everything every loop
            renderAll();

            // Sleep a bit to prevent CPU hogging
            try {
                Thread.sleep(2); // 2 ms is enough
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        stop(); // stop thread safely
    }

    private void renderAll() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        if (g == null) return;

        switch (gameState) {
            case MENU -> gameRenderer.renderMenu(g, WIDTH, HEIGHT);
            case LEVEL_SELECT -> gameRenderer.renderLevelSelect(g, WIDTH, HEIGHT, levels, selectedLevel);
            case PLAYING -> {
                if (level != null) {
                    gameRenderer.render(g, robot1, robot2, level, camera,
                            robotSystem.getWinner(),
                            projectileSystem);
                } else {
                    g.setColor(Color.RED);
                    g.fillRect(0, 0, WIDTH, HEIGHT);
                    g.setColor(Color.WHITE);
                    g.drawString("LEVEL FAILED TO LOAD", WIDTH / 2 - 100, HEIGHT / 2);
                }
            }
        }

        g.dispose();
        bs.show();
    }


}
