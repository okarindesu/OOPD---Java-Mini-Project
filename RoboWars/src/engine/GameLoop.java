package engine;

import entities.Level;
import entities.LevelLoader;
import physics.CollisionHandler;
import physics.CollisionResolver;
import physics.GameRenderer;
import physics.PhysicsSystem;
import utils.Vector2D;

import java.awt.*;
import entities.Robot;
import java.util.ArrayList;
import java.util.List;

public class GameLoop extends Canvas implements Runnable {
    private Thread thread ;
    private boolean running = false ;
    private Vector2D initPos1 ;
    private Vector2D initPos2 ;

    private List<PlayerController> playerControllers ;
    private InputHandler inputHandler ;
    private List<Robot> robots ;
    private PhysicsSystem physicsSystem ;
    private GameRenderer gameRenderer ;
    private CollisionHandler collisionHandler ;
    private CollisionResolver collisionResolver ;
    private String filePath ;
    private Level level ;
    private String gameStatus = "Playing" ; // Playing, Player 1 Wins, Player 2 Wins

    public static final int WIDTH = 1280 ;
    public static final int HEIGHT = 720 ;


    public GameLoop() {
        initPos1 = new Vector2D(100 , 0) ;
        initPos2 = new Vector2D(1000 , 0) ;
        
        robots = new ArrayList<>();
        robots.add(new Robot(initPos1));
        robots.add(new Robot(initPos2));

        inputHandler = new InputHandler() ;
        addKeyListener(inputHandler) ;
        setFocusable(true) ;

        playerControllers = new ArrayList<>();
        playerControllers.add(new PlayerController(robots.get(0), inputHandler, true));
        playerControllers.add(new PlayerController(robots.get(1), inputHandler, false));
        
        physicsSystem = new PhysicsSystem() ;
        gameRenderer = new GameRenderer(this , WIDTH , HEIGHT) ;
        setPreferredSize(new Dimension(WIDTH , HEIGHT)) ;

        collisionHandler = new CollisionHandler() ;
        collisionResolver = new CollisionResolver() ;

        filePath = "resources/levels/Basic_Platforms.wrl" ;
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

    @Override
    public void run() {
        long lastTime = System.nanoTime() ;
        double nsPerUpdate = 1_000_000_000.0/60.0 ;
        double delta = 0 ;

        while(running && gameStatus.equals("Playing")) {
            for (int i = 0; i < playerControllers.size(); i++) {
                playerControllers.get(i).control() ;
                playerControllers.get(i).applyBoundaryConstraints();
            }
            
            collisionHandler.handleCollisions(collisionResolver , level , robots) ;
            
            // The 3 will check for attacks, respawns, and win conditions 
            checkAttacks();
            
            checkRespawns();
            
            checkWinCondition();

            long now = System.nanoTime() ;
            delta += (now - lastTime) / nsPerUpdate ;
            lastTime = now ;

            while(delta >= 1) {
                for (Robot robot : robots) {
                    physicsSystem.update(robot , level) ;
                }
                delta-- ;
            }
            gameRenderer.render(robots , level, gameStatus) ;
        }
        stop() ;
    }
    
    private void checkAttacks() {
        for (int i = 0; i < robots.size(); i++) {
            Robot attacker = robots.get(i);
            if (attacker.isAttacking) {
                for (int j = 0; j < robots.size(); j++) {
                    if (i != j) {
                        Robot target = robots.get(j);
                        if (checkAttackCollision(attacker, target)) {
                            target.takeDamage(Robot.getAttackDamage());
                            attacker.isAttacking = false;
                        }
                    }
                }
                attacker.isAttacking = false;
            }
        }
    }
    
    private boolean checkAttackCollision(Robot attacker, Robot target) {
        float ax = attacker.getPosition().getVector2DX();
        float ay = attacker.getPosition().getVector2DY();
        float aw = attacker.getRoboWidth();
        float ah = attacker.getRoboHeight();
        
        float tx = target.getPosition().getVector2DX();
        float ty = target.getPosition().getVector2DY();
        float tw = target.getRoboWidth();
        float th = target.getRoboHeight();
        
        // Extend attack range slightly
        float attackRangeExtension = 15.0f;
        
        boolean overlapX = ax - attackRangeExtension < tx + tw && ax + aw + attackRangeExtension > tx;
        boolean overlapY = ay - attackRangeExtension < ty + th && ay + ah + attackRangeExtension > ty;
        
        return overlapX && overlapY;
    }
    
    private void checkRespawns() {
        for (Robot robot : robots) {
            if (robot.isDead() && robot.hasLivesRemaining()) {
                robot.respawn();
            }
        }
    }
    
    private void checkWinCondition() {
        Robot p1 = robots.get(0);
        Robot p2 = robots.get(1);
        
        if (!p1.hasLivesRemaining() && p1.isDead()) {
            gameStatus = "Player 2 Wins!";
        } else if (!p2.hasLivesRemaining() && p2.isDead()) {
            gameStatus = "Player 1 Wins!";
        }
    }
    
    public String getGameStatus() {
        return gameStatus;
    }
}
