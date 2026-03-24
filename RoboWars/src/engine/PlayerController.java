package engine;

import entities.Robot;
import utils.Vector2D;

import java.awt.event.KeyEvent;

public class PlayerController {
    private Robot robot ;
    private InputHandler input ;
    private boolean isPlayer1 ;
    private static final int WORLD_WIDTH = 1280 ;
    private static final int WORLD_HEIGHT = 720 ;

    public PlayerController(Robot robot , InputHandler input, boolean isPlayer1) {
        this.robot = robot ;
        this.input = input ;
        this.isPlayer1 = isPlayer1 ;
    }

    public void control() {
        if(robot.onSurface) robot.getVelocity().set(0 , robot.getVelocity().getVector2DY()) ;
        
        Vector2D pos = robot.getPosition();
        
        if (isPlayer1) {
            // Player 1: WASD controls and Q for attack
            if(input.isKeyPressed(KeyEvent.VK_A)) {
                Vector2D vec = robot.getVelocity() ;
                float newX = pos.getVector2DX() - robot.defaultVel * 0.016f;
                newX = Math.max(0, newX);
                vec.set(new Vector2D(-robot.defaultVel , vec.getVector2DY()));
            }

            if(input.isKeyPressed(KeyEvent.VK_D)) {
                Vector2D vec = robot.getVelocity() ;
                float newX = pos.getVector2DX() + robot.defaultVel * 0.016f + robot.getRoboWidth();
                newX = Math.min(newX, WORLD_WIDTH);
                vec.set(new Vector2D(robot.defaultVel , vec.getVector2DY()));
            }

            if(input.isKeyPressed(KeyEvent.VK_W)) {
                robot.jump() ;
            }
            
            if(input.isKeyPressed(KeyEvent.VK_Q)) {
                robot.attack() ;
            }
        } else {
            // Player 2: Arrow key controls and space for attack
            if(input.isKeyPressed(KeyEvent.VK_LEFT)) {
                Vector2D vec = robot.getVelocity() ;
                float newX = pos.getVector2DX() - robot.defaultVel * 0.016f;
                newX = Math.max(0, newX);
                vec.set(new Vector2D(-robot.defaultVel , vec.getVector2DY()));
            }

            if(input.isKeyPressed(KeyEvent.VK_RIGHT)) {
                Vector2D vec = robot.getVelocity() ;
                float newX = pos.getVector2DX() + robot.defaultVel * 0.016f + robot.getRoboWidth();
                newX = Math.min(newX, WORLD_WIDTH);
                vec.set(new Vector2D(robot.defaultVel , vec.getVector2DY()));
            }

            if(input.isKeyPressed(KeyEvent.VK_UP)) {
                robot.jump() ;
            }
            
            if(input.isKeyPressed(KeyEvent.VK_SPACE)) {
                robot.attack() ;
            }
        }
    }
    
    public void applyBoundaryConstraints() {
        Vector2D pos = robot.getPosition();
        float x = pos.getVector2DX();
        float y = pos.getVector2DY();
        float w = robot.getRoboWidth();
        float h = robot.getRoboHeight();
        
        // Clamp horizontal position
        if (x < 0) {
            pos.set(0, y);
            robot.getVelocity().set(0, robot.getVelocity().getVector2DY());
        }
        if (x + w > WORLD_WIDTH) {
            pos.set(WORLD_WIDTH - w, y);
            robot.getVelocity().set(0, robot.getVelocity().getVector2DY());
        }
        
        // If robot falls below screen, respawn
        if (y > WORLD_HEIGHT) {
            robot.respawn();
        }
    }
}
