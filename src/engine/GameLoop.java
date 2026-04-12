package engine;

import entities.*;
import entities.Robot;
import physics.CollisionHandler;
import physics.CollisionResolver;
import physics.GameRenderer;
import physics.PhysicsSystem;
import utils.Vector2D;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static entities.Robot.EXPLOSION_DURATION;

public class GameLoop extends Canvas implements Runnable {
    private Thread thread ;
    private boolean running = false ;
    private Vector2D initPos1 ;
    private Vector2D initPos2 ;


    private PlayerController playerController ;
    private InputHandler inputHandler ;
    private Robot robot1 ;
    private Robot robot2 ;
    private RobotSystem robotSystem ;
    private PhysicsSystem physicsSystem ;
    private GameRenderer gameRenderer ;
    private CollisionHandler collisionHandler ;
    private CollisionResolver collisionResolver ;
    private Camera camera ;
    private PowerUpSystem powerUpSystem;
    private AnimationInitializer animationInitializer ;
    private GameStateHandler gameStateHandler ;
    private LevelSelectionContext levelSelectionContext ;
    private StartScreenContext startScreenContext ;
    private GameOverContext gameOverContext;

    private static float BGMMUSIC = 0.25f ;
    private static float SFXMUSIC = 8.00f ;

    private int selectedLevel = 0 ;
    private LevelInfo[] levels ;

    public static final int WIDTH = 1280 ;
    public static final int HEIGHT = 720 ;


    public GameLoop() {
        initPos1 = new Vector2D(100 , 0) ;
        robot1 = new Robot(initPos1) ;

        initPos2 = new Vector2D(400 , 0) ;
        robot2 = new Robot(initPos2) ;

        robotSystem = new RobotSystem(robot1 , robot2) ;

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
        powerUpSystem = new PowerUpSystem();

        animationInitializer = new AnimationInitializer(robot1.getAnimationManager() , robot2.getAnimationManager()) ;
        animationInitializer.initializeRoboAnimation();

        levels = new LevelInfo[] {
                new LevelInfo(
                        "resources/levels/Level1.wrl",
                        "Aurora",
                        loadImage("resources/backgrounds/preview/b8.png")
                ),
                new LevelInfo(
                        "resources/levels/Level2.wrl",
                        "Dunes",
                        loadImage("resources/backgrounds/preview/b2.png")
                ),
                new LevelInfo(
                        "resources/levels/Level3.wrl",
                        "Dunes",
                        loadImage("resources/backgrounds/preview/b3.png")
                ),
                new LevelInfo(
                        "resources/levels/Level4.wrl",
                        "Dunes",
                        loadImage("resources/backgrounds/preview/b4.png")
                ),
                new LevelInfo(
                        "resources/levels/Level5.wrl",
                        "Dunes",
                        loadImage("resources/backgrounds/preview/b5.png")
                ),
                new LevelInfo(
                        "resources/levels/Level6.wrl",
                        "Dunes",
                        loadImage("resources/backgrounds/preview/b6.png")
                ),
                new LevelInfo(
                        "resources/levels/Vertical_Movement.wrl",
                        "Dunes",
                        loadImage("resources/backgrounds/preview/b7.png")
                ),
                new LevelInfo(
                        "resources/levels/Basic_Platforms.wrl",
                        "Aurora",
                        loadImage("resources/backgrounds/preview/b1.png")
                )
        };

        SoundManager.load("move_ui", "resources/sounds/selecting_buttons2.wav");
        SoundManager.load("select_ui", "resources/sounds/select_button.wav");
        SoundManager.load("game_over_effect" , "resources/sounds/game_over_effect.wav");

        SoundManager.load("robo_jump", "resources/sounds/jump.wav");
        SoundManager.load("robo_explode", "resources/sounds/robo_explosion.wav");
        SoundManager.load("robo_shoot", "resources/sounds/shoot.wav");
        SoundManager.load("robo_attack", "resources/sounds/attack.wav");
        SoundManager.load("robo_damage", "resources/sounds/damage.wav");

        MusicPlayer.setVolume(BGMMUSIC);
        SoundManager.setVolume(SFXMUSIC);



        gameStateHandler = new GameStateHandler(GameState.START_SCREEN_STATE) ;
        levelSelectionContext = new LevelSelectionContext(gameStateHandler , levels , selectedLevel) ;
        startScreenContext = new StartScreenContext(gameStateHandler) ;
        gameOverContext = new GameOverContext(gameStateHandler);
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("Failed to load: " + path);
            e.printStackTrace();
            return null;
        }
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
        // Try to close the window that contains this canvas
        java.awt.Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        // Ensure JVM exits in case other threads are still alive
        System.exit(0);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime() ;
        double nsPerUpdate = 1_000_000_000.0/60.0 ;
        double delta = 0 ;
        GameState previousState = null ;

        while(running) {

            long now = System.nanoTime() ;
            delta += (now - lastTime) / nsPerUpdate ;
            lastTime = now ;

            while(delta >= 1) {
                GameState gameState = gameStateHandler.getGameState() ;

                if (gameState != previousState) {
                    switch (gameState) {
                        case START_SCREEN_STATE:
                        case LEVEL_SELECTION_STATE:
                            MusicPlayer.play("resources/sounds/start_screen_ost.wav");
                            break;

                        case GAME_PLAYING_STATE:
                            if (previousState == GameState.LEVEL_SELECTION_STATE) {
                                powerUpSystem.reset();
                            }
                            MusicPlayer.play("resources/sounds/gameplay_ost.wav");
                            break;

                        case GAME_OVER_STATE:
                            MusicPlayer.play("resources/sounds/game_over.wav");
                            break;
                    }
                    previousState = gameState;
                }

                switch(gameState) {
                    case START_SCREEN_STATE:
                        updateStartScreen(startScreenContext) ;
                        break ;
                    case LEVEL_SELECTION_STATE :
                        updateLevelSelection(levelSelectionContext);
                        break;
                    case GAME_PLAYING_STATE:
                        updateGame(levelSelectionContext);
                        break;
                    case GAME_OVER_STATE:
                        updateGameOver();
                        break;
                }
                delta-- ;
            }
            gameRenderer.render(startScreenContext , levelSelectionContext , gameOverContext , robot1 , robot2 , camera, powerUpSystem) ;
        }
        stop() ;
    }

    private void updateStartScreen(StartScreenContext startScreenContext) {
        playerController.control(startScreenContext , levelSelectionContext , gameOverContext) ;
        if(startScreenContext.isStartPressed()) levelSelectionContext.getGameStateHandler().setGameState(GameState.LEVEL_SELECTION_STATE) ;
        if(startScreenContext.isQuitPressed()) stop() ;
    }

    private void updateGame(LevelSelectionContext levelSelectionContext) {
        updateExplosions(robot1);
        updateExplosions(robot2);
        if(robot1.explosionJustStarted || robot2.explosionJustStarted) {
            if(!robot1.hasLivesRemaining() || !robot2.hasLivesRemaining()) {
                gameRenderer.triggerScreenShake(700, 15);// BIG explosion
                gameRenderer.triggerRedTint(500);
            } else {
                gameRenderer.triggerScreenShake(700, 15);
                gameRenderer.triggerRedTint(500);
            }
            robot1.explosionJustStarted = false ;
            robot2.explosionJustStarted = false ;
        }

        playerController.control(startScreenContext , levelSelectionContext , gameOverContext) ;
        collisionHandler.handleCollisions(collisionResolver , levelSelectionContext.getLevel() , robot1 , robot2 , WIDTH , HEIGHT) ;

        robot1.updateAnimation();
        robot2.updateAnimation();

        robotSystem.robotsShooting();
        robotSystem.checkShootingRobots();
        robotSystem.checkAttacksRobots();
        robotSystem.checkRespawns();
        powerUpSystem.update(levelSelectionContext.getLevel(), robot1, robot2);
        GameOverState result = robotSystem.checkWinCondition();
        if (result != GameOverState.NONE) {
            gameOverContext.setGameOverState(result);
            gameOverContext.startTime = System.currentTimeMillis();
            gameOverContext.justEntered = true;
            gameStateHandler.setGameState(GameState.GAME_OVER_STATE);
        }
        physicsSystem.update(robot1 , robot2 , levelSelectionContext.getLevel()) ;
    }

    private void updateLevelSelection(LevelSelectionContext levelSelectionContext) {
        playerController.control(startScreenContext , levelSelectionContext , gameOverContext) ;
    }

    private void updateGameOver() {
        playerController.control(startScreenContext , levelSelectionContext , gameOverContext) ;

        if (gameOverContext.isReplayPressed()) {
            resetForReplay();
            gameOverContext.resetActions();
            gameStateHandler.setGameState(GameState.GAME_PLAYING_STATE);
        } else if (gameOverContext.isLevelSelectPressed()) {
            resetForReplay();
            gameOverContext.resetActions();
            gameStateHandler.setGameState(GameState.LEVEL_SELECTION_STATE);
        } else if (gameOverContext.isQuitPressed()) {
            // Mirror main menu quit behavior: exit immediately
            System.exit(0);
        }
    }

    private void resetForReplay() {
        robot1.resetForNewGame();
        robot2.resetForNewGame();
        powerUpSystem.reset();
    }

    private void updateExplosions(Robot robot) {
        if(robot.isExploding) {
            long currentTime = System.currentTimeMillis();

            if(currentTime - robot.explosionStartTime >= Robot.EXPLOSION_DURATION) {
                robot.isExploding = false;
                if(robot.hasLivesRemaining()) {
                    robot.respawn();
                } else {
                    robot.hasExplodedFinal = true;
                    robot.death();
                }
            }
        }
    }
}
