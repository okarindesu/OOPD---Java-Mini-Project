package engine;

import entities.*;
import entities.Robot;
import physics.CollisionHandler;
import physics.CollisionResolver;
import physics.GameRenderer;
import physics.PhysicsSystem;
import utils.Vector2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

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
    private AnimationInitializer animationInitializer ;
    private LevelSelectionContext levelSelectionContext ;

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
                        "resources/levels/Level7.wrl",
                        "Dunes",
                        loadImage("resources/backgrounds/preview/b7.png")
                ),
        };

        levelSelectionContext = new LevelSelectionContext(GameState.LEVEL_SELECTION_STATE , levels , selectedLevel) ;
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
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime() ;
        double nsPerUpdate = 1_000_000_000.0/60.0 ;
        double delta = 0 ;

        while(running) {

            long now = System.nanoTime() ;
            delta += (now - lastTime) / nsPerUpdate ;
            lastTime = now ;

            while(delta >= 1) {
                GameState gameState = levelSelectionContext.getGameState() ;
                switch(gameState) {
                    case LEVEL_SELECTION_STATE :
                        updateLevelSelection(levelSelectionContext);
                        break;
                    case GAME_PLAYING_STATE:
                        updateGame(levelSelectionContext);
                        break;
                }
                delta-- ;
            }
            /*switch render logic */
            gameRenderer.render(levelSelectionContext , robot1 , robot2 , camera) ;
        }
        stop() ;
    }

    private void updateGame(LevelSelectionContext levelSelectionContext) {
        playerController.control(levelSelectionContext) ;
        collisionHandler.handleCollisions(collisionResolver , levelSelectionContext.getLevel() , robot1 , robot2 , WIDTH , HEIGHT) ;

        robot1.updateAnimation();
        robot2.updateAnimation();

        robotSystem.robotsShooting();
        robotSystem.checkShootingRobots();
        robotSystem.checkAttacksRobots();
        robotSystem.checkRespawns();
        robotSystem.checkWinCondition();
        physicsSystem.update(robot1 , robot2 , levelSelectionContext.getLevel()) ;
    }

    private void updateLevelSelection(LevelSelectionContext levelSelectionContext) {
        playerController.control(levelSelectionContext) ;
    }
}
