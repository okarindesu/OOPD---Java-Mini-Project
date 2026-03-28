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
    private Camera camera ;

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



        filePath = "resources/levels/Vertical_Movement.wrl" ;
        level = LevelLoader.loadlevel(filePath) ;
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

        robot1 = new Robot(initPos1);
        robot2 = new Robot(initPos2);

        robotSystem = new RobotSystem(robot1, robot2);

        level = LevelLoader.loadlevel(filePath);

        playerController = new PlayerController(robot1, robot2, inputHandler);

        camera.setCameraX(0);
        camera.setCameraY(0);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime() ;
        double nsPerUpdate = 1_000_000_000.0/60.0 ;
        double delta = 0 ;

        while(running) {
            playerController.control() ;
            collisionHandler.handleCollisions(collisionResolver, level, robot1, robot2, projectileSystem, WIDTH, HEIGHT);

            robotSystem.checkAttacksRobots();
            robotSystem.checkRespawns();
            robotSystem.checkWinCondition();

            // restart logic
            if (robotSystem.getWinner() != null) {
                if (inputHandler.isKeyPressed(KeyEvent.VK_R) && !restartHandled) {
                    restartGame();
                    restartHandled = true;
                }

                if (!inputHandler.isKeyPressed(KeyEvent.VK_R)) {
                    restartHandled = false;
                }
            }

            long now = System.nanoTime() ;
            delta += (now - lastTime) / nsPerUpdate ;
            lastTime = now ;

            while(delta >= 1) {
                physicsSystem.update(robot1 , robot2 , level, projectileSystem) ;
                delta-- ;
            }
            gameRenderer.render(robot1 , robot2 , level , camera, robotSystem.getWinner(), projectileSystem) ;
        }
        stop() ;
    }


}
